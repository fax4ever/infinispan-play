package fax.play;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@Indexed
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
   @KeywordField
   public String getOriginal() {
      return original;
   }

   @ProtoField(value = 2)
   @KeywordField
   public String getDifferent() {
      return different;
   }
}
