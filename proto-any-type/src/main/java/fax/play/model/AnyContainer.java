package fax.play.model;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.types.protobuf.AnySchema;

public class AnyContainer {

   private AnySchema.Any object;

   private Integer counter;

   private String description;

   @ProtoFactory
   public AnyContainer(AnySchema.Any object, Integer counter, String description) {
      this.object = object;
      this.counter = counter;
      this.description = description;
   }

   @ProtoField(value = 1)
   public AnySchema.Any getObject() {
      return object;
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
