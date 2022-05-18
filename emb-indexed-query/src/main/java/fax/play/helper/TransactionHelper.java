package fax.play.helper;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * Inspired by org.hibernate.search.util.impl.integrationtest.mapper.orm.OrmUtils
 * of https://github.com/hibernate/hibernate-search.
 */
public final class TransactionHelper {

   private TransactionHelper() {
   }

   public static void withinTransaction(TransactionManager transactionManager, Runnable action) throws Exception {
      Transaction tx = null;
      try {
         transactionManager.begin();
         action.run();
         transactionManager.commit();
      }
      catch (Throwable t) {
         if ( tx == null ) {
            throw t;
         }
         try {
            tx.rollback();
         }
         catch (AssertionError e) {
            // An assertion failed while rolling back...
            if ( t instanceof AssertionError ) {
               // The original exception was an assertion error, so it's more important.
               t.addSuppressed( e );
            }
            else {
               // The original exception was not an assertion error, so it's less important.
               // Propagate the assertion error, with the suppressed exception as added context.
               e.addSuppressed( t );
               throw e;
            }
         }
         catch (RuntimeException e) {
            t.addSuppressed( e );
         }
         throw t;
      }
   }

}
