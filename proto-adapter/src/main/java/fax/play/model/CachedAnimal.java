package fax.play.model;

public class CachedAnimal {

   private final Animal animal;
   private final String duration;

   public CachedAnimal(Animal animal, String duration) {
      this.animal = animal;
      this.duration = duration;
   }

   public Animal getAnimal() {
      return animal;
   }

   public String getDuration() {
      return duration;
   }
}
