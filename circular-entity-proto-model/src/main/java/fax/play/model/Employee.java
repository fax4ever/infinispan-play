package fax.play.model;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Employee {

   private String name;
   private Company company;

   @ProtoFactory
   public Employee(String name, Company company) {
      this.name = name;
      this.company = company;
   }

   @ProtoField(value = 1)
   public String getName() {
      return name;
   }

   @ProtoField(value = 2)
   public Company getCompany() {
      return company;
   }
}
