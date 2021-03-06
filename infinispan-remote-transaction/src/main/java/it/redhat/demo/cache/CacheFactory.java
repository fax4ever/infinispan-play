package it.redhat.demo.cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;

public class CacheFactory {

	public static final String TRANSACTIONAL_SERVER_SIDE_DEFINED_CACHE = "special-cache";
	public static final String NON_TRANSACTIONAL_SERVER_SIDE_DEFINED_CACHE = "default";
	public static final String TRANSACTIONAL_SERVER_SIDE_DEFINED_WITH_CONFIGURATION_CACHE = "use-trx-config-cache";

	private static final String[] SERVER_SIDE_DEFINED_CACHES = {
			TRANSACTIONAL_SERVER_SIDE_DEFINED_CACHE, NON_TRANSACTIONAL_SERVER_SIDE_DEFINED_CACHE, TRANSACTIONAL_SERVER_SIDE_DEFINED_WITH_CONFIGURATION_CACHE
	};
	private static final String TRANSACTIONAL_CLIENT_SIDE_DEFINED_CONFIG =
			"<infinispan><cache-container>" +
					"	<distributed-cache-configuration name=\"%s\">" +
					"     <locking isolation=\"REPEATABLE_READ\"/>" +
					"     <transaction locking=\"PESSIMISTIC\" mode=\"%s\" />" +
					"   </distributed-cache-configuration>" +
					"</cache-container></infinispan>";

	private final RemoteCacheManager manager;
	private final ConcurrentHashMap<String, Boolean> createdCaches = new ConcurrentHashMap<String, Boolean>();

	public CacheFactory(RemoteCacheManager manager) {
		this.manager = manager;
	}

	public RemoteCache<String, String> getCache(String name) {
		// skip creation if cache is already defined on Infinispan server config
		if ( Arrays.asList( SERVER_SIDE_DEFINED_CACHES ).contains( name ) ) {
			return manager.getCache( name );
		}

		createdCaches.computeIfAbsent( name, cacheName -> {
			manager.administration().createCache( cacheName, getConfigurationByName( cacheName ) );
			return true;
		} );

		return manager.getCache( name );
	}

	public void close() throws IOException {
		disposeAllCaches();
		manager.close();
	}

	private synchronized void disposeAllCaches() {
		createdCaches.keySet().forEach( name -> {
			manager.administration().removeCache( name );
		} );
	}

	private XMLStringConfiguration getConfigurationByName(String cacheName) {
		return new XMLStringConfiguration( String.format( TRANSACTIONAL_CLIENT_SIDE_DEFINED_CONFIG, cacheName, CacheManagerFactory.TRANSACTION_MODE ) );
	}
}
