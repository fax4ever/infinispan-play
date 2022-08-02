package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeEmbeddedTest {

   private Config config;

   @BeforeAll
   public void start() {
      config = new Config();
   }

   @AfterAll
   public void end() {
      if (config != null) {
         config.shutdown();
      }
   }

   @Test
   public void newAnnotations() {
      RemoteCache<Integer, Poem> cache = config.getPoemCache();

      // biographies taken from Wikipedia https://en.wikipedia.org/wiki
      cache.put(1, new Poem("The Raven", new Author("Edgar Allen Poe", "Edgar Allan Poe (/poʊ/; born Edgar Poe; January 19, 1809 – October 7, 1849) was an American writer, poet, editor, and literary critic.")));
      cache.put(2, new Poem("Because I could not stop for Death", new Author("Emily Dickinson", "Emily Elizabeth Dickinson (December 10, 1830 – May 15, 1886) was an American poet. Little-known during her life, she has since been regarded as one of the most important figures in American poetry.")));
      cache.put(3, new Poem("The New Colossus", new Author("Emma Lazarus", "Emma Lazarus (July 22, 1849 – November 19, 1887) was an American author of poetry, prose, and translations, as well as an activist for Jewish and Georgist causes.")));

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<Poem> query = queryFactory.create("from Poem p where p.author.biography : 'american'");
      QueryResult<Poem> result = query.execute();

      assertThat(result.hitCount()).hasValue(3);
   }

   @Test
   public void oldAnnotations() {
      RemoteCache<String, Model3J> cache = config.getModelCache();

      for (int i=0; i<3; i++) {
         Model3J model3J = createModel3J(1, i);
         cache.put(model3J.id, model3J);
      }

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<Model3J> query = queryFactory.create("from Model3 m WHERE m.nestedModel.nameAnalyzed : 'model'");
      QueryResult<Model3J> result = query.execute();

      assertThat(result.hitCount()).hasValue(3);
   }

   public static Model3J createModel3J(int version, int i) {
      Model3J m = new Model3J();
      m.entityVersion = version;
      m.id = String.valueOf((8 * 100000) + i);
      m.nameIndexed = "modelJ # " + i;
      m.name = "modelJ # " + i;

      NestedModel nestedModel = new NestedModel();
      nestedModel.name = "Nested model in Model3J";
      nestedModel.nameAnalyzed = "Nested model in Model3J";

      m.nestedModel = nestedModel;
      return m;
   }
}
