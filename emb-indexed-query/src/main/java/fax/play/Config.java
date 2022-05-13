package fax.play;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

public class Config {

   public static final String CACHE_NAME = "my-cache";

   public static EmbeddedCacheManager start() {
      GlobalConfigurationBuilder global = new GlobalConfigurationBuilder()
            .nonClusteredDefault();
      global.serialization()
            .addContextInitializer(GameSchema.INSTANCE);

      EmbeddedCacheManager cacheManager = new DefaultCacheManager(global.build());

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder
            .encoding()
               .mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE)
            .indexing()
               .enable()
               .storage(IndexStorage.LOCAL_HEAP)
               .addIndexedEntity(Game.class)
            .locking()
               .lockAcquisitionTimeout(120000)
               .concurrencyLevel(500)
               .isolationLevel(IsolationLevel.REPEATABLE_READ)
            .transaction()
               .lockingMode(LockingMode.PESSIMISTIC)
               .autoCommit(false)
               .transactionMode(TransactionMode.TRANSACTIONAL)
               .transactionManagerLookup(new GenericTransactionManagerLookup());

      cacheManager.createCache(CACHE_NAME, builder.build());
      return cacheManager;
   }
}
