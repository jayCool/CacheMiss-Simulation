
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
class IQIYIHDDCache extends LinkedHashMap<String, Long> {

    HashMap<String, Double> objectSize = new HashMap<String, Double>();
    double cacheSize=0;
    double curSize=0;
    Queue<CacheObject> objectPQ;
    int initialSize = 1000;
    
    ArrayList<String> evictedBatchObjectes = new ArrayList<>();

    public ArrayList<String> getEvictedBatchObjectes() {
        return evictedBatchObjectes;
    }

    public ArrayList<Integer> getEvictedBatchObjectSizes() {
        return evictedBatchObjectSizes;
    }
    ArrayList<Integer> evictedBatchObjectSizes = new ArrayList<>();
    HashMap<String, Integer> objectFrequencies = new HashMap<>();
    int batchFrequency = 1;

    public void setBatchFrequency(int batchFrequency) {
        this.batchFrequency = batchFrequency;
    }
    
    
    public IQIYIHDDCache(long cacheSize){
        this.cacheSize = cacheSize;
        int queueCapacity = Math.min(8000000, (int)cacheSize);
        objectPQ = new PriorityQueue<>(queueCapacity, objectComparator);
    }
    
    public static Comparator<CacheObject> objectComparator = new Comparator<CacheObject>(){
        @Override
        public int compare(CacheObject o1, CacheObject o2) {
            return (int)(o1.timestamp - o2.timestamp);
        }
    };
    
    public String add(String key, Long time, int sizeInt){
        double size = 1.0 * sizeInt ;
        updateFrequency(key);
        if (size > cacheSize){
            return key + " " + size;
        }
        String result = "";
        
        CacheObject newObj = new CacheObject(key, time);
         if (objectSize.containsKey(key)){
            objectPQ.remove(newObj);
            double oldSize = this.objectSize.get(key);
            curSize -= oldSize;
        }
         
        while (size + curSize > cacheSize){
            if (this.objectPQ.size()==0){
                System.err.println("objectSize: " + objectSize.size() + " " + cacheSize + " " + curSize + " " + size);
            }
            String tempkey = this.objectPQ.poll().key;
            double tempSize = objectSize.get(tempkey);
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
            temp = new CacheObject(key, 0);
            objectPQ.remove(temp);
            curSize -= objectSize.get(key);
            objectSize.remove(key);
        }
    }
    
    
    public void processEvictedBatchObjects(){
        evictedBatchObjectSizes.clear();
        evictedBatchObjectes.clear();
        for (Map.Entry<String, Integer> entry: objectFrequencies.entrySet()){
            if (entry.getValue() >= batchFrequency){
                evictedBatchObjectes.add(entry.getKey());
                evictedBatchObjectSizes.add(entry.getValue());
            }
        }
        objectFrequencies.clear();
    }

    @Override
    public String toString() {
        return objectSize.toString() + " ==== " + objectFrequencies.toString();
    }
    
    

    private void updateFrequency(String object) {
        int count = 0;
        if (objectFrequencies.containsKey(object)){
            count = objectFrequencies.get(object);
        }
        objectFrequencies.put(object, count+1);
    }
    
    
    
    
   
    
    
   
    

   
}
