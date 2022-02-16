package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.model1.Model1A;
import fax.play.model1.Schema1A;
import fax.play.model1.Model1B;
import fax.play.model1.Schema1B;
import fax.play.model3.Model2A;
import fax.play.model3.Model2Migrator;
import fax.play.model3.Schema2A;
import fax.play.service.CacheDefinition;
import fax.play.service.CacheProvider;
import fax.play.service.Model;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private final CacheProvider cacheProvider = new CacheProvider();

   @Test
   public void schemaEvolution() {
      RemoteCache<String, Model> cache = cacheProvider
            .init(new CacheDefinition(CacheProvider.CACHE1_NAME, "Model1"), Schema1A.INSTANCE)
            .getCache(CacheProvider.CACHE1_NAME);
      cache.clear();

      cache.put("1", new Model1A("Fabio"));

      Query<Model> query = Search.getQueryFactory(cache).create("from Model1");
      List<Model> result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactly("Fabio");
      assertThat(result).hasOnlyElementsOfType(Model1A.class);

      cache = cacheProvider
            .updateSchemaAndGet(Schema1B.INSTANCE)
            .getCache(CacheProvider.CACHE1_NAME);

      cache.put("2", new Model1B("Alessia", "Alessia"));

      QueryFactory factory = Search.getQueryFactory(cache);

      query = factory.create("from Model1");
      result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactlyInAnyOrder("Fabio", "Alessia");
      assertThat(result).hasOnlyElementsOfType(Model1B.class);

      // targeting old field
      query = factory.create("from Model1 where oldName is not null");
      result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactlyInAnyOrder("Fabio", "Alessia");
      assertThat(result).hasOnlyElementsOfType(Model1B.class);

      // targeting new field
      assertThatThrownBy(() -> factory.create("from Model1 where newName is not null").execute())
            .isInstanceOf(HotRodClientException.class)
            .hasMessageContaining("Unknown field 'newName");

      RemoteCacheManager cacheManager = cacheProvider
            .updateSchemaAndGet(new CacheDefinition(CacheProvider.CACHE2_NAME, "Model2"), Schema1B.INSTANCE, Schema2A.INSTANCE);

      cache = cacheManager.getCache(CacheProvider.CACHE1_NAME);
      RemoteCache<String, Model> cache2 = cacheManager.getCache(CacheProvider.CACHE2_NAME);

      // migrate the entities to the new cache
      assertThat(cache.get("1")).isInstanceOf(Model1B.class);
      assertThat(cache.get("2")).isInstanceOf(Model1B.class);

      cache2.put("1", Model2Migrator.migrate((Model1B) cache.get("1")));
      cache2.put("2", Model2Migrator.migrate((Model1B) cache.get("2")));

      // add new entities
      cache2.put("3", new Model2A("Valentina"));

      query = Search.getQueryFactory(cache2).create("from Model2");
      result = query.execute().list();
      assertThat(result).hasSize(3);
      assertThat(result).hasOnlyElementsOfType(Model2A.class);

      // on the new cache it is possible to target the new fields on queries
      query = Search.getQueryFactory(cache2).create("from Model2 where newName is not null");
      result = query.execute().list();
      assertThat(result).hasSize(3);
      assertThat(result).hasOnlyElementsOfType(Model2A.class);
   }

   @AfterAll
   public void afterAll() {
      cacheProvider.stop();
   }
}
