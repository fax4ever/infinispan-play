package fax.play.model1;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
@ProtoName("Model1")
public class Model1B implements Model {

   @Deprecated
   public String oldName;

   public String newName;

   @ProtoFactory
   public Model1B(String oldName, String newName) {
      // fill legacies:
      this.oldName = oldName;

      this.newName = newName;
   }

   @Deprecated
   @ProtoField(value = 1, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getOldName() {
      return oldName;
   }

   @ProtoField(value = 2)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getNewName() {
      return newName;
   }
}
