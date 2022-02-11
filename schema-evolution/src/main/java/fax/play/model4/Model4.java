package fax.play.model4;

import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

@ProtoName("model")
public class Model4 implements Model {

   @ProtoField(number = 1, required = true)
   public int entityVersion = 4;

   @ProtoField(number = 2, required = true)
   public String id;

   @ProtoField(number = 3)
   public String name;

   @ProtoField(number = 6)
   public String clientScopeId;

   @ProtoField(number = 8)
   public Integer timeout1;

   @ProtoField(number = 9)
   public Integer timeout2;

}
