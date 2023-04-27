package fax.play.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.protobuf.AnySchema;

@AutoProtoSchemaBuilder(includeClasses = { Hotel.class, House.class, AnyContainer.class },
      dependsOn = AnySchema.class,
      schemaPackageName = "fax.play.any",
      schemaFileName = "any-container-fax.proto", schemaFilePath = "proto")
public interface AnyContainerSchema extends GeneratedSchema {
}
