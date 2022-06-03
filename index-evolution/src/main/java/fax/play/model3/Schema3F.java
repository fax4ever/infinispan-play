package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3F.class, schemaFileName = "model3-schema.proto")
public interface Schema3F extends GeneratedSchema {

   Schema3F INSTANCE = new Schema3FImpl();

}
