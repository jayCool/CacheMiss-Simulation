Cache Miss Simulator for Tree Structured Architecture

This project implments a tree-structured cache simulator with the support of the following features:

1. Cache replacement policies: LRU, FIFO, Random, ARC, LRFU, Batch Recyle Cache (HDD, SSD).
2. Various Object Size: The objects are allowed to be of various size, not restricted to unique object size.
3. Flexible Cache Architecture: The user can specify the architecture of the caches.
4. Various Workload Generator: Uniform access, Zipf Access, B-C Access.
5. Various Cache Operations: LCE, MCD, MCD.
6. Flexibility of defining warming files for each cache.
7. Flexibility of defining processing time, and latency.

================================
1. TO GENERATE SOME WORKLOADS:
================================

java -jar CacheMissSimulator.jar -usage 1 -config workload-configuration.json

The above command has two arguments, -usage 1: generate workload, and -config: configuration file location

workload-configuration.json is a JSON file which specifies the workload.

{
    "clients" :
    [
        {"id" : 0, "workloadType": 1, "workLoadLength" : 10000, "distinctObject": 1000},
        {"id" : 1, "workloadType": 2, "workLoadLength" : 10000, "distinctObject": 1000, "b":"0.8", "c":"0.2"},
        {"id" : 2, "workloadType": 3, "workLoadLength" : 10000, "distinctObject": 1000, "a":"0.1"}
    ],

    "outputDir" : "workload-dir/"
}

The above configuration file says that generate THREE workloads, and output to "workload-dir/"
The first workload is a UNIFORM access workload, there are total 10000 objects, and there are 1000 distinct objects.
The second workload is a UNIFORM access workload, there are total 10000 objects, and there are 1000 distinct objects.
The third workload is a UNIFORM access workload, there are total 10000 objects, and there are 1000 distinct objects.

================================
2. TO SIMULATE THE CACHE MISSES:
================================

java -jar CacheMissSimulator.jar -usage 2 -config simulation-configuration.json

The above command has two arguments, -usage 2: simulate cache miss, and -config: configuration file location

{
    "clients":
    [
        {"id": "0", "cacheSize": "500", "cacheType": "6", "inputWorkloadAddress": "workload-dir/0.txt", "parent": "4"},
        {"id": "1", "cacheSize": "500", "cacheType": "4", "inputWorkloadAddress": "workload-dir/0.txt", "parent": "4"},
        {"id": "2", "cacheSize": "500", "cacheType": "1", "inputWorkloadAddress": "workload-dir/1.txt", "parent": "5"},
        {"id": "3", "cacheSize": "500", "cacheType": "3", "inputWorkloadAddress": "workload-dir/2.txt", "parent": "5"},
        {"id": "4", "cacheSize": "500", "cacheType": "3"},
        {"id": "5", "cacheSize": "500", "cacheType": "3"}
    ]
}

There are 6 caches in total.
cache 0, cache 1, cache 2, cache 3 are the first level cache.
cache 4 is the second level cache, and it is connected to cache 0, cache 1.
cache 5 is the second level cache, and it is connected to cache 2, cache 3.

For caches without parent cache, we assume the cache is connected to the ROOT (contains everything).

cacheType specifies the cachetype, for more details, refer to CacheType.java.


=============================
3. TO IMPLEMENT OWN CACHE:
=============================

One must extends the Cache.java and implements the abstract method.


