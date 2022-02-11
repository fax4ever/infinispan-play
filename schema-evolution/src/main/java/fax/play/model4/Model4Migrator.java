package fax.play.model4;

import java.util.Collection;
import java.util.function.Consumer;

import fax.play.model1.Model1;
import fax.play.model2.Model2;
import fax.play.model3.Model3;
import fax.play.service.Model;

public final class Model4Migrator {

   private Model4Migrator() {
   }

   public static void migrate(Collection<Model> models, Consumer<Model4> operation) {
      models.stream().map(Model4Migrator::migrate).forEach(operation);
   }

   private static Model4 migrate(Model model) {
      if (model instanceof Model1) {
         return migrate((Model1) model);
      }
      if (model instanceof Model2) {
         return migrate((Model2) model);
      }
      if (model instanceof Model3) {
         return migrate((Model3) model);
      }

      throw new RuntimeException("Unsupported model type: " + model);
   }

   private static Model4 migrate(Model1 model1) {
      Model4 model4 = new Model4();
      model4.id = model1.id;
      model4.name = model1.name;
      model4.clientScopeId = model1.clientTemplateId.replace("t", "s");
      model4.timeout1 = 10;
      model4.timeout2 = 15;
      return model4;
   }

   private static Model4 migrate(Model2 model2) {
      Model4 model4 = new Model4();
      model4.id = model2.id;
      model4.name = model2.name;
      model4.clientScopeId = model2.clientScopeId;
      model4.timeout1 = model2.timeout;
      model4.timeout2 = model2.timeout;
      return model4;
   }

   private static Model4 migrate(Model3 model3) {
      Model4 model4 = new Model4();
      model4.id = model3.id;
      model4.name = model3.name;
      model4.clientScopeId = model3.clientScopeId;
      model4.timeout1 = model3.timeout1;
      model4.timeout2 = model3.timeout2;
      return model4;
   }

}
