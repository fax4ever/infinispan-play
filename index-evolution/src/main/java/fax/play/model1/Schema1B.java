package fax.play.model1;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model1B.class, schemaFileName = "model1-schema.proto")
public interface Schema1B extends GeneratedSchema {

   Schema1B INSTANCE = new Schema1BImpl();

}
