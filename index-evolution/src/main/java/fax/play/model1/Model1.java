package fax.play.model1;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
public class Model1 implements Model {

   private String oldName;

   @ProtoFactory
   public Model1(String oldName) {
      this.oldName = oldName;
   }

   @ProtoField(value = 1, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"keyword\"))")
   public String getOldName() {
      return oldName;
   }
}
