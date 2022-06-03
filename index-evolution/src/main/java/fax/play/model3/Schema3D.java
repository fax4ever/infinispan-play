package fax.play.model3;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model3D.class, schemaFileName = "model3-schema.proto")
public interface Schema3D extends GeneratedSchema {

   Schema3D INSTANCE = new Schema3DImpl();

}
