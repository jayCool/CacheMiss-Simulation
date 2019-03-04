package caches.implementations;

import caches.Cache;
import java.util.LinkedList;
import java.util.Queue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JIANGWEI
 */
public class FIFOCache extends Cache{
    Queue fifoqueue = new LinkedList<String>();
    long cacheSize;

    public FIFOCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);
        this.cacheSize = cacheSize;
    }
    
    public String add(String element, long time) {
        String result;
        if (fifoqueue.contains(element)) {
            return "";
        }

        if (fifoqueue.size() < cacheSize) {
            fifoqueue.offer(element);
            return "";
        }

        result = (String) fifoqueue.poll();
        fifoqueue.offer(element);
        return result + " 1";
    }

    @Override
    public boolean contains(String object) {
    return this.fifoqueue.contains(object);
    }

    @Override
    public void remove(String object) {
        this.removeFIFO(object);
    }
    
    public void removeFIFO(String object) {
        this.fifoqueue.remove(object);
    }

    @Override
    public String toString() {
        return "cacheSize: " + this.cacheSize + "   contentSize: " + fifoqueue.size();
    }

    @Override
    public String add(String object, long time, int objSize) {
        return this.add(object, time);
    }

    @Override
    public boolean isCacheFull() {
        return fifoqueue.size() >= cacheSize;
    }

    
}