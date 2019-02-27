package caches;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author workshop
 */
public class LRUCache extends LinkedHashMap<String, Long> {

    int cacheSize;
    String lruEldest;

    @Override
    public String toString() {
        return " " + this.keySet();
    }

    public LRUCache(int cacheSize) {
        super(cacheSize, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    public boolean contains(String object) {
        return this.containsValue(object);
    }

    public String addLRU(String object, long time) {
        //if (this.size())
        this.put(object, time);

        String result = this.lruEldest;
        this.lruEldest = "";
        return result;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {

        if (size() > this.cacheSize) {
            lruEldest = eldest.getKey();
        } else {
            lruEldest = "";
        }
        return size() > this.cacheSize;
    }

}
