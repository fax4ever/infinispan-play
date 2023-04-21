package fax.play.model;

public class Employee {

   private String name;
   private Company company;

   public Employee(String name, Company company) {
      this.name = name;
      this.company = company;
   }

   public String getName() {
      return name;
   }

   public Company getCompany() {
      return company;
   }
}
