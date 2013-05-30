/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Render extends Thread
{
    Launcher l;
    public Render(Launcher l)
    {
        this.l = l;
        start();
    }

    @Override
    public void run() 
    {
        while(true)
        {
            l.render();
        }
    }
    
}
