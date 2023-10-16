package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.infinispan.Cache;
import org.infinispan.commons.api.query.HitCount;
import org.infinispan.commons.api.query.Query;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   static final Logger log = Logger.getLogger(SmokeTest.class.getName());

   public static final int NUMBER_OF_DAYS = 10;
   public static final int CHUNK_PER_DAY = 1000;
   public static final int CHUNK_SIZE = 100;
   public static final int ITEMS_PER_DAY = CHUNK_PER_DAY * CHUNK_SIZE;

   private final EmbeddedCacheManager cacheManager = Config.cacheManager();
   private final SaleFactory factory = new SaleFactory();

   @Test
   public void test() {
      Cache<String, Sale> cache = cacheManager.getCache();
      assertThat(cache).isNotNull();

      long start = System.currentTimeMillis();
      LocalDate startDay = LocalDate.of(2021, Month.JANUARY, 1);
      for (int i = 0; i < NUMBER_OF_DAYS; i++) {
         LocalDate day = startDay.plusDays(i);
         for (int j = 0; j < CHUNK_PER_DAY; j++) {
            cache.putAll(factory.chunk(day, j * CHUNK_SIZE, (j + 1) * CHUNK_SIZE));
         }

         log.info("Inserted day " + (i + 1) + " on " + NUMBER_OF_DAYS + ". Item size: " + ITEMS_PER_DAY + ".");
      }
      long end = System.currentTimeMillis();

      long targetDay = factory.randomDay(startDay, NUMBER_OF_DAYS);

      log.info("Inserted " + ITEMS_PER_DAY * NUMBER_OF_DAYS + " in " + (end - start) + " millis");
      Query<Object[]> query = cache.query("select status, count(code) from fax.play.Sale where moment = :moment group by status order by status");
      query.setParameter("moment", targetDay);

      start = System.currentTimeMillis();
      List<Object[]> result = query.list();
      end = System.currentTimeMillis();

      log.info("Aggregation query executed in " + (end - start) + " millis");
      Map<String, Long> aggregatedResult = SaleFactory.convert(result);
      assertThat(SaleFactory.countAllValues(aggregatedResult)).isEqualTo(ITEMS_PER_DAY);

      HashMap<String, Long> aggregationResults = new HashMap<>();
      for (Status status : Status.values()) {
         Query<Sale> specific = cache.query("from fax.play.Sale where moment = :moment and status = :status");
         specific.setParameter("moment", targetDay);
         specific.setParameter("status", status.name());
         specific.maxResults(10); // lower the max result => we don't need the values only the count
         specific.hitCountAccuracy(100_000); // raise the count accuracy if we need to target more than 10_000 items

         start = System.currentTimeMillis();
         HitCount count = specific.execute().count();
         end = System.currentTimeMillis();
         log.info("Simple query executed in " + (end - start) + " millis");

         assertThat(count.isExact()).isTrue(); // we need exact count
         aggregationResults.put(status.name(), (long) count.value());
      }
      assertThat(aggregatedResult).isEqualTo(aggregationResults);
   }
}
