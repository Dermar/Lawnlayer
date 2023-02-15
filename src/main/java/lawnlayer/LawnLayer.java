package lawnlayer;
import java.util.ArrayList;
import processing.core.PImage;
import processing.core.PApplet;
import java.util.HashMap;
import java.util.Vector;
import java.awt.Point;

public class LawnLayer extends MovingObject{
    private int lives;

    // Path hit by enemy variables:
    private int ticker; // used to count up to three frames if a pathtile is hit
    private int iterator; // used to keep track of index in path that should be reddened
    private boolean pathDying; // used to check if the path needs to start reddening up to the ball or not

    /**A hashmap with the lawnlayer's chosen direction
     * The keys are the player's direction so their x or y value can be changed accordingly
     * The value is the boolean that will be checked when the ball moves
     */ 
    private HashMap<String, Boolean> directions  = new HashMap<String, Boolean>() {{
        put("right", false);
        put("left", false);
        put("up", false);
        put("down", false);
    }};
    /** Used to traverse completely into a tile
     * It saves the direction that is currently true in 'directions'
     */ 
    private String currDir; 
    /**
     * The key the user has just pressed (given from app). 
     * Used to change directions in one-tile movement by only updating currDir to this
     * When the ball is on a new tile
     */
    private String savedDir;
    /**
     * The last direction that the user has chosen. If the user is on dirt,
     * even if all directions are false in movementDir the user will keep going by following 
     * this direction
     */
    private String lastDir;

    /**
     * Arraylists saving the ball's path on dirt, the concrete in the level, the grass tiles, and the enemies.
     */
    private ArrayList<Tile> path = new ArrayList<Tile>();
    private ArrayList<Tile> concreteTiles = new ArrayList<Tile>();
    private ArrayList<Tile> farmTiles = new ArrayList<Tile>();
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private String currTile;

    private PImage greenPath;
    private PImage redPath;
    private PImage grass;
    
    /**Constructor for the lawnlayer
     * @param x the ball's x-coord
     * @param y the ball's y-coord
     * @param sprite the image of the ball
     * @param concTiles all the concrete tiles in the current level
     * @param farmTiles all the farm/grass tiles in the current level
     * @param enemies all the enemies in the current level
     * @param green the sprite for the green path, to be used when creating a path
     * @param red the sprite for the red path, to be used if/when a path is hit by an enemy 
     * @param grass the image of the grass tile
     * @param lives the number of times the lawnlayer can die before it's game over
     */ 
    public LawnLayer(int x, int y, PImage sprite, ArrayList<Tile> concTiles, ArrayList<Tile> farmTiles, 
    ArrayList<Enemy> enemies, PImage green, PImage red, PImage grass, int lives){
        super(x, y, sprite);
        this.concreteTiles = concTiles;
        this.farmTiles = farmTiles;
        this.enemies = enemies;
        this.lives = lives;
        currTile = "concrete";
        currDir = "";
        savedDir = currDir;
        greenPath = green;
        redPath = red;
        this.grass = grass;
    }

    /**
     * Draws the ball and the path to screen- overrides the GameObject draw method
     * @param app the app file will draw the image for us - overriden from GameObject
     */
    public void draw(PApplet app){
        for (Tile square: path){
            square.draw(app);
        }
        for (Tile grass: farmTiles){
            grass.draw(app);
        }

        app.image(sprite, x, y);
        
    }

