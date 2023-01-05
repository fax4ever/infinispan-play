package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContainerTest {

   private Config config;
   private RemoteCache<String, Container> cache;

   @BeforeAll
   public void start() {
      config = new Config();
      cache = config.cache();
   }

   @AfterAll
   public void end() {
      if (config != null) {
         config.shutdown();
      }
   }

   @BeforeEach
   public void beforeEach() {
      cache.clear();
   }

   @Test
   public void test() {
      Content[] contentArray = {new Content("a"), new Content("b"), new Content("c")};
      List<Content> contentList = Arrays.asList(contentArray);
      Container container = new Container(
            new Content("some-content"), contentList, new HashSet<>(contentList), contentArray);
      cache.put("1", container);

      Container loaded = cache.get("1");
      assertThat(loaded.getContent().getValue()).isEqualTo("some-content");
      assertThat(loaded.getContentList()).extracting("value").containsExactly("a", "b", "c");
      assertThat(loaded.getContentSet()).extracting("value").containsExactlyInAnyOrder("a", "b", "c");
      assertThat(loaded.getContentArray()).extracting("value").containsExactly("a", "b", "c");
   }
}
