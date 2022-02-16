package fax.play.model2;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
@ProtoName("Model")
public class Model2 implements Model {

   @Deprecated
   public String oldName;

   public String newName;

   @ProtoFactory
   public Model2(String oldName, String newName) {
      // fill legacies:
      this.oldName = oldName;

      this.newName = newName;
   }

   @Deprecated
   @ProtoField(value = 1)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"keyword\"))")
   public String getOldName() {
      return oldName;
   }

   @ProtoField(value = 2, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"keyword\"))")
   public String getNewName() {
      return newName;
   }
}
