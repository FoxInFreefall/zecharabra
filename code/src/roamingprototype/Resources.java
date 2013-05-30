package roamingprototype;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public final class Resources
{
    private HashMap<String,Image> images = new HashMap<>();
    private HashMap<String,AudioInputStream> audio = new HashMap<>();
    
    private static Resources instance = new Resources();
    
    public static Resources getInstance() { return instance; }

    public Resources()
    {
        preload();
    }

    public void preload()
    {
        
    }

    /***** IMAGES *****/
    
    public Image getImage(String imageName)
    {
        if(!images.containsKey(imageName))
        {
            addImage(imageName, imageName);
        }
        return images.get(imageName);
    }

    public void addImage(String imageName, String imagePath)
    {
        images.put(imageName,getSrcImage(imagePath));
    }

    public Image getSrcImage(String path)
    {
        Image im = null;
        try
        {
            InputStream file = getClass().getResourceAsStream("/resources/" + path);
            System.out.println("/resources/" + path);
            im = ImageIO.read(file);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return im;
    }
    
    
    /***** AUDIO *****/
    
    public AudioInputStream getAudio(String audioName)
    {
        return audio.get(audioName);
    }

    public void addAudio(String audioName, String audioPath)
    {
        audio.put(audioName, getSrcAudio(audioPath));
    }

    public AudioInputStream getSrcAudio(String path)
    {
        AudioInputStream stream = null;
        try 
        {
            URL url = getClass().getResource("/" + path);
            stream = AudioSystem.getAudioInputStream(url);
//            AudioClip ac = Applet.newAudioClip(url);
        } 
        catch (UnsupportedAudioFileException | IOException ex) 
        {
            
        }
        
        return stream;
        
//        AudioStream stream = null;
//
//        try
//        {
//            InputStream file = getClass().getResourceAsStream("/" + path);
//            stream = new AudioStream(file);
//        }
//        catch(Exception e) { e.printStackTrace(); }
//
//        return stream;
    }
}
