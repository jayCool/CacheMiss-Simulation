package caches.implementations;

import caches.Cache;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This implements a random cache with unique object size.
 *
 * @author JIANGWEI
 */
public class RandomCache extends Cache {

    Random rand = new Random();
    ArrayList<String> objectList = new ArrayList<>();
    HashSet<String> objectSet = new HashSet<>();

    public RandomCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);

    }

    public String add(String object, long time) {
        String result = "";
        if (objectSet.contains(object)) {
            return "";
        }
        if (objectSet.size() < this.getCacheSize()) {
            objectList.add(object);
            objectSet.add(object);
            return "";
        }

        int index = rand.nextInt((int) objectList.size());
        result = objectList.get(index);

        objectList.remove(index);
        objectSet.remove(result);
        objectSet.add(object);
        return result;
    }

    @Override
    public boolean contains(String object) {
        return this.objectSet.contains(object);
    }

    @Override
    public void remove(String object) {
        objectSet.remove(object);

        int index = 0;
        for (int i = 0; i < objectList.size(); i++) {
            if (objectList.get(i).equals(object)) {
                index = i;
            }
        }
        objectList.remove(index);
    }

    @Override
    public String toString() {
        return "Cache ID: " + this.getCacheID() + " Cache size: " + this.getCacheSize() + "  Content size: " + this.objectSet.size();
    }

    @Override
    public String add(String object, long time, int objSize) {
        return this.add(object, time);
    }

    @Override
    public boolean isCacheFull() {
        return objectSet.size() >= this.getCacheID();
    }

}
