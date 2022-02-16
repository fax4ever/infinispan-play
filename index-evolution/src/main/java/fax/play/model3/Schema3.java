package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3.class, schemaFileName = "model-schema.proto")
public interface Schema3 extends GeneratedSchema {

   Schema3 INSTANCE = new Schema3Impl();

}
