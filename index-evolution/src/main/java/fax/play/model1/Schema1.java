package fax.play.model1;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model1.class, schemaFileName = "model-schema.proto")
public interface Schema1 extends GeneratedSchema {

   Schema1 INSTANCE = new Schema1Impl();

}
