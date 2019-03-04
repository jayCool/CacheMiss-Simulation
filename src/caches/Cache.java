package caches;

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

public abstract class Cache {
   
   
    int cacheSize;
    int cacheID;
    int cacheType; // 1 FIFO, 2 LRU, 3 RANDOM, 4 ARCCache, 7, SSD, 8 HDD
    int cacheStrategy = 1; //1 LCE, 2 LCD, 3 MCD
    double totalReceived = 0;
    double totalMissed = 0;
    int missRatioOption = 0; // 0 for by request, 1 by object size
    //boolean cacheFull = false;
    
    
   
    
    private ArrayList<Cache> children = new ArrayList<>();
    public Cache parent;
    PriorityQueue<ReferencePattern> eventList;
    
   
    /**
     * Get Parent Cache
     * @return parent cache
     */
   public  Cache getParent() {
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
     * Initialize the cache with 'cacheSize' and 'type'.
     * @param cacheSize
     * @param id
     * @param type 
     */
    public Cache(int cacheSize, int id, int type) {

        this.cacheSize = cacheSize;
        this.cacheID = id;
        this.cacheType = type;
        Comparator<ReferencePattern> comparator = new ReferenceComparator();
        int eventListCapacity = Math.min(8000000, (int) cacheSize);
        eventList = new PriorityQueue(eventListCapacity, comparator);
    }

    
    /**
     * 
     * @param object
     * @param time
     * @param objSize
     * @return object<space>objectSize 
     */
    abstract public String add(String object, long time, int objSize);
    
    /**
     * remove object from the cache.
     * @param object
     * 
     */
    abstract public void remove(String object);
   
    /**
     * 
     * @param object
     * @return whether the object is contained in the cache
     */
    abstract public boolean contains(String object);
  
    /**
     * 
     * @return whether the cache is full. 
     */ 
    abstract public boolean isCacheFull();
    
   
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
     * 
     * @return cache size
     */
    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
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

   

   
}