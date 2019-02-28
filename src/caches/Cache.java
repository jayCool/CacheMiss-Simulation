package caches;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import caches.implementations.ARCCache;
import caches.implementations.SSDCache;
import caches.FIFOCache;
import caches.implementations.LRUNonUniformCache;
import caches.HDDCache;
import caches.LRUCache;
import caches.LRFUCache;
import caches.RandomCache;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Zhang Jiangwei
 */

public abstract class Cache {
   
   
    long cacheSize;
    int cacheID;
    int cacheType; // 1 FIFO, 2 LRU, 3 RANDOM, 4 ARCCache, 7, SSD, 8 HDD
    int cacheStrategy = 1; //1 LCE, 2 LCD, 3 MCD
    double totalReceived = 0;
    double totalMissed = 0;
    int missRatioOption = 0; // 0 for by request, 1 by object size
    //boolean cacheFull = false;
    
    LRFUCache lrfuCache;
    LRUCache lruCache;
    ARCCache arcCache;
    LRUNonUniformCache lruNonUniformCache;
    SSDCache ssdCache;
    HDDCache hddCache;
    FIFOCache fifoCache;
    private RandomCache randCache;
    
    private ArrayList<Cache> children = new ArrayList<>();
    public Cache parent;
    PriorityQueue<ReferencePattern> eventList;
    
   
    /**
     * Get Parent Cache
     * @return parent cache
     */
    Cache getParent() {
        return parent;
    }
   

    Cache() {
    }

    /**
     * add a child to the current cache
     * @param child 
     */
    public void addChild(Cache child) {
        children.add(child);
    }
  
    /**
     * 
     * @return String representation of the cache.
     */
    
    abstract public String toString();
    

    /**
     * 
     * @return the evicted objects by HDD Cache
     */
    ArrayList<String> getEvictedObjects() {
        return hddCache.getEvictedBatchObjectes();
    }

    /**
     * 
     * @return the evicted objects' cache size
     */
    ArrayList<Integer> getEvictedObjectSizes() {
        return hddCache.getEvictedBatchObjectSizes();
    }
  
    /**
     * Initialize the cache with 'cacheSize' and 'type'.
     * @param cacheSize
     * @param id
     * @param type 
     */
    public Cache(long cacheSize, int id, int type) {

        this.cacheSize = cacheSize;
        this.cacheID = id;
        this.cacheType = type;
        Comparator<ReferencePattern> comparator = new ReferenceComparator();
        int eventListCapacity = Math.min(8000000, (int) cacheSize);
        eventList = new PriorityQueue(eventListCapacity, comparator);
    }
    /*
        switch (this.cacheType) {
            case CacheType.FIFO:
                fifoCache = new FIFOCache((int) cacheSize);
                break;
            case CacheType.LRU:
                lruCache = new LRUCache((int) cacheSize);
                break;
            case CacheType.RANDOM:
                randCache = new RandomCache(cacheSize);
                break;
            case CacheType.ARC:
                arcCache = new ARCCache(cacheSize);
                break;
            case CacheType.LRFU:
                lrfuCache = new LRFUCache((int) cacheSize);
                break;
            case CacheType.LRU_NON_UNIFORM:
                lruNonUniformCache = new LRUNonUniformCache((int) cacheSize);
                break;
            case CacheType.SSD:
                ssdCache = new SSDCache(cacheSize);
                break;
            case CacheType.HDD:
                hddCache = new HDDCache(cacheSize);
                break;
        }
        
        

    }
    */
    
    /**
     * Add an object.
     * @param object
     * @param time
     * @param objSize
     * @return the evicted object if not NULL.
     */
    /*
    public String add(String object, long time, int objSize) {
        String output = "";
        switch (this.cacheType) {
            case CacheType.FIFO:
                output = fifoCache.add(object, time);
                break;
            case CacheType.LRU:
                output = this.lruCache.addLRU(object, time);
                break;
            case CacheType.RANDOM:
                output = randCache.add(object, time);
                break;
            case CacheType.ARC:
                output = this.arcCache.add(object, time, objSize);
                break;
            case CacheType.LRFU:
                output = this.lrfuCache.add(object, time);
                 break;
            case CacheType.LRU_NON_UNIFORM:
                output = lruNonUniformCache.add(object, time, objSize);
                break;
            case CacheType.SSD:
                output = ssdCache.add(object, time, objSize);
                break;
            case CacheType.HDD:
                output = hddCache.add(object, time, objSize);
                break;
        }
       
        if (cacheType == CacheType.SSD && ssdCache.objectSize.size() == cacheSize) {
            cacheFull = true;
        }
        
        if (cacheType == CacheType.ARC){
            cacheFull = arcCache.checkFull(objSize);
        }
        

        if (!output.isEmpty() && cacheType!=CacheType.ARC) {
            cacheFull = true;
        }
        
        return output;
    }
    */
    
    abstract public String add(String object, long time, int objSize);
    
    /**
     * remove object from the cache.
     * @param object
     * 
     */
    abstract public void remove(String object);
    /*{
        switch (this.cacheType) {
            case CacheType.FIFO:
                fifoCache.remove(object);
                break;
            case CacheType.LRU:
                lruCache.remove(object);
                break;
            case CacheType.RANDOM:
                randCache.remove(object);
                break;
            case CacheType.ARC:
                arcCache.remove(object);
                break;
            case CacheType.LRFU:
                lrfuCache.remove(object);
                break;
            case CacheType.LRU_NON_UNIFORM:
                lruNonUniformCache.remove(object);
                break;
            case CacheType.SSD:
                ssdCache.remove(object);
                break;
            case CacheType.HDD:
                hddCache.remove(object);
                break;
          }
    }
*/
    abstract public boolean contains(String object);
    /**
     * Check if the cache contains the object.
     * @param object
     * @return whether the cache contains the object
     */
    /*
    
    public boolean contains(String object) {
        switch (this.cacheType) {
            case CacheType.FIFO:
                return  fifoCache.contains(object);
            case CacheType.LRU:
                return lruCache.containsKey(object);
            case CacheType.RANDOM:
                return randCache.contains(object);
            case CacheType.ARC:
                return arcCache.contains(object);
            case CacheType.LRFU:
                return this.lrfuCache.contains(object);
            case CacheType.LRU_NON_UNIFORM:
                return lruNonUniformCache.contains(object);
            case CacheType.SSD:
                return ssdCache.contains(object);
            case CacheType.HDD:
                return hddCache.contains(object);
                
         }
        return false;
    }
*/
   
    
   /**
    * add a parent for the current cache.
    * @param cache 
    */
    void addParent(Cache cache) {
        this.parent = cache;
    }

    /**
     * add counts for the missing object
     * @param objSize 
     */
    void addMissedCount(int objSize) {
      
        if (this.missRatioOption == 0) {
            totalMissed += 1;
        } else {
            totalMissed += objSize;
        }
    }
    
    /**
     * add totally received object
     * @param objSize 
     */
    void addReceivedObject(int objSize) {
       
        if (this.missRatioOption == 0) {
            totalReceived += 1;
        } else {
            totalReceived += objSize;
        }
    }

   
    /**
     * batch update objects for ssd.
     * @param evictedObjects
     * @param evictedObjectSizes
     * @param timestamp 
     */
    void batchUpdateObjects(ArrayList<String> evictedObjects, ArrayList<Integer> evictedObjectSizes, long timestamp) {
        this.ssdCache.batchUpdating(evictedObjects, evictedObjectSizes, timestamp);
       
        
    }
    
    /**
     * Compute the evicted objects from HDD.
     */
    void calculateEvictedObjects() {
        this.hddCache.processEvictedBatchObjects();
    }

    /**
     * Set the frequency threshold for the batch update.
     * @param batchFrequency 
     */
    void setBatchFrequency(int batchFrequency) {
        this.hddCache.setBatchFrequency(batchFrequency);
    }

   
    /**
     * 
     * @return cache size
     */
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

   
    public ArrayList<Cache> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Cache> children) {
        this.children = children;
    }

   
    public PriorityQueue<ReferencePattern> getEventList() {
        return eventList;
    }

    public void setEventList(PriorityQueue<ReferencePattern> eventList) {
        this.eventList = eventList;
    }

    public int getMissRatioOption() {
        return missRatioOption;
    }

    public void setMissRatioOption(int missRatioOption) {
        this.missRatioOption = missRatioOption;
    }

    /*
    public boolean isCacheFull() {
        return cacheFull;
    }
    */
    abstract public boolean isCacheFull();
    
   

   
}