package fax.play;

import org.infinispan.client.rest.RestCacheClient;
import org.infinispan.client.rest.RestClient;
import org.infinispan.client.rest.RestEntity;
import org.infinispan.client.rest.RestResponse;
import org.infinispan.client.rest.configuration.RestClientConfigurationBuilder;
import org.infinispan.commons.dataconversion.MediaType;

public class Config {

   private static final String CACHE_NAME = "blablabla";
   private static final String CACHE_DEFINITION = "{ \"local-cache\" : { \"encoding\" : { \"media-type\" : \"application/x-protostream\" } } }";

   private final RestClient restClient;
   private final RestCacheClient cache;

   public Config() {
      restClient = RestClient.forConfiguration(new RestClientConfigurationBuilder().addServer()
            .host("127.0.0.1").port(11222)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .build());

      cache = restClient.cache(CACHE_NAME);

      RestResponse getCacheList = restClient.caches().toCompletableFuture().join();
      String body = getCacheList.getBody();

      if (!body.contains(CACHE_NAME)) {
         RestResponse response = cache.createWithConfiguration(RestEntity.create(MediaType.APPLICATION_JSON, CACHE_DEFINITION))
               .toCompletableFuture().join();

         int status = response.getStatus();
         if (status < 200 || status >= 300 ) {
            throw new RuntimeException("Unexpected error creating the cache: " + response.getBody());
         }
      }
   }

   public void shutdown() throws Exception {
      if (restClient != null) {
         restClient.close();
      }
   }

   public RestCacheClient getCache() {
      return cache;
   }
}
