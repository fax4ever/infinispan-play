package fax.play;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Container {

   private Content content;

   @ProtoFactory
   public Container(Content content) {
      this.content = content;
   }

   @ProtoField(value = 1)
   public Content getContent() {
      return content;
   }

   @AutoProtoSchemaBuilder(includeClasses = { Container.class, Content.class }, schemaFileName = "model-schema.proto")
   public interface ModelSchema extends GeneratedSchema {
      ModelSchema INSTANCE = new ModelSchemaImpl();
   }
}
