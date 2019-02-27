# cacheMissSimulator
Cache simulator for multi-level structure

This project implements a tree-structured cache simulator with the support of the following features:
1. Various Replacement Policies: LRU, FIFO, Radom, ARC, LRFU.
2. Various Object Size: The objects are allowed to be of different size, which is more appliable to the real life applications.
3. Customized cache specification: The user can specify the configuration of each individual cache.
4. Takes traces in various forms: Empirical traces or synthetic traces with uniform access, b-c access or Zipf Access.

For a more detailed background of tree-structure cache, reader please refer to the following document under the same dir: tree-cache.pdf

Below are some argument options for the tool:
java cacheMissSimulator [options...] arguments...
 -cacheConfigFile VAL      : read the cache definition from file (default: )
 
 -cacheSize N              : single cache size (default: 100)
 
 -cacheStrategy N          : cache strategy 1 LCE, 2 LCD, 3 MCD (default: 1)
 
 -cacheType N              : cache type 1 FIFO, 2, LRU, 3,Random, 4.ARC,
                             5.LRFU, 6.LRU different object size (default: 2)
 
 -iter N                   : iteration (default: 10)
 
 -lamda N                  : for LRFU (default: 0.5)
 
 -requestAddress VAL       : real trace file address (default: )
 
 -requestOutPutAddress VAL : Save the synthetic workload for debugging
                             (default: requestOut.txt)
 
 -requestType N            : workload type 1 Uniform, 2 B-C, 3 Zipf, 4. Real
                             Trace (default: 1)
 
 For more options, please type
 
 java -jar CacheMiss.jar -H
 
 For example:
To define a 1-cache simulation, the user just needs to define the breath as 1, and depth as 1. 

java -jar CacheMiss.jar -requestAddress file.txt -requestType 4 -cacheSize 100 -iter 1

This command takes the trace from file.txt as input. It uses ARC, however the total cacheSize is 400, since there are 4 caches (b1, b2, t1, t2) in ARC. 

If use LRFU as cache, Then the command will be

java -jar CacheMiss.jar -requestAddress file.txt -requestType 5 -cacheSize 100 -iter 1 -lamda 0.4

This command uses LRFU, and set the lamda as 0.4.
