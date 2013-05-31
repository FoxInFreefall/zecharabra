/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public class Launcher extends JFrame implements Settings, KeyListener
{
    //Double buffer
    public Image buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    public Image commit = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    public Graphics canvas = buffer.getGraphics();
    public Semaphore mutex = new Semaphore(1);
    
    //Status
    public int nFrames = 0;
    public int lastNFrames = 0;
    public long lastSecond = 0;
    
    //Player
    public Player player = new Player();
    
    //Grid
    public static HashMap<Integer, Tile> tileMap;
    public static Tile[][][] grid;
    public static int[][] collisions;
    public Image[] layers;
    
    //AnimGrid
    public static ArrayList<AnimTile> animTiles;
    public static ArrayList<Triple> animTilesOnGrid;
    public ArrayList<Triple> updateTiles = new ArrayList<>();
    public long lastAnimFrame = System.currentTimeMillis();
    
    public Launcher() 
    {
        //Set up applet
        setUndecorated(true);
        initComponents();
        setSize(width, height);
        
        //Load tiles
        TileLoader.getInstance().load("/tiles/tiles.xml");
        tileMap = TileLoader.getInstance().getTiles();
        animTiles = TileLoader.getInstance().getAnimTiles();
        System.out.println(tileMap);
        
        //Load level
        LevelLoader.getInstance().load("/levels/level.xml");
        grid = LevelLoader.getInstance().getData();
        animTilesOnGrid = LevelLoader.getInstance().getAnimTiles();
        collisions = new int[grid[0].length][grid[0][0].length];
        layers = new Image[grid.length];
        for(int l = 0; l < layers.length; l++)
        {
            //Create image for this layer
            Image image = new BufferedImage(grid[0][0].length*cell_size, grid[0].length*cell_size, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            
            if(l == 0)
            {
                g.setColor(Color.gray);
                g.fillRect(0, 0, image.getWidth(this), image.getHeight(this));
            }
            
            //Paint all tiles on this layer to this image
            for(int row = 0; row < grid[0].length; row++)
            {
                for(int col = 0; col < grid[0][0].length; col++)
                {
                    Tile tile = grid[l][row][col];
                    if(tile != null)
                    {
                        g.drawImage(tile.image, col*cell_size, row*cell_size, this);
                        collisions[row][col] |= tile.collision;
                    }
                }
            }
            
            layers[l] = image;
        }
        
        new Render(this);
        new Update(this);
        new Commit(this);
        
        addKeyListener(this);
    }
    
    public void update()
    {
        //Move player
        int dir = player.dir;
        
        //settle within a cell
        if(dir == -1)
        {
            if(!player.isAligned())
            {
                dir = player.lastdir;
            }
        }
        
        //collision
        if(player.isAligned())
        {
            if(player.nextdir != -1)
            {
                player.dir = player.nextdir;
                player.nextdir = -1;
                dir = player.dir;
            }
            
            if(player.x > 0 && player.x < width && player.y > 0 && player.y < height)
            {
                switch(dir)
                {
                    case 0:
                        if((collisions[(int)player.y / cell_size][(int)player.x / cell_size + 1] & 0x1) == 1)
                        {
                            player.stop();
                            dir = -1;
                        }
                        break;
                    case 1:
                        if((collisions[(int)player.y / cell_size + 1][(int)player.x / cell_size] & 0x1) == 1)
                        {
                            player.stop();
                            dir = -1;
                        }
                        break;
                    case 2:
                        if((collisions[(int)player.y / cell_size][(int)player.x / cell_size - 1] & 0x1) == 1)
                        {
                            player.stop();
                            dir = -1;
                        }
                        break;
                    case 3:
                        if((collisions[(int)player.y / cell_size - 1][(int)player.x / cell_size] & 0x1) == 1)
                        {
                            player.stop();
                            dir = -1;
                        }
                        break;
                }
            }
        }
        
        //move
        switch(dir)
        {
            case 0: player.x += player_speed; break;
            case 1: player.y += player_speed; break;
            case 2: player.x -= player_speed; break;
            case 3: player.y -= player_speed; break;
        }
        
        //Progress animated tiles
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastAnimFrame >= animtile_frameDelay)
        {
            for(int i = 0; i < animTiles.size(); i++)
            {
                animTiles.get(i).progress();
            }
            for(int i = 0; i < animTilesOnGrid.size(); i++)
            {
                updateTiles.add(animTilesOnGrid.get(i));
            }
            lastAnimFrame = currentTime;
        }
    }
    
    public void render()
    {
        mutex.down();
        
            //Clean canvas
            canvas.setColor(Color.black);
            canvas.fillRect(0, 0, width, height);

            //Update tiles
            for(int i = 0; i < updateTiles.size(); i++)
            {
                Triple t = updateTiles.remove(0);
                i--;
                layers[t.z].getGraphics().drawImage(grid[t.z][t.y][t.x].image, t.x * cell_size, t.y * cell_size, this);
            }

            //Ground and bottom decoration
            canvas.drawImage(layers[0], (int)-player.x + width/2 - cell_size/2, (int)-player.y + height/2 - cell_size/2, this);
            canvas.drawImage(layers[1], (int)-player.x + width/2 - cell_size/2, (int)-player.y + height/2 - cell_size/2, this);

            //Display player
            canvas.setColor(Color.yellow);
//            canvas.fillRect((int) player.x, (int) player.y, 32, 32);
            canvas.fillRect(width/2 - cell_size/2, height/2 - cell_size/2, 32, 32);
            
            //Top decoration
            canvas.drawImage(layers[2], (int)-player.x + width/2 - cell_size/2, (int)-player.y + height/2 - cell_size/2, this);
            canvas.drawImage(layers[3], (int)-player.x + width/2 - cell_size/2, (int)-player.y + height/2 - cell_size/2, this);
            
            //Display FPS
            canvas.setColor(Color.white);
            canvas.drawString("FPS " + String.valueOf(lastNFrames), width - 75, 40);
            
            //Commit
            commit.getGraphics().drawImage(buffer, 0, 0, this);
        
        mutex.up();
    }

    @Override
    public void paint(Graphics g) 
    {
        //Wait for render to commit
//        mutex.down();
//        mutex.up();

        //Display on screen
        g.drawImage(commit, 0, 0, this);
//        g.drawImage(commit, 0, 0, 1920, 1080, this);

        //Get actual FPS
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastSecond >= 1000)
        {
            lastSecond = currentTime;
            lastNFrames = nFrames;
            nFrames = 0;
        }
        else
        {
            nFrames++;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Launcher().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    
    //Keys
    private boolean right_pressed = false;
    private boolean down_pressed = false;
    private boolean left_pressed = false;
    private boolean up_pressed = false;
    @Override
    public void keyTyped(KeyEvent ke) 
    {
        
    }

    @Override
    public void keyPressed(KeyEvent ke) 
    {
        switch(ke.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                if(!right_pressed)
                {
                    if(player.isAligned())
                    {
                        player.dir = 0;
                    }
                    else
                    {
                        player.nextdir = 0;
                    }
                }
                right_pressed = true;
                break;
            case KeyEvent.VK_DOWN:
                if(!down_pressed)
                {
                    if(player.isAligned())
                    {
                        player.dir = 1;
                    }
                    else
                    {
                        player.nextdir = 1;
                    }
                }
                down_pressed = true;
                break;
            case KeyEvent.VK_LEFT:
                if(!left_pressed)
                {
                    if(player.isAligned())
                    {
                        player.dir = 2;
                    }
                    else
                    {
                        player.nextdir = 2;
                    }
                }
                left_pressed = true;
                break;
            case KeyEvent.VK_UP:
                if(!up_pressed)
                {
                    if(player.isAligned())
                    {
                        player.dir = 3;
                    }
                    else
                    {
                        player.nextdir = 3;
                    }
                }
                up_pressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) 
    {
        switch(ke.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                if(player.dir == 0)
                {
                    player.stop();
                }
                right_pressed = false;
                break;
            case KeyEvent.VK_DOWN:
                if(player.dir == 1)
                {
                    player.stop();
                }
                down_pressed = false;
                break;
            case KeyEvent.VK_LEFT:
                if(player.dir == 2)
                {
                    player.stop();
                }
                left_pressed = false;
                break;
            case KeyEvent.VK_UP:
                if(player.dir == 3)
                {
                    player.stop();
                }
                up_pressed = false;
                break;
        }
    }
}
