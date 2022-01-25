package fax.play.config;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

import fax.play.entity.KeywordEntity;

@AutoProtoSchemaBuilder(includeClasses = KeywordEntity.class)
public interface KeywordSchema extends GeneratedSchema {
}
