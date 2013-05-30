/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Semaphore
{
    /***** VARIABLES *****/
    
        private int resource;       //core
    
    
    /***** WRAPPER *****/
            
        public Semaphore(int resource)
        {
            this.resource = resource;
        }
            
        
    /***** METHODS *****/
        
        public synchronized void up()
        {
            resource++;
            //If there are threads waiting, let them know they can go
            if(resource <= 0)
            {
                notify();
            }
        }
        
        public synchronized void down()
        {
            resource--;
            if(resource < 0)
            {
                try
                {
                    wait();
                }
                catch(InterruptedException e) {}
            }
        }
        
        public synchronized int count()
        {
            return resource;
        }
}
