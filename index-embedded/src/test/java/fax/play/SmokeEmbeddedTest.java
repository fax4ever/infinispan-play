package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
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
   public void test() {
      RemoteCache<Integer, Poem> cache = config.getPoemCache();

      // biographies taken from Wikipedia https://en.wikipedia.org/wiki:
      cache.put(1, new Poem("The Raven", new Author("Edgar Allen Poe", "Edgar Allan Poe (/poʊ/; born Edgar Poe; January 19, 1809 – October 7, 1849) was an American writer, poet, editor, and literary critic.")));
      cache.put(2, new Poem("Because I could not stop for Death", new Author("Emily Dickinson", "Emily Elizabeth Dickinson (December 10, 1830 – May 15, 1886) was an American poet. Little-known during her life, she has since been regarded as one of the most important figures in American poetry.")));
      cache.put(3, new Poem("The New Colossus", new Author("Emma Lazarus", "Emma Lazarus (July 22, 1849 – November 19, 1887) was an American author of poetry, prose, and translations, as well as an activist for Jewish and Georgist causes.")));

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<Poem> poems = queryFactory.create("from poem.Poem p where p.author.biography : 'american'");

      assertThat(poems).hasSize(3);
   }
}
