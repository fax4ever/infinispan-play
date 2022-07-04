package fax.play.model;

import org.infinispan.api.annotations.indexing.Basic;
import org.infinispan.api.annotations.indexing.Indexed;
import org.infinispan.api.annotations.indexing.Keyword;
import org.infinispan.api.annotations.indexing.Text;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@Indexed
@ProtoName("shape")
public class Shape {

   private String name;
   private Integer dimensions;
   private Integer vertices;
   private String description;

   @ProtoFactory
   public Shape(String name, Integer dimensions, Integer vertices, String description) {
      this.name = name;
      this.dimensions = dimensions;
      this.vertices = vertices;
      this.description = description;
   }

   @Keyword(sortable = true, projectable = true, aggregable = true)
   @ProtoField(value = 1)
   public String getName() {
      return name;
   }

   @Basic
   @ProtoField(value = 2)
   public Integer getDimensions() {
      return dimensions;
   }

   @Basic
   @ProtoField(value = 3)
   public Integer getVertices() {
      return vertices;
   }

   @Text
   @ProtoField(value = 4)
   public String getDescription() {
      return description;
   }
}
