package fax.play.model3;

import fax.play.model1.Model1;
import fax.play.model2.Model2;
import fax.play.service.Model;

public final class Model3Migrator {

   private Model3Migrator() {
   }

   public static Model3 migrate(Model model) {
      if (model instanceof Model1) {
         return migrate((Model1) model);
      }
      if (model instanceof Model2) {
         return migrate((Model2) model);
      }
      if (model instanceof Model3) {
         return (Model3) model;
      }

      throw new RuntimeException("Unsupported model type: " + model);
   }

   private static Model3 migrate(Model1 model1) {
      return new Model3(model1.getOldName());
   }

   private static Model3 migrate(Model2 model2) {
      return new Model3(model2.getNewName());
   }
}
