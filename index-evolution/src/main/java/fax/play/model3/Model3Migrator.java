package fax.play.model3;

import fax.play.model2.Model2;

public final class Model3Migrator {

   private Model3Migrator() {
   }

   @SuppressWarnings("deprecation")
   public static Model3 migrate(Model2 model2) {
      String name = model2.getNewName();
      if (name == null) {
         name = model2.getOldName();
      }
      return new Model3(name);
   }
}
