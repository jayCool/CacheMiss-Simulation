package caches;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import caches.implementations.ARCCache;
import caches.implementations.FIFOCache;
import caches.implementations.HDDCache;
import caches.implementations.LRFUCache;
import caches.implementations.LRUCache;
import caches.implementations.RandomCache;
import caches.implementations.SSDCache;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ZhangJiangwei
 */
public class Simulator {

    Random rand = new Random();
    public double expo_factor = 0.2;
    public int nodeTransTime = 1;
    int poolDelay = 10;
    public long serial_id = 0;
    int proc_time = 1;
    public double[] misses;
    int batchFrequency;
    String outputDir;
    String config = "";
    ArrayList<Cache> hddCaches = new ArrayList<>();

    /**
     * Initialize the simulator with config file.
     *
     * @param config
     */
    Simulator(String config) {
        this.config = config;
    }

    public void run() {
        HashMap<Integer, Queue<String>> workloads = new HashMap<>();
        HashMap<Integer, Cache> cacheMaps = new HashMap<>();
        HashMap<Integer, Cache> clientCaches = new HashMap<>();

        loadConfiguration(config, workloads, cacheMaps, clientCaches);

        HashMap<Integer, ReferencePattern> clientRequests = new HashMap<>();
        Clock clock = new Clock();

        while (nonEmptyWorkload(workloads.values()) || unResolvedRequests(clientRequests, clock)) {

            retriveWorkLoads(cacheMaps, workloads, clock, clientCaches, clientRequests);
            processRequest(cacheMaps, clock, clientRequests, clientCaches);

            clock.forwardTime();
        }

       
        
        misses = new double[cacheMaps.size()];
        System.out.println("Simulation result:");
        for (Entry<Integer, Cache> cache : cacheMaps.entrySet()) {
            misses[cache.getKey()] = 1.0 * cache.getValue().totalMissed / cache.getValue().totalReceived;
            System.out.println("cache: " + cache.getKey() + "   : " + cache.getValue().totalReceived + " " + cache.getValue().cacheSize);
        }
        System.out.println();

    }

    

    /**
     * Check if all workloads are consumed.
     *
     * @param workloads
     * @return whether all workloads are consumed
     */
    private boolean nonEmptyWorkload(Collection<Queue<String>> workloads) {
        for (Queue queue : workloads) {
            if (queue.size() > 0) {
                return true;
            }
        }
        return false;
    }


    int exponentialTime(double lambda) {
        return (int) Math.round(Math.log(1 - rand.nextDouble()) / (-lambda));
    }

