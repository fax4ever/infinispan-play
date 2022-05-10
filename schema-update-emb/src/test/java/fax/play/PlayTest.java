package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.infinispan.Cache;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PlayTest {

   private Cache<Object, Model> cache;

   @Test
   public void smoke() {
      cache = Config.createCache();

      cache.put("1", new ModelA("Fabio"));

      Query<Model> query = Search.getQueryFactory(cache).create("from fax.play.ModelA where original is not null");
      List<Model> result = query.execute().list();

      assertThat(result).extracting("original").containsExactly("Fabio");
      assertThat(result).hasOnlyElementsOfType(ModelA.class);

      // TODO ISPN-13648
//      cache.getCacheManager().administration()
//            .updateIndexSchema(Config.CACHE_NAME, Collections.singleton(ModelB.class))
//            .join();

      // we need also to update the serialization context that I think it is impossible in embedded mode

//      cache.put("2", new ModelB("Alessia", "Alessia"));
//
//      query = Search.getQueryFactory(cache).create("from fax.play.ModelB where original is not null");
//      result = query.execute().list();
//
//      assertThat(result).extracting("original").containsExactly("Alessia");
//      assertThat(result).hasOnlyElementsOfType(ModelB.class);
//
//      query = Search.getQueryFactory(cache).create("from fax.play.ModelB where original is not null");
//      result = query.execute().list();
//
//      assertThat(result).extracting("different").containsExactly("Alessia");
//      assertThat(result).hasOnlyElementsOfType(ModelB.class);
   }

   @AfterEach
   public void after() {
      if ( cache != null ) {
         cache.stop();
         cache.getCacheManager().stop();
      }
      cache = null;
   }
}
