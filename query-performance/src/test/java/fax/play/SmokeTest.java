package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.client.rest.RestClient;
import org.infinispan.client.rest.RestResponse;
import org.infinispan.client.rest.configuration.RestClientConfigurationBuilder;
import org.infinispan.query.dsl.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.model.Bla;
import fax.play.model.Shape;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private static final int CHUNK_SIZE = 10000;
   private static final int MASSIVE_SIZE = CHUNK_SIZE * 100;

   private Config config;
   private RestClient restClient;

   @BeforeAll
   public void start() {
      config = new Config();
      restClient = RestClient.forConfiguration(new RestClientConfigurationBuilder().addServer()
            .host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .build());
   }

   @AfterAll
   public void end() throws Exception {
      if (restClient != null) {
         restClient.close();
      }
      if (config != null) {
         config.shutdown();
      }
   }

   @Test
   public void shape() throws Exception {
      RemoteCache<String, Shape> cache = config.getShapeCache();

      cache.put("circle", new Shape("circle", 7, Integer.MAX_VALUE, "A circle is a shape consisting ... bla bla bla"));
      cache.put("square", new Shape("square", 9, 4, "In Euclidean geometry, a square is a regular quadrilateral ... bla bla bla"));
      cache.put("triangle", new Shape("triangle", 29, 3, "In Euclidean geometry, any three points, when non-collinear ... bla bla bla"));

      Query<Long[]> query = Search.getQueryFactory(cache).create("select count(s.name) from shape s");
      List<Long[]> result = query.execute().list();

      assertThat(result).containsExactly(new Long[]{3L});

      RestResponse restResponse = restClient.cache(cache.getName()).searchStats().toCompletableFuture().get();
      assertThat(restResponse).isNotNull();
      String body = restResponse.getBody();
      assertThat(body).isNotNull();

      QueryStatistics queryStatistics = new QueryStatistics(body);
      assertThat(queryStatistics).isNotNull();

      // aggregations always produces hybrid queries
      assertThat(queryStatistics.getHybridCount()).isPositive();
   }

   @Test
   public void bla() {
      RemoteCache<String, Bla> cache = config.getBlaCache();
      HashMap<String, Bla> bulk = new HashMap<>(CHUNK_SIZE);

      ArrayList<CompletableFuture> promises = new ArrayList<>(MASSIVE_SIZE / CHUNK_SIZE + 1);

      for (long i = 0; i < MASSIVE_SIZE; i++) {
         int numeral = (int) i % CHUNK_SIZE;
         String sign = numeral + "";
         bulk.put(sign, new Bla(numeral, sign, i));

         if (numeral == CHUNK_SIZE - 1) {
            // flush the bulk
            promises.add(cache.putAllAsync(bulk));
            bulk.clear();
         }
      }

      CompletableFuture.allOf(promises.toArray(new CompletableFuture[promises.size()]));
   }
}
