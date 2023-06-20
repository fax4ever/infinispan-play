package fax.play.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.protobuf.AnySchema;

@AutoProtoSchemaBuilder(includeClasses = { Animal.class, CachedAnimalAdapter.class },
      dependsOn = AnySchema.class,
      schemaPackageName = "fax.play.adapter",
      schemaFileName = "adapter-fax.proto", schemaFilePath = "proto")
public interface AnimalSchema extends GeneratedSchema {
}
