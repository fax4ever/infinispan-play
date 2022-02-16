package fax.play.model2;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
public class Model2 implements Model {

   public String oldName;
   public String newName;

   @ProtoFactory
   public Model2(String oldName, String newName) {
      this.oldName = oldName;
      this.newName = newName;
   }

   @ProtoField(value = 1, required = true)
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
