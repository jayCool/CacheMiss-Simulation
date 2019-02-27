
import java.util.HashSet;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JIANGWEI
 */
public class RandomCache {
 Random rand = new Random();
    String[] randomKeys;
      HashSet<String> randomCache = new HashSet<>();
    int cacheSize=0;
    RandomCache(long cacheSize) {
        this.cacheSize = (int) cacheSize;
        randomKeys = new String[(int) this.cacheSize];
                for (int i = 0; i < randomKeys.length; i++) {
                    randomKeys[i] = "";
                }
    }
    
    
     public String add(String element, long time) {
        String result = "";
        if (randomCache.contains(element)) {
            return "";
        }
        if (randomCache.size() < cacheSize) {
            randomKeys[randomCache.size()] = element;
            randomCache.add(element);
            return "";
        }

        int index = rand.nextInt((int) cacheSize);
        result = randomKeys[index];
        randomCache.remove(randomKeys[index]);
        randomCache.add(element);
        randomKeys[index] = element;
        return result;
    }

    boolean contains(String object) {
    return this.randomCache.contains(object);}

    void remove(String object) {
        randomCache.remove(object);

        int index = 0;
        for (int i = 0; i < randomKeys.length; i++) {
            if (randomKeys[i].equals(object)) {
                index = i;
            }
        }
        randomKeys[index] = "";
    }

    
}