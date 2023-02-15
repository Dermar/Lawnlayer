package lawnlayer;
import processing.core.PImage;
import processing.core.PApplet;

public class PowerUp extends GameObject implements Collide<LawnLayer>{
    private int timer = 1200; // stays on screen for 20 seconds
    private int delay = 600; // only appears on-screen after 10 seconds
    /**Constructor for powerUp
     * @param x the x position
     * @param y the y-coord
     * @param sprite the image for the powerup
     */
    public PowerUp(int x, int y, PImage sprite){
        super(x, y, sprite);
    }

    /** Computes changes in the powerup each second */
    public void tick(){
        delay--;
        if (delay == 0){
            timer--;
        }
    }

    /** Draw method which only draws if the object is within its timer and after a delay
     * @param app handles the drawing of the object
     */
    public void draw(PApplet app){
        if (timer > 0 && delay == 0){
            app.image(sprite, x, y);
        }
    }

    /** A method for computing what should happen when the ball picks up the powerup
     * @param ball the lawnLayer- the user
     * @return a boolean of whether a collision has occurred
     */
    public boolean collide(LawnLayer ball){
        return true;
    }

}
