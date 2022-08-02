package fax.play;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = {Poem.class, Author.class}, schemaPackageName = "poem")
public interface Schema extends GeneratedSchema {

   Schema INSTANCE = new SchemaImpl();

}
