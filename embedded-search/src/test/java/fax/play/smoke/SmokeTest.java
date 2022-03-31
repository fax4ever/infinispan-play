package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.entity.Developer;
import fax.play.service.Config;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   public EmbeddedCacheManager cacheManager;

   @BeforeAll
   public void setUp() {
      cacheManager = Config.start();
   }

   @AfterAll
   public void tearDown() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }

   @Test
   public void test() {
      Cache<String, Developer> cache = cacheManager.getCache(Config.CACHE_NAME);

      cache.put("fabio", new Developer("fax4ever", "Java Go"));
      cache.put("alessia", new Developer("always7pan", "Java C++ Cobol Python"));
      cache.put("nenna", new Developer("vale5paga", "C# Lisp Cobol"));
      cache.put("antonia", new Developer("antonia3mini", "Java C Prolog"));

      QueryFactory factory = Search.getQueryFactory(cache);
      Query<Developer> query = factory.create("from fax.play.entity.Developer where languages : 'Java'");
      List<Developer> list = query.execute().list();
      assertThat(list).extracting("nick").containsExactlyInAnyOrder("fax4ever", "always7pan", "antonia3mini");

      query = factory.create("from fax.play.entity.Developer order by nick");
      list = query.execute().list();
      assertThat(list).extracting("nick").containsExactly("always7pan", "antonia3mini", "fax4ever", "vale5paga");

      // multiple index fields from same entity field are not supported at the moment
      assertThatThrownBy(() -> factory.create("from fax.play.entity.Developer order by alternative").execute().list())
            .isInstanceOf(NullPointerException.class);
   }
}
