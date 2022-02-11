package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.model1.Model1;
import fax.play.model1.Schema1;
import fax.play.model2.Model2;
import fax.play.model2.Schema2;
import fax.play.model3.Model3;
import fax.play.model3.Schema3;
import fax.play.model4.Model4;
import fax.play.model4.Model4Migrator;
import fax.play.model4.Schema4;
import fax.play.service.CacheProvider;
import fax.play.service.Model;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private final CacheProvider cacheProvider = new CacheProvider();

   @Test
   @SuppressWarnings("deprecation") // filling legacy fields
   public void schemaEvolution() {
      RemoteCache<String, Model> cache = cacheProvider.updateSchemaAndGet(Schema1.INSTANCE);

      cache.clear();

      Model1 model1 = new Model1();
      model1.id = "10000000";
      model1.name = "model1 # 000000";
      model1.clientTemplateId = "ct0";
      cache.put("10000000", model1);

      Query<Model> query = Search.getQueryFactory(cache).create("from model");
      List<Model> result = query.execute().list();
      assertThat(result).extracting("id").containsExactly("10000000");
      assertThat(result).hasOnlyElementsOfType(Model1.class);

      cache = cacheProvider.updateSchemaAndGet(Schema2.INSTANCE);

      Model2 model2 = new Model2();
      model2.id = "20000000";
      model2.name = "model2 # 000000";
      model2.clientScopeId = "cs0";

      // fill legacies:
      model2.clientTemplateId = "ct0";

      cache.put("20000000", model2);

      query = Search.getQueryFactory(cache).create("from model");
      result = query.execute().list();
      assertThat(result).extracting("id").containsExactlyInAnyOrder("10000000", "20000000");
      assertThat(result).hasOnlyElementsOfType(Model2.class);

      cache = cacheProvider.updateSchemaAndGet(Schema3.INSTANCE);

      Model3 model3 = new Model3();
      model3.id = "30000000";
      model3.name = "model3 # 000000";
      model3.clientScopeId = "cs0";
      model3.timeout1 = 10;
      model3.timeout2 = 5;

      // fill legacies:
      model3.clientTemplateId = "ct0";

      cache.put("30000000", model3);

      query = Search.getQueryFactory(cache).create("from model");
      result = query.execute().list();
      assertThat(result).extracting("id").containsExactlyInAnyOrder("10000000", "20000000", "30000000");
      assertThat(result).hasOnlyElementsOfType(Model3.class);

      query = Search.getQueryFactory(cache).create("from model where clientTemplateId = 'ct0'");
      result = query.execute().list();
      assertThat(result).extracting("id").containsExactlyInAnyOrder("10000000", "20000000", "30000000");
      assertThat(result).hasOnlyElementsOfType(Model3.class);

      cache = cacheProvider.updateSchemaAndGet(Schema4.INSTANCE);

      RemoteCache<String, Model> finalCache = cache;
      Model4Migrator.migrate(result, (model4) -> finalCache.replace(model4.id, model4));

      query = Search.getQueryFactory(cache).create("from model");
      result = query.execute().list();
      assertThat(result).extracting("id").containsExactlyInAnyOrder("10000000", "20000000", "30000000");
      assertThat(result).hasOnlyElementsOfType(Model4.class);
   }

   @AfterAll
   public void afterAll() {
      cacheProvider.stop();
   }
}
