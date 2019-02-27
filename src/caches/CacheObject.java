package caches;

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
    String key;
    long timestamp;

    CacheObject(String key, long time) {
        this.key = key;
        this.timestamp = time;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CacheObject){
            CacheObject cacheObject = (CacheObject) obj;
            return this.key.equals(cacheObject.key);
        } 
        return false;
    }


    @Override
    public String toString() {
        return "key: " + key + "; time: " + timestamp;
    }

}
