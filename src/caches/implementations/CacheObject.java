package caches.implementations;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Zhang-Jiangwei
 */
public class CacheObject {
    public String object;
    public long timestamp;
    public int objectSize;
    CacheObject prev;
    CacheObject next;

    public CacheObject(String object, long time, int objectSize) {
        this.object = object;
        this.timestamp = time;
        this.objectSize = objectSize;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CacheObject){
            CacheObject cacheObject = (CacheObject) obj;
            return this.object.equals(cacheObject.object);
        } 
        return false;
    }


    @Override
    public String toString() {
        return "key: " + object + "; time: " + timestamp;
    }

}
