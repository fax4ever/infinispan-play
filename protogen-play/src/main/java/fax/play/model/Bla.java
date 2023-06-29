package fax.play.model;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@ProtoDoc("@Indexed")
@ProtoName("bla")
public class Bla {

   private Integer a;
   private String b;
   private Long c;

   @ProtoFactory
   public Bla(Integer a, String b, Long c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.NO)")
   @ProtoField(value = 1)
   public Integer getA() {
      return a;
   }

   @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.NO)")
   @ProtoField(value = 2)
   public String getB() {
      return b;
   }

   @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.NO)")
   @ProtoField(value = 3)
   public Long getC() {
      return c;
   }
}
