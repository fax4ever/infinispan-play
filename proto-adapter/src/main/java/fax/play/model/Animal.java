package fax.play.model;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public abstract class Animal {

   private final String name;

   @ProtoFactory
   public Animal(String name) {
      this.name = name;
   }

   @ProtoField(number = 1)
   public String getName() {
      return name;
   }
}
