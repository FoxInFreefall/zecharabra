/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Update extends Thread implements Settings
{
    Launcher l;
    public Update(Launcher l)
    {
        this.l = l;
        start();
    }

    @Override
    public void run() 
    {
        while(true)
        {
            l.update();
            
            try { sleep(frameDelay); } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
