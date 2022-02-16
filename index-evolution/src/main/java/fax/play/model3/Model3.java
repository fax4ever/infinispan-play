package fax.play.model3;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
public class Model3 implements Model {

   private String newName;

   @ProtoFactory
   public Model3(String newName) {
      this.newName = newName;
   }

   @ProtoField(value = 2, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"keyword\"))")
   public String getNewName() {
      return newName;
   }
}