    /**
     * Computes the ball's changes each frame. Includes movement and subsequent collision detections.
     */
    public void tick(){
        move();
        
        for(Enemy bug: enemies){
            // lose a life if you collide with an enemy
            if (collideEnemy(bug)){
                x = 0;
                y = 80;
                lives -= 1;
                currDir = "";
            }  
            // start red tile propagation process if enemy hits a path tile
            Tile tileHit = bug.collideAll(path);
            if (tileHit != null){
                // To deal with the case where the path has been hit more than once,
                // only update iterator if the path tile that was just hit is closer to the player than the 
                // previous tile that was hit
                if (pathDying && path.indexOf(tileHit) > iterator){
                    iterator = path.indexOf(tileHit);
                }
                else if (!pathDying){
                    iterator = path.indexOf(tileHit);
                    pathDying = true;
                }
                
            }
        }
        
        // If the path has been hit, then redden the path
        if (pathDying){
            ticker++;
            propagateRed();
        }

        // Lose a life if you collide with your own path
            // first compute a path that doesn't include the last path 
            // the ball just put since they'll always be colliding with that
            // also remove the second last path bc path widths mean that the layer is likely to collide with that while turning
        ArrayList<Tile> pathWoLast = new ArrayList<Tile>(path);
        if (pathWoLast.size() != 0){
            pathWoLast.remove(pathWoLast.size() - 1);
            
        }
        if (pathWoLast.size() != 0){
            pathWoLast.remove(pathWoLast.size() - 1);
        }
        
        if (this.collideAll(pathWoLast) != null){
            x = 0;
            y = 80;
            lives -= 1;
            currDir = "";
        }
        
    }


    /**
     * Maintains continuous or tile-to-tile movement depending on what tile the ball is on.
     * Also sets up whether the ball will place a path in that position
     */
    public void move(){
        // If we're not within a tile properly, then keep going in the current direction
        if (x % 20 != 0 || y % 20 != 0){
            allButOne(currDir);
        }
        
        else{
            // We're now in another tile so update 'currTile' and place down the path if needed
            currTile = getCurTile(x, y, concreteTiles, farmTiles);
            createPath();
            // Set all directions to false until we get a new input.
            allButOne("");
            // Define one-tile movement for concrete tiles
            if (currTile.equals("concrete")){
                currDir = savedDir;
                directions.put(currDir, true); 
            }
            // Continuous movement for dirt and grass
            else{
                // make sure the player can't move in the opposite direction
                if ((currDir.equals("left") && (lastDir.equals("up") || lastDir.equals("down")))
                || (currDir.equals("right") && (lastDir.equals("up") || lastDir.equals("down")))
                || (currDir.equals("up") && (lastDir.equals("right") || lastDir.equals("left")))
                || (currDir.equals("down") && (lastDir.equals("right") || lastDir.equals("left")))){
                    currDir = lastDir;
                }
                directions.put(currDir, true); 
            }
        }
        turn();
        
    }

    /**
     * Move according to which direction in our 'directions' hashmap is true
     */ 
    public void turn(){
        if (directions.get("left")){
            pressLeft();
        }
        else if (directions.get("right")){
            pressRight();
        }
        else if (directions.get("up")){
            pressUp();
        }
        else if (directions.get("down")){
            pressDown();
        }
    }

    /** To traverse cleanly between tiles, make all directions 
     * except the direction you want to go to false so that the user 
     * isn't allowed to switch directions until they've moved completely into a tile
     * @param currDir the current direction that the user is going in
     */
    public void allButOne(String currDir){
        for (String eachDir: directions.keySet()){
            if (eachDir.equals(currDir)){
                directions.put(eachDir, true);
            }
            else{
                directions.put(eachDir, false);
            }
        }
    }
       
    /**
     * Move left when the user has pressed left and we're at a new tile
     */
    public void pressLeft() {
        // Maintain the left border
        if (x <= 0){
            return;
        }
        x -= 2;   
    }

    /**
     * Move right when the user has pressed right and we're at a new tile
     */
    public void pressRight() {
        // Maintain the right border
        if (x >= 1260){
            return;
        }
        x += 2;
    }

    /**
     * Move up when the user has pressed up and we're at a new tile
     */
    public void pressUp(){
        // Maintain the top border
        if (y <= 80){
            return;
        }
        y -= 2;
    }

    /**
     * Move down when the user has pressed down and we're at a new tile
     */
    public void pressDown() {
        // Maintain the bottom border
        if (y >= 700){
            return;
        }
        y += 2;
    }

    /** Check if the lawnlayer has collided with an enemy
     * @param enemy the enemy we're checking if we've collided with.
     * @return boolean of whether we've collided with enemy or not.
     */
    public boolean collideEnemy(Enemy enemy){
        boolean collided = true;
            // The objects do NOT overlap if one is completely above, below, to the right of, or to the left of the other:
            if (y + this.getHeight() < enemy.y || y > enemy.y + enemy.getHeight() || x > enemy.x + enemy.getWidth() || x + this.getWidth() < enemy.x){
                collided = false;
            }
        return collided;
    }

