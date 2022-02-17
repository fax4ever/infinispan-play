package fax.play.model2;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

import fax.play.service.Model;

@ProtoDoc("@Indexed")
@ProtoName("Model2")
public class Model2A implements Model {

   private String newName;

   @ProtoFactory
   public Model2A(String newName) {
      this.newName = newName;
   }

   @ProtoField(value = 1, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getNewName() {
      return newName;
   }
}
