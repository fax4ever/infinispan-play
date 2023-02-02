package fax.play.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = { KeywordMessage.class, TextMessage.class }, schemaFileName = "message.proto")
public interface MessageSchema extends GeneratedSchema {

   MessageSchema INSTANCE = new MessageSchemaImpl();

}
