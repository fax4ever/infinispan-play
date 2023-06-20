package fax.play.model;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(CachedAnimal.class)
public class CachedAnimalAdapter {

   @ProtoFactory
   static CachedAnimal create(Animal animal, String cacheDuration) {
      return new CachedAnimal(animal, cacheDuration);
   }

   @ProtoField(number = 1)
   Animal getAnimal(CachedAnimal cachedAnimal) {
      return cachedAnimal.getAnimal();
   }

   @ProtoField(number = 2)
   String getCacheDuration(CachedAnimal cachedAnimal) {
      return cachedAnimal.getDuration();
   }
}
