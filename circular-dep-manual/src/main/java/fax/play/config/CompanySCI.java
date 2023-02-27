package fax.play.config;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;

import fax.play.model.CompanyMarshaller;
import fax.play.model.EmployeeMarshaller;

public class CompanySCI implements SerializationContextInitializer {

   @Override
   public String getProtoFileName() {
      return "company.proto";
   }

   @Override
   public String getProtoFile() throws UncheckedIOException {
      throw new IllegalStateException("This is not supposed to be used");
   }

   @Override
   public void registerSchema(SerializationContext serCtx) {
      try {
         serCtx.registerProtoFiles(FileDescriptorSource.fromResources(getProtoFileName()));
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   @Override
   public void registerMarshallers(SerializationContext serCtx) {
      serCtx.registerMarshaller(new CompanyMarshaller());
      serCtx.registerMarshaller(new EmployeeMarshaller());
   }
}
