package fax.play.model3;

import fax.play.model1.Model1B;

public final class Model2Migrator {

   private Model2Migrator() {
   }

   @SuppressWarnings("deprecation")
   public static Model2A migrate(Model1B model1B) {
      String name = model1B.getNewName();
      if (name == null) {
         name = model1B.getOldName();
      }
      return new Model2A(name);
   }
}
