# Schema evolution

## Preface

While ProtocolBuffer allows to evolve the schema within certain limits, 
at the moment the query system does not allow querying fields that have not been added when the cache is started.
This project shows a possible solution to bypass this limitation, 
allowing at the same time a near-zero downtime of the service.

## First static model

The first model is very simple, we have the entity `Model1A` containing an indexed field named `oldName`:

``` java
@ProtoDoc("@Indexed")
@ProtoName("Model1")
public class Model1A implements Model {

   private String oldName;

   @ProtoFactory
   public Model1A(String oldName) {
      this.oldName = oldName;
   }

   @ProtoField(value = 1)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getOldName() {
      return oldName;
   }
}
```
It corresponds to the `Model1` ProtocolBuffer message:

``` proto
// File name: model1-schema.proto
// Generated from : fax.play.model1.Schema1A

/**
 * @Indexed
 */
message Model1 {
   
   /**
    * @Field(store = Store.NO, analyze = Analyze.NO)
    */
   required string oldName = 1;
}
```

Remember that we need to list this `Model1` on the `indexed-entities` attribute of the cache definition.
This configuration is done when the cache `keyword1` is created.
As long as the mapping is not updated everything works fine, both working on cache data and queries, 
see `fax.play.smoke.OldWorkaroundSolutionTest`.

## Add a new field

We’re adding a new field `newName` on the `Model1` using a different pojo that we will name `Model1B`.
This is the new pojo:

``` java
@ProtoDoc("@Indexed")
@ProtoName("Model1")
public class Model1B implements Model {

   @Deprecated
   public String oldName;

   public String newName;

   @ProtoFactory
   public Model1B(String oldName, String newName) {
      // fill legacies:
      this.oldName = oldName;

      this.newName = newName;
   }

   @Deprecated
   @ProtoField(value = 1, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getOldName() {
      return oldName;
   }

   @ProtoField(value = 2)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getNewName() {
      return newName;
   }
}
```

Notice that the pojo is mapped to the same ProtocolBuffer message `Message1` and the new field is not marked required.
The `Message1` is updated as:

``` proto
// File name: model1-schema.proto
// Generated from : fax.play.model1.Schema1B

/**
 * @Indexed
 */
message Model1 {
   
   /**
    * @Field(store = Store.NO, analyze = Analyze.NO)
    */
   required string oldName = 1;
   
   /**
    * @Field(store = Store.NO, analyze = Analyze.NO)
    */
   optional string newName = 2;
}
```

When we work on cache data everything works, the old and the new entities are retrieved now as `Model1B`,
but only the latter will contain `newName`s filled. See `SmokeTest` again.

Notice that if we try to query targeting the new field we get an execution from the Hibernate Search subsystem.
We can only query the old field, because the query context is static 
and predetermined at the time of the cache creation.
We're working to reduce this limitation in the future versions of Infinispan.

## Remove the old field

If we need to remove some old fields, we need at this point to migrate the old entities, 
so that their new fields will be filled with the old values.

We cannot replace the entities on the same cache for the limitation we exposed above about the fact that
the search context is static.
We have to define another cache instance for the new model.

Using another cache it is also a way to reduce the downtime of the service, because the old cache is still working
during the process of the migration.

Let's start with the new pojo for the cleaned model:

``` java
@ProtoDoc("@Indexed")
@ProtoName("Model2")
public class Model2A implements Model {

   private String newName;

   @ProtoFactory
   public Model2A(String newName) {
      this.newName = newName;
   }

   @ProtoField(value = 1, required = true)
   @ProtoDoc("@Field(store = Store.NO, analyze = Analyze.NO)")
   public String getNewName() {
      return newName;
   }
}
```

We can see that the old field is gone and the new field can be now declared required,
we're also introduce a new ProtocolBuffer message type `Model2`:

``` proto
// File name: model2-schema.proto
// Generated from : fax.play.model2.Schema2A

/**
 * @Indexed
 */
message Model2 {
   
   /**
    * @Field(store = Store.NO, analyze = Analyze.NO)
    */
   required string newName = 1;
}
```

We're using a different message type, so that the old cache `keyword1` can continue to operate during the period
of migration. Remember that the ProtocolBuffer message definitions are not scoped on a single cache,
but shared by all caches.

We operate with the new model on the cache, that we named `keyword2`.
Still see the `SmokeTest`, we extract the `Model1B` instances from the first cache, and we put the `Model2A` instances
on the new cache.
We can also add new entities.

We can see that now everything works, both working on caches data and queries.

## Conclusion

Given the current limitation this is the solution we're proposing to support indexed queries on schema that changes
time to time.
As we said, we hope that will limit the limitation in the future. 
Moreover, we're always available for an open discussion about the fact this solution may or may not solve specific
use case.