package fax.play.model2;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model2.class, schemaFileName = "model-schema.proto")
public interface Schema2 extends GeneratedSchema {

   Schema2 INSTANCE = new Schema2Impl();

}
