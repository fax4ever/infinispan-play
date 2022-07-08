# Query Performance

In order to run test, you need a Infinispan 14.0.0.Dev03 listening on 11222 (default Hot Rod port)
and an admin group user defined on it, having credential user / pass.

``` bash
query-performance> mvn clean install
```

on my machine:
``` bash
ENTITIES: 1000000
QUERY EXECUTED:100.0
MAX TIME:9.081338E7
AVERAGE TIME:2235003.14
LOAD EXECUTED:100.0
MAX TIME:171558.0
AVERAGE TIME:89304.51
```