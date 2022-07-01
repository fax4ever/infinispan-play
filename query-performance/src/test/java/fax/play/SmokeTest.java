package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SmokeTest {

   private RemoteCache<Object, Shape> cache;

   @BeforeEach
   public void start() {
      cache = Config.setupAll();
   }

   @AfterEach
   public void end() {
      if (cache != null) {
         cache.getRemoteCacheContainer().stop();
      }
   }

   @Test
   public void test() {
      cache.put("circle", new Shape("circle", 7, Integer.MAX_VALUE, "A circle is a shape consisting ... bla bla bla"));
      cache.put("square", new Shape("square", 9, 4, "In Euclidean geometry, a square is a regular quadrilateral ... bla bla bla"));
      cache.put("triangle", new Shape("triangle", 29, 3, "In Euclidean geometry, any three points, when non-collinear ... bla bla bla"));

      Query<?> query = Search.getQueryFactory(cache).create("select count(s.name) from Shape s");
      List<?> result = query.execute().list();

      assertThat(result).isNotNull();
   }
}
