package caches.implementations;

import caches.Cache;
import java.util.HashMap;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * This is an efficient implementation of LRU Cache. 
 *
 * @author Zhang-Jiangwei
 */
public class LRUCache extends Cache {

    HashMap<String, CacheObject> objectMap = new HashMap<>();
    int currentContentSize = 0;
    CacheObject head = null;
    CacheObject tail = null;
    
    /**
     * constructor
     *
     * @param cacheSize
     * @param id
     * @param type
     */
    public LRUCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);
    }

    public int getContentSize(){
        return this.currentContentSize;
    }
    
    @Override
    public String toString() {
        return "Cache ID: " + this.getCacheID() + "   Cache Size: " + this.getCacheSize() + "  Content Size: " +  currentContentSize;
    }

    @Override
    public String add(String object, long time, int objSize) {

        //if the current object size is larger than cache size,
        //do not take this into cache
        if (objSize > this.getCacheSize()) {
            return object + " " + objSize;
        }

        String result = "";

        if (objectMap.containsKey(object)) {
            CacheObject cacheObject = objectMap.get(object);
            cacheObject.objectSize = objSize;
            result = remove(cacheObject);
            setHead(cacheObject);
            return result;
        }

        while (objSize + currentContentSize > this.getCacheSize()) {
            objectMap.remove(tail.object);
            result = remove(tail);
        }

        CacheObject cacheObject = new CacheObject(object, time, objSize);
        this.objectMap.put(object, cacheObject);
        setHead(cacheObject);
        
        
        return result;
    }

    /**
     * remove an object node
     * @param object
     * @return removed object and size
     */
    private String remove(CacheObject object) {
        this.currentContentSize -= object.objectSize;
        String result = object.object + " " + object.objectSize;

        if (object.prev != null) {
            object.prev.next = object.next;
        } else {
            head = object.next;
        }

        if (object.next != null) {
            object.next.prev = object.prev;
        } else {
            tail = object.prev;
        }
        return result;
    }

    /**
     * add an object node
     * @param t 
     */
    private void setHead(CacheObject t) {
        this.currentContentSize += t.objectSize;
        if (head != null) {
            head.prev = t;
        }

        t.next = head;
        t.prev = null;
        head = t;

        if (tail == null) {
            tail = head;
        }
    }

    /**
     *
     * @return oldest object
     */
    public String removeOldest() {
        this.objectMap.remove(tail.object);

        return remove(tail);
    }

    @Override
    public void remove(String object) {
        if (objectMap.containsKey(object)) {
            CacheObject cacheObject = objectMap.get(object);
            this.objectMap.remove(object);
            remove(cacheObject);
        }
    }

    @Override
    public boolean contains(String object) {
        if (!objectMap.containsKey(object)){
            return false;
        }
        
        CacheObject cacheObject = objectMap.get(object);
        remove(cacheObject);
        setHead(cacheObject);
        
        return true;
    }

    @Override
    public boolean isCacheFull() {
        return currentContentSize >= this.getCacheSize();
    }

    public boolean isEmpty(){
        return objectMap.isEmpty();
    }
    
}
