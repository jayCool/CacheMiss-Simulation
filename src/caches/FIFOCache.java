package caches;

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
public class FIFOCache {
    Queue fifoqueue = new LinkedList<String>();
    long cacheSize;

    public FIFOCache(int cachesize) {
        this.cacheSize = cacheSize;
    }
    
    public String add(String element, long time) {
        String result = "";
        if (fifoqueue.contains(element)) {
            return "";
        }

        if (fifoqueue.size() < cacheSize) {
            fifoqueue.offer(element);
            return "";
        }

        result = (String) fifoqueue.poll();
        fifoqueue.offer(element);
        return result;
    }

    public boolean contains(String object) {
    return this.fifoqueue.contains(object);
            }

    public void remove(String object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void removeFIFO(String object) {
        this.fifoqueue.remove(object);
    }

    
}