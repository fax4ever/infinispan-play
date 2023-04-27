package fax.play.model;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class House {

   private String address;
   private String color;
   private Integer size;

   @ProtoFactory
   public House(String address, String color, Integer size) {
      this.address = address;
      this.color = color;
      this.size = size;
   }

   @ProtoField(value = 1)
   public String getAddress() {
      return address;
   }

   @ProtoField(value = 2)
   public String getColor() {
      return color;
   }

   @ProtoField(value = 3)
   public Integer getSize() {
      return size;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof House house)) return false;
      return Objects.equals(address, house.address) && Objects.equals(color, house.color) && Objects.equals(size, house.size);
   }

   @Override
   public int hashCode() {
      return Objects.hash(address, color, size);
   }
}
