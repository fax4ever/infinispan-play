package fax.play.model4;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model4.class, schemaFileName = "model-schema.proto")
public interface Schema4 extends GeneratedSchema {

   Schema4 INSTANCE = new Schema4Impl();

}
