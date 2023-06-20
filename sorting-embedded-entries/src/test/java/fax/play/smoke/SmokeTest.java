package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.config.CacheManagerFactory;
import fax.play.model.BasicContainer;
import fax.play.model.IndexedContainer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   public EmbeddedCacheManager cacheManager;

   @BeforeAll
   public void beforeAll() {
      cacheManager = CacheManagerFactory.create();
   }

   @AfterAll
   public void afterAll() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }

   @Test
   public void basicSorting_noPagination() {
      Cache<Integer, BasicContainer> cache = cacheManager.getCache(CacheManagerFactory.BASIC_CACHE_NAME);
      for (int i = 0; i < 100_000; i++) {
         cache.put(i, new BasicContainer(new Object(), i, "bla bla bla"));
      }

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<BasicContainer> query = queryFactory.create("from fax.play.model.BasicContainer c order by c.moment");
      query.maxResults(100_000);
      List<BasicContainer> list = query.list();

      assertThat(list).hasSize(100_000);
   }

   @Test
   public void basicSorting_pagination() { // this is very slow!!
      Cache<Integer, BasicContainer> cache = cacheManager.getCache(CacheManagerFactory.BASIC_CACHE_NAME);
      for (int i = 0; i < 100_000; i++) {
         cache.put(i, new BasicContainer(new Object(), i, "bla bla bla"));
      }

      ArrayList<Object> sink = new ArrayList<>(100_000);

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<BasicContainer> query = queryFactory.create("from fax.play.model.BasicContainer c order by c.moment");
      for (int i = 0; i < 100; i++) {
         query.maxResults(1000);
         query.startOffset(i * 1000);
         List<BasicContainer> chunk = query.list();
         sink.addAll(chunk);
      }

      assertThat(sink).hasSize(100_000);
   }

   @Test
   public void indexedSorting_noPagination() {
      Cache<Integer, IndexedContainer> cache = cacheManager.getCache(CacheManagerFactory.BASIC_CACHE_NAME);
      for (int i = 0; i < 100_000; i++) {
         cache.put(i, new IndexedContainer(new Object(), i, "bla bla bla"));
      }

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<BasicContainer> query = queryFactory.create("from fax.play.model.IndexedContainer c order by c.moment");
      query.maxResults(100_000);
      List<BasicContainer> list = query.list();

      assertThat(list).hasSize(100_000);
   }

   @Test
   public void indexedSorting_pagination() {
      Cache<Integer, IndexedContainer> cache = cacheManager.getCache(CacheManagerFactory.INDEXED_CACHE_NAME);
      for (int i = 0; i < 100_000; i++) {
         cache.put(i, new IndexedContainer(new Object(), i, "bla bla bla"));
      }

      ArrayList<Object> sink = new ArrayList<>(100_000);

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<IndexedContainer> query = queryFactory.create("from fax.play.model.IndexedContainer c order by c.moment");
      for (int i = 0; i < 100; i++) {
         query.maxResults(1000);
         query.startOffset(i * 1000);
         List<IndexedContainer> chunk = query.list();
         sink.addAll(chunk);
      }

      assertThat(sink).hasSize(100_000);
   }

   @Test
   public void indexedSorting_scrolling() {
      Cache<Integer, IndexedContainer> cache = cacheManager.getCache(CacheManagerFactory.INDEXED_CACHE_NAME);
      for (int i = 0; i < 100_000; i++) {
         cache.put(i, new IndexedContainer(new Object(), i, "bla bla bla"));
      }

      ArrayList<Object> sink = new ArrayList<>(100_000);
      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<IndexedContainer> query = queryFactory.create("from fax.play.model.IndexedContainer c order by c.moment");
      query.maxResults(100_000);
      try (CloseableIterator<IndexedContainer> iterator = query.iterator()) {
         while (iterator.hasNext()) {
            sink.add(iterator.next());
         }
      }

      assertThat(sink).hasSize(100_000);
   }
}

