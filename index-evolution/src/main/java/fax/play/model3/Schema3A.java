package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3A.class, schemaFileName = "model3-schema.proto")
public interface Schema3A extends GeneratedSchema {

   Schema3A INSTANCE = new Schema3AImpl();

}