    /**
     * Check if the eventlist for a cache is empty.
     * @param cache
     * @return whether the eventlist is empty 
     */
    private boolean isEmptyEvent(Cache cache) {
        if (cache.eventList.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * retrieve workload objects for each client cache.
     *
     * @param cacheMaps
     * @param workLoads
     * @param clock
     * @param clientCaches
     * @param client_requests
     */
    private void retriveWorkLoads(HashMap<Integer, Cache> cacheMaps, HashMap<Integer, Queue<String>> workLoads,
            Clock clock, HashMap<Integer, Cache> clientCaches, HashMap<Integer, ReferencePattern> client_requests) {

        for (int cacheID : clientCaches.keySet()) {
            Cache cache = cacheMaps.get(cacheID);

            if (workLoads.get(cacheID).size() > 0 && ((client_requests.get(cacheID) != null
                    && client_requests.get(cacheID).getStatus() == CacheStatus.PROCESSED
                    && clock.getCurrentTime() >= client_requests.get(cacheID).getTimestamp())
                    || client_requests.get(cacheID) == null)) {
                //calculate the detla time that the current object needs to wait
                int deltaTime = this.exponentialTime(this.expo_factor) + 1;

                String line = workLoads.get(cacheID).poll();
                String[] splits = line.split("\\s+");

                //calculate the object size, default 1
                int objSize = 1;
                if (splits.length > 1) {
                    objSize = Integer.parseInt(splits[1]);
                }

                ReferencePattern rp = new ReferencePattern(cacheID, clock.getCurrentTime() + deltaTime,
                        splits[0], this.serial_id, objSize);
                rp.setChildID(cacheID);

                ReferencePattern client_rp = new ReferencePattern(cacheID, clock.getCurrentTime(),
                        rp.object, this.serial_id, objSize);
                client_rp.setChildID(cacheID);
                client_requests.put(cacheID, client_rp);

                this.serial_id++;
                cache.eventList.add(rp); 
                //object in the cache queue might be process later than it was retrieved.
            }
        }
    }
    
    /**
     * Process each request
     * @param cacheMaps
     * @param clock
     * @param clientRequests
     * @param clientCaches 
     */
    private void processRequest(HashMap<Integer, Cache> cacheMaps, Clock clock,
            HashMap<Integer, ReferencePattern> clientRequests, HashMap<Integer, Cache> clientCaches) {

        //check for batch updates for hdd caches
        if (!this.hddCaches.isEmpty()) {
            for (Cache cache : hddCaches) {
                cache.calculateEvictedObjects();
                ArrayList<String> evictedObjects = cache.getEvictedObjects();
                ArrayList<Integer> evictedObjectSizes = cache.getEvictedObjectSizes();
                for (Cache clientCache : clientCaches.values()) {
                    if (clientCache.getParent().getCacheID() == (cache.getCacheID())) {
                        clientCache.batchUpdateObjects(evictedObjects, evictedObjectSizes, clock.getCurrentTime());
                    }
                }
            }
        }

        for (Entry<Integer, Cache> entry : cacheMaps.entrySet()) {
            Cache cache = entry.getValue();

            if (!isEmptyEvent(cache)) {
                if (cache.eventList.peek().getTimestamp() <= clock.getCurrentTime()) {
                    ArrayList<ReferencePattern> processed = new ArrayList<>();
                    for (ReferencePattern rp : cache.eventList) {
                        if (rp.getTimestamp() <= clock.currentTime) {
                            switch (rp.getStatus()) {
                                case CacheStatus.NULL:
                                    nonProcessedPR(rp, clock, cache, cacheMaps, processed, clientRequests);
                                    break;
                                case  CacheStatus.PROCESSED:
                                    processedRP(rp, clock, cache, cacheMaps, processed, clientRequests);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    for (ReferencePattern rp : processed) {
                        cache.eventList.remove(rp);
                    }
                }
            }
        }
    }

    /**
     * Process the rp when it is not cache yet.
     * @param rp
     * @param clock
     * @param cache
     * @param cacheMaps
     * @param processed
     * @param client_requests 
     */
    private void nonProcessedPR(ReferencePattern rp, Clock clock, Cache cache,
            HashMap<Integer, Cache> cacheMaps, ArrayList<ReferencePattern> processed,
            HashMap<Integer,ReferencePattern> client_requests) {

        cache.addReceivedObject(rp.getObjSize());

        //If the object is contained in the cache
        if (cache.contains(rp.getObject())) {
            rp.setHitNode(cache.getCacheID());
          
            if (cache.getCacheStrategy() != CacheStrategy.MCD) {
                cache.add(rp.getObject(), clock.currentTime, rp.getObjSize());
            } else if (rp.getChildID() >= 0) {
                cache.remove(rp.getObject());
            }

            passEventToChild(cacheMaps, rp, clock, client_requests);
            processed.add(rp);
           
            return;
        } 
        // not containing the object, forward to the parent node
        else if (cache.getParent() == null) {
            requestFromPool(rp, clock, cache);
        } else {
            forwardRequestToParent(cache, rp, clock, CacheStatus.WAITING);
        }
    }

    /**
     * Pass the processed reference to the child cache.
     * @param rp
     * @param clock
     * @param cache
     * @param cacheMaps
     * @param processed
     * @param client_requests 
     */
    private void processedRP(ReferencePattern rp, Clock clock, Cache cache, HashMap<Integer, Cache> cacheMaps,
            ArrayList<ReferencePattern> processed, HashMap<Integer, ReferencePattern> client_requests) {
        //String evictionObject = "";
        switch (cache.getCacheStrategy()){
                case CacheStrategy.LCE:case CacheStrategy.SSDHDD:
                    cache.add(rp.getObject(), clock.currentTime, rp.getObjSize());
                    break;
                case CacheStrategy.LCD : case CacheStrategy.MCD:
                     if (rp.getParent_id() == rp.getHitNode()){
                         cache.add(rp.getObject(), clock.currentTime, rp.getObjSize());
                     }
                     break;
            }

        passEventToChild(cacheMaps, rp, clock, client_requests);
        processed.add(rp);

    }
    
    
    /**
     * Pass the hit object event to the children cache.
     * @param cacheMaps
     * @param rp
     * @param clock
     * @param client_requests 
     */
    private void passEventToChild(HashMap<Integer, Cache> cacheMaps, ReferencePattern rp, Clock clock, 
            HashMap<Integer,ReferencePattern> client_requests) {
        
        //if there is not child cache
        if (rp.getChildID() < 0) {
            int client_id = rp.getChildID();
            client_requests.get(client_id).setStatus(CacheStatus.PROCESSED); 
            client_requests.get(client_id).setTimestamp(clock.currentTime + this.proc_time + this.nodeTransTime);
            return;
        }
        
        //move to the child cache, and update the status of the reference pattern.
        for (ReferencePattern preRP : cacheMaps.get(rp.getChildID()).eventList) {
            if (preRP.getObject_id() == rp.getObject_id()) {
                preRP.setTimestamp(clock.currentTime + this.proc_time + this.nodeTransTime);
                preRP.setStatus(CacheStatus.PROCESSED);
                preRP.setParent_id(rp.getNode_id()); 
                preRP.setHitNode(rp.getHitNode());
                break;
            }
        }
    }

    /**
     * Check if there are client caches that is not resolved yet.
     *
     * @param client_requests
     * @param clock
     * @return whether there are un-resolved requests
     */
    private boolean unResolvedRequests(HashMap<Integer, ReferencePattern> client_requests, Clock clock) {
        boolean unsolved = false;
        for (ReferencePattern rp : client_requests.values()) {
            if (rp != null && (rp.getStatus() != CacheStatus.PROCESSED || rp.getTimestamp() >= clock.currentTime)) {
                unsolved = true;
            }
        }
        return unsolved;
    }
    
    /**
     * Request the object from the root (Pool).
     * @param rp
     * @param clock
     * @param cache 
     */
    private void requestFromPool(ReferencePattern rp, Clock clock, Cache cache) {
        rp.setTimestamp(clock.currentTime + this.proc_time + this.poolDelay);
        rp.setStatus(CacheStatus.PROCESSED);
        rp.setHitNode(-1);
        rp.setParent_id(-1);
        cache.addMissedCount(rp.getObjSize());
    }
    
    /**
     * Forward the object to the parent for content retrieval
     * @param cache
     * @param rp
     * @param clock
     * @param status 
     */
    private void forwardRequestToParent(Cache cache, ReferencePattern rp, Clock clock, int status) {
        Cache parentCache = cache.getParent();
        ReferencePattern newRp = new ReferencePattern(parentCache.getCacheID(), clock.currentTime + this.proc_time + this.nodeTransTime, rp.getObject(), rp.getObject_id(), rp.getObjSize());
        newRp.setChildID(cache.getCacheID());
        parentCache.eventList.add(newRp);
        rp.setStatus(status);
        if (status == 1) {
            cache.addMissedCount(newRp.getObjSize());
        }
    }

 
    /**
     * load cache and workload information.
     *
     * @param config
     * @param workloads
     * @param cacheMaps
     * @param clientCaches
     */
    private void loadConfiguration(String config, HashMap<Integer, Queue<String>> workloads, HashMap<Integer, Cache> cacheMaps, HashMap<Integer, Cache> clientCaches) {
        HashMap<Integer, Integer> parentMap = new HashMap<>();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(config));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray clients = (JSONArray) jsonObject.get("clients");
            for (Object object : clients) {

                JSONObject client = (JSONObject) object;
                int id = Integer.parseInt((String) client.get("id"));
                int cacheSize = Integer.parseInt((String) client.get("cacheSize"));
                int cacheType = Integer.parseInt((String) client.get("cacheType"));

                Cache newCache = initializeCache(cacheSize, id, cacheType);
                
                if (cacheType == CacheType.HDD) {
                    newCache.setBatchFrequency(this.batchFrequency);
                    hddCaches.add(newCache);
                }
                
                
                if (client.get("cacheStrategy") != null) {
                    newCache.setCacheStrategy(Integer.parseInt((String) client.get("cacheStrategy")));
                }

                if (client.get("missRatioOption") != null) {
                    newCache.setMissRatioOption(Integer.parseInt((String) client.get("missRatioOption")));
                }

                if (client.get("warmingFile") != null) {
                    String objectAddress = (String) client.get("warmingFile");
                    warmingCache(newCache, objectAddress);
                }

                if (client.get("parent") != null) {
                    parentMap.put(id, Integer.parseInt((String) client.get("parent")));
                }

                if (client.get("inputWorkloadAddress") != null) {
                    workloads.put(id, (Queue<String>) loadWorkLoadFromFile((String) client.get("inputWorkloadAddress")));
                    clientCaches.put(id, newCache);
                }

                cacheMaps.put(id, newCache);
            }
            for (Entry<Integer, Integer> entry : parentMap.entrySet()) {
                int childID = entry.getKey();
                int parentID = entry.getValue();
                cacheMaps.get(childID).addParent(cacheMaps.get(parentID));
                cacheMaps.get(parentID).addChild(cacheMaps.get(childID));
            }

        } catch (IOException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load the workload objects from file
     *
     * @param file
     * @return queue of workload objects
     */
    public Queue<String> loadWorkLoadFromFile(String file) {
        Queue<String> result = new LinkedList<>();
        try {
            Scanner scanner = new Scanner(new File(file));

            while (scanner.hasNext()) {
                result.add(scanner.nextLine().trim());
            }
            scanner.close();

        } catch (FileNotFoundException ex) {
        }
        return result;
    }

    /**
     * Warming the cache by pre-loading objects.
     *
     * @param newCache
     * @param objectAddress
     */
    private void warmingCache(Cache newCache, String objectAddress) {
        try {
            Scanner scanner = new Scanner(new File(objectAddress));
            while (scanner.hasNext()) {
                String[] splits = scanner.nextLine().split("\\s+");
                if (splits.length > 1) {
                    newCache.add(splits[0], 0, Integer.parseInt(splits[1]));
                } else {
                    newCache.add(splits[0], 0, 1);
                }
                if (newCache.isCacheFull()) {
                    break;
                }
            }
            scanner.close();
            //newCache.setCacheFull(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Cache initializeCache(int cacheSize, int id, int cacheType) {
        switch (cacheType) {
            case CacheType.ARC:
                return new ARCCache(cacheSize, id, cacheType);
            case CacheType.FIFO:
                return new FIFOCache(cacheSize, id, cacheType);
            case CacheType.HDD:
                return new HDDCache(cacheSize, id, cacheType);
            case CacheType.LRFU:
                return new LRFUCache(cacheSize, id, cacheType);
            case CacheType.LRU:
                return new LRUCache(cacheSize, id, cacheType);
            case CacheType.RANDOM:
                return new RandomCache(cacheSize, id, cacheType);
            case CacheType.SSD:
                return new SSDCache(cacheSize, id, cacheType);
        }
        System.out.println("Invalid Cache Type Define!!!");
        System.exit(-1);
        
        return null;
    }
}
