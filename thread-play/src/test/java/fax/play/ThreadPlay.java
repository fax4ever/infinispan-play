package fax.play;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test
@Listeners(org.infinispan.commons.test.TestNGTestListener.class)
public class ThreadPlay {

   public void run() {
      try {
         while (true) {
            System.out.println("ciao");
            Thread.sleep(1000L);
         }
      } catch (Exception ex) {

      }
   }

   public void test() {
      ExecutorService executorService = Executors.newFixedThreadPool(1);
      executorService.submit(this::run);

      // create a thread leak!
   }
}
