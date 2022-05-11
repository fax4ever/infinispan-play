package fax.play;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@ProtoDoc("@Indexed")
@ProtoName("Model")
public class ModelA implements Model {

   private String original;

   @ProtoFactory
   public ModelA(String original) {
      this.original = original;
   }

   @ProtoField(value = 1)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getOriginal() {
      return original;
   }
}
