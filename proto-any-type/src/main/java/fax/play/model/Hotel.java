package fax.play.model;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Hotel {

   private String name;
   private String address;
   private Integer rooms;
   private Byte stars;

   @ProtoFactory
   public Hotel(String name, String address, Integer rooms, Byte stars) {
      this.name = name;
      this.address = address;
      this.rooms = rooms;
      this.stars = stars;
   }

   @ProtoField(value = 1)
   public String getName() {
      return name;
   }

   @ProtoField(value = 2)
   public String getAddress() {
      return address;
   }

   @ProtoField(value = 3)
   public Integer getRooms() {
      return rooms;
   }

   @ProtoField(value = 4)
   public Byte getStars() {
      return stars;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Hotel hotel)) return false;
      return Objects.equals(name, hotel.name) && Objects.equals(address, hotel.address) && Objects.equals(rooms, hotel.rooms) && Objects.equals(stars, hotel.stars);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, address, rooms, stars);
   }
}
