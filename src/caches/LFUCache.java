package caches;


import java.util.HashMap;
import java.util.LinkedHashSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jw
 */
public class LFUCache {

   HashMap<String, Integer> vals;
    HashMap<String, Integer> counts;
    HashMap<Integer, LinkedHashSet<String>> lists;
    int cap;
    int min = -1;
    
    public LFUCache(int capacity) {
        cap = capacity;
        vals = new HashMap<>();
        counts = new HashMap<>();
        lists = new HashMap<>();
        lists.put(1, new LinkedHashSet<String>());
    }
    
    public int get(String key) {
        if(!vals.containsKey(key))
            return -1;
        int count = counts.get(key);
        counts.put(key, count+1);
        lists.get(count).remove(key);
        if(count==min && lists.get(count).size()==0)
            min++;
        if(!lists.containsKey(count+1))
            lists.put(count+1, new LinkedHashSet<String>());
        lists.get(count+1).add(key);
        return vals.get(key);
    }
    
    
    
    public String set(String key, int value) {
        if(cap<=0)
            return "";
        if(vals.containsKey(key)) {
            vals.put(key, value);
            get(key);
            return "";
        } 
        
        if(vals.size() >= cap) {
            String evit = lists.get(min).iterator().next();
            lists.get(min).remove(evit);
            vals.remove(evit);
            return evit;
        }
        
        vals.put(key, value);
        counts.put(key, 1);
        min = 1;
        lists.get(1).add(key);
        return "";
    }

    public String add(String object) {
        return set(object, 1);
    }

    boolean contains(String object) {
        return vals.containsKey(object);
    }
    
}
