/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.nodes.Attributes;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class TileLoader extends Loader
{    
    //Data
    private HashMap<Integer, Tile> tilemap = new HashMap<>();
    private ArrayList<AnimTile> animtiles = new ArrayList<>();
    
    //workbench
    private int workid;
    private ArrayList<Image> workframes = new ArrayList<>();
    private int workcoll;
    
    //Instance
    private static TileLoader instance = new TileLoader();
    public static TileLoader getInstance() { return instance; }
    
    @Override
    public void processOpenTag(String tagName, Attributes atts)
    {        
        
    }
    
    @Override
    public void processContent(String tagName, String text)
    {
        switch(tagName)
        {                
            case "id":
                workid = Integer.valueOf(text);
                break;
                
            case "path":
                workframes.add(Resources.getInstance().getImage("tiles/" + text));
                break;
                
            case "collision":
                workcoll = (int) Long.parseLong(text, 16);              //16 for Hexadecimal
                break;
        }
    }
    
    @Override
    public void processCloseTag(String tagName)
    {
        if(tagName.equals("tile"))
        {
            Tile tile;
            if(workframes.size() == 1)
            {
                tile = new Tile();
            }
            else
            {
                tile = new AnimTile();
                AnimTile atile = (AnimTile) tile;
                atile.frames = new Image[workframes.size()];
                for(int i = 0; i < workframes.size(); i++)
                {
                    atile.frames[i] = workframes.get(i);
                }
                animtiles.add(atile);
            }
            tile.image = workframes.get(0);
            tile.collision = workcoll;
            tilemap.put(workid, tile);
            
            workframes.clear();
        }
    }
    
    public HashMap<Integer, Tile> getTiles()
    {
        HashMap tiles = tilemap;
        tilemap = new HashMap<>();
        return tiles;
    }
    
    public ArrayList<AnimTile> getAnimTiles()
    {
        ArrayList tiles = animtiles;
        animtiles = new ArrayList<>();
        return tiles;
    }
}
