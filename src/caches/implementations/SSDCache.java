package caches.implementations;


import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zhang-Jiangwei
 */
public class SSDCache extends LRUCache {

    double curSize = 0;
    
    public SSDCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);
    }


    public void batchUpdateObjects(ArrayList<String> objects, ArrayList<Integer> sizes, long timestamp) {
        for (int i = 0; i < sizes.size(); i++) {
            String object = objects.get(i);
            int size = sizes.get(i);
            this.add(object, timestamp, size);
        }
    }

    
  
}
