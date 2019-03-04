package caches.implementations;

import caches.Cache;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author JIANGWEI
 */
public class ARCCache extends Cache {

    LRUCache t1;
    LRUCache t2;
    LRUCache b1;
    LRUCache b2;
    int p = 0;

    public ARCCache(int cacheSize, int id, int type) {
        super(cacheSize, id, type);
        t1 = new LRUCache(cacheSize, id, type);
        t2 = new LRUCache(cacheSize, id, type);
        b1 = new LRUCache(cacheSize, id, type);
        b2 = new LRUCache(cacheSize, id, type);
    }

    /**
     *
     * @param b1
     * @param b2
     * @return delta in ARC
     */
    private int calDelta(LRUCache b1, LRUCache b2) {
        if (b1.getContentSize() >= b2.getContentSize()) {
            return 1;
        }
        return (int) Math.round(1.0 * b2.getContentSize() / b1.getContentSize());
    }

    private String replace(String object, int p, long time, int objSize) {
        String out = "";
        if ((!t1.isEmpty()) && (t1.currentContentSize > p || (b2.contains(object) && t1.currentContentSize == p))) {

            String old = t1.removeOldest();

            b1.add(old, time, 1);
            while (t1.currentContentSize + t2.currentContentSize + objSize > this.getCacheSize() && !t1.isEmpty()) {
                old = t1.removeOldest();
                b1.add(old, time, 1);
            }
            out = old;
        } else if (!t2.isEmpty()) {
            String old = t2.removeOldest();
            b2.add(old, time, 1);

            while (t1.currentContentSize + t2.currentContentSize + objSize > this.getCacheSize() && !t2.isEmpty()) {
                old = t2.removeOldest();
                b2.add(old, time, 1);
            }
            out = old;
        }
        return out;
    }

    @Override
    public String toString() {
        return "ARC-Cache: t1: " + this.t1.toString() + " t2: " + t2.toString() + "b1: " + b1.toString() + "b2: " + b2.toString();
    }

    @Override
    public String add(String object, long time, int objSize) {
        String output = "";

        if (this.t1.contains(object)) {
            this.t1.remove(object);
            this.t2.add(object, time, objSize);
        } else if (this.t2.contains(object)) {
            this.t2.remove(object);
            this.t2.add(object, time, objSize);
        } else if (b1.contains(object)) {
            int d = calDelta(b1, b2);
            p = Math.min(p + d, (int) this.getCacheSize());
            replace(object, p, time, objSize);
            b1.remove(object);
            t2.add(object, time, objSize);
        } else if (b2.contains(object)) {
            int d = calDelta(b2, b1);
            p = Math.max(p - d, 0);
            replace(object, p, time, objSize);
            b2.remove(object);
            t2.add(object, time, objSize);
        } else {
            if (this.t1.currentContentSize + b1.currentContentSize >= this.getCacheSize()) {
                if (t1.currentContentSize < this.getCacheSize()) {
                    b1.removeOldest();
                    output = replace(object, p, time, objSize);
                } else {
                    t1.removeOldest();
                }
            } else if (this.t1.currentContentSize + b1.currentContentSize < this.getCacheSize()) {
                if (t1.currentContentSize + t2.currentContentSize + b1.currentContentSize + b2.currentContentSize + objSize >= this.getCacheSize()) {
                    if (t1.currentContentSize + t2.currentContentSize + b1.currentContentSize + b2.currentContentSize + objSize >= 2 * this.getCacheSize()) {
                        if (!b2.isEmpty()) {
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
        return output;
    }

    @Override
    public void remove(String object) {
        t1.remove(object);
        t2.remove(object);
    }

    public boolean checkFull(int objSize) {
        return t1.currentContentSize + t2.currentContentSize + objSize >= this.getCacheSize();
    }

    @Override
    public boolean contains(String object) {
        return t1.contains(object) || t2.contains(object);

    }

    @Override
    public boolean isCacheFull() {
        return checkFull(0);
    }

}
