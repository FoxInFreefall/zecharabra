/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public interface Settings 
{
    public final int width = 1366;
    public final int height = 768;
    
    public final int fps = 30;
    public final long frameDelay = (long) (1000f/fps);
    
    public final int player_speed = 16;
    
    public final int cell_size = 32;
    public final int animtile_fps = 2;
    public final long animtile_frameDelay = (long) (1000f/animtile_fps);
}
