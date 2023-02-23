package fax.play.model;

import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed
public class Developer {

   private String nick;

   private Integer contributions;

   private Color favouriteColor;

   @ProtoFactory
   public Developer(String nick, Integer contributions, Color favouriteColor) {
      this.nick = nick;
      this.contributions = contributions;
      this.favouriteColor = favouriteColor;
   }

   @Basic
   @ProtoField(value = 1)
   public String getNick() {
      return nick;
   }

   @Basic // not sortable!
   @ProtoField(value = 2)
   public Integer getContributions() {
      return contributions;
   }

   @ProtoField(value = 3)
   public Color getFavouriteColor() {
      return favouriteColor;
   }

   @AutoProtoSchemaBuilder(includeClasses = { Developer.class, ColorAdapter.class },
         schemaFileName = "developer.proto", schemaFilePath = "proto")
   public interface DeveloperSchema extends GeneratedSchema {
      DeveloperSchema INSTANCE = new DeveloperSchemaImpl();
   }
}
