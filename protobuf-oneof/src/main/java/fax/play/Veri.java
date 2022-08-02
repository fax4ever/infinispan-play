package fax.play;

import java.util.Objects;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@ProtoName("veri")
public class Veri {

   private String name;
   private String code;
   private Integer virtual;

   @ProtoFactory
   public Veri(String name, String code, Integer virtual) {
      this.name = name;
      this.code = code;
      this.virtual = virtual;
   }

   @ProtoField(value = 1)
   public String getName() {
      return name;
   }

   @ProtoField(value = 2, oneof = "group1")
   public String getCode() {
      return code;
   }

   @ProtoField(value = 3, oneof = "group1")
   public Integer getVirtual() {
      return virtual;
   }

   @Override
   public String toString() {
      return "Veri{" +
            "name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", virtual=" + virtual +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Veri veri = (Veri) o;
      return Objects.equals(name, veri.name) && Objects.equals(code, veri.code) && Objects.equals(virtual, veri.virtual);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, code, virtual);
   }

   @AutoProtoSchemaBuilder(includeClasses = { Veri.class }, schemaFileName = "model-schema.proto")
   public interface ModelSchema extends GeneratedSchema {
      ModelSchema INSTANCE = new ModelSchemaImpl();
   }
}
