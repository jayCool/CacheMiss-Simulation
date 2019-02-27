/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author workshop
 */
public class Clock {
    long currentTime = 0;
    
    public void forwardTime(){
        this.currentTime ++;
    }

    public long getCurrentTime() {
        return currentTime;
    }
    
    
}
