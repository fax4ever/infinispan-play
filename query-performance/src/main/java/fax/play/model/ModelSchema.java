package fax.play.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = { Shape.class, Bla.class }, schemaFileName = "model-schema.proto")
public interface ModelSchema extends GeneratedSchema {

   ModelSchema INSTANCE = new ModelSchemaImpl();

}
