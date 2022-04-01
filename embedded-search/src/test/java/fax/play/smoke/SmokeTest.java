package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.objectfilter.ParsingException;
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
   public void beforeAll() {
      cacheManager = Config.start();
   }

   @AfterAll
   public void afterAll() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }

   @Test
   public void test() {
      Cache<String, Developer> cache = cacheManager.getCache(Config.CACHE_NAME);
      cache.clear();

      cache.put("fabio", new Developer("fax4ever", "Java Go", 2));
      cache.put("alessia", new Developer("always7pan", "Java C++ Cobol Python", 7));
      cache.put("nenna", new Developer("vale5paga", "C# Lisp Cobol", 3));
      cache.put("antonia", new Developer("antonia3mini", "Java C*a Prolog", 3));

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

      query = factory.create("from fax.play.entity.Developer where languages : 'C*a'");
      list = query.execute().list();
      // ISPN-13603 Escape special char issue
      assertThat(list).isEmpty();

      // ISPN-13603 Escape special char issue
      assertThatThrownBy(() -> factory.create("from fax.play.entity.Developer where languages : 'C\\*a'").execute().list())
            .isInstanceOf(ParsingException.class);

      for (int i = 0; i < 20; i++) {
         String id = (i < 10) ? "d0" + i : "d" + i;
         cache.put(id, new Developer(id, "Java Go", i % 20));
      }

      query = factory.create("from fax.play.entity.Developer d WHERE d.projects IN (:id0, :id1, :id2, :id3, :id4, :id5, :id6, :id7, :id8, :id9, :id10, :id11, :id12, :id13, :id14, :id15, :id16, :id17, :id18, :id19) and d.languages : 'Java' order by d.nick");
      for (int i = 0; i < 20; i++) {
         query.setParameter("id" + i, i);
      }
      query.startOffset(10);
      query.maxResults(-1);
      list = query.execute().list();
      // No ISPN-13702 on embedded
      assertThat(list).extracting("nick").containsExactly("d08", "d09", "d10", "d11", "d12", "d13", "d14", "d15", "d16", "d17", "d18", "d19", "fax4ever");
   }
}
