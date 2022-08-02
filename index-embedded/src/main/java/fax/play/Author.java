package fax.play;

import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.Keyword;
import org.infinispan.api.annotations.indexing.Text;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed
public class Author {

   private String name;
   private String biography;

   @ProtoFactory
   public Author(String name, String biography) {
      this.name = name;
      this.biography = biography;
   }

   @Keyword
   @ProtoField(value = 1)
   public String getName() {
      return name;
   }

   @Text
   @ProtoField(value = 2)
   public String getBiography() {
      return biography;
   }
}
