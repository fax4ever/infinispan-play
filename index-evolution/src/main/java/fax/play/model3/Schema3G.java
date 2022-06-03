package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3G.class, schemaFileName = "model3-schema.proto")
public interface Schema3G extends GeneratedSchema {

   Schema3G INSTANCE = new Schema3GImpl();

}
