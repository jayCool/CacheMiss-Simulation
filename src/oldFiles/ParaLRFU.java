
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zhang-Jiangwei
 */
public class ParaLRFU implements Runnable {

    HashMap<String, BigDecimal> cache;
    double lambda;
    String curObject;
    String minPage;
    //BigDecimal minValue;
    boolean finished = false;

    @Override
    public void run() {
        //try {
            //String result = "";
            //Thread.sleep(1L);
           // BigDecimal min = new BigDecimal(100);
            BigDecimal gapBig = new BigDecimal(Math.pow(0.5, lambda));
            
            for (Map.Entry<String, BigDecimal> entry : cache.entrySet()) {
                BigDecimal val = entry.getValue();
                entry.setValue(val.multiply(gapBig));
                //if (!curObject.equals(entry.getKey())) {
                //    if (min.compareTo(val) > 0) {
                //        minPage = entry.getKey();
                 //       min = val;
                //    }
                //}//
            }
            //minValue = min;
            finished = true;
       // } catch (InterruptedException ex) {
       //     Logger.getLogger(ParaLRFU.class.getName()).log(Level.SEVERE, null, ex);
       // }
    }
}
