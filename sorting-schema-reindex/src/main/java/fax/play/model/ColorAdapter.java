package fax.play.model;


import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoEnumValue;

@ProtoAdapter(Color.class)
public enum ColorAdapter {

   @ProtoEnumValue(number = 0, name = "red")
   RED,

   @ProtoEnumValue(number = 1, name = "green")
   GREEN,

   @ProtoEnumValue(number = 2, name = "blue")
   BLUE

}
