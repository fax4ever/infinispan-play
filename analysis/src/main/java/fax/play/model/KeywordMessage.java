package fax.play.model;

import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.Keyword;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed
public class KeywordMessage {

   private String message;

   @ProtoFactory
   public KeywordMessage(String message) {
      this.message = message;
   }

   @ProtoField(value = 1)
   @Keyword
   public String getMessage() {
      return message;
   }
}
