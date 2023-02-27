package fax.play.model;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;

public class EmployeeMarshaller implements MessageMarshaller<Employee> {

  @Override
  public String getTypeName() {
    return "fax.play.Employee";
  }

  @Override
  public Class<Employee> getJavaClass() {
    return Employee.class;
  }

  @Override
  public Employee readFrom(ProtoStreamReader reader) throws IOException {
    String name = reader.readString("name");
    Company company = reader.readObject("company", Company.class);

    return new Employee(name, company);
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, Employee employee) throws IOException {
    writer.writeString("name", employee.getName());
    writer.writeObject("company", employee.getCompany(), Company.class);
  }
}
