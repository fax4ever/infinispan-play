package fax.play.model3;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
@ProtoName("Model3")
public class Model3 implements Model {

   private String newName;

   @ProtoFactory
   public Model3(String newName) {
      this.newName = newName;
   }

   @ProtoField(value = 2)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getNewName() {
      return newName;
   }
}
