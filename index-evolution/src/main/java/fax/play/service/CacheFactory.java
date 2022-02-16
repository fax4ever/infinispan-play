package fax.play.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class CacheFactory {

   private static final String CACHE_NAME_PLACEHOLDER = "{{cache-name}}";

   public static final String CACHE1_NAME = "keyword1";
   public static final String CACHE2_NAME = "keyword2";

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"" + CACHE_NAME_PLACEHOLDER + "\" statistics=\"true\">" +
               "    <encoding media-type=\"application/x-protostream\"/>" +
               "    <indexing enabled=\"true\" storage=\"local-heap\">" +
               "        <index-reader />" +
               "        <indexed-entities>" +
               "            <indexed-entity>Model</indexed-entity>" +
               "        </indexed-entities>" +
               "    </indexing>" +
               "</local-cache>";

   public static RemoteCacheManager create() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class);

      return new RemoteCacheManager(builder.build());
   }

   public static RemoteCacheManager create(GeneratedSchema schema, String cacheName) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .remoteCache(cacheName)
            .configuration(CACHE_DEFINITION.replace(CACHE_NAME_PLACEHOLDER, cacheName))
            .marshaller(ProtoStreamMarshaller.class);

      // Add marshaller in the client
      builder.addContextInitializer(schema);

      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());

      // Register proto schema on server side
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

      return remoteCacheManager;
   }

}
