package oldFiles;


import java.math.BigDecimal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Zhang-Jiangwei
 */
public class ObjectPair {
    public String object;
    public BigDecimal crfValue;

    public ObjectPair(String object, BigDecimal ONE) {
        this.object = object;
        this.crfValue = ONE;
    }

    @Override
    public String toString() {
       return ("o:" + object + " crf:" + crfValue);
    }
    
    
}