    /**
     * Place a green path tile at the player's new tile if it's a dirt tile
     */
    public void createPath(){
        if (!currTile.equals("dirt")){
            pathDying = false;
            ticker = 0;
            iterator = 0;
            if (path.size() > 0){
                // No matter what, you'll always fill the path with grass, so do that first:
                for (Tile square: path){
                    Tile newGrass = new Tile(square.x, square.y, grass, "grass", redPath);
                    farmTiles.add(newGrass);
                }
                // Compute a tile inside the path's area to start using flood fill algorithm
                int[] coords= getIntCoords(path.get(0), path.get(path.size() - 1));
                // Now we can try to place the grass inside the area if it's not a straight line and there are no enemies inside it
                if (!(coords[0] == 0) && !(coords [1] == 0)){
                    //floodFill(coords);
                    // ^THE ABOVE METHOD WORKS MOST OF THE TIME BUT MY IMPLEMENTATION DOESN'T PICK A POINT INSIDE THE RECTANGLE CORRECTLY .. 
                    // NOT SURE IF THIS IS WORTH ANY MARKS OR NOT
                }
                
            }
            path.clear();  
            return;
        }
        // Set the coords of the new path tile according to the lawnlayer's coords
        Tile newPath = new Tile(x, y, this.greenPath, "path", redPath);
        path.add(newPath);
    }


    /** A method to find a tile within the area created by the path
     * @param startPath the beginning of the path, which we're going to use to find out if we're in the case where either the tile inside the area is to the left, right, above, or below the start tile.
     * @param endPath the end of the path
     * @return an array of ints with the coordinates of the tile inside the area- null if no such tile is found
     */
    public int[] getIntCoords(Tile startPath, Tile endPath){
        boolean isY = true;
        // Check where we just came from. If we came from the right or left, then we're in the case 
        // where the inner tile is within the area across the x axis
        if (directions.get("left") || directions.get("right")){
            isY = false;
        }

        int[] coords = new int[2];
        // if the inner tile must be different to the startpath along the x-axis
        if (!isY){
            if (endPath.x > startPath.x){
                coords[0] = startPath.x + 20;
                coords[1] = startPath.y;
            }
            else if (endPath.x < startPath.x){
                coords[0] = startPath.x;
                coords[1] = startPath.y - 20;
            }
        }

        // if it's along the y-axis
        if (isY){
            if (endPath.y > startPath.y){
                coords[0] = startPath.x;
                coords[1] = startPath.y + 20;
            }
            else if (endPath.y < startPath.y){
                coords[0] = startPath.x;
                coords[1] = startPath.y - 20;
            }

        }
        return coords;
    }

    /** An algorithm that uses BFS to place grass on all tiles above, below, right, and left of the given coordinates
     * Adapted from: https://www.geeksforgeeks.org/flood-fill-algorithm/
     * @param coords the coordinates of a tile inside the made path shape
    */
    public void floodFill(int[] coords){
        // initialise an int of number of new grass tiles we're trying to add to farmTiles. 
        // In case we need to remove them if we are going to collide with an enemy
        int numAdded = 1;
        Vector<Point> dirt = new Vector<Point>();
        dirt.add(new Point(coords[0], coords[1]));
        Tile newGrass = new Tile(coords[0], coords[1], grass, "grass", redPath);
        farmTiles.add(newGrass);

        while (dirt.size() > 0){
            numAdded++;
            // Dequeue the front node
            Point thisDirt = dirt.get(dirt.size() - 1);
            dirt.remove(dirt.size() - 1);
   
            int posX = thisDirt.x;
            int posY = thisDirt.y;
            farmTiles.add(new Tile(posX, posY, grass, "grass", redPath));
            // Check if the adjacent pixels are dirt and if so, add them to newGrassTiles
            // if the tile is going to collide with an enemy then don't add any of the tiles in newGrassTiles
            
            // check tile to the right
            String tileType = getCurTile(posX + 20, posY, concreteTiles, farmTiles);
            if (tileType.equals("dirt")){
                dirt.add(new Point(posX + 20, posY));
                newGrass = new Tile(posX + 20, posY, grass, "grass", redPath);

                if (onEnemy(numAdded, newGrass)){
                    return;
                }
                
                farmTiles.add(newGrass);
            }
            
            // tile to the left
            tileType = getCurTile(posX - 20, posY, concreteTiles, farmTiles);
            if (tileType.equals("dirt")){
                dirt.add(new Point(posX - 20, posY));
                newGrass = new Tile(posX - 20, posY, grass, "grass", redPath);

                if (onEnemy(numAdded, newGrass)){
                    return;
                }
                
                farmTiles.add(newGrass);
            }

            // tile below
            tileType = getCurTile(posX, posY + 20, concreteTiles, farmTiles);
            if (tileType.equals("dirt")){
                dirt.add(new Point(posX, posY + 20));
                newGrass = new Tile(posX, posY + 20, grass, "grass", redPath);

                if (onEnemy(numAdded, newGrass)){
                    return;
                }
                
                farmTiles.add(newGrass);
            }

            // tile above
            tileType = getCurTile(posX, posY - 20, concreteTiles, farmTiles);
            if (tileType.equals("dirt")){
                dirt.add(new Point(posX, posY - 20));
                newGrass = new Tile(posX, posY - 20, grass, "grass", redPath);

                if (onEnemy(numAdded, newGrass)){
                    return;
                }
                
                farmTiles.add(newGrass);
            }
        }

    }
    
    /** A method which checks if a possible grass tile at that location would collied with an enemy
     * @param numAdded the number of grass tiles we just added to the end of the farm tiles arraylist
     * @param newGrass the tile that we're trying to add to the map
     * @return a boolean of whether the new grass tile would collide with an enemy or not
    */
    public boolean onEnemy(int numAdded, Tile newGrass){
        for (Enemy bug: enemies){
            if (bug.collide(newGrass)){
                // If the grass is going to collide with an enemy then remove all the grass tiles we just put in farmTiles
                while (numAdded > 0){
                    farmTiles.remove(farmTiles.size() - 1);
                    numAdded--;
                }
                
                return true;
            }
        }
        return false;
    }

    /** If an enemy hits the player's path, then start reddening the path tiles up to them*/
    public void propagateRed(){
        if (ticker % 3 == 0){
            iterator++;
            path.get(iterator).redden(); 
            // If the path that the ball has just placed was just reddened, then kill the ball
            if (iterator + 1 == path.size()){
                x = 0;
                y = 80;
                path.clear();
                currDir = "";
            }
        }
        
    }

    /**Set wanted direction
     * @param newDir the direction that we want to change the saved direction to 
     */ 
    public void setDir(String newDir){
        savedDir = newDir; 
    }

    /**Set actual direction
     * @param newDir the direction that we want to change the direction to 
     */ 
    public void setCurrDir(String newDir){
        currDir = newDir;
    }

    /** Gets the last arrow key the last arrow key the user has pressed from app 
     * Used to maintain continuous movement
     * @param key an int that represents which key the user has pressed. If it's one of the arrow keys, then we try to move in that direction.
     */  
    public void giveDir(int key){
        if (key == 37) {
            lastDir = "left";
        }
        if (key == 38) {
            lastDir = "up";
        }
        if (key == 39) {
            lastDir = "right";
        }
        if (key == 40) {
            lastDir = "down";
        }
    }

    /** Getter for currTile variable.
     * @return the tile that the object is currently on
    */
    public String getCurrTile(){
        return currTile;
    }

    /** Getter for currDir variable.
     * @return the direction that the ball is going in
    */
    public String getCurrDir(){
        return currDir;
    }

    /** Getter for if alive. Used to change the 'lives' variable in app 
     * @return the number of lives the ball has left
    */
    public int getLives(){
        return lives;
    }

    /** Getter for the ball's movement. Used for testing
     * @return an arraylist of the truth of each direction's movement
    */
    public HashMap<String, Boolean> getDirections(){
        return directions;
    }

    /** Getter for the path of the ball
     * @return an arraylist of each green/ red path tile.
     */
    public ArrayList<Tile> getPath(){
        return path;
    }
}
