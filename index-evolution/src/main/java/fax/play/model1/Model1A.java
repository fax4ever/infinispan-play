package fax.play.model1;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
@ProtoName("Model1")
public class Model1A implements Model {

   private String oldName;

   @ProtoFactory
   public Model1A(String oldName) {
      this.oldName = oldName;
   }

   @ProtoField(value = 1)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getOldName() {
      return oldName;
   }
}
