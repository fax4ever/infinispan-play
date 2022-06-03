package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3I.class, schemaFileName = "model3-schema.proto")
public interface Schema3I extends GeneratedSchema {

   Schema3I INSTANCE = new Schema3IImpl();

}
