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

import fax.play.model1.Model1;
import fax.play.model1.Schema1;
import fax.play.model2.Model2;
import fax.play.model2.Schema2;
import fax.play.model3.Model3;
import fax.play.model3.Model3Migrator;
import fax.play.model3.Schema3;
import fax.play.service.CacheDefinition;
import fax.play.service.CacheFactory;
import fax.play.service.CacheProvider;
import fax.play.service.Model;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private final CacheProvider cacheProvider = new CacheProvider();

   @Test
   public void schemaEvolution() {
      RemoteCache<String, Model> cache = cacheProvider
            .init(new CacheDefinition(CacheProvider.CACHE1_NAME, "Model"), Schema1.INSTANCE)
            .getCache(CacheProvider.CACHE1_NAME);
      cache.clear();

      cache.put("1", new Model1("Fabio"));

      Query<Model> query = Search.getQueryFactory(cache).create("from Model");
      List<Model> result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactly("Fabio");
      assertThat(result).hasOnlyElementsOfType(Model1.class);

      cache = cacheProvider
            .updateSchemaAndGet(Schema2.INSTANCE)
            .getCache(CacheProvider.CACHE1_NAME);

      cache.put("2", new Model2("Alessia", "Alessia"));

      QueryFactory factory = Search.getQueryFactory(cache);

      query = factory.create("from Model");
      result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactlyInAnyOrder("Fabio", "Alessia");
      assertThat(result).hasOnlyElementsOfType(Model2.class);

      // targeting old field
      query = factory.create("from Model where oldName is not null");
      result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactlyInAnyOrder("Fabio", "Alessia");
      assertThat(result).hasOnlyElementsOfType(Model2.class);

      // targeting new field
      assertThatThrownBy(() -> factory.create("from Model where newName is not null").execute())
            .isInstanceOf(HotRodClientException.class)
            .hasMessageContaining("Unknown field 'newName");

      RemoteCacheManager cacheManager = cacheProvider
            .updateSchemaAndGet(new CacheDefinition(CacheProvider.CACHE2_NAME, "Model3"), Schema2.INSTANCE, Schema3.INSTANCE);

      cache = cacheManager.getCache(CacheProvider.CACHE1_NAME);
      RemoteCache<String, Model> cache2 = cacheManager.getCache(CacheProvider.CACHE2_NAME);

      // migrate the entities to the new cache
      assertThat(cache.get("1")).isInstanceOf(Model2.class);
      assertThat(cache.get("2")).isInstanceOf(Model2.class);

      cache2.put("1", Model3Migrator.migrate((Model2) cache.get("1")));
      cache2.put("2", Model3Migrator.migrate((Model2) cache.get("2")));

      // add new entities
      cache2.put("3", new Model3("Valentina"));

      query = Search.getQueryFactory(cache2).create("from Model3");
      result = query.execute().list();
      assertThat(result).hasSize(3);
      assertThat(result).hasOnlyElementsOfType(Model3.class);

      // on the new cache it is possible to target the new fields on queries
      query = Search.getQueryFactory(cache2).create("from Model3 where newName is not null");
      result = query.execute().list();
      assertThat(result).hasSize(3);
      assertThat(result).hasOnlyElementsOfType(Model3.class);
   }

   @AfterAll
   public void afterAll() {
      cacheProvider.stop();
   }
}
