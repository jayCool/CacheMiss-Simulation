package caches.implementations;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import caches.Cache;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This LRFU Only support unique size object
 *
 * @author JIANGWEI
 */
public class LRFUCache extends Cache {

    HashMap<String, Double> objectToScore = new HashMap<>();
    double lamba = 0.0;

    public LRFUCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);
        objectToScore = new HashMap<String, Double>(cacheSize);
    }

    public String refresh(String curPage) {
        String result = "";
        double min = 100;
        double gap = lamba * (1);
        for (Entry<String, Double> entry : objectToScore.entrySet()) {
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

    @Override
    public boolean contains(String object) {
        if (objectToScore.containsKey(object)) {
            refresh(object);
            return true;
        }
        return false;
    }

    public String add(String object, long time) {
        String minimal = refresh(object);

        if (!this.objectToScore.containsKey(object)) {
            if (objectToScore.size() == this.getCacheSize()) {
                objectToScore.remove(minimal);
            }
            objectToScore.put(object, 1.0);
        } else {
            objectToScore.put(object, 1.0 + objectToScore.get(object));
        }

        return minimal;
    }

    @Override
    public String toString() {
        return "Cache ID: " + this.getCacheID() + " Cache Size: " + this.getCacheSize() + " Cache Content Size: " + objectToScore.size();
    }

    void setlamba(double lamda) {
        this.lamba = lamda;
    }

    @Override
    public void remove(String object) {
        this.objectToScore.remove(object);
    }

    @Override
    public String add(String object, long time, int objSize) {
        String output = this.add(object, time);
        if (output.equals("")) {
            return output;
        }

        return output + " 1";
    }

    @Override
    public boolean isCacheFull() {
        return objectToScore.size() == this.getCacheSize();
    }

}
