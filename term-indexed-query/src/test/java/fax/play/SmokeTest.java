/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package fax.play;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.Test;

import fax.play.config.Config;
import fax.play.entity.KeywordEntity;

public class SmokeTest {

   public static final String DESCRIPTION = "foo bar% baz";

   @Test
   public void smoke() {
      RemoteCacheManager remoteCacheManager = Config.start();
      RemoteCache<Integer, KeywordEntity> remoteCache = remoteCacheManager.getCache(Config.CACHE_NAME);

      assertThatThrownBy(() -> remoteCache.put(1, new KeywordEntity(createLargeDescription(3000))))
            .isInstanceOf(HotRodClientException.class)
            .hasMessageContaining("bytes can be at most 32766");

      // the server continue to work
      KeywordEntity entity = new KeywordEntity(createLargeDescription(1));
      remoteCache.put(1, entity);

      QueryFactory queryFactory = Search.getQueryFactory(remoteCache);
      assertThat(queryFactory.create("from KeywordEntity where keyword : 'foo bar0 baz'").execute().hitCount().orElse(-1)).isOne();
   }

   public String createLargeDescription(int times) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < times; i++) {
         String desc = DESCRIPTION.replace("%", i + "");
         builder.append(desc);
         if (i < times - 1) {
            builder.append(" ");
         }
      }
      return builder.toString();
   }
}
