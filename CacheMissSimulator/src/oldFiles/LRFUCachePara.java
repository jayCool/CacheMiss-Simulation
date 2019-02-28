/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JIANGWEI
 */
public class LRFUCachePara {

    HashMap<String, BigDecimal> pq = new HashMap<>();
    ParaLRFU[] parallelPQ;
    HashMap<String, Long> lastTouched = new HashMap<>();
    TreeMap<BigDecimal, String> treeMap = new TreeMap<>();
    double lamba = 0.0;
    int cacheSize = 0;
    int leastIndex = 0;
    ArrayList<Entry<String, BigDecimal>> sortedList;
    int removedSize = 0;
    HashSet<String> updatedObject = new HashSet<>();
    HashMap<String, Integer> objectIndex = new HashMap<>();
    int pIndex = 0;
    int numOfMachine = 10;
    BigDecimal threshold = BigDecimal.ONE;
    BigDecimal basic;
    double ratio = 1;

    LRFUCachePara(int cacheSize) {
        pq = new HashMap<String, BigDecimal>(cacheSize);
        this.cacheSize = cacheSize;
        parallelPQ = new ParaLRFU[numOfMachine];
        sortedList = new ArrayList<>(cacheSize);

    }

    public String refresh(String curPage, boolean searchMin) {
        String result = "";
        BigDecimal min = new BigDecimal(100);
        BigDecimal gapBig = new BigDecimal(Math.pow(0.5, lamba));

        Iterator<Entry<String, BigDecimal>> iter = pq.entrySet().iterator();
        boolean reOrder = false;

        if (searchMin && updatedObject.size() >= 0.6 * sortedList.size()) {
            sortedList.clear();
            removedSize = 0;
            updatedObject.clear();
            reOrder = true;
            threshold = BigDecimal.ONE;
        }

        while (iter.hasNext()) {
            Entry<String, BigDecimal> entry = iter.next();
            BigDecimal val = entry.getValue();
            entry.setValue(val.multiply(gapBig));
            if (searchMin && reOrder) {
                sortedList.add(entry);
                /*if (min.compareTo(val)>0) {
                    result = entry.getKey();
                    min = val;
                }*/
            }
        }

        if (searchMin && reOrder) {
            Collections.sort(sortedList, new Comparator<Entry<String, BigDecimal>>() {

                @Override
                public int compare(Entry<String, BigDecimal> o1, Entry<String, BigDecimal> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            result = sortedList.get(0).getKey();
        }

        if (searchMin && !reOrder) {
            for (int startID = removedSize; startID < sortedList.size(); startID++) {
                if (!updatedObject.contains(sortedList.get(startID).getKey()) && pq.containsKey(sortedList.get(startID).getKey())) {
                    result = sortedList.get(startID).getKey();
                    if (sortedList.get(startID).getValue().compareTo(threshold) > 0) {
                        System.err.println("error!");
                        System.exit(-1);
                    }
                    removedSize = startID + 1;
                    break;
                }
            }
            if (result.equals("")) {
                System.err.println("something wrong!");
                System.exit(-1);
            }

        }
       
        /*
        for (Entry<String, BigDecimal> entry : pq.entrySet()) {
            BigDecimal val = entry.getValue();
            entry.setValue(val.multiply(gapBig));
            if (!curPage.equals(entry.getKey())) {
                if (min.compareTo(val) > 0) {
                    result = entry.getKey();
                    min = val;
                }
            }
        }*/

        return result;
    }

    public String refreshParallel(String curPage, boolean searchMin) {
        String result = "";
        ArrayList<Thread> threadList = new ArrayList<>();
        for (ParaLRFU paraLRFU : parallelPQ) {
            paraLRFU.curObject = curPage;
            Thread thread = new Thread(paraLRFU);
            thread.start();
        }

        while (true) {
            boolean redFlag = false;
            for (ParaLRFU parallelPQ1 : parallelPQ) {
                if (!parallelPQ1.finished) {
                    redFlag = true;
                    break;
                }
            }
            if (!redFlag) {
                break;
            }
        }

        for (ParaLRFU parallelPQ1 : parallelPQ) {
            parallelPQ1.finished = false;
        }
        threshold = threshold.multiply(basic);
        boolean reorder = false;
        if (searchMin && updatedObject.size() >= ratio*sortedList.size()) {
            result = restartSorting();
            reorder = true;
            return result;
        }

        if (searchMin && !reorder) {
            for (int startID = removedSize; startID < sortedList.size(); startID++) {
                if (!updatedObject.contains(sortedList.get(startID).getKey()) && pq.containsKey(sortedList.get(startID).getKey())) {
                    result = sortedList.get(startID).getKey();
                    
                    if (sortedList.get(startID).getValue().compareTo(threshold) > 0) {
                       // System.err.println("restart!");
                        //System.exit(-1);
                        result = restartSorting();
                       // threshold = threshold.multiply(basic);
                        return result;
                    } else {
                        removedSize = startID + 1;
                    }
                    break;
                }
            }
            if (removedSize >= sortedList.size()) {
                // System.err.println("restartBBBBBBBBBB!");
                result = restartSorting();
                //threshold = threshold.multiply(basic);
                return result;
            }

        }
        
        
        

        //System.err.println("minBig: " + minBigDecimal);
        return result;
    }

    public boolean contains(String object) {
        return pq.containsKey(object);
    }

    public String addLRFUParallel(String object, long time) {
        String result = "";
       // System.out.print("object: " + object + " ");
        long startTime = System.currentTimeMillis();
        if (!this.pq.containsKey(object)) {
            if (pq.size() == this.cacheSize) {
                String minimal = refreshParallel(object, true);
                pq.remove(minimal);
                int minimalIndex = objectIndex.get(minimal);
                parallelPQ[minimalIndex].cache.remove(minimal);
                parallelPQ[minimalIndex].cache.put(object, BigDecimal.ONE);
                result = (minimal);
            } else {
                refreshParallel(object, false);
                if (pIndex >= parallelPQ.length) {
                    pIndex = 0;
                }
                parallelPQ[pIndex].cache.put(object, BigDecimal.ONE);
                pIndex++;
                // System.err.println(parallelPQ[pIndex].cache.size());
            }

            pq.put(object, BigDecimal.ONE);
        } else {
            refreshParallel(object, false);
            for (int i = 0; i < parallelPQ.length; i++) {
                if (parallelPQ[i].cache.containsKey(object)) {
                    parallelPQ[i].cache.put(object, pq.get(object).add(BigDecimal.ONE));
                    break;
                }
            }
            pq.put(object, pq.get(object).add(BigDecimal.ONE));
        }

        if (pq.size() == cacheSize) {
            updatedObject.add(object);
        }
        //System.out.println(System.currentTimeMillis() - startTime + " " + updatedObject.size() + " " + pq.size() + " sort:" + sortedList.size());

        return result;
    }

    public String addLRFU(String object, long time) {
        String result = "";
        // System.out.print("object: " + object + " ");

        long startTime = System.currentTimeMillis();
        if (!this.pq.containsKey(object)) {
            if (pq.size() == this.cacheSize) {
                String minimal = refresh(object, true);
                pq.remove(minimal);
                result = (minimal);
                //    System.out.print(System.currentTimeMillis() - startTime + " ***************** ");
            } else {
                refresh(object, false);
            }
            pq.put(object, BigDecimal.ONE);

        } else {
            refresh(object, false);
            pq.put(object, pq.get(object).add(BigDecimal.ONE));
        }

        if (pq.size() == cacheSize) {
            updatedObject.add(object);
        }

        System.out.println(System.currentTimeMillis() - startTime + " " + updatedObject.size() + " " + pq.size() + " sort:" + sortedList.size());
        return result;
    }

    private String retrieveMiminal() {
        String result = "";
        BigDecimal min = new BigDecimal(100);

        for (Entry<String, BigDecimal> entry : pq.entrySet()) {
            if (min.compareTo(entry.getValue()) > 0) {
                result = entry.getKey();
                min = entry.getValue();
            }
        }

        return result;
    }

    void setlamba(double lamda) {
        this.lamba = lamda;
        for (int i = 0; i < parallelPQ.length; i++) {
            parallelPQ[i] = new ParaLRFU();
            parallelPQ[i].cache = new HashMap<>();
            parallelPQ[i].lambda = lamba;
        }

        basic = new BigDecimal(Math.pow(0.5, lamda));

    }

    private String restartSorting() {
        sortedList.clear();
        removedSize = 0;
        updatedObject.clear();
        objectIndex.clear();
        threshold = BigDecimal.ONE;
        //reorder = true;
        for (int i = 0; i < parallelPQ.length; i++) {
            ParaLRFU paraLRFU = parallelPQ[i];
            for (Entry<String, BigDecimal> entry : paraLRFU.cache.entrySet()) {
                sortedList.add(entry);
                objectIndex.put(entry.getKey(), i);
            }
        }
        Collections.sort(sortedList, new Comparator<Entry<String, BigDecimal>>() {

            @Override
            public int compare(Entry<String, BigDecimal> o1, Entry<String, BigDecimal> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return sortedList.get(0).getKey();
    }

}
