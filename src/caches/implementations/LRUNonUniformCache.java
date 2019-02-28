package caches.implementations;


import caches.Cache;
import java.util.Comparator;
import java.util.HashMap;

import java.util.PriorityQueue;
import java.util.Queue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zhang-Jiangwei
 */
public class LRUNonUniformCache extends Cache{

    HashMap<String, Integer> objectSize = new HashMap<String, Integer>();
    double curSize=0;
    Queue<CacheObject> objectPQ;
    
    public LRUNonUniformCache(long cacheSize, int id, int type ){
        super(cacheSize, id, type);
        objectPQ = new PriorityQueue<>((int)cacheSize, objectComparator);
    }

    @Override
    public String toString() {
        return "LRU_Non_Uniform_Cache:" + curSize;
    }
    
    
    
    public static Comparator<CacheObject> objectComparator = new Comparator<CacheObject>(){
        @Override
        public int compare(CacheObject o1, CacheObject o2) {
            return (int)(o1.timestamp - o2.timestamp);
        }
    };
    
    

    @Override
    public String add(String key, long time, int size){
        if (size > this.getCacheSize()){
            return key + " " + size;
        }
        String result = "";
        
        CacheObject newObj = new CacheObject(key, time);
         if (objectSize.containsKey(key)){
            objectPQ.remove(newObj);
            int oldSize = this.objectSize.get(key);
            curSize -= oldSize;
        }
         
        while (size + curSize > this.getCacheSize()){
            String tempkey = this.objectPQ.poll().key;
            int tempSize = objectSize.get(tempkey);
            objectSize.remove(tempkey);
            result = tempkey + " " + tempSize;
            curSize -= tempSize;
        }
        
        objectPQ.add(newObj);
        objectSize.put(key, size);
        curSize += size;
        
        return result;
    }
    
    /**
     * 
     * @return oldest object
     */
    public String removeOldest(){
         String tempkey = this.objectPQ.poll().key;
            int tempSize = objectSize.get(tempkey);
            objectSize.remove(tempkey);
            curSize -= tempSize;
            return tempkey;
    }
    
    @Override
    public void remove(String key){
        CacheObject temp;
        if (objectSize.containsKey(key)){
            temp = new CacheObject(key, objectSize.get(key));
            objectPQ.remove(temp);
            curSize -= objectSize.get(key);
            objectSize.remove(key);
        }
    }

    @Override
    public boolean contains(String object) {
        return this.objectSize.containsKey(object);
    }



    @Override
    public boolean isCacheFull() {
        return curSize >= this.getCacheSize();
    }
    
    
}
