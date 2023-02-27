package fax.play.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;

public class CompanyMarshaller implements MessageMarshaller<Company> {

  @Override
  public String getTypeName() {
    return "fax.play.Company";
  }

  @Override
  public Class<Company> getJavaClass() {
    return Company.class;
  }

  @Override
  public Company readFrom(ProtoStreamReader reader) throws IOException {
    final String name = reader.readString("name");
    final List<Employee> employees = reader.readCollection("employees", new ArrayList<>(), Employee.class);

    Company company = new Company();
    company.setName(name);
    company.setEmployees(employees);
    return company;
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, Company company) throws IOException {
    writer.writeString("name", company.getName());
    writer.writeCollection("employees", company.getEmployees(), Employee.class);
  }
}
