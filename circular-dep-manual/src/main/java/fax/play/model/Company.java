package fax.play.model;

import java.util.List;

import org.infinispan.protostream.annotations.ProtoField;

public class Company {

   private String name;
   private List<Employee> employees;

   @ProtoField(value = 1)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @ProtoField(value = 2)
   public List<Employee> getEmployees() {
      return employees;
   }

   public void setEmployees(List<Employee> employees) {
      this.employees = employees;
   }
}
