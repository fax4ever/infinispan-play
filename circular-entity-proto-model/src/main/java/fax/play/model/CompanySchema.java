package fax.play.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = { Company.class, Employee.class }, schemaPackageName = "fax.play",
      schemaFileName = "company-fax.proto", schemaFilePath = "proto")
public interface CompanySchema extends GeneratedSchema {

   CompanySchema INSTANCE = new CompanySchemaImpl();

}
