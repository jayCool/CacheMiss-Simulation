
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
class LRUNonUniformCache extends LinkedHashMap<String, Long> {

    HashMap<String, Integer> objectSize = new HashMap<String, Integer>();
    double cacheSize=0;
    double curSize=0;
    Queue<CacheObject> objectPQ;
    int initialSize = 1000;
    public LRUNonUniformCache(int cacheSize){
        this.cacheSize = cacheSize;
        objectPQ = new PriorityQueue<>(cacheSize, objectComparator);
    }

    @Override
    public String toString() {
        //return "LRUNonUniformCache{" + "objectSize=" + objectSize + ", cacheSize=" + cacheSize + ", curSize=" + curSize + ", objectPQ=" + objectPQ + ", initialSize=" + initialSize + '}';
        return "" + curSize;
    }
    
    
    
    public static Comparator<CacheObject> objectComparator = new Comparator<CacheObject>(){
        @Override
        public int compare(CacheObject o1, CacheObject o2) {
            return (int)(o1.timestamp - o2.timestamp);
        }
    };
    
    public String removeOldest(){
         String tempkey = this.objectPQ.poll().key;
            int tempSize = objectSize.get(tempkey);
            objectSize.remove(tempkey);
            curSize -= tempSize;
            return tempkey;
    }
    public String add(String key, Long time, int size){
        if (size > cacheSize){
            return key + " " + size;
        }
        String result = "";
        
        CacheObject newObj = new CacheObject(key, time);
         if (objectSize.containsKey(key)){
            objectPQ.remove(newObj);
            int oldSize = this.objectSize.get(key);
            curSize -= oldSize;
        }
         
        while (size + curSize > cacheSize){
            if (this.objectPQ.size()==0){
                System.err.println("objectSize: " + objectSize.size() + " " + cacheSize + " " + curSize + " " + size);
            }
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
    
    public void remove(String key){
        CacheObject temp;
        if (objectSize.containsKey(key)){
            temp = new CacheObject(key, objectSize.get(key));
            objectPQ.remove(temp);
            curSize -= objectSize.get(key);
            objectSize.remove(key);
        }
    }
    
    
}
