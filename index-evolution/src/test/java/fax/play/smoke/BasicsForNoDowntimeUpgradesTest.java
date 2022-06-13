package fax.play.smoke;

import fax.play.model3.Model3D;
import fax.play.model3.Model3G;
import fax.play.model3.Model3I;
import fax.play.model3.Schema3A;
import fax.play.model3.Schema3B;
import fax.play.model3.Schema3C;
import fax.play.model3.Schema3D;
import fax.play.model3.Schema3E;
import fax.play.model3.Schema3F;
import fax.play.model3.Schema3G;
import fax.play.model3.Schema3H;
import fax.play.model3.Schema3I;
import fax.play.service.CacheDefinition;
import fax.play.service.CacheProvider;
import fax.play.service.Model;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fax.play.service.CacheProvider.CACHE1_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class contains use-cases that are necessary in the first phase of Keycloak no-downtime store to work
 *
 * All of these use-cases are build with an assumption that any field that was or will be used in a query is indexed
 * this means
 *   - If we remove index, we stop using the field in queries or remove field
 *   - If we add index we didn't use the field in any query, but now we want to
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicsForNoDowntimeUpgradesTest {

    private final CacheProvider cacheProvider = new CacheProvider();

    /**
     * This is used, for example, when a new feature is added
     * the field has no meaning in the older version, so there is no migration needed
     * once the new feature is used, the field is set including the index
     */
    @Test
    void testAddNewFieldWithIndex() {
         // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3A.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create first version entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelA(1));

        // Check there is only one with 3 in name field
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        // VERSION 2
        cacheProvider.updateIndexSchema(cache, Schema3E.INSTANCE);

        // Create second version entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelE(2));

        // check there are all entities, new and old in the cache
        assertThat(cache.size()).isEqualTo(10);

        // Only new entities contain the new field, no old entity should be returned
        doQuery("FROM Model3 WHERE newField LIKE '%3%'", cache, 1);

        // Test lowercase normalizer
        doQuery("FROM Model3 WHERE newField LIKE 'Cool%fiEld%3%' ORDER BY newField", cache, 1);
    }

    /**
     * This example shows addition of an index on existing field that was NOT used in any query in the previous version
     *
     * This is used when we add some functionality that needs to search entities by field that existed before, but
     * was not queried
     *
     * Currently, all fields, that are included in some query, are indexed
     *
     */
    @Test
    void testAddAIndexOnExistingFieldThatWasNotUsedInAnyQueryBefore() {
        // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3A.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 1 entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelA(1));

        // This is just for testing, the field is not used in any used query in VERSION 1
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        // VERSION 2 - note in this version we are NOT able to use the functionality that for the reason for adding index
        // Update to second schema that adds nameIndexed field
        CacheProvider cacheProviderV2 = new CacheProvider();
        RemoteCache<String, Model> cacheV2 = cacheProviderV2.recreateRemoteCacheManager(Schema3D.INSTANCE)
                .getCache(CACHE1_NAME);

        // Index schema needs to be updated (this is done with no-downtime)
        cacheProvider.getCacheManager().administration().updateIndexSchema(CACHE1_NAME);

        // Create VERSION 2 entities
        // Entities created in this version needs to have both name and nameIndexed fields so that VERSION 1 is able to read
        // these entities
        ModelUtils.createModel1Entities(cacheV2, 5, ModelUtils.createModelD(2));

        // This is just to check data were created correctly, VERSION 2 doesn't use name nor nameIndexed in any query
        // non-indexed field name should be set on both version new and old
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cacheV2, 2);
        // indexed field nameIndexed should be present only in entities created by VERSION 2 as no reindexing was done yet
        doQuery("FROM Model3 WHERE nameIndexed : '*3*'", cacheV2, 1);

        // In this state we can ask administrator to perform update of all entities to VERSION 2 before migrating to VERSION 3
        // The advantage of this approach is:
        //     1. Keycloak is fully functional in the meanwhile
        //     2. Migration can be postponed to any time the administrator wishes (e.g. midnight)
        migrateEntityAToEntityD(cacheProviderV2.getCacheManager().getCache(CACHE1_NAME));

        // VERSION 3 - note from this version we are able to use the functionality that was the reason for adding the index
        // Update schema to version without name field
        cache = cacheProvider.recreateRemoteCacheManager(Schema3F.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 3 entities, entity V3 can't contain name field because ModelF doesn't contain it
        // in other words, migrating all entities to V3 removes name field from all entities
        // The migration is not necessary though the presence of name field should not interfere normal Keycloak behaviour
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelF(3));

        // It is possible there is an older VERSION 2 node writing/reading to/from the cache, it should work
        ModelUtils.createModel1Entities(cacheV2, 5, i -> ModelUtils.createModelD(2).apply(i + 10)); // Creating entities with ids +10 offset

        // This is to check that older version is correctly writing also deprecated name field even though current
        // schema present in the Infinispan server doesn't contain it
        RemoteCache<String, Model3D> cacheV2Typed = cacheProviderV2.getCacheManager().getCache(CACHE1_NAME);
        Model3D model3D = cacheV2Typed.get("300013");
        assertThat(model3D.name).isEqualTo("modelD # 13");

        // Query on nameIndexed should work with all entities >= 2 (e.g. all in the storage because we did the migration `migrateEntityAToEntityD`)
        doQuery("FROM Model3 WHERE nameIndexed : '*3*'", cache, 4);
    }

    /**
     * This example shows how an index can be removed
     *
     * In this example we assume the reason for removing the index is that it is no longer used in any query anymore
     */
    @Test
    void testRemoveIndexWhenNoLongerUsedInQueryInNewerVersion() {
        // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3B.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 1 entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelB(1));

        // VERSION 1 uses name in a query
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        // VERSION 2
        // In this version we cannot remove the index as node of VERSION 1 can still make a query with name in it
        // The important part of this version is to remove all occurrences of the name field in queries
        // Note: this version is needed only for analyzed fields, non-analyzed fields could work even without this,
        // although currently we need it as well unless https://issues.redhat.com/browse/ISPN-8584 is resolved

        // VERSION 3
        // In this version we can be sure there is no query with name in it, therefore we can remove the index

        // Update schema to not include index on name
        // Note: If the reason for removal is removal of field completely, the process would be the same with the difference,
        //  that this schema does not contain the field
        // update index schema
        cacheProvider.updateIndexSchema(cache, Schema3A.INSTANCE);

        // Create entities without index
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelA(2));

        // Try query with field that has the index in both versions
        doQuery("FROM Model3 WHERE entityVersion >= 1", cache, 10);
        // TODO ISPN-13948 Can we somehow cleanup indexed data only for name field?
    }

    @Test
    void testMigrateAnalyzedFieldToNonAnalyzed() {
        // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3C.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 1 entities with analyzed index
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelC(1));
        doQuery("FROM Model3 WHERE name : '*3*'", cache, 1);

        // Update schema to VERSION 2 that contains both analyzed and non-analyzed field
        // update index schema
        cacheProvider.updateIndexSchema(cache, Schema3G.INSTANCE);

        // Create VERSION 2 entities with both indexes
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelG(2));

        // We need to do queries that are backward compatible
        doQuery("FROM Model3 WHERE (entityVersion < 2 AND name : '*3*') OR (entityVersion >= 2 AND nameNonAnalyzed LIKE '%3%')", cache, 2);

        // In this version we request administrator to migrate to VERSION 2 entities
        migrateEntityDToEntityG(cacheProvider.getCacheManager().getCache(CACHE1_NAME));

        // VERSION 3
        // In this version we can stop using query that uses both old and new field because we know that all entities
        //  in the storage are upgraded to VERSION 2
        doQuery("FROM Model3 WHERE nameNonAnalyzed LIKE '%3%'", cache, 2);

        // VERSION 4
        // Now we can remove deprecated name field
        cache = cacheProvider
                .recreateRemoteCacheManager(Schema3H.INSTANCE)
                .getCache(CACHE1_NAME);

        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelH(3));
        doQuery("FROM Model3 WHERE nameNonAnalyzed LIKE '%3%'", cache, 3);
    }

    @Test
    void testMigrateNonAnalyzedFieldToAnalyzed() {
        // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3B.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 1 entities with non-analyzed index
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelB(1));
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        // Update schema to VERSION 2 that contains both non-analyzed and analyzed field
        // update index schema
        cacheProvider.updateIndexSchema(cache, Schema3I.INSTANCE);

        // Create VERSION 2 entities with both indexes
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelI(2));

        // We need to do queries that are backward compatible
        doQuery("FROM Model3 WHERE (entityVersion < 2 AND name LIKE '%3%') OR (entityVersion >= 2 AND nameIndexed : '*3*')", cache, 2);

        // In this version we request administrator to migrate to VERSION 2 entities
        migrateEntityBToEntityI(cacheProvider.getCacheManager().getCache(CACHE1_NAME));

        // VERSION 3
        // In this version we can stop using query that uses both old and new field because we know that all entities
        //  in the storage are upgraded to VERSION 2
        doQuery("FROM Model3 WHERE nameIndexed : '*3*'", cache, 2);

        // VERSION 4
        // Now we can remove deprecated name field
        cache = cacheProvider
                .recreateRemoteCacheManager(Schema3F.INSTANCE)
                .getCache(CACHE1_NAME);

        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelF(3));
        doQuery("FROM Model3 WHERE nameIndexed : '*3*'", cache, 3);
    }

    private <T> void doQuery(String query, RemoteCache<String, T> messageCache, int expectedResults) {
        QueryFactory queryFactory = Search.getQueryFactory(messageCache);
        Query<T> infinispanObjectEntities = queryFactory.create(query);
        Set<T> result = StreamSupport.stream(infinispanObjectEntities.spliterator(), false)
                .collect(Collectors.toSet());

        assertThat(result).hasSize(expectedResults);
    }

    private void migrateEntityAToEntityD(final RemoteCache<String, Model3D> cache) {
        new HashSet<>(cache.keySet())
                .forEach(e -> {
                    Model3D model3D = cache.get(e);
                    if (model3D.entityVersion == 1) {
                        model3D.nameIndexed = model3D.name;
                        model3D.name = null;
                        model3D.entityVersion = 2;
                        cache.put(model3D.id, model3D);
                    }
                });
    }

    private void migrateEntityDToEntityG(final RemoteCache<String, Model3G> cache) {
        new HashSet<>(cache.keySet())
                .forEach(e -> {
                    Model3G model3G = cache.get(e);
                    if (model3G.entityVersion == 1) {
                        model3G.nameNonAnalyzed = model3G.name;
                        model3G.name = null;
                        model3G.entityVersion = 2;
                        cache.put(model3G.id, model3G);
                    }
                });
    }

    private void migrateEntityBToEntityI(final RemoteCache<String, Model3I> cache) {
        new HashSet<>(cache.keySet())
                .forEach(e -> {
                    Model3I model3G = cache.get(e);
                    if (model3G.entityVersion == 1) {
                        model3G.nameIndexed = model3G.name;
                        model3G.name = null;
                        model3G.entityVersion = 2;
                        cache.put(model3G.id, model3G);
                    }
                });
    }

    @AfterAll
    public void afterAll() {
        cacheProvider.stop();
    }

}
