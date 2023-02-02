package fax.play.model;

import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.Text;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed
public class TextMessage {

   private String message;

   @ProtoFactory
   public TextMessage(String message) {
      this.message = message;
   }

   @ProtoField(value = 1)
   @Text
   public String getMessage() {
      return message;
   }
}
