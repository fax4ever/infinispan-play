package fax.play;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Indexed(index = "play")
public class Game {

   @KeywordField(projectable = Projectable.YES)
   private String name;

   @FullTextField
   private String description;

   @ProtoFactory
   public Game(String name, String description) {
      this.name = name;
      this.description = description;
   }

   @ProtoField(1)
   public String getName() {
      return name;
   }

   @ProtoField(2)
   public String getDescription() {
      return description;
   }
}
