package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class Config {

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"{{message-name}}\" statistics=\"true\">" +
         "    <encoding media-type=\"application/x-protostream\"/>" +
         "    <indexing enabled=\"true\" storage=\"filesystem\">" +
         "        <index-reader />" +
         "        <indexed-entities>" +
         "            <indexed-entity>{{message-name}}</indexed-entity>" +
         "        </indexed-entities>" +
         "    </indexing>" +
         "</local-cache>";

   private static final String POEM_MESSAGE_TYPE = "poem.Poem";

   private final RemoteCacheManager remoteCacheManager;
   private final RemoteCache<Integer, Poem> poemCache;

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class)
            // Register proto schema && entity marshallers on client side
            .addContextInitializer(Schema.INSTANCE);

      remoteCacheManager = new RemoteCacheManager(builder.build());

      // Register proto schema on server side
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(Schema.INSTANCE.getProtoFileName(), Schema.INSTANCE.getProtoFile());

      remoteCacheManager.administration().removeCache(POEM_MESSAGE_TYPE);
      remoteCacheManager.administration().createCache(POEM_MESSAGE_TYPE, new StringConfiguration(CACHE_DEFINITION.replace("{{message-name}}", POEM_MESSAGE_TYPE)));

      poemCache = remoteCacheManager.getCache(POEM_MESSAGE_TYPE);
   }

   public void shutdown() {
      remoteCacheManager.stop();
   }

   public RemoteCache<Integer, Poem> getPoemCache() {
      return poemCache;
   }
}
