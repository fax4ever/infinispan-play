package fax.play;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Collections;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.config.Config;
import fax.play.model.Company;
import fax.play.model.Employee;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CircularDependencyTest {

   private Config config;
   private RemoteCache<Object, Object> cache;

   @BeforeAll
   public void beforeAll() throws Exception {
      config = new Config();
      cache = config.recreateCache();
   }

   @AfterAll
   public void afterAll() {
      if (config != null) {
         config.close();
      }
   }

   @Test
   public void testPut() {
      Company company = new Company();
      company.setName("Red Hat");

      Employee employee = new Employee("fax4ever", company);
      company.setEmployees(Collections.singletonList(employee));

      assertThatThrownBy(() -> cache.put("fax4ever", employee))
            // https://issues.redhat.com/browse/IPROTO-262
            // https://issues.redhat.com/browse/ISPN-14534
            // https://issues.redhat.com/browse/JDG-6001
            .isInstanceOf(StackOverflowError.class);
   }
}
