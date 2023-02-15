package lawnlayer;

import java.util.ArrayList;
import java.util.HashMap;
import processing.core.PImage;
import java.util.Random;

public class Enemy extends MovingObject{
    private String type;
    // A hashmap containing which diagonal direction the enemy will be moving in
    private HashMap<String, Boolean> movementType = new HashMap<String, Boolean>() {{
        put("NE", false);
        put("NW", false);
        put("SE", false);
        put("SW", false);
    }};

    // Arraylists of the map tiles
    private ArrayList<Tile> concreteTiles = new ArrayList<Tile>();
    private ArrayList<Tile> farmTiles = new ArrayList<Tile>();

    /** The constructor
     * @param x the enemy's x-coord
     * @param y the enemy's y-coord
     * @param sprite the image of the enemy
     * @param concreteTiles an ArrayList of all the concrete tiles in the level
     * @param farmTiles an ArrayList of all the grass tiles in the level
     * @param type a string, "0" or "1" depending on whether the enemy is a worm or beetle respectively 
     */
    public Enemy(int x, int y, PImage sprite, ArrayList<Tile> concreteTiles, ArrayList<Tile> farmTiles, String type){
        super(x, y, sprite);
        this.concreteTiles = concreteTiles;
        this.farmTiles = farmTiles;
        this.type = type;

        // Make the enemy move in a random direction on start-up
        Random direction = new Random();
        int moveDir = direction.nextInt(4);
        int i = 0;
        for (String compass: movementType.keySet()){
            if (i == moveDir){
                movementType.put(compass, true);
            }
            i++;
        }
        
    }

    /** The enemy moves by first checking if it needs to change its direction and then
     * changing its x and y coords depending
     */
    public void move(){
        Tile hit = checkCollided();
        if (hit != null){
            computeRefraction(hit);
        }
        cardinalMovement();
    }

    /** The change in the enemy per frame */
    public void tick(){
        move();
    }

    /** This method checks whether the enemy has collided with an enemy this turn
     * @return the tile that the enemy has collided with, null if they haven't collided with anything
    */
    public Tile checkCollided(){
        // Use the collided method from the MovingObject class against the concrete
        // and the grass tiles to see if the enemy has collided against one in this frame or not
        Tile concCollided = this.collideAll(concreteTiles);
        Tile farmCollided = this.collideAll(farmTiles);
        if (concCollided == null && farmCollided == null){
            return null;
        }

        Tile collided = concreteTiles.get(0); // placeholder until we get the actual values of the tile
        if (concCollided != null){
            collided = concCollided; 
        }
        else{
            collided = farmCollided;
            if (type.equals("1")){
                farmTiles.remove(collided);
            }  
        }
        return collided;
    }
        
    /**Computes the refraction by changing the movement direction of the enemy
     * depending on if/how they hit the tile
     * @param collided the tile that the enemy has collided with
    */     
    public void computeRefraction(Tile collided){
        // if we're moving northeast, then we could've only hit the left or bottom side of the tile
        if (movementType.get("NE")){
            movementType.put("NE", false);
            // if bottom side
            if (collided.y + collided.getHeight() - 3 <= this.y && (!(this.x + this.getWidth() - 3 <= collided.x))){
                movementType.put("SE", true); 
            }
            else{
                movementType.put("NW", true);
            }
        }
                
        // if we're moving northwest, then we could've only hit the bottom or right side of the tile
        else if (movementType.get("NW")){
            movementType.put("NW", false);
            // if bottom side
            if (collided.y + collided.getHeight() - 3 <= this.y && ((this.x - 3 >= collided.x + collided.getWidth()))){
                movementType.put("SW", true);
            }
            else{
                movementType.put("NE", true);
            }
        }
        
        // if we're moving southeast, then we only could've hit the left or top side of the tile
        else if (movementType.get("SE")){
            movementType.put("SE", false);
            // if top side
            if (collided.y + 3 >= this.y + this.getHeight() && (!(this.x + this.getWidth() - 3 <= collided.x))){
                movementType.put("NE", true);
            }
            else{
                movementType.put("SW", true);
            }
        }

        // if we're moving southwest, then we only could've hit the top or right side of the tile
        else if (movementType.get("SW")){
            movementType.put("SW", false);
            // if top side
            if (collided.y + 3 >= this.y + this.getHeight() && (!(this.x - 3 >= collided.x + collided.getWidth()))){
                movementType.put("NW", true);
            }
            else{
                movementType.put("SE", true);
            }
        }
    
    }
        
    /** Move the enemy diagonally depending on which direction they're going in */
    public void cardinalMovement(){
        if (movementType.get("NE")){
            x += 2;
            y -= 2;
        }
        else if (movementType.get("NW")){
            x -= 2;
            y -= 2;
        }
        else if (movementType.get("SE")){
            x += 2;
            y += 2;
        }
        else if (movementType.get("SW")){
            x -= 2;
            y += 2;
        }
    }

}
        
