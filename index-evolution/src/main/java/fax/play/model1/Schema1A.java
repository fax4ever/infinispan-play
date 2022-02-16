package fax.play.model1;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model1A.class, schemaFileName = "model1-schema.proto")
public interface Schema1A extends GeneratedSchema {

   Schema1A INSTANCE = new Schema1AImpl();

}
