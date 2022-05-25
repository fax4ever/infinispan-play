package fax.play;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = Game.class)
public interface GameSchema extends GeneratedSchema {
   GameSchema INSTANCE = new GameSchemaImpl();
}
