package caches.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zhang-Jiangwei
 */
public class HDDCache extends LRUCache {

    ArrayList<String> evictedBatchObjectes = new ArrayList<>();
    ArrayList<Integer> evictedBatchObjectSizes = new ArrayList<>();
    HashMap<String, Integer> objectFrequencies = new HashMap<>();
    int batchFrequency = 1;

    public HDDCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);
    }

    @Override
    public String add(String key, long time, int sizeInt) {
        updateFrequency(key);
        return super.add(key, time, sizeInt);
    }

    public void calculateEvictedObjects() {
        evictedBatchObjectSizes.clear();
        evictedBatchObjectes.clear();
        for (Map.Entry<String, Integer> entry : objectFrequencies.entrySet()) {
            if (entry.getValue() >= batchFrequency) {
                evictedBatchObjectes.add(entry.getKey());
                evictedBatchObjectSizes.add(entry.getValue());
            }
        }
        objectFrequencies.clear();
    }

    private void updateFrequency(String object) {
        int count = 0;
        if (objectFrequencies.containsKey(object)) {
            count = objectFrequencies.get(object);
        }
        objectFrequencies.put(object, count + 1);
    }

    @Override
    public boolean contains(String object) {
        updateFrequency(object);
        return super.contains(object);
    }

    public ArrayList<String> getEvictedBatchObjectes() {
        return evictedBatchObjectes;
    }

    public ArrayList<Integer> getEvictedBatchObjectSizes() {
        return evictedBatchObjectSizes;
    }

    public void setBatchFrequency(int batchFrequency) {
        this.batchFrequency = batchFrequency;
    }

}
