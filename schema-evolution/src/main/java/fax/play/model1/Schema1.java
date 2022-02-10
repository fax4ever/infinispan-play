package fax.play.model1;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Model1.class)
public interface Schema1 extends GeneratedSchema {

   Schema1 INSTANCE = new Schema1Impl();

}
