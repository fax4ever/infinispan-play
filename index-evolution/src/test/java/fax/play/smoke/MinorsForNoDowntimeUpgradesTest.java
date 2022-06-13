package fax.play.smoke;

import fax.play.model3.Model3D;
import fax.play.model3.Schema3A;
import fax.play.model3.Schema3B;
import fax.play.model3.Schema3D;
import fax.play.model3.Schema3F;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MinorsForNoDowntimeUpgradesTest {
    private final CacheProvider cacheProvider = new CacheProvider();

    /**
     * This is the same usecase as {@link BasicsForNoDowntimeUpgradesTest#testAddAIndexOnExistingFieldThatWasNotUsedInAnyQueryBefore}
     *
     * The difference is, that it seems for non-analyzed fields it could be possible to do the migration in one step
     * on the other hand, I am not sure whether this works correctly as currently I don't know how to check whether
     * the query used the index or not
     */
    @Test
    void testAddNonAnalyzedIndexOnExistingField() {
        // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3A.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 1 entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelA(1));

        // Check there is only one with 3 in name field (Query that doesn't use index)
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        cache = cacheProvider.recreateRemoteCacheManager(Schema3B.INSTANCE)
                .getCache(CACHE1_NAME);

        // TODO ISPN-13946: No index schema update nor reindex needed, is it correct?
        //  Can I somehow check whether Infinispan used the index or not?

        // Create second version entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelB(2));

        // check there are two entities with 3 in name field
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 2);
    }

    /**
     * Effectively the same as {@link BasicsForNoDowntimeUpgradesTest#testAddAIndexOnExistingFieldThatWasNotUsedInAnyQueryBefore}
     * but in this case we want to add index on a field that was previously used in a query
     */
    @Test
    void testAnalyzedIndexAddedInMoreSteps() {
        // VERSION 1
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3A.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 1 entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelA(1));
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        // VERSION 2
        // Note: this version needs to be backward compatible with the old version
        // Update to second schema
        // Index schema needs to be updated
        cacheProvider.updateIndexSchema(cache, Schema3D.INSTANCE);

        // Create second version entities
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelD(2));

        // In this state the product works correctly, the older version is able to read because the newer stores both name and nameIndexed, the newer version
        // is able to read both the older and newer entities because it uses both name and nameIndexed in the query

        // At this moment we can request administrator to migrate all stored entities from version 1 to version 2
        // The advantage of this approach is:
        //     1. Keycloak is fully functional in the meanwhile
        //     2. Migration can be postponed to any time the administrator wishes (e.g. midnight)
        migrateEntityAToEntityD(cacheProvider.getCacheManager().getCache(CACHE1_NAME));

        // VERSION 3
        // Now we can stop doing backward compatible queries (we won't let administrator update to this version unless
        // all entities are VERSION 3) and use just this:
        doQuery("FROM Model3 WHERE nameIndexed : '*3*'", cache, 2);

        // VERSION 4
        // Now we can remove deprecated name field
        cache = cacheProvider.recreateRemoteCacheManager(Schema3F.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create VERSION 4 entities, entity V4 can't contain name field because ModelF doesn't contain it
        // in other words, migrating all entities to V4 removes name field from all entities, but it is not necessary
        // the presence of name field should not interfere normal Keycloak behaviour
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelF(3));
        doQuery("FROM Model3 WHERE nameIndexed : '*3*'", cache, 3);
    }

    /**
     * In this case, we remove index but preserve usage of the field in queries
     *
     * Since the index is non-analyzed the old queries should be valid also in the newer version therefore it should be
     * enough to just stop using the index.
     */
    @Test
    void testRemoveNonAnalyzedIndex() {
        RemoteCache<String, Model> cache = cacheProvider
                .init(new CacheDefinition(CACHE1_NAME, "Model3"), Schema3B.INSTANCE)
                .getCache(CACHE1_NAME);

        // Create entities with index
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelB(1));
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);

        // Update schema to not include index on name
        // update index schema
        cacheProvider.updateIndexSchema(cache, Schema3A.INSTANCE);

        // Create entities without index
        ModelUtils.createModel1Entities(cache, 5, ModelUtils.createModelA(2));

        // Try query with field that has the index in both versions
        doQuery("FROM Model3 WHERE entityVersion >= 1", cache, 10);

        // Try query with name, it should not use index and should return both new and old entities
        // TODO ISPN-13947 this doesn't work it seems it is using index even though new schema doesn't contain it
        doQuery("FROM Model3 WHERE name LIKE '%3%'", cache, 1);
    }

    /**
     * This is a little more complicated than ^ as older full-text queries are no longer applicable for newer version
     * that drops analyzed index
     *
     * Just realized this is effectively the same as {@link BasicsForNoDowntimeUpgradesTest#testMigrateAnalyzedFieldToNonAnalyzed()}
     */
    @Test
    void testRemoveAnalyzedIndex() {
    }

    private void migrateEntityAToEntityD(final RemoteCache<String, Model3D> cache) {
        new HashSet<>(cache.keySet())
                .forEach(e -> {
                    Model3D model3D = cache.get(e);
                    if (model3D.entityVersion == 1) {
                        model3D.nameIndexed = model3D.name;
                        model3D.entityVersion = 2;
                        cache.put(model3D.id, model3D);
                    }
                });
    }

    private <T> void doQuery(String query, RemoteCache<String, T> messageCache, int expectedResults) {
        QueryFactory queryFactory = Search.getQueryFactory(messageCache);
        Query<T> infinispanObjectEntities = queryFactory.create(query);
        Set<T> result = StreamSupport.stream(infinispanObjectEntities.spliterator(), false)
                .collect(Collectors.toSet());

        assertThat(result).hasSize(expectedResults);
    }


    @AfterAll
    public void afterAll() {
        cacheProvider.stop();
    }



}
