/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Comparator;

/**
 *
 * @author JIANGWEI
 */
public class ReferenceComparator implements Comparator<ReferencePattern>{

    @Override
    public int compare(ReferencePattern o1, ReferencePattern o2) {
        if (o1.timestamp < o2.timestamp){
            return -1;
        }
        if (o1.timestamp > o2.timestamp){
            return 1;
        }
        
        return 0;
        
    }
    
    
    
}
