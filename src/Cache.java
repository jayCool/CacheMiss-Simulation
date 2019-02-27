/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zhang Jiangwei
 */
public class Cache {

    Queue fifoqueue = new LinkedList<String>();
    HashSet<String> randomCache = new HashSet<>();
    String[] randomKeys;
    Random rand = new Random();
    long cacheSize;
    int cacheID;
    boolean aval = true;

    public void setFifoqueue(Queue fifoqueue) {
        this.fifoqueue = fifoqueue;
    }

    public void setRandomCache(HashSet<String> randomCache) {
        this.randomCache = randomCache;
    }

    public void setRandomKeys(String[] randomKeys) {
        this.randomKeys = randomKeys;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setCacheID(int cacheID) {
        this.cacheID = cacheID;
    }

    public void setAval(boolean aval) {
        this.aval = aval;
    }

    public void setLruEldest(String lruEldest) {
        this.lruEldest = lruEldest;
    }

    public void setCacheType(int cacheType) {
        this.cacheType = cacheType;
    }

    public void setCacheStrategy(int cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    public void setTotalReceived(double totalReceived) {
        this.totalReceived = totalReceived;
    }

    public void setTotalMissed(double totalMissed) {
        this.totalMissed = totalMissed;
    }

    public void setLrfu(LRFUCacheOld lrfu) {
        this.lrfu = lrfu;
    }

    public void setLruCache(LRUCache lruCache) {
        this.lruCache = lruCache;
    }

    public void setLrupCache(LRUpCache lrupCache) {
        this.lrupCache = lrupCache;
    }

    public void setArcCache(ARC arcCache) {
        this.arcCache = arcCache;
    }

    public void setLRUNUC(LRUNonUniformCache LRUNUC) {
        this.LRUNUC = LRUNUC;
    }

    public void setIqiyissdcache(IQIYISSDCache iqiyissdcache) {
        this.iqiyissdcache = iqiyissdcache;
    }

    public void setIqiyihddcache(IQIYIHDDCache iqiyihddcache) {
        this.iqiyihddcache = iqiyihddcache;
    }

    public void setRandomWarming(boolean randomWarming) {
        this.randomWarming = randomWarming;
    }

    public void setLfuCache(LFUCache lfuCache) {
        this.lfuCache = lfuCache;
    }

    public void setChildren(ArrayList<Cache> children) {
        this.children = children;
    }

    public void setParent(Cache parent) {
        this.parent = parent;
    }

    public void setEventList(PriorityQueue<ReferencePattern> eventList) {
        this.eventList = eventList;
    }

    public void setP(int p) {
        this.p = p;
    }

    public void setMissRatioOption(int missRatioOption) {
        this.missRatioOption = missRatioOption;
    }

    public void setCacheFull(boolean cacheFull) {
        this.cacheFull = cacheFull;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    public void setTraceFile(String traceFile) {
        this.traceFile = traceFile;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Queue getFifoqueue() {
        return fifoqueue;
    }

    public HashSet<String> getRandomCache() {
        return randomCache;
    }

    public String[] getRandomKeys() {
        return randomKeys;
    }

    public Random getRand() {
        return rand;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public int getCacheID() {
        return cacheID;
    }

    public boolean isAval() {
        return aval;
    }

    public String getLruEldest() {
        return lruEldest;
    }

    public int getCacheType() {
        return cacheType;
    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public double getTotalReceived() {
        return totalReceived;
    }

    public double getTotalMissed() {
        return totalMissed;
    }

    public LRFUCacheOld getLrfu() {
        return lrfu;
    }

    public LRUCache getLruCache() {
        return lruCache;
    }

    public LRUpCache getLrupCache() {
        return lrupCache;
    }

    public ARC getArcCache() {
        return arcCache;
    }

    public LRUNonUniformCache getLRUNUC() {
        return LRUNUC;
    }

    public IQIYISSDCache getIqiyissdcache() {
        return iqiyissdcache;
    }

    public IQIYIHDDCache getIqiyihddcache() {
        return iqiyihddcache;
    }

    public boolean isRandomWarming() {
        return randomWarming;
    }

    public LFUCache getLfuCache() {
        return lfuCache;
    }

    public ArrayList<Cache> getChildren() {
        return children;
    }

    public PriorityQueue<ReferencePattern> getEventList() {
        return eventList;
    }

    public int getP() {
        return p;
    }

    public int getMissRatioOption() {
        return missRatioOption;
    }

    public boolean isCacheFull() {
        return cacheFull;
    }

    public boolean isHistory() {
        return history;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public String getTraceFile() {
        return traceFile;
    }

    public boolean isDebug() {
        return debug;
    }

    String lruEldest;
    int cacheType; // 1 FIFO, 2 LRU, 3 RANDOM, 4 ARC, 7, SSD, 8 HDD, 9 lrupcache
    int cacheStrategy = 1; //1 LCE, 2 LCD, 3 MCD
    double totalReceived = 0;
    double totalMissed = 0;

    LRFUCacheOld lrfu;
    LRUCache lruCache;
    LRUpCache lrupCache;
    ARC arcCache;
    LRUNonUniformCache LRUNUC;
    IQIYISSDCache iqiyissdcache;
    IQIYIHDDCache iqiyihddcache;
    boolean randomWarming = false;
    LFUCache lfuCache;
    //double lamba;
    private ArrayList<Cache> children = new ArrayList<>();
    public Cache parent;

    public Cache getParent() {
        return parent;
    }
    PriorityQueue<ReferencePattern> eventList;
    int p = 0;
    
    /**
     * The metric of calculating miss ratio, 0 by # request, 1 by object size, default is 0.
     */
    int missRatioOption = 0; 
    boolean cacheFull = false;
    boolean history = false;
    PrintWriter pw;
    String traceFile = "";
    boolean debug = false;

    Cache() {
    }

    public void setWarming(boolean warming) {
        this.randomWarming = warming;
    }

    public void addChild(Cache child) {
        children.add(child);
    }

    public String getARCstring() {
        if (cacheFull) {
            return "" + arcCache.t1.curSize + " " + arcCache.t2.curSize + " " + arcCache.b1.objectSize.size() + " " + arcCache.b2.objectSize.size() + " " + arcCache.p;
        } // }
        else {
            return "";
        }
        //   return "";
    }

    //LRFUCache lrfu ;
    @Override
    public String toString() {

        switch (this.cacheType) {
            case 1:
                return "FIFOCache: " + this.fifoqueue.toString();
            case 2:
                return "LRUCache: " + lruCache.keySet();
            case 3:
                return "RANDOMCache: " + this.randomCache.toString();
            case 4:
                return arcCache.toString();
            case 5:
                return this.lrfu.toString().trim();
            case 6:
                return "LRUDiff Size: " + LRUNUC.size();
            case 7:
                return "IQIYI-SSD Size: " + this.iqiyissdcache.toString();
            case 8:
                return "IQIYI-HDD Size: " + this.iqiyihddcache.toString();
            case 9:
                return "LRUpCache: " + this.lrupCache.toString();
            case 10:
                return "LFUCache: " + this.lfuCache.toString();
        }

        return "Cache{" + "fifoqueue=" + fifoqueue + ", randomCache=" + randomCache + "type: " + this.cacheType + "}";
    }

    ArrayList<String> getEvictedObjects() {
        return iqiyihddcache.getEvictedBatchObjectes();
    }

    ArrayList<Integer> getEvictedObjectSizes() {
        return iqiyihddcache.getEvictedBatchObjectSizes();
    }

    public void setLamda(double lamda) {
        this.lrfu.setlamba(lamda);
    }

    public Cache(long cacheSize, int id, int type) {

        this.cacheSize = cacheSize;
        this.cacheID = id;
        this.cacheType = type;
        Comparator<ReferencePattern> comparator = new ReferenceComparator();
        int eventListCapacity = Math.min(8000000, (int) cacheSize);
        eventList = new PriorityQueue(eventListCapacity, comparator);

        switch (this.cacheType) {
            case 1:
                break;
            case 2:
                lruCache = new LRUCache((int) cacheSize);
                break;
            case 3:
                randomKeys = new String[(int) this.cacheSize];
                for (int i = 0; i < randomKeys.length; i++) {
                    randomKeys[i] = "";
                }
                break;
            case 4:
                arcCache = new ARC(cacheSize);

                break;
            case 5:
                lrfu = new LRFUCacheOld((int) cacheSize);
                break;
            case 6:
                LRUNUC = new LRUNonUniformCache((int) cacheSize);
                break;
            case 7:
                iqiyissdcache = new IQIYISSDCache(cacheSize);
                break;
            case 8:
                iqiyihddcache = new IQIYIHDDCache(cacheSize);
                break;
            case 9:
                this.lrupCache = new LRUpCache(cacheSize);
                break;
            case 10:
                this.lfuCache = new LFUCache((int) cacheSize);
                break;
        }

    }

    public void setLRUpCachePvalue(double p) {
        this.lrupCache.setP(p);
    }

    public String add(String object, long time, int objSize) {
        String output = "";
        if (debug) {
            //    System.out.println("adding item!");
            //    System.out.println(this.cacheType);
        }// System.out.println(this.toString());
        switch (this.cacheType) {
            case 1:
                output = this.addFIFO(object, time);
                break;
            case 2:
                output = this.lruCache.addLRU(object, time);
                this.lruEldest = "";
                break;
            case 6:
                output = LRUNUC.add(object, time, objSize);
                break;
            case 3:
                output = this.addRandom(object, time);
                break;
            case 4:
                // cacheFull = true;

                output = this.arcCache.add(object, time, objSize);
                break;

            case 5:
                output = this.lrfu.addLRFU(object, time);
                //output = this.lrfu.addLRFUParallel(object, time);
                break;
            case 7:
                output = iqiyissdcache.add(object, time, objSize);
                break;
            case 8:
                output = iqiyihddcache.add(object, time, objSize);
                break;
            case 9:
                output = this.lrupCache.add(object, time);
                break;
            case 10:
                output = this.lfuCache.add(object);

        }
        if (history) {
            pw.println(object);
            pw.println(this.toString());
            pw.flush();
        }
        if (cacheType == 7 && iqiyissdcache.objectSize.size() == cacheSize) {
            cacheFull = true;
        }

        if (cacheType == 4) {
            if (arcCache.t1.curSize + arcCache.t2.curSize + objSize >= cacheSize) {
                if (randomWarming) {
                    if (Math.random() < 0.005) {
                        cacheFull = true;
                    }
                } else {
                    cacheFull = true;
                }
            }
        }

        if (cacheType == 9) {
            if (lrupCache.t1.curSize + lrupCache.t2.curSize + objSize >= cacheSize) {
                if (randomWarming) {
                    if (Math.random() < 0.005) {
                        cacheFull = true;
                    }
                } else {
                    cacheFull = true;
                }
            }
        }

        if (!output.isEmpty() && cacheType != 4 && cacheType != 9) {
            //System.out.println(output);

            cacheFull = true;
        }
        return output;
    }

    public String remove(String object, long time) {
        String output = "";
        switch (this.cacheType) {
            case 1:
                this.removeFIFO(object, time);
                break;
            case 2:
                lruCache.remove(object);
                break;
            case 3:
                this.removeRandom(object, time);
                break;
            case 6:
                LRUNUC.remove(object);
                break;
            case 7:
                iqiyissdcache.remove(object);
                break;
            case 8:
                iqiyihddcache.remove(object);
                break;

        }
        return output;
    }

    public boolean contains(String object) {
        switch (this.cacheType) {
            case 1:
                return this.fifoqueue.contains(object);
            case 2:
                return lruCache.containsKey(object);
            case 3:
                return this.randomCache.contains(object);
            case 4:
                return arcCache.t1.objectSize.containsKey(object) || arcCache.t2.objectSize.containsKey(object);
            case 5:
                return this.lrfu.contains(object);
            case 6:
                return this.LRUNUC.objectSize.containsKey(object);
            case 7:
                return iqiyissdcache.objectSize.containsKey(object);
            case 8:
                return iqiyihddcache.objectSize.containsKey(object);
            case 9:
                return lrupCache.contains(object);
            case 10:
                return lfuCache.contains(object);
        }
        return false;
    }

    public String addFIFO(String element, long time) {
        String result = "";
        if (fifoqueue.contains(element)) {
            return "";
        }

        if (fifoqueue.size() < cacheSize) {
            fifoqueue.offer(element);
            return "";
        }

        result = (String) fifoqueue.poll();
        fifoqueue.offer(element);
        return result;
    }

    public String addRandom(String element, long time) {
        String result = "";
        if (randomCache.contains(element)) {
            return "";
        }
        if (randomCache.size() < cacheSize) {
            randomKeys[randomCache.size()] = element;
            randomCache.add(element);
            return "";
        }

        int index = rand.nextInt((int) cacheSize);
        result = randomKeys[index];
        randomCache.remove(randomKeys[index]);
        randomCache.add(element);
        randomKeys[index] = element;
        return result;
    }

    private void removeFIFO(String object, long time) {
        this.fifoqueue.remove(object);
    }

    private void removeRandom(String object, long time) {
        randomCache.remove(object);

        int index = 0;
        for (int i = 0; i < randomKeys.length; i++) {
            if (randomKeys[i].equals(object)) {
                index = i;
            }
        }
        randomKeys[index] = "";
    }

    void addParent(Cache cache) {
        this.parent = cache;
    }

    void addMissedCount(int objSize) {
        if (!cacheFull) {
            return;
        }
        if (this.missRatioOption == 0) {
            totalMissed += 1;
        } else {
            totalMissed += objSize;
        }
    }

    void addReceivedObject(int objSize) {
        if (!cacheFull) {
            return;
        }
        if (this.missRatioOption == 0) {
            totalReceived += 1;
        } else {
            totalReceived += objSize;
        }
    }

    void setHistory(boolean history, String requestAddress) {
        if (history) {
            this.history = history;
            try {
                pw = new PrintWriter(new File(requestAddress + "-" + this.lrfu.lamba + "-" + cacheSize + ".history"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void batchUpdateObjects(ArrayList<String> evictedObjects, ArrayList<Integer> evictedObjectSizes, long timestamp) {
        this.iqiyissdcache.batchUpdating(evictedObjects, evictedObjectSizes, timestamp);
        if (iqiyissdcache.objectSize.size() == iqiyissdcache.cacheSize) {
            this.cacheFull = true;
        }
    }

    void calculateEvictedObjects() {
        this.iqiyihddcache.processEvictedBatchObjects();
    }

    void setBatchFrequency(int batchFrequency) {
        this.iqiyihddcache.setBatchFrequency(batchFrequency);
    }

    void writeToFile(String outputDir) {
        try {
            PrintWriter pw = new PrintWriter(new File(outputDir + File.separator + cacheID + ".content"));
            switch (this.cacheType) {
                case 7:
                    for (Entry<String, Double> entry : iqiyissdcache.objectSize.entrySet()) {
                        pw.println(entry.getKey() + " " + entry.getValue());
                    }
                    break;
                case 8:
                    for (Entry<String, Double> entry : iqiyihddcache.objectSize.entrySet()) {
                        pw.println(entry.getKey() + " " + entry.getValue());
                    }
                    break;
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String getLRUPMaxsize() {
        return "" + this.lrupCache.maxSize;
    }

}
