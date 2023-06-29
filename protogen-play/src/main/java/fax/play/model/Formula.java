package fax.play.model;

import java.util.ArrayList;
import java.util.Collection;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Formula {

   private Collection<Bla> wrappedValues;

   @ProtoFactory
   public Formula(Collection<Bla> wrappedValues) {
      this.wrappedValues = wrappedValues;
   }

   @ProtoField(number = 1, collectionImplementation = ArrayList.class)
   Collection<Bla> getWrappedValues() {
      return new ArrayList<>(wrappedValues);
   }

}
