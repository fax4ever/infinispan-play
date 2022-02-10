package fax.play.model3;

import org.infinispan.protostream.annotations.ProtoField;

/**
 * Inspired by {@code org.keycloak.playground.nodowntimeupgrade.infinispan.v4.InfinispanObjectEntity}
 * of https://github.com/keycloak/keycloak-playground
 */
public class Model3 {

   @ProtoField(number = 1, required = true)
   public int entityVersion = 3;

   @ProtoField(number = 2, required = true)
   public String id;

   @ProtoField(number = 3)
   public String name;

   @Deprecated
   @ProtoField(number = 4)
   public String clientTemplateId;

   @Deprecated
   @ProtoField(number = 5)
   public String node2 = "";

   @ProtoField(number = 6)
   public String clientScopeId;

   @Deprecated // Remove in next version
   @ProtoField(number = 7, defaultValue = "30")
   public int timeout = 30;

   @ProtoField(number = 8)
   public Integer timeout1;

   @ProtoField(number = 9)
   public Integer timeout2;

}
