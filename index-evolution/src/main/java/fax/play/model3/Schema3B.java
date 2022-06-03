package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3B.class, schemaFileName = "model3-schema.proto")
public interface Schema3B extends GeneratedSchema {

   Schema3B INSTANCE = new Schema3BImpl();

}
