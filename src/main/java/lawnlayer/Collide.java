package lawnlayer;
/** An interface to check collision between an object and all the objects in a given Arraylist
 */
public interface Collide<M> {

    /**
     * A method that checks collision between two objects
     * @param single a single object in the arraylist given in the 'collideAll' method
     * @return boolean of whether 'single' has collided with this or not
     */
    public boolean collide(M single);

}
