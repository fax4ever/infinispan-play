package fax.play;

import java.util.Map;

import com.google.gson.Gson;

public class QueryStatistics {

   private final Gson gson = new Gson();

   private final Double localCount;
   private final Double localAverage;
   private final Double localMax;
   private final String localSlowest;

   private final Double distributedCount;
   private final Double distributedAverage;
   private final Double distributedMax;

   private final Double hybridCount;
   private final Double hybridAverage;
   private final Double hybridMax;
   private final String hybridSlowest;

   private final Double nonIndexedCount;
   private final Double nonIndexedAverage;
   private final Double nonIndexedMax;

   private final Double loadCount;
   private final Double loadAverage;
   private final Double loadMax;

   public QueryStatistics(String json) {
      Map<String, Map> statistics = gson.fromJson(json, Map.class);
      Map<String, Map> queryStat = statistics.get("query");

      Map<String, Object> indexedLocal = queryStat.get("indexed_local");
      localCount = (Double) indexedLocal.get("count");
      localAverage = (Double) indexedLocal.get("average");
      localMax = (Double) indexedLocal.get("max");
      localSlowest = (String) indexedLocal.get("slowest");

      Map<String, Object> indexedDistributed = queryStat.get("indexed_distributed");
      distributedCount = (Double) indexedDistributed.get("count");
      distributedAverage = (Double) indexedDistributed.get("average");
      distributedMax = (Double) indexedDistributed.get("max");

      Map<String, Object> hybrid = queryStat.get("hybrid");
      hybridCount = (Double) hybrid.get("count");
      hybridAverage = (Double) hybrid.get("average");
      hybridMax = (Double) hybrid.get("max");
      hybridSlowest = (String) hybrid.get("slowest");

      Map<String, Object> nonIndexed = queryStat.get("non_indexed");
      nonIndexedCount = (Double) nonIndexed.get("count");
      nonIndexedAverage = (Double) nonIndexed.get("average");
      nonIndexedMax = (Double) nonIndexed.get("max");

      Map<String, Object> entityLoad = queryStat.get("entity_load");
      loadCount = (Double) entityLoad.get("count");
      loadAverage = (Double) entityLoad.get("average");
      loadMax = (Double) entityLoad.get("max");
   }

   public Double getLocalCount() {
      return localCount;
   }

   public Double getLocalAverage() {
      return localAverage;
   }

   public Double getLocalMax() {
      return localMax;
   }

   public String getLocalSlowest() {
      return localSlowest;
   }

   public Double getDistributedCount() {
      return distributedCount;
   }

   public Double getDistributedAverage() {
      return distributedAverage;
   }

   public Double getDistributedMax() {
      return distributedMax;
   }

   public Double getHybridCount() {
      return hybridCount;
   }

   public Double getHybridAverage() {
      return hybridAverage;
   }

   public Double getHybridMax() {
      return hybridMax;
   }

   public String getHybridSlowest() {
      return hybridSlowest;
   }

   public Double getNonIndexedCount() {
      return nonIndexedCount;
   }

   public Double getNonIndexedAverage() {
      return nonIndexedAverage;
   }

   public Double getNonIndexedMax() {
      return nonIndexedMax;
   }

   public Double getLoadCount() {
      return loadCount;
   }

   public Double getLoadAverage() {
      return loadAverage;
   }

   public Double getLoadMax() {
      return loadMax;
   }
}
