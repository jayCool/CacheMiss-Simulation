/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import oldFiles.ObjectPair;

/**
 *
 * @author JIANGWEI
 */
public class LRFUCache {

    //HashMap<String, BigDecimal> pq = new HashMap<>();
    HashMap<String, Long> lastTouched = new HashMap<>();
    PriorityQueue<ObjectPair> priorityQueue;
    double lamba = 0.0;
    int cacheSize = 0;
    HashMap<String, ObjectPair> objectToPairMapping;
    
    LRFUCache(int cacheSize) {
    //    pq = new HashMap<String, BigDecimal>(cacheSize);
        this.cacheSize = cacheSize;
        priorityQueue = new PriorityQueue<>(cacheSize,idComparator);
        objectToPairMapping = new HashMap<>();
    }
    
    public static Comparator<ObjectPair> idComparator = new Comparator<ObjectPair>(){
		
		@Override
		 public int compare(ObjectPair o1, ObjectPair o2){
                return o1.crfValue.compareTo(o2.crfValue);
            }
        
	};

   
    public boolean contains(String object) {
        return objectToPairMapping.containsKey(object);
    }

    void recalculateCRF(long time){
        Iterator<ObjectPair> iter = priorityQueue.iterator();
        while (iter.hasNext()){
            ObjectPair op = iter.next();
            String object = op.object;
            BigDecimal oldCRF = op.crfValue;
            BigDecimal newCRF = calculateCRF(object, time, oldCRF);
            op.crfValue = newCRF;
            lastTouched.put(object, time);
            //objectToPairMapping.put(object, op);
        }
     
        
    }
    public String addLRFU(String object, long time) {
        String result = "";
        System.out.print("object: " + object + "\t");
        long startTime = System.currentTimeMillis();
        if (!this.objectToPairMapping.containsKey(object)) {
            if (objectToPairMapping.size() == this.cacheSize) {
                String minimal = priorityQueue.poll().object;
                objectToPairMapping.remove(minimal);
                result = (minimal);
            }
            
            recalculateCRF(time);
            System.out.print(System.currentTimeMillis() - startTime + " ");
            ObjectPair op = new ObjectPair(object, BigDecimal.ONE);
           
            
            priorityQueue.offer(op);
          
            objectToPairMapping.put(object, op);
            
        } else {
            BigDecimal oldCRF = objectToPairMapping.get(object).crfValue;
            BigDecimal newCRF = calculateCRF(object, time, oldCRF);
            //pq.put(object, newCRF);
            
            priorityQueue.remove(objectToPairMapping.get(object));
            ObjectPair op = new ObjectPair(object,newCRF);
            priorityQueue.offer(op);
            objectToPairMapping.put(object, op);
        }
          System.out.println(System.currentTimeMillis() - startTime);
        lastTouched.put(object, time);

        return result;
    }

 


    private BigDecimal calculateCRF(String object, long time, BigDecimal oldCRF) {
     //BigDecimal oldCRF = pq.get(object);
            BigDecimal fDelta = new BigDecimal(Math.pow(0.5,lamba)).pow((int) (time - lastTouched.get(object)));
            BigDecimal remainder = oldCRF.multiply(fDelta);
            return remainder.add(BigDecimal.ONE);         
    }
    
    void setlamba(double lamda) {
        this.lamba = lamda;}

}
