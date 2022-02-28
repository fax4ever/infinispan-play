package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.Test;

import fax.play.config.Config;
import fax.play.entity.KeywordEntity;

public class SimpleIndexedEntityTest {

   @Test
   public void smoke() {
      RemoteCacheManager remoteCacheManager = Config.start();
      RemoteCache<Integer, KeywordEntity> cache = remoteCacheManager.getCache(Config.CACHE_NAME);

      KeywordEntity entity = new KeywordEntity("blablabla");
      cache.put(1, entity);

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      assertThat(queryFactory.create("from KeywordEntity where keyword : 'blablabla'").execute().hitCount().orElse(-1))
            .isEqualTo(1);
   }

   @Test
   public void reindex() {
      RemoteCacheManager remoteCacheManager = Config.start();
      RemoteCache<Integer, KeywordEntity> cache = remoteCacheManager.getCache(Config.CACHE_NAME);

      KeywordEntity entity = new KeywordEntity("blablabla");
      cache.put(1, entity);

      remoteCacheManager.administration().reindexCache(Config.CACHE_NAME);

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      assertThat(queryFactory.create("from KeywordEntity where keyword : 'blablabla'").execute().hitCount().orElse(-1))
            .isEqualTo(1);
   }

}
