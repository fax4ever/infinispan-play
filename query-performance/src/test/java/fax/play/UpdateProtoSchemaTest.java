package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.client.rest.RestClient;
import org.infinispan.client.rest.RestResponse;
import org.infinispan.client.rest.RestSchemaClient;
import org.infinispan.client.rest.configuration.RestClientConfigurationBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fax.play.model.ModelSchema;

public class UpdateProtoSchemaTest {

   private RestClient restClient;

   @BeforeEach
   public void before() {
      restClient = RestClient.forConfiguration(new RestClientConfigurationBuilder().addServer()
            .host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
               .authentication()
                  .username("user")
                  .password("pass")
            .build());

      assertThat(restClient.getConfiguration().toURI()).isEqualTo("http://user:pass@127.0.0.1:11222");
   }

   @Test
   public void test() {
      RestSchemaClient schemas = restClient.schemas();

      RestResponse response = schemas.put(ModelSchema.INSTANCE.getProtoFileName(), ModelSchema.INSTANCE.getProtoFile())
            .toCompletableFuture().join();
      assertThat(response.getStatus()).isEqualTo(200);

      response = schemas.names().toCompletableFuture().join();
      assertThat(response.getStatus()).isEqualTo(200);

      String body = response.getBody();
      assertThat(body).contains(ModelSchema.INSTANCE.getProtoFileName());
   }

   @AfterEach
   public void after() throws Exception {
      if (restClient != null) {
         restClient.close();
      }
   }
}
