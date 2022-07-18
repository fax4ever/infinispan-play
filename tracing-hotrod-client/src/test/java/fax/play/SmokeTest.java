package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
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
   public void end() {
      config.shutdown();
   }

   @Test
   public void test() {
      RemoteCache<Integer, String> cache = config.getCache();

      withinClientSideSpan("bulk-1", () -> {
         withinClientSideSpan("sub-bulk-a", () -> {
            cache.put(1, "A");
            cache.put(2, "B");
            cache.put(3, "C");
         });
         withinClientSideSpan("sub-bulk-b", () -> {
            cache.put(1, "A");
            cache.put(2, "B");
            cache.put(3, "C");
         });
      });

      withinClientSideSpan("bulk-2", () -> {
         withinClientSideSpan("sub-bulk-a", () -> {
            cache.put(1, "A");
            cache.put(2, "B");
            cache.put(3, "C");
         });
         withinClientSideSpan("sub-bulk-b", () -> {
            cache.put(1, "A");
            cache.put(2, "B");
            cache.put(3, "C");
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
}
