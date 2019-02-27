/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zhang Jiangwei
 */
public class Cache1 {

    Queue fifoqueue = new LinkedList<String>();
    HashSet<String> randomCache = new HashSet<>();
    String[] randomKeys;
    Random rand = new Random();
    long cacheSize;
    int cacheID;
    boolean aval = true;

    String lruEldest;
    int cacheType; // 1 FIFO, 2 LRU, 3 RANDOM, 4 ARC, 7, SSD, 8 HDD
    int cacheStrategy=1; //1 LCE, 2 LCD, 3 MCD
    double totalReceived = 0;
    double totalMissed = 0;
    LRUCache t1, t2;
    LRUCache b1, b2;
    LRFUCacheOld lrfu;
    LRUCache lruCache;
    LRUNonUniformCache LRUNUC;
    IQIYISSDCache iqiyissdcache;
    IQIYIHDDCache iqiyihddcache;
    //double lamba;
    private ArrayList<Cache> children = new ArrayList<>();
    public Cache parent;

    public Cache getParent() {
        return parent;
    }
    PriorityQueue<ReferencePattern> eventList;
    int p = 0;
    int missRatioOption = 0; // 0 for by request, 1 by object size
    boolean cacheFull = false;
    boolean history = false;
    PrintWriter pw;
    String traceFile="";
    
    Cache1() {
    }

    public void addChild(Cache child) {
        children.add(child);
    }

    //LRFUCache lrfu ;
    @Override
    public String toString() {

        switch (this.cacheType) {
            case 1:
                return "FIFOCache: " + this.fifoqueue.toString();
            case 2:
                return "LRUCache: " + lruCache.keySet();
            case 3:
                return "RANDOMCache: " + this.randomCache.toString();
            case 4:
                return "ARC-Cache: " + this.t1.toString() + "l2; " + t2.toString();
            case 5:
                return this.lrfu.toString().trim();
            case 6:
                return "LRUDiff Size: " + LRUNUC.size();
            case 7:
                return "IQIYI-SSD Size: " + this.iqiyissdcache.toString();
            case 8:
                return "IQIYI-HDD Size: " + this.iqiyihddcache.toString();
        }

        return "Cache{" + "fifoqueue=" + fifoqueue + ", randomCache=" + randomCache + "type: " + this.cacheType + "}";
    }
    
    ArrayList<String> getEvictedObjects(){
        return  iqiyihddcache.getEvictedBatchObjectes();
    }
    
    ArrayList<Integer> getEvictedObjectSizes(){
        return iqiyihddcache.getEvictedBatchObjectSizes();
    }
    
    public void setLamda(double lamda){
    this.lrfu.setlamba(lamda);
    }
    public Cache1(long cacheSize, int id, int type) {

        this.cacheSize = cacheSize;
        this.cacheID = id;
        this.cacheType = type;
        Comparator<ReferencePattern> comparator = new ReferenceComparator();
        int eventListCapacity = Math.min(8000000, (int)cacheSize);
        eventList = new PriorityQueue(eventListCapacity, comparator);

        switch (this.cacheType) {
            case 1:
                break;
            case 2:
                lruCache = new LRUCache((int)cacheSize);
                break;
            case 3:
                randomKeys = new String[(int)this.cacheSize];
                for (int i = 0; i < randomKeys.length; i++) {
                    randomKeys[i] = "";
                }
                break;
            case 4:
                t1 = new LRUCache((int)cacheSize);
                t2 = new LRUCache((int)cacheSize);
                b1 = new LRUCache((int)cacheSize);
                b2 = new LRUCache((int)cacheSize);
                break;
            case 5:
                lrfu = new LRFUCacheOld((int)cacheSize);
                break;
            case 6:
                LRUNUC = new LRUNonUniformCache((int)cacheSize);
                break;
            case 7:
                iqiyissdcache = new IQIYISSDCache(cacheSize);
                break;
            case 8:
                iqiyihddcache = new IQIYIHDDCache(cacheSize);
                break;
        }
     
    }

    public String add(String object, long time, int objSize) {
        String output = "";
       // System.out.println("adding item!");
       // System.out.println(this.toString());
        switch (this.cacheType) {
            case 1:
                output = this.addFIFO(object, time);
                break;
            case 2:
                output = this.lruCache.addLRU(object, time);
                this.lruEldest = "";
                break;
            case 6:
                output = LRUNUC.add(object, time, objSize);
                break;
            case 3:
                output = this.addRandom(object, time);
                break;
            case 4:
                if (this.t1.containsKey(object)) {
                    this.t1.remove(object);
                    this.t2.addLRU(object, time);
                } else if (this.t2.containsKey(object)) {
                    this.t2.remove(object);
                    this.t2.addLRU(object, time);
                } else if (b1.containsKey(object)) {
                    int d = calDelta(b1, b2);
                    p = Math.min(p + d, (int)this.cacheSize);
                    output = replace(object, p, time);
                    b1.remove(object);
                    t2.addLRU(object, time);
                } else if (b2.containsKey(object)) {
                    int d = calDelta(b2, b1);
                    p = Math.max(p - d, 0);
                    output = replace(object, p, time);
                    b2.remove(object);
                    t2.addLRU(object, time);
                } else {
                    if (this.t1.size() + b1.size() == this.cacheSize) {
                        if (t1.size() < this.cacheSize) {
                            String old = b1.entrySet().iterator().next().getKey();
                            b1.remove(old);
                            output = replace(object, p, time);
                        } else {
                            output = t1.entrySet().iterator().next().getKey();
                            t1.remove(output);
                        }
                    } else if (this.t1.size() + b1.size() < this.cacheSize) {
                        if (t1.size() + t2.size() + b1.size() + b2.size() >= this.cacheSize) {
                            if (t1.size() + t2.size() + b1.size() + b2.size() == 2 * this.cacheSize) {
                                String old = b2.entrySet().iterator().next().getKey();
                                b2.remove(old);
                            }
                            replace(object, p, time);
                        }

                    }
                    String temp = "";

                    temp = t1.addLRU(object, time);
                    if (output != "") {
                        output = temp;
                    }
                }
                break;

            case 5:
                output = this.lrfu.addLRFU(object, time);
                //output = this.lrfu.addLRFUParallel(object, time);
                break;
            case 7:
                output = iqiyissdcache.add(object, time, objSize);
                break;
            case 8:
                output = iqiyihddcache.add(object, time, objSize);
                break;

        }
        if (history){
            pw.println(object);
            pw.println(this.toString());
            pw.flush();
        }
        if (cacheType==7 && iqiyissdcache.objectSize.size()==cacheSize){
            cacheFull = true;
        }
        if (!output.isEmpty()){
            //System.out.println(output);
            cacheFull = true;
        }
        return output;
    }

