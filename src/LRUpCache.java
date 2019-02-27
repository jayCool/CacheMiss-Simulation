/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JIANGWEI
 */
class LRUpCache {

    LRUNonUniformCache t1, t2;
    LRUNonUniformCache b1, b2;
    int p;
    long cacheSize = 0;
    long l1Limit = 0;
    int maxSize = 0;
    LRUpCache(long cacheSize) {
        this.cacheSize = cacheSize;
        t1 = new LRUNonUniformCache((int) cacheSize);
        t2 = new LRUNonUniformCache((int) cacheSize);
        b1 = new LRUNonUniformCache((int) cacheSize);
        b2 = new LRUNonUniformCache((int) cacheSize);
    }

    public void setP(double p) {
        this.p =  (int) (this.cacheSize * p);
      
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
            out = old;
        }
        return out;
    }
    String add(String object, long time) {
        int objSize = 1;
        String output = "";
        if (this.t1.objectSize.containsKey(object)) {
            this.t1.remove(object);
            this.t2.add(object, time, objSize);
        } else if (this.t2.objectSize.containsKey(object)) {
            this.t2.remove(object);
            this.t2.add(object, time, objSize);
        } else if (b1.objectSize.containsKey(object)) {
            //   System.err.println("branch3");
            replace(object, p, time, objSize);
            b1.remove(object);
            t2.add(object, time, objSize);
        } else if (b2.objectSize.containsKey(object)) {
            //    System.err.println("branch4");
            replace(object, p, time, objSize);
            b2.remove(object);
            t2.add(object, time, objSize);
        } else {
            if (this.t1.curSize + b1.objectSize.size() >= this.cacheSize) {
                if (t1.curSize < this.cacheSize) {
                    b1.removeOldest();
                    output = replace(object, p, time, objSize);
                } else {
                    t1.removeOldest();
                }
            } else if (this.t1.curSize + b1.objectSize.size() < this.cacheSize) {
                if (t1.curSize + t2.curSize + b1.objectSize.size() + b2.objectSize.size() + objSize >= this.cacheSize) {
                    if (t1.curSize + t2.curSize + b1.objectSize.size() + b2.objectSize.size() + objSize >= 2 * this.cacheSize) {
                        if (!b2.objectSize.isEmpty()) {
                            b2.removeOldest();
                        }
                    }
                    output = replace(object, p, time, objSize);
                }
            }

           

            String temp = t1.add(object, time, objSize);
            if (!"".equals(output)) {
                output = temp;
            }
        }
        
        if (b1.objectSize.size()+b2.objectSize.size() > maxSize){
            maxSize = b1.objectSize.size() + b2.objectSize.size();
        }
        return output;
    }

    boolean contains(String object) {
        return t1.objectSize.containsKey(object) || t2.objectSize.containsKey(object);
    }
    
    @Override
    public String toString(){
        return t1.objectSize.keySet() + "---" + t2.objectSize.keySet();
    }

}
