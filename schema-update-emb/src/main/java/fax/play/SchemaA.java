package fax.play;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = ModelA.class, schemaFileName = "model-schema.proto")
public interface SchemaA extends GeneratedSchema {

   SchemaA INSTANCE = new SchemaAImpl();

}
