package fax.play.smoke;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.model1.Model1;
import fax.play.model1.Schema1;
import fax.play.model2.Model2;
import fax.play.model2.Schema2;
import fax.play.model3.Model3;
import fax.play.model3.Schema3;
import fax.play.service.RemoteCacheManagerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   @Test
   public void schemaEvolution() {
      RemoteCacheManager cacheManager = RemoteCacheManagerFactory.create(Schema1.INSTANCE);
      RemoteCache<String, Object> cache = cacheManager.getCache(RemoteCacheManagerFactory.CACHE_NAME);

      Model1 model1 = new Model1();
      model1.id = "10000000";
      model1.name = "model1 # 000000";
      model1.clientTemplateId = "ct0";
      cache.put("10000000", model1);

      cacheManager = RemoteCacheManagerFactory.create(Schema2.INSTANCE);
      cache = cacheManager.getCache(RemoteCacheManagerFactory.CACHE_NAME);

      Model2 model2 = new Model2();
      model2.id = "20000000";
      model2.name = "model2 # 000000";
      model2.clientScopeId = "cs0";
      cache.put("20000000", model2);

      cacheManager = RemoteCacheManagerFactory.create(Schema3.INSTANCE);
      cache = cacheManager.getCache(RemoteCacheManagerFactory.CACHE_NAME);

      Model3 model3 = new Model3();
      model3.id = "30000000";
      model3.name = "model3 # 000000";
      model3.clientScopeId = "cs0";
      model3.timeout1 = 10;
      model3.timeout2 = 5;
      cache.put("30000000", model3);
   }
}
