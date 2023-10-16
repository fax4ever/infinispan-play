package fax.play;

import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.api.annotations.indexing.Indexed;

@Indexed
public class Sale {

   private String id;

   @Basic(projectable = true, aggregable = true)
   private String code;

   @Basic(sortable = true, projectable = true, aggregable = true)
   private String status;

   @Basic
   private Long moment;

   public Sale(String id, String code, String status, Long moment) {
      this.id = id;
      this.code = code;
      this.status = status;
      this.moment = moment;
   }

   public String getId() {
      return id;
   }

   public String getCode() {
      return code;
   }

   public String getStatus() {
      return status;
   }

   public Long getMoment() {
      return moment;
   }

   @Override
   public String toString() {
      return "Sale{" +
            "id='" + id + '\'' +
            ", code='" + code + '\'' +
            ", status='" + status + '\'' +
            ", moment=" + moment +
            '}';
   }
}
