package fax.play.model2;

import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

/**
 * Inspired by {@code org.keycloak.playground.nodowntimeupgrade.infinispan.v3.InfinispanObjectEntity}
 * of https://github.com/keycloak/keycloak-playground
 */
@ProtoName("model")
public class Model2 implements Model {

   @ProtoField(number = 1, required = true)
   public int entityVersion = 2;

   @ProtoField(number = 2, required = true)
   public String id;

   @ProtoField(number = 3)
   public String name;

   @Deprecated
   @ProtoField(number = 4)
   public String clientTemplateId;

   @ProtoField(number = 5)
   public String node2 = "";

   @ProtoField(number = 6)
   public String clientScopeId;

   @ProtoField(number = 7, defaultValue = "30")
   public int timeout = 30;

}
