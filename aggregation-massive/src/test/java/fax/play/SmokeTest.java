package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.logging.Logger;

import org.infinispan.Cache;
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
      assertThat(SaleFactory.countAllValues(result)).isEqualTo(ITEMS_PER_DAY);
   }
}
