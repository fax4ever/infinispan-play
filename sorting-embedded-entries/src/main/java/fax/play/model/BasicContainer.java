package fax.play.model;

import java.time.Instant;

public class BasicContainer {

   private final Object object;
   private final int counter;
   private final String description;
   private final Instant moment;

   public BasicContainer(Object object, int counter, String description) {
      this.object = object;
      this.counter = counter;
      this.description = description;
      this.moment = Instant.now();
   }

   public Object getObject() {
      return object;
   }

   public int getCounter() {
      return counter;
   }

   public String getDescription() {
      return description;
   }

   public Instant getMoment() {
      return moment;
   }
}
