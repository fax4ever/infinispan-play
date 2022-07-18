# Tracing Hot Rod client requests

We need a Jaeger server instance:

``` sh
jaeger> ./jaeger-all-in-one --collector.otlp.enabled
```

And an Infinispan server listening on 11222 (default Hot Rod port)
Furthermore we need to create the admin user and to configure the tracing on Infinispan server instance: 

``` sh
infinispan-server/bin>./cli.sh user create -g admin -p pass user
export JAVA_OPTS="-Dinfinispan.tracing.enabled=true -Dotel.service.name=infinispan-server-service -Dotel.exporter.otlp.endpoint=http://localhost:4317 -Dotel.metrics.exporter=none"
infinispan-server/bin>./server.sh
```

## Run the test

``` sh
tracing-hotrod-client> mvn clean install
```

## Look the result

Go to Jaeger console: http://localhost:16686/


