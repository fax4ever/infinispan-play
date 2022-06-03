package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3H.class, schemaFileName = "model3-schema.proto")
public interface Schema3H extends GeneratedSchema {

   Schema3H INSTANCE = new Schema3HImpl();

}
