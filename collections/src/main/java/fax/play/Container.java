package fax.play;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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

   // list specific implementations
   private ArrayList<Content> contentArrayList;
   private LinkedList<Content> contentLinkedList;

   // set specific implementations
   private HashSet<Content> contentHashSet;
   private LinkedHashSet<Content> contentLinkedHashSet;

   @ProtoFactory
   public Container(Content content, List<Content> contentList, Set<Content> contentSet, Content[] contentArray,
                    ArrayList<Content> contentArrayList, LinkedList<Content> contentLinkedList,
                    HashSet<Content> contentHashSet, LinkedHashSet<Content> contentLinkedHashSet) {
      this.content = content;
      this.contentList = contentList;
      this.contentSet = contentSet;
      this.contentArray = contentArray;
      this.contentArrayList = contentArrayList;
      this.contentLinkedList = contentLinkedList;
      this.contentHashSet = contentHashSet;
      this.contentLinkedHashSet = contentLinkedHashSet;
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

   @ProtoField(value = 5)
   public ArrayList<Content> getContentArrayList() {
      return contentArrayList;
   }

   @ProtoField(value = 6)
   public LinkedList<Content> getContentLinkedList() {
      return contentLinkedList;
   }

   @ProtoField(value = 7)
   public HashSet<Content> getContentHashSet() {
      return contentHashSet;
   }

   @ProtoField(value = 8)
   public LinkedHashSet<Content> getContentLinkedHashSet() {
      return contentLinkedHashSet;
   }

   @AutoProtoSchemaBuilder(includeClasses = { Container.class, Content.class }, schemaFileName = "model-schema.proto")
   public interface ModelSchema extends GeneratedSchema {
      ModelSchema INSTANCE = new ModelSchemaImpl();
   }
}
