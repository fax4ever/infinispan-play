package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
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
import fax.play.service.CacheProvider;
import fax.play.service.Model;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private final CacheProvider cacheProvider = new CacheProvider();

   @Test
   public void schemaEvolution() {
      RemoteCache<String, Model> cache = cacheProvider.init(Schema1.INSTANCE);
      cache.clear();

      cache.put("1", new Model1("Fabio"));

      Query<Model> query = Search.getQueryFactory(cache).create("from Model");
      List<Model> result = query.execute().list();
      assertThat(result).extracting("oldName").containsExactly("Fabio");
      assertThat(result).hasOnlyElementsOfType(Model1.class);

      cache = cacheProvider.updateSchemaAndGet(Schema2.INSTANCE);

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
            .hasMessageContaining("org.infinispan.objectfilter.ParsingException", "" +
                  "ISPN028501: The type Model does not have an accessible property named 'newName'");

      cache = cacheProvider.updateSchemaAndGet(Schema3.INSTANCE);

      cache.replace("1", Model3Migrator.migrate(cache.get("1")));
      cache.replace("2", Model3Migrator.migrate(cache.get("2")));
      cache.put("3", new Model3("Valentina"));

      query = Search.getQueryFactory(cache).create("from Model");
      result = query.execute().list();
      assertThat(result).hasSize(3);
      assertThat(result).hasOnlyElementsOfType(Model3.class);
   }

   @AfterAll
   public void afterAll() {
      cacheProvider.stop();
   }
}
