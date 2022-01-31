package fax.play;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

@Indexed(index = "play")
public class Game {

   @KeywordField
   private String name;

   @FullTextField
   private String description;

   public Game(String name, String description) {
      this.name = name;
      this.description = description;
   }
}
