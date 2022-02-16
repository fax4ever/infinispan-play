package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model2A.class, schemaFileName = "model2-schema.proto")
public interface Schema2A extends GeneratedSchema {

   Schema2A INSTANCE = new Schema2AImpl();

}
