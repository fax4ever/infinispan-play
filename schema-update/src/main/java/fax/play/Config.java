package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheContainer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class Config {

   public static final String CACHE_NAME = "index-evolution-cache";

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"" + CACHE_NAME + "\" statistics=\"true\">" +
               "    <encoding media-type=\"application/x-protostream\"/>" +
               "    <indexing enabled=\"true\" storage=\"local-heap\">" +
               "        <index-reader />" +
               "        <indexed-entities>" +
               "            <indexed-entity>Model</indexed-entity>" +
               "        </indexed-entities>" +
               "    </indexing>" +
               "</local-cache>";

   private static ProtoStreamMarshaller marshaller = new ProtoStreamMarshaller();

   public static RemoteCache<Object, Model> createCache() {
      SchemaA schema = SchemaA.INSTANCE;

      ConfigurationBuilder builder = new ConfigurationBuilder();
      marshaller = new ProtoStreamMarshaller();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(marshaller);

      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());

      // Register proto schema && entity marshaller on client side
      schema.registerSchema(marshaller.getSerializationContext());
      schema.registerMarshallers(marshaller.getSerializationContext());

      // Register proto schema && entity marshaller on server side
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

      remoteCacheManager.administration().removeCache(CACHE_NAME);
      remoteCacheManager.administration().createCache(CACHE_NAME, new StringConfiguration(CACHE_DEFINITION));

      return remoteCacheManager.getCache(CACHE_NAME);
   }

   public static void updateSchemaIndex(RemoteCache<Object, Model> cache) {
      SchemaB schema = SchemaB.INSTANCE;

      RemoteCacheContainer remoteCacheContainer = cache.getRemoteCacheContainer();

      // Register proto schema && entity marshaller on client side
      schema.registerSchema(marshaller.getSerializationContext());
      schema.registerMarshallers(marshaller.getSerializationContext());

      // Register proto schema && entity marshaller on server side
      RemoteCache<String, String> metadataCache = remoteCacheContainer.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

      ((RemoteCacheManager) remoteCacheContainer).administration()
            .updateIndexSchema(CACHE_NAME);
   }
}
