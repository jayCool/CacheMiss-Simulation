package caches;



import java.util.ArrayList;
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
public class SSDCache extends LinkedHashMap<String, Long> {

    public HashMap<String, Double> objectSize = new HashMap<String, Double>();
    double cacheSize = 0;
    double curSize = 0;
    Queue<CacheObject> objectPQ;
    int initialSize = 1000;

    public SSDCache(long cacheSize) {
        this.cacheSize = cacheSize;
        int queueCapacity = Math.min(8000000, (int)cacheSize);
        objectPQ = new PriorityQueue<>(queueCapacity, objectComparator);
    }

    public static Comparator<CacheObject> objectComparator = new Comparator<CacheObject>() {
        @Override
        public int compare(CacheObject o1, CacheObject o2) {
            return (int) (o1.timestamp - o2.timestamp);
        }
    };

    @Override
    public String toString() {
        return objectSize.toString();
    }

    public String add(String key, Long time, int sizeInt) {
        double size = 1.0 * sizeInt;
        if (size > cacheSize) {
            return key + " " + size;
        }
        String result = key + " " + size;

        CacheObject newObj = new CacheObject(key, time);
        if (objectSize.containsKey(key)) {
            objectPQ.remove(newObj);
            double oldSize = this.objectSize.get(key);
            curSize -= oldSize;
            objectPQ.add(newObj);
            objectSize.put(key, size);
            curSize += size;
            result = "";
        }
        return result;
    }

    public String addForBatchUpdating(String key, Long time, int sizeInt) {
        double size = 1.0 * sizeInt;
        if (size > cacheSize) {
            return key + " " + size;
        }
        String result = "";

        CacheObject newObj = new CacheObject(key, time);
        if (objectSize.containsKey(key)) {
            objectPQ.remove(newObj);
            double oldSize = this.objectSize.get(key);
            curSize -= oldSize;
        }

        while (size + curSize > cacheSize) {
            if (this.objectPQ.size() == 0) {
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

    public void remove(String key) {
        CacheObject temp;
        if (objectSize.containsKey(key)) {
            temp = new CacheObject(key, 0);
            objectPQ.remove(temp);
            curSize -= objectSize.get(key);
            objectSize.remove(key);
        }
    }

    public void batchUpdating(ArrayList<String> objects, ArrayList<Integer> sizes, long timestamp) {
        //System.err.println("objects: " + objects);
        //System.err.println("sizes: " + sizes);
        for (int i = 0; i < sizes.size(); i++) {
            String object = objects.get(i);
            int size = sizes.get(i);
            addForBatchUpdating(object, timestamp, size);
        }
    }

    public boolean contains(String object) {
        return objectSize.containsKey(object);
    }

    public boolean isFull() {
    if (objectSize.size() == cacheSize) {
           return true;
        }
    return false;
    }

}
