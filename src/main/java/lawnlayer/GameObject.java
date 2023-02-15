package lawnlayer;
import processing.core.PImage;
import processing.core.PApplet;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected PImage sprite;

    /**
     * The constructor for the gameobject class
     * @param x the x-coord pixel location of the object
     * @param y the y-coord pixel location of the object
     * @param sprite the image of the object
     */
    public GameObject(int x, int y, PImage sprite){
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    /**
     * Getter for the sprite's height
     * @return the sprite PImage's height
     */
    public int getHeight(){
        return sprite.height;
    }

    /**
     * Getter for the sprite's width
     * @return the sprite PImage's width
     */
    public int getWidth(){
        return sprite.width;
    }

    /** An abstract method for changes between frames to be implemented by child classes */
    public abstract void tick();

    /** Method to draw the object to screen
     * @param app the app file will draw the image for us
     */
    public void draw(PApplet app){
        app.image(sprite, x, y);
    }

}
