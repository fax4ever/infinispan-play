package fax.play.entity;

import org.hibernate.search.engine.backend.types.Sortable;
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
   @KeywordField(name = "alternative", sortable = Sortable.YES)
   private String languages;

   @GenericField
   private Integer projects;

   @ProtoFactory
   public Developer(String nick, String languages, Integer projects) {
      this.nick = nick;
      this.languages = languages;
      this.projects = projects;
   }

   @ProtoField(value = 1)
   public String getNick() {
      return nick;
   }

   @ProtoField(value = 2)
   public String getLanguages() {
      return languages;
   }

   @ProtoField(value = 3)
   public Integer getProjects() {
      return projects;
   }

   @Override
   public String toString() {
      return (nick != null) ? nick : "<<no-nick-developer>>";
   }

   @AutoProtoSchemaBuilder(includeClasses = Developer.class, schemaFileName = "developer.proto", schemaPackageName = "fax.play.entity")
   public interface Schema extends GeneratedSchema {
   }
}
