package fax.play.model1;

import org.infinispan.protostream.annotations.ProtoField;

/**
 * Inspired by {@code org.keycloak.playground.nodowntimeupgrade.infinispan.v1.InfinispanObjectEntity}
 * of https://github.com/keycloak/keycloak-playground
 */
public class Model1 {

   @ProtoField(number = 1, required = true)
   public int entityVersion = 1;

   @ProtoField(number = 2, required = true)
   public String id;

   @ProtoField(number = 3)
   public String name;

   @ProtoField(number = 4)
   public String clientTemplateId;

   @ProtoField(number = 5)
   public String node2 = "";

}