    public String remove(String object, long time) {
        String output = "";
        switch (this.cacheType) {
            case 1:
                this.removeFIFO(object, time);
                break;
            case 2:
                lruCache.remove(object);
                break;
            case 3:
                this.removeRandom(object, time);
                break;
            case 6:
                LRUNUC.remove(object);
                break;
            case 7:
                iqiyissdcache.remove(object);
                break;
            case 8:
                iqiyihddcache.remove(object);
                break;
        }
        return output;
    }

    public boolean contains(String object) {
        switch (this.cacheType) {
            case 1:
                return this.fifoqueue.contains(object);
            case 2:
                return lruCache.containsKey(object);
            case 3:
                return this.randomCache.contains(object);
            case 4:
                return t1.containsKey(object) || t2.containsKey(object);
            case 5:
                return this.lrfu.contains(object);
            case 6:
                return this.LRUNUC.objectSize.containsKey(object);
            case 7:
                return iqiyissdcache.objectSize.containsKey(object);
            case 8:
                return iqiyihddcache.objectSize.containsKey(object);
        }
        return false;
    }

    public String addFIFO(String element, long time) {
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

    public String addRandom(String element, long time) {
        String result = "";
        if (randomCache.contains(element)) {
            return "";
        }
        if (randomCache.size() < cacheSize) {
            randomKeys[randomCache.size()] = element;
            randomCache.add(element);
            return "";
        }

        int index = rand.nextInt((int)cacheSize);
        result = randomKeys[index];
        randomCache.remove(randomKeys[index]);
        randomCache.add(element);
        randomKeys[index] = element;
        return result;
    }

    private void removeFIFO(String object, long time) {
        this.fifoqueue.remove(object);
    }

    private void removeRandom(String object, long time) {
        randomCache.remove(object);

        int index = 0;
        for (int i = 0; i < randomKeys.length; i++) {
            if (randomKeys[i].equals(object)) {
                index = i;
            }
        }
        randomKeys[index] = "";
    }

    private int calDelta(LRUCache b1, LRUCache b2) {
        if (b1.size() >= b2.size()) {
            return 1;
        }
        return b2.size() / b1.size();
    }

    private String replace(String object, int p, long time) {
        String out = "";
        if ((!t1.isEmpty()) && (t1.size() > p || (b2.containsKey(object) && t1.size() == p))) {
            String old = t1.entrySet().iterator().next().getKey();
            b1.addLRU(old, time);
            t1.remove(old);
            out = old;
        } else if (!t2.isEmpty()) {
            String old = t2.entrySet().iterator().next().getKey();
            t2.remove(old);
            b2.addLRU(old, time);
            out = old;
        }
        return out;
    }

    void addParent(Cache cache) {
        this.parent = cache;
    }

    void addMissedCount(int objSize) {
        if (!cacheFull){
            return;
        }
        if (this.missRatioOption == 0){
            totalMissed += 1;
        } else {
            totalMissed += objSize;
        }
    }

    void addReceivedObject(int objSize) {
       if (!cacheFull){
            return;
        }
        if (this.missRatioOption == 0){
            totalReceived += 1;
        } else {
            totalReceived += objSize;
        } }

    void setHistory(boolean history, String requestAddress) {
        if (history){
            this.history = history;
            try {
                pw = new PrintWriter(new File(requestAddress + "-" + this.lrfu.lamba + "-" + cacheSize + ".history"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void batchUpdateObjects(ArrayList<String> evictedObjects, ArrayList<Integer> evictedObjectSizes, long timestamp) {
        this.iqiyissdcache.batchUpdating(evictedObjects, evictedObjectSizes, timestamp);
        if (iqiyissdcache.objectSize.size() == iqiyissdcache.cacheSize){
            this.cacheFull = true;
        }
    }

    void calculateEvictedObjects() {
        //System.out.print(this.toString());
        this.iqiyihddcache.processEvictedBatchObjects();
    }

    void setBatchFrequency(int batchFrequency) {
        this.iqiyihddcache.setBatchFrequency(batchFrequency);
    }

    void writeToFile(String outputDir) {
        try {
            PrintWriter pw = new PrintWriter(new File(outputDir + File.separator + cacheID + ".content"));
            switch (this.cacheType) {
                case 7:
                    for (Entry<String, Double> entry: iqiyissdcache.objectSize.entrySet()){
                        pw.println(entry.getKey() + " " + entry.getValue());
                    }
                    break;
                case 8:
                    for (Entry<String, Double> entry: iqiyihddcache.objectSize.entrySet()){
                        pw.println(entry.getKey() + " " + entry.getValue());
                    }
                    break;
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
