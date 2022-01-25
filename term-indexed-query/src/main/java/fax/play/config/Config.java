/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package fax.play.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class Config {

   public static final String CACHE_NAME = "keyword";

   public static RemoteCacheManager start() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass");

      URI cacheConfigUri;
      try {
         cacheConfigUri = Config.class.getClassLoader().getResource("keyword.xml").toURI();
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }

      builder.remoteCache(CACHE_NAME).configurationURI(cacheConfigUri);
      builder.remoteCache(CACHE_NAME).marshaller(ProtoStreamMarshaller.class);

      KeywordSchemaImpl schema = new KeywordSchemaImpl();

      // Add marshaller in the client, the class is generated from the interface in compile time
      builder.addContextInitializer(schema);
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());

      // Register proto schema on server side
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

      return remoteCacheManager;
   }

}
