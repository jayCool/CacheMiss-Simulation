package caches;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author JIANGWEI
 */
public class LRFUCache {

    HashMap<String, Double> pq = new HashMap<>();

    double lamba = 0.0;
    int cacheSize = 0;

    public LRFUCache(int cacheSize) {
        pq = new HashMap<String, Double>(cacheSize);
        this.cacheSize = cacheSize;
    }

    public String refresh(String curPage) {
        String result = "";
        double min = 100;
        double gap = lamba * (1);
        for (Entry<String, Double> entry : pq.entrySet()) {
            double val = entry.getValue();
            entry.setValue(val * Math.pow(0.5, gap));
            if (!curPage.equals(entry.getKey())) {
                if (min > val) {
                    result = entry.getKey();
                    min = val;
                }
            }
        }
        return result;
    }

    public boolean contains(String object) {
        return pq.containsKey(object);
    }

    public String add(String object, long time) {
        String minimal = refresh(object);

        if (!this.pq.containsKey(object)) {
            if (pq.size() == this.cacheSize) {
                pq.remove(minimal);
            }
            pq.put(object, 1.0);
        } else {
            pq.put(object, 1.0 + pq.get(object));
        }

        return minimal;
    }

    @Override
    public String toString() {
        String result = "";
        for (String key: pq.keySet()){
            result += key + "\t";
        }
        return result;
    }
    
    
    
        
    void setlamba(double lamda) {
        this.lamba = lamda;}

    public void remove(String object) {
        this.pq.remove(object);
    }
   

}