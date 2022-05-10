package fax.play;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@Indexed
@ProtoName("Model")
public class ModelA implements Model {

   private String original;

   @ProtoFactory
   public ModelA(String original) {
      this.original = original;
   }

   @ProtoField(value = 1)
   @KeywordField
   public String getOriginal() {
      return original;
   }
}
