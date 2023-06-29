package fax.play.model;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.types.protobuf.AnySchema;

public class ListOfAnyContainer {

   private List<AnySchema.Any> objects;

   private Integer counter;

   private String description;

   @ProtoFactory
   public ListOfAnyContainer(List<AnySchema.Any> objects, Integer counter, String description) {
      this.objects = objects;
      this.counter = counter;
      this.description = description;
   }

   @ProtoField(value = 1, collectionImplementation = ArrayList.class)
   public List<AnySchema.Any> getObjects() {
      return objects;
   }

   @ProtoField(value = 2)
   public Integer getCounter() {
      return counter;
   }

   @ProtoField(value = 3)
   public String getDescription() {
      return description;
   }
}
