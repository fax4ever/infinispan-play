package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

import fax.play.model.Bla;
import fax.play.model.ModelSchema;
import fax.play.model.Shape;

public class Config {

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"{{message-name}}\" statistics=\"true\">" +
               "    <encoding media-type=\"application/x-protostream\"/>" +
               "    <indexing enabled=\"true\" storage=\"local-heap\">" +
               "        <index-reader />" +
               "        <indexed-entities>" +
               "            <indexed-entity>{{message-name}}</indexed-entity>" +
               "        </indexed-entities>" +
               "    </indexing>" +
               "</local-cache>";

   private final RemoteCacheManager remoteCacheManager;
   private final RemoteCache<Object, Shape> shapeCache;
   private final RemoteCache<Object, Bla> blaCache;

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class)
            // Register proto schema && entity marshallers on client side
            .addContextInitializer(ModelSchema.INSTANCE);

      remoteCacheManager = new RemoteCacheManager(builder.build());

      // Register proto schema on server side
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(ModelSchema.INSTANCE.getProtoFileName(), ModelSchema.INSTANCE.getProtoFile());

      remoteCacheManager.administration().removeCache("shape");
      remoteCacheManager.administration().createCache("shape", new StringConfiguration(CACHE_DEFINITION.replace("{{message-name}}", "shape")));

      remoteCacheManager.administration().removeCache("bla");
      remoteCacheManager.administration().createCache("bla", new StringConfiguration(CACHE_DEFINITION.replace("{{message-name}}", "bla")));

     shapeCache = remoteCacheManager.getCache("shape");
     blaCache = remoteCacheManager.getCache("bla");
   }

   public void shutdown() {
      remoteCacheManager.stop();
   }

   public RemoteCache<Object, Shape> getShapeCache() {
      return shapeCache;
   }

   public RemoteCache<Object, Bla> getBlaCache() {
      return blaCache;
   }
}
