package fax.play;

import java.util.List;
import java.util.Set;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Container {

   private Content content;
   private List<Content> contentList;
   private Set<Content> contentSet;
   private Content[] contentArray;

   @ProtoFactory
   public Container(Content content, List<Content> contentList, Set<Content> contentSet, Content[] contentArray) {
      this.content = content;
      this.contentList = contentList;
      this.contentSet = contentSet;
      this.contentArray = contentArray;
   }

   @ProtoField(value = 1)
   public Content getContent() {
      return content;
   }

   @ProtoField(value = 2)
   public List<Content> getContentList() {
      return contentList;
   }

   @ProtoField(value = 3)
   public Set<Content> getContentSet() {
      return contentSet;
   }

   @ProtoField(value = 4)
   public Content[] getContentArray() {
      return contentArray;
   }

   @AutoProtoSchemaBuilder(includeClasses = { Container.class, Content.class }, schemaFileName = "model-schema.proto")
   public interface ModelSchema extends GeneratedSchema {
      ModelSchema INSTANCE = new ModelSchemaImpl();
   }
}
