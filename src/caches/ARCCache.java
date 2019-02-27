package caches;


import caches.LRUNonUniformCache;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JIANGWEI
 */
public class ARCCache {
      LRUNonUniformCache t1, t2;
    LRUNonUniformCache b1, b2;
    long cacheSize;
    int p=0;
    public ARCCache(long cacheSize) {
        this.cacheSize = cacheSize;
       t1 = new LRUNonUniformCache((int) cacheSize);
                t2 = new LRUNonUniformCache((int) cacheSize);
                b1 = new LRUNonUniformCache((int) cacheSize);
                b2 = new LRUNonUniformCache((int) cacheSize);
    }
    
     private int calDelta(LRUNonUniformCache b1, LRUNonUniformCache b2) {
        if (b1.objectSize.size() >= b2.objectSize.size() ) {
            return 1;
        }
        return (int) (b2.objectSize.size()  / b1.objectSize.size() );
    }

    private String replace(String object, int p, long time, int objSize) {
        String out = "";
        if ((!t1.objectSize.isEmpty()) && (t1.curSize > p || (b2.objectSize.containsKey(object) && t1.curSize == p))) {
            
             String old = t1.removeOldest();
            
            b1.add(old, time, objSize);
          while (t1.curSize+t2.curSize+objSize > this.cacheSize && !t1.objectSize.isEmpty()){
                 old = t1.removeOldest();
            
            b1.add(old, time, objSize);
            }
       //     System.out.println("here");
            out = old;
        } else if (!t2.objectSize.isEmpty()) {
            String old = t2.removeOldest();
            b2.add(old, time, objSize);
           
            while (t1.curSize+t2.curSize+objSize > this.cacheSize&& !t2.objectSize.isEmpty()){
                 old = t2.removeOldest();
            
            b2.add(old, time, objSize);
            }
        //    System.out.println("there");
            out = old;
        }
        return out;
    }

   public String toString(){
       
         return "ARC-Cache: t1: " + this.t1.toString() + " t2: " + t2.toString() + "b1: " + b1.toString() + "b2: " + b2.toString();
          
   }

    public String add(String object, long time, int objSize) {
        String output = "";
        
                if (this.t1.objectSize.containsKey(object)) {
                 //   System.err.println("branch1");
                    this.t1.remove(object);
                    this.t2.add(object, time, objSize);
                } else if (this.t2.objectSize.containsKey(object)) {
                //    System.err.println("branch2");
                    this.t2.remove(object);
                    this.t2.add(object, time, objSize);
                    //System.out.println(output);
                } else if (b1.objectSize.containsKey(object)) {
                 //   System.err.println("branch3");
                    int d = calDelta(b1, b2);
                    p = Math.min(p + d, (int) this.cacheSize);
                    replace(object, p, time, objSize);
                    b1.remove(object);
                    t2.add(object, time, objSize);
                } else if (b2.objectSize.containsKey(object)) {
                //    System.err.println("branch4");
                    int d = calDelta(b2, b1);
                    p = Math.max(p - d, 0);
                    replace(object, p, time, objSize);
                    b2.remove(object);
                    t2.add(object, time, objSize);
                } else {
                   // if (debug) {
                  //      System.err.println("branch5");
                    //}
                    if (this.t1.curSize + b1.objectSize.size() >= this.cacheSize) {
                        if (t1.curSize < this.cacheSize) {
                            b1.removeOldest();
                            output = replace(object, p, time, objSize);
                        } else {
                            t1.removeOldest();
                        }
                    } else if (this.t1.curSize + b1.objectSize.size() < this.cacheSize) {
                        if (t1.curSize + t2.curSize + b1.objectSize.size() + b2.objectSize.size()+objSize >= this.cacheSize) {
                            if (t1.curSize + t2.curSize + b1.objectSize.size() + b2.objectSize.size()+ objSize >= 2 * this.cacheSize) {
                                if (!b2.objectSize.isEmpty())
                                b2.removeOldest();
                            }
                            output = replace(object, p, time, objSize);
                        }
                    }

                  
                    
                    String temp = t1.add(object, time, objSize);
                    //System.err.println("return: " + temp + "\n");
                    if (!"".equals(output)) {
                        output = temp;
                    }
                }
                return output;}

    public void remove(String object) {
        t1.remove(object);
        t2.remove(object);
    }

    public boolean checkFull(int objSize) {
        return t1.curSize + t2.curSize + objSize >= cacheSize;
    }

    public boolean contains(String object) {
                    return t1.objectSize.containsKey(object) || t2.objectSize.containsKey(object);
            
    }
            
}
