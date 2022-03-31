package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

      cache.put("fabio", new Developer("fax4ever", "Java Go", 2));
      cache.put("alessia", new Developer("always7pan", "Java C++ Cobol Python", 7));
      cache.put("nenna", new Developer("vale5paga", "C# Lisp Cobol", 3));
      cache.put("antonia", new Developer("antonia3mini", "Java C Prolog", 3));

      QueryFactory factory = Search.getQueryFactory(cache);
      Query<Developer> query = factory.create("from fax.play.entity.Developer where languages : 'Java'");
      List<Developer> list = query.execute().list();
      assertThat(list).extracting("nick").containsExactlyInAnyOrder("fax4ever", "always7pan", "antonia3mini");

      query = factory.create("from fax.play.entity.Developer order by nick");
      list = query.execute().list();
      assertThat(list).extracting("nick").containsExactly("always7pan", "antonia3mini", "fax4ever", "vale5paga");

      // HSEARCH-13801 multiple index fields from same entity field are not supported at the moment
      assertThatThrownBy(() -> factory.create("from fax.play.entity.Developer order by alternative").execute().list())
            .isInstanceOf(NullPointerException.class);

      Map<String, Object> params = Collections.singletonMap("projects", 3);

      query = factory.create("from fax.play.entity.Developer where projects = :projects");
      query.setParameters(params);
      list = query.execute().list();
      assertThat(list).extracting("nick").containsExactlyInAnyOrder("antonia3mini", "vale5paga");

      query = factory.create("from fax.play.entity.Developer where languages : :language");
      query.setParameter("language", "Java");
      list = query.execute().list();
      // ISPN-13499 Ickle fulltext query not working with query parameters
      // assertThat(list).extracting("nick").containsExactlyInAnyOrder("fax4ever", "always7pan", "antonia3mini");
      assertThat(list).isEmpty();
   }
}
