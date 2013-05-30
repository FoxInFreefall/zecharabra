/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Player implements Settings
{
    public float x, y;
    public int dir = -1;
    public int lastdir = -1;
    public int nextdir = -1;
    
    public int xcell, ycell;
    
    public void stop()
    {
        lastdir = dir;
        dir = -1;
    }
    
    public boolean isAligned()
    {
        return x % cell_size == 0 && y % cell_size == 0;
    }
}
