/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

import java.util.ArrayList;
import org.jsoup.nodes.Attributes;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class LevelLoader extends Loader
{
    //Data
    private Tile[][][] grid;
    private ArrayList<Triple> animTiles = new ArrayList<>();
    private int[][] events;
    
    //workbench
    private int wide, high;
    private int currentLayer;
    private int tileid, xcell, ycell;
    
    //Instance
    private static LevelLoader instance = new LevelLoader();
    public static LevelLoader getInstance() { return instance; }
    
    @Override
    public void processOpenTag(String tagName, Attributes atts)
    {
        
    }
    
    @Override
    public void processContent(String tagName, String text)
    {
        switch(tagName)
        {
            case "width":
                wide = Integer.valueOf(text);
                break;
                
            case "height":
                high = Integer.valueOf(text);
                break;
                
            case "ndepths":
                grid = new Tile[Integer.valueOf(text)][high][wide];
                events = new int[high][wide];
                break;
                
            case "depth":
                currentLayer = Integer.valueOf(text);
                break;
                
            case "id":
            case "tileid":
                tileid = Integer.valueOf(text);
                break;
                
            case "x":
                xcell = Integer.valueOf(text);
                break;
                
            case "y":
                ycell = Integer.valueOf(text);
                break;
        }
    }
    
    @Override
    public void processCloseTag(String tagName)
    {
        if(tagName.equals("tile"))
        {
            Tile t = Launcher.tileMap.get(tileid);
            grid[currentLayer][ycell][xcell] = t;
            if(t instanceof AnimTile)
            {
                animTiles.add(new Triple(xcell, ycell, currentLayer));
            }
        }
    }
    
    public Tile[][][] getData()
    {
        return grid;
    }
    
    public ArrayList<Triple> getAnimTiles()
    {
        ArrayList tiles = animTiles;
        animTiles = new ArrayList<>();
        return tiles;
    }
}
