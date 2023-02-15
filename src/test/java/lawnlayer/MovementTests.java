package lawnlayer;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

public class MovementTests {

    @Test
    public void testBallConcMovement() {
        PImage mockPImage = new PImage();
        LawnLayer ball = new LawnLayer(0, 80, mockPImage, new ArrayList<Tile>(), new ArrayList<Tile>(), 
        new ArrayList<Enemy>(), mockPImage, mockPImage, mockPImage, 3);
        // I'll move the ball in different (awkwardly timed for conciseness) directions to test the pixel movement and borders
        
        // Check right movmement
        ball.pressRight();
        assert(ball.x == 2 && ball.y == 80);

        // Check left movement then left border
        ball.pressLeft();
        assert(ball.x == 0 && ball.y == 80);
        ball.pressLeft();
        assert(ball.x == 0 && ball.y == 80);

        //Check down movement
        ball.pressDown();
        assert(ball.x == 0 && ball.y == 82);

        // Check top movement and top border 
        ball.pressUp();
        assert(ball.x == 0 && ball.y == 80);
        ball.pressUp();
        assert(ball.x == 0 && ball.y == 80);

        // iterate all the way to the right border to check that border

        for(int i = 0; i < 642; i++){
            ball.pressRight();
        }
        assert(ball.x == 1260 && ball.y == 80);

        // iterate to the bottom border
        for (int k = 0; k < 352; k++){
            ball.pressDown();
        }
        assert(ball.x == 1260 && ball.y == 700);
    }

    @Test
    public void testBallDirtMovement(){
        App app = new App();
        app.noLoop();
        // Tell PApplet to create the worker threads for the program
        PApplet.runSketch(new String[] {"App"}, app);
        app.delay(1000); //to give time to initialise stuff before drawing begins
        app.keyCode = 39; 
        app.keyPressed();
        app.keyReleased();

        PImage mockPImage = new PImage();
        ArrayList<Tile> concrete = new ArrayList<Tile>();
        ArrayList<Tile> grass = new ArrayList<Tile>();
        LawnLayer ball = new LawnLayer(0, 80, mockPImage, concrete, grass, 
        new ArrayList<Enemy>(), mockPImage, mockPImage, mockPImage, 3);
        
        // move the ball partially into the right dirt tile
        ball.setCurrDir("right");
        ball.giveDir(39);
        
        ball.move();
        assertEquals(ball.getCurrTile(),"dirt");
        assertEquals(ball.getCurrDir(),"right");
        for (int i = 0; i < 8; i++){
           ball.tick(); 
        }

        // try to move the ball down- we shouldn't be able to since we're not completely in the next tile.
        ball.giveDir(40);
        ball.tick();
        assert(ball.getDirections().get("right"));
        ball.giveDir(39);

        // even when the ball gets to the next tile, it won't change its direction to left since that's the opposite direction
        ball.tick();
        ball.tick();
        ball.setCurrDir("right");
        
        grass.add(new Tile(20, 80, mockPImage, "grass", mockPImage)); // it reacts the same on grass
        ball.giveDir(37);
        
        ball.tick();
        assert(ball.getDirections().get("right"));
        
        // and stops on concrete
        ball.setDir("");
        ball.giveDir(0);
        concrete.add(new Tile(40, 80, mockPImage, "concrete", mockPImage));
        for(int i = 0; i < 10; i++){
            ball.tick();
        }
        
        assert(!ball.getDirections().get("right"));
    }

    @Test
    public void testPathCreation(){
        PImage mockPImage = new PImage();
        ArrayList<Tile> concrete = new ArrayList<Tile>();
        ArrayList<Tile> grass = new ArrayList<Tile>();
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(40, 100, mockPImage, new ArrayList<Tile>(), new ArrayList<Tile>(), "0"));
        concrete.add(new Tile(0, 80, mockPImage, "grass", mockPImage));
        LawnLayer ball = new LawnLayer(1000, 400, mockPImage, concrete, grass, 
        enemies, mockPImage, mockPImage, mockPImage, 3);
        ball.setCurrDir("down");
        ball.setDir("down");
        ball.giveDir(40);
        
        for(int i = 0; i < 20; i++){
            ball.tick();
        }

        ball.giveDir(37);
        ball.setDir("left");
        ball.setCurrDir("left");
        for(int i = 0; i < 10; i++){
            ball.tick();
        }
        
        // See that we've drawn a path size of 3
        assert(ball.getPath().size() == 3);
        ArrayList<Tile> ballPath = ball.getPath();
        
        // test tile redden method
        ballPath.get(0).redden();
        assertEquals(ballPath.get(0).getType(), "red");
    
    }
}
