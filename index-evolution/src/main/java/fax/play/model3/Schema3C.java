package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3C.class, schemaFileName = "model3-schema.proto")
public interface Schema3C extends GeneratedSchema {

   Schema3C INSTANCE = new Schema3CImpl();

}
