package fax.play.entity;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@ProtoDoc("@Indexed")
@ProtoName("Developer")
public class Developer {

   private String nick;
   private String languages;

   @ProtoFactory
   public Developer(String nick, String languages) {
      this.nick = nick;
      this.languages = languages;
   }

   @ProtoField(value = 1)
   @ProtoDoc("@Field(store = Store.YES, analyze = Analyze.NO)")
   public String getNick() {
      return nick;
   }

   @ProtoField(value = 2)
   @ProtoDoc("@Field(store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"standard\"))")
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
