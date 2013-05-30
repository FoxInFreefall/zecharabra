/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

import java.awt.Image;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Tile 
{
    public Image image;
    public int collision;
    
    @Override
    public String toString()
    {
        return "passability: " + collision + ", image: " + image;
    }
}

class AnimTile extends Tile
{
    public Image[] frames;
    public int frame;
    
    public void progress()
    {
        frame++;
        if(frame >= frames.length)
        {
            frame = 0;
        }
        image = frames[frame];
    }
}