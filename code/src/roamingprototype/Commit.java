/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Commit extends Thread
{
    Launcher l;
    public Commit(Launcher l)
    {
        this.l = l;
        start();
    }

    @Override
    public void run() 
    {
        while(true)
        {
            l.repaint();
        }
    }
    
}
