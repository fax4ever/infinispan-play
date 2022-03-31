package fax.play.entity;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed(index = "developer")
public class Developer {

   @GenericField
   private String nick;

   @FullTextField
   @KeywordField(name = "alternative")
   private String languages;

   @ProtoFactory
   public Developer(String nick, String languages) {
      this.nick = nick;
      this.languages = languages;
   }

   @ProtoField(value = 1)
   public String getNick() {
      return nick;
   }

   @ProtoField(value = 2)
   public String getLanguages() {
      return languages;
   }

   @Override
   public String toString() {
      return (nick != null) ? nick : "<<no-nick-developer>>";
   }

   @AutoProtoSchemaBuilder(includeClasses = Developer.class, schemaFileName = "developer.proto", schemaPackageName = "fax.play.entity")
   public interface Schema extends GeneratedSchema {
   }
}
