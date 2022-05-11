package fax.play;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@ProtoDoc("@Indexed")
@ProtoName("Model")
public class ModelB implements Model {

   @Deprecated
   public String original;

   public String different;

   @ProtoFactory
   public ModelB(String original, String different) {
      this.original = original;
      this.different = different;
   }

   @Deprecated
   @ProtoField(value = 1)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getOriginal() {
      return original;
   }

   @ProtoField(value = 2)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getDifferent() {
      return different;
   }
}
