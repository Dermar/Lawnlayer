package lawnlayer;

import processing.core.PApplet;
import processing.core.PImage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class InitialisationTests {
    App app;

    @Test
    public void testConstructors() {
        PImage mockPImage = new PImage();
        assertNotNull(new LawnLayer(0, 80, mockPImage, new ArrayList<Tile>(), new ArrayList<Tile>(), 
        new ArrayList<Enemy>(), mockPImage, mockPImage, mockPImage, 3));
        assertNotNull(new Enemy(40, 100, mockPImage, new ArrayList<Tile>(), new ArrayList<Tile>(), "0"));
        assertNotNull(new Tile(0, 80, mockPImage, "red", mockPImage));
        assertNotNull(new PowerUp(60, 80, mockPImage));

    }

    @Test
    public void testSetup(){
        App app = new App();
        app.noLoop(); //optional
        // Tell PApplet to create the worker threads for the program
        PApplet.runSketch(new String[] {"App"}, app);
        app.delay(1000); //to give time to initialise stuff before drawing begins
        
        // checkLeveloutlays, levelenems, lives, and levelGoals
        assertEquals(app.lives, 3);
        assertIterableEquals(app.levelOutlays, new ArrayList<String>() {
            {
                add("level1.txt");
                add("level2.txt");
                
            }
        });
        assertEquals(app.levelGoals.size(), 2);
        assert(Float.compare(app.levelGoals.get(0), (float)0.02) == 0);
        assert(Float.compare(app.levelGoals.get(1), (float)0.01) == 0);
        
        assertIterableEquals(app.levelEnems, new ArrayList<ArrayList<String>>() {
            {
                add((new ArrayList<String>(){
                    {
                        add("0");
                        add("random");
                        add("0");
                        add("random");
                        
                    }
                }));
                add((new ArrayList<String>(){
                    {
                        add("0");
                        add("10,23");
                        add("0");
                        add("random");
                        add("1");
                        add("random");
                        
                    }
                }));
                
            }
        });

        // Check that enemies and concrete tiles for the file have been initiallised correctly
        assertEquals(app.levelNum, 0);
        assertEquals(app.enemies.size(), 2);
        // randomised location is not in concrete:
        assert(app.enemies.get(0).x > 20 && app.enemies.get(0).x < 1260);
        assert(app.enemies.get(0).y > 80 && app.enemies.get(0).y < 700);
        assert(app.concreteTiles.size() == 188);
        
    }

    public void testLvProgression(){
        PImage mockPImage = new PImage();
        for (int i = 0; i < 20; i++){
            app.farmTiles.add(new Tile(0, 80, mockPImage, "grass", mockPImage));
        }
        // check has initialised correctly for lv2
        assert(app.levelNum == 1);
        assert(app.concreteTiles.size() == 233);
        assert(app.farmTiles.size() == 0);
        assert(app.enemies.size() == 3);
        assert(app.enemies.get(0).x == 10 * 20 && app.enemies.get(0).x == 80 + 23 *20);
        
        // 
        for (int i = 0; i < 20; i++){
            app.farmTiles.add(new Tile(0, 80, mockPImage, "grass", mockPImage));
        }
        assert(app.hasWon);

    }
}
