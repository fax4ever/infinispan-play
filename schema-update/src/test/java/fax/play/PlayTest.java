package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PlayTest {

   private RemoteCache<Object, Model> cache;

   @Test
   public void smoke() {
      cache = Config.createCache();

      cache.put("1", new ModelA("Fabio"));

      Query<Model> query = Search.getQueryFactory(cache).create("from Model where original is not null");
      List<Model> result = query.execute().list();

      assertThat(result).extracting("original").containsExactly("Fabio");
      assertThat(result).hasOnlyElementsOfType(ModelA.class);

      Config.updateSchemaIndex(cache);

      cache.put("2", new ModelB("Alessia", "Alessia"));

      query = Search.getQueryFactory(cache).create("from Model where original is not null");
      result = query.execute().list();

      assertThat(result).extracting("original").containsExactly("Fabio", "Alessia");
      assertThat(result).hasOnlyElementsOfType(ModelB.class);

      query = Search.getQueryFactory(cache).create("from Model where different is not null");
      result = query.execute().list();

      assertThat(result).extracting("different").containsExactly("Alessia");
      assertThat(result).hasOnlyElementsOfType(ModelB.class);
   }

   @AfterEach
   public void after() {
      if ( cache != null ) {
         cache.stop();
         cache.getRemoteCacheContainer().stop();
      }
      cache = null;
   }
}
