/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Zhang Jiangwei
 */

public class Cache {
   
   
    long cacheSize;
    int cacheID;
 
    int cacheType; // 1 FIFO, 2 LRU, 3 RANDOM, 4 ARC, 7, SSD, 8 HDD, 9 lrupcache
    int cacheStrategy = 1; //1 LCE, 2 LCD, 3 MCD
    double totalReceived = 0;
    double totalMissed = 0;
  
    LRFUCacheOld lrfu;
    LRUCache lruCache;
    LRUpCache lrupCache;
    ARC arcCache;
    LRUNonUniformCache LRUNUC;
    SSDCache iqiyissdcache;
    HDDCache iqiyihddcache;
    FIFOCache fifoCache;
   
    private ArrayList<Cache> children = new ArrayList<>();
    public Cache parent;
    private RandomCache randCache;
     PriorityQueue<ReferencePattern> eventList;
    int p = 0;
    int missRatioOption = 0; // 0 for by request, 1 by object size
    boolean cacheFull = false;
    
   
    
    public Cache getParent() {
        return parent;
    }
   

    Cache() {
    }

    public void addChild(Cache child) {
        children.add(child);
    }
    
    public String getARCstring(){
        if (cacheFull)
           return "" + arcCache.t1.curSize + " "+ arcCache.t2.curSize +" "+ arcCache.b1.objectSize.size() +" "+ arcCache.b2.objectSize.size()+" "+arcCache.p;
        else return "";
    }

  
    /**
     * 
     * @return String representation of the cache.
     */
    @Override
    public String toString() {
        switch (this.cacheType) {
            case CacheType.FIFO:
                return "FIFO Cache: " + fifoCache.toString();
            case CacheType.LRU:
                return "LRU Cache: " + lruCache.keySet();
            case CacheType.RANDOM:
                return "RANDOM Cache: " + randCache.toString();
            case CacheType.ARC:
                return "ARC Cache: " + arcCache.toString();
            case CacheType.LRFU:
                return "LRFU Cache: " + lrfu.toString().trim();
            case CacheType.LRFU_NON_UNIFORM:
                return "LRU_NON_UNIFORM Cache: " + LRUNUC.size();
            case CacheType.SSD:
                return "SSD Cache: " + iqiyissdcache.toString();
            case CacheType.HDD:
                return "HDD Cache: " + iqiyihddcache.toString();
            case CacheType.LRUP:
                return "LRUpCache: " + lrupCache.toString();
        }
        return "something interesting!!!!";
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
                fifoCache = new FIFOCache((int) cacheSize);
                break;
            case 2:
                lruCache = new LRUCache((int) cacheSize);
                break;
            case 3:
                 randCache = new RandomCache(cacheSize);
               
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
                iqiyissdcache = new SSDCache(cacheSize);
                break;
            case 8:
                iqiyihddcache = new HDDCache(cacheSize);
                break;
            case 9:
                this.lrupCache = new LRUpCache(cacheSize);
                break;
        }
        
        

    }
    
    public void setLRUpCachePvalue(double p){
      this.lrupCache.setP(p);
    }

    public String add(String object, long time, int objSize) {
        String output = "";
        switch (this.cacheType) {
            case 1:
                output = fifoCache.add(object, time);
                break;
            case 2:
                output = this.lruCache.addLRU(object, time);
                break;
            case 6:
                output = LRUNUC.add(object, time, objSize);
                break;
            case 3:
                output = randCache.add(object, time);
                break;
            case 4:
                 output = this.arcCache.add(object, time, objSize);
               break;
            case 5:
                output = this.lrfu.add(object, time);
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

        }
       
        if (cacheType == 7 && iqiyissdcache.objectSize.size() == cacheSize) {
            cacheFull = true;
        }
        
        if (cacheType == 4){
            if (arcCache.t1.curSize + arcCache.t2.curSize + objSize >= cacheSize){
                cacheFull = true;
            }
        }
        
         if (cacheType == 9){
            if (lrupCache.t1.curSize + lrupCache.t2.curSize + objSize >= cacheSize){
                cacheFull = true;
            }
        }
        
        
        if (!output.isEmpty() && cacheType!=4) {
            cacheFull = true;
        }
        
       
        
        return output;
    }

    public String remove(String object, long time) {
        String output = "";
        switch (this.cacheType) {
            case 1:
                fifoCache.remove(object);
                break;
            case 2:
                lruCache.remove(object);
                break;
            case 3:
                randCache.remove(object);
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
                return  fifoCache.contains(object);
            case 2:
                return lruCache.containsKey(object);
            case 3:
                return randCache.contains(object);
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
        }
        return false;
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

   

    void batchUpdateObjects(ArrayList<String> evictedObjects, ArrayList<Integer> evictedObjectSizes, long timestamp) {
        this.iqiyissdcache.batchUpdating(evictedObjects, evictedObjectSizes, timestamp);
        if (iqiyissdcache.objectSize.size() == iqiyissdcache.cacheSize) {
            this.cacheFull = true;
        }
    }

    void calculateEvictedObjects() {
        //System.out.print(this.toString());
        this.iqiyihddcache.processEvictedBatchObjects();
    }

    void setBatchFrequency(int batchFrequency) {
        this.iqiyihddcache.setBatchFrequency(batchFrequency);
    }

    public FIFOCache getFifoCache() {
        return fifoCache;
    }

    public void setFifoCache(FIFOCache fifoCache) {
        this.fifoCache = fifoCache;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheID() {
        return cacheID;
    }

    public void setCacheID(int cacheID) {
        this.cacheID = cacheID;
    }


    public int getCacheType() {
        return cacheType;
    }

    public void setCacheType(int cacheType) {
        this.cacheType = cacheType;
    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public void setCacheStrategy(int cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    public double getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(double totalReceived) {
        this.totalReceived = totalReceived;
    }

    public double getTotalMissed() {
        return totalMissed;
    }

    public void setTotalMissed(double totalMissed) {
        this.totalMissed = totalMissed;
    }

    public LRFUCacheOld getLrfu() {
        return lrfu;
    }

    public void setLrfu(LRFUCacheOld lrfu) {
        this.lrfu = lrfu;
    }

    public LRUCache getLruCache() {
        return lruCache;
    }

    public void setLruCache(LRUCache lruCache) {
        this.lruCache = lruCache;
    }

    public LRUpCache getLrupCache() {
        return lrupCache;
    }

    public void setLrupCache(LRUpCache lrupCache) {
        this.lrupCache = lrupCache;
    }

    public ARC getArcCache() {
        return arcCache;
    }

    public void setArcCache(ARC arcCache) {
        this.arcCache = arcCache;
    }

    public LRUNonUniformCache getLRUNUC() {
        return LRUNUC;
    }

    public void setLRUNUC(LRUNonUniformCache LRUNUC) {
        this.LRUNUC = LRUNUC;
    }

    public SSDCache getIqiyissdcache() {
        return iqiyissdcache;
    }

    public void setIqiyissdcache(SSDCache iqiyissdcache) {
        this.iqiyissdcache = iqiyissdcache;
    }

    public HDDCache getIqiyihddcache() {
        return iqiyihddcache;
    }

    public void setIqiyihddcache(HDDCache iqiyihddcache) {
        this.iqiyihddcache = iqiyihddcache;
    }

    public ArrayList<Cache> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Cache> children) {
        this.children = children;
    }

    public RandomCache getRandCache() {
        return randCache;
    }

    public void setRandCache(RandomCache randCache) {
        this.randCache = randCache;
    }

    public PriorityQueue<ReferencePattern> getEventList() {
        return eventList;
    }

    public void setEventList(PriorityQueue<ReferencePattern> eventList) {
        this.eventList = eventList;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getMissRatioOption() {
        return missRatioOption;
    }

    public void setMissRatioOption(int missRatioOption) {
        this.missRatioOption = missRatioOption;
    }

    public boolean isCacheFull() {
        return cacheFull;
    }

    public void setCacheFull(boolean cacheFull) {
        this.cacheFull = cacheFull;
    }

   

   

   

}