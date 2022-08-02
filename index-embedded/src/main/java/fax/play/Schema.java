package fax.play;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = {Poem.class, Author.class, Model3J.class, NestedModel.class})
public interface Schema extends GeneratedSchema {

   Schema INSTANCE = new SchemaImpl();

}
