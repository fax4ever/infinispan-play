package fax.play.model;

import java.time.Instant;

import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.api.annotations.indexing.Indexed;

@Indexed
public class IndexedContainer {

   private final Object object;
   private final int counter;
   private final String description;

   // with searchable false we're not going to create inverted indexes for search
   // with sortable true we're going to create indexes to support specifically efficient sorting
   @Basic(searchable = false, sortable = true)
   private Instant moment;

   public IndexedContainer(Object object, int counter, String description) {
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
