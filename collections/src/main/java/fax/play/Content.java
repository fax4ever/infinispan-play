package fax.play;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Content {

   private String value;

   @ProtoFactory
   public Content(String value) {
      this.value = value;
   }

   @ProtoField(value = 1)
   public String getValue() {
      return value;
   }
}
