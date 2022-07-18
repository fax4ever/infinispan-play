package fax.play;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.infinispan.client.rest.RestCacheClient;
import org.infinispan.client.rest.RestEntity;
import org.infinispan.commons.dataconversion.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private Config config;
   private OpenTelemetry openTelemetry;
   private Tracer tracer;

   @BeforeAll
   public void start() {
      System.setProperty("otel.exporter.otlp.endpoint", "http://localhost:4317");
      System.setProperty("otel.service.name", "infinispan-client-service");
      System.setProperty("otel.metrics.exporter", "none");

      openTelemetry = AutoConfiguredOpenTelemetrySdk.builder()
            .build()
            .getOpenTelemetrySdk();

      tracer = openTelemetry.getTracer("fax.play.infinispan.client");
      config = new Config();
   }

   @AfterAll
   public void end() throws Exception {
      config.shutdown();
   }

   @Test
   public void test() {
      RestCacheClient cache = config.getCache();

      withinClientSideSpan("bulk-1", () -> {
         withinClientSideSpan("sub-bulk-a", () -> {
            putSomeEntries(cache);
         });
         withinClientSideSpan("sub-bulk-b", () -> {
            putSomeEntries(cache);
         });
      });

      withinClientSideSpan("bulk-2", () -> {
         withinClientSideSpan("sub-bulk-a", () -> {
            putSomeEntries(cache);
         });
         withinClientSideSpan("sub-bulk-b", () -> {
            putSomeEntries(cache);
         });
      });
   }

   public void withinClientSideSpan(String spanName, Runnable operations) {
      Span span = tracer.spanBuilder(spanName).setSpanKind(SpanKind.CLIENT).startSpan();
      // put the span into the current Context
      try (Scope scope = span.makeCurrent()) {
         operations.run();
      } catch (Throwable throwable) {
         span.setStatus(StatusCode.ERROR, "Something bad happened!");
         span.recordException(throwable);
         throw throwable;
      } finally {
         span.end(); // Cannot set a span after this call
      }
   }

   private void putSomeEntries(RestCacheClient cache) {
      Map<String, String> contextMap = getContextMap();

      CompletableFuture[] futures = new CompletableFuture[3];

      futures[0] = cache.put("1", MediaType.TEXT_PLAIN.toString(),
            RestEntity.create(MediaType.TEXT_PLAIN, "A"), contextMap).toCompletableFuture();
      futures[1] = cache.put("2", MediaType.TEXT_PLAIN.toString(),
            RestEntity.create(MediaType.TEXT_PLAIN, "B"), contextMap).toCompletableFuture();
      futures[2] = cache.put("3", MediaType.TEXT_PLAIN.toString(),
            RestEntity.create(MediaType.TEXT_PLAIN, "C"), contextMap).toCompletableFuture();

      CompletableFuture.allOf(futures).join();
   }

   public static Map<String, String> getContextMap() {
      HashMap<String, String> result = new HashMap<>();

      // Inject the request with the *current* Context, which contains our current Span.
      W3CTraceContextPropagator.getInstance().inject(Context.current(), result,
            (carrier, key, value) -> carrier.put(key, value));
      return result;
   }
}
