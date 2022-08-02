package fax.play;

import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.api.annotations.indexing.Embedded;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.option.Structure;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed
public class Poem {

   private String title;
   private Author author;

   @ProtoFactory
   public Poem(String title, Author author) {
      this.title = title;
      this.author = author;
   }

   @Basic
   @ProtoField(value = 1)
   public String getTitle() {
      return title;
   }

   @Embedded(structure = Structure.NESTED)
   @ProtoField(value = 2)
   public Author getAuthor() {
      return author;
   }
}
