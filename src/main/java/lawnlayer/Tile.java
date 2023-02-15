package lawnlayer;
import processing.core.PImage;

public class Tile extends GameObject{
    private String type;
    private PImage red;

    /** Constructor for tiles
     * @param x the tile's x-coord
     * @param y the tile's y-coord
     * @param sprite the tile's picture
     * @param type a string representing the tile's type: "path", grass", or "concrete"
     * @param red the image of a red path tile. Used to redden paths when hit
     */
    public Tile(int x, int y, PImage sprite, String type, PImage red){
        super(x, y, sprite);
        this.type = type;
        this.red = red;

    }

    /** Changes in the tile each frame. Not much: just watching grass grow */
    public void tick(){}

    /** Getter for the tile's type
     * @return a string with the tile's type: "path", grass", "red", or "concrete"
    */
    public String getType(){
        return type;
    }

    /** Method for making a tile red- to be used when a tile is hit by an enemy */
    public void redden(){
        sprite = red;
        type = "red";
    }


}
