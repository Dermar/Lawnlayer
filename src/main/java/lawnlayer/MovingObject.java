package lawnlayer;
import processing.core.PImage;
import java.util.ArrayList;

public abstract class MovingObject extends GameObject implements Collide<Tile>{
    
    /**Constructor for moving objects
     * @param x the object's x-coord
     * @param y the object's y-coord
     * @param sprite the object's image
     */
    public MovingObject(int x, int y, PImage sprite){
        super(x, y, sprite);
    }

    /** Looks through the map and tells us what tile we're on 
     * @param x the x coord of the object
     * @param y the y coord
     * @param concreteTiles an arraylist of all the concrete tiles in the level
     * @param farmTiles an arraylist of all the grass tiles in the level
     * @return returns the type of the current tile in a string
    */
    public static String getCurTile(int x, int y, ArrayList<Tile> concreteTiles, ArrayList<Tile> farmTiles){
        for (Tile conc: concreteTiles){
            if (conc.x == x && conc.y == y){
                return "concrete";
            }
        }

        for (Tile grass: farmTiles){
            if (grass.x == x && grass.y == y){
                return "grass";
            }
        }

        return "dirt";
    }

    /**Check whether the object has collided with a tile by iterating through the given arraylist. 
     * The children decide what to do if a tile is returned.
     * @param tiles an arraylist of the tiles that we want to check if we've hit.
     * @return the tile that the object has hit. Null if the moving object has not hit anything
     */
    public Tile collideAll(ArrayList<Tile> tiles){
        for (Tile tile: tiles){
            if (collide(tile)){
                return tile;
            }    
        }
        return null;
    }

    /** Takes in a tile object and checks if the current moving object has collided with it
     * @param tile the tile that could've been collided with
     * @return a boolean: true if the moving object has collided with the tile and false if not
     */
    public boolean collide(Tile tile){
        int tileLeft = tile.x;
        int tileRight = tile.x + tile.getWidth();
        int tileTop = tile.y;
        int tileBottom = tile.y + tile.getHeight();

        int thisLeft = this.x;
        int thisRight = this.x + this.getWidth();
        int thisTop = this.y;
        int thisBottom = this.y + this.getHeight();

        // The objects do NOT overlap if one is completely above, below, to the right of, or to the left of the other:
        if (thisBottom < tileTop || thisTop > tileBottom || thisLeft > tileRight || thisRight < tileLeft){
            return false;
        }
        return true;
    }

    

    /** An abstract method to ensure all descendants of this class move*/
    public abstract void move();

}
