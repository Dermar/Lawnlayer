package lawnlayer;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PFont;

public class App extends PApplet {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public PFont f;

    public static final int FPS = 60;

    public String configPath;

    public ArrayList<String> levelOutlays = new ArrayList<String>();
    public ArrayList<ArrayList<String>> levelEnems = new ArrayList<ArrayList<String>>();
    public ArrayList<Float> levelGoals = new ArrayList<Float>();

    public ArrayList<Tile> concreteTiles = new ArrayList<Tile>();
    public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public ArrayList<Tile> farmTiles = new ArrayList<Tile>();

    public double currProg; // percentage of farm tiles to dirt tiles
    public int lives;
    public int levelNum;
    public boolean hasWon;

    public PImage redPath;
    public PImage greenPath;
	public PImage grass;
    public PImage concrete;
    public PImage worm;
    public PImage beetle;
    public PImage gardener;
    public LawnLayer lawnLayer;

    /**Constructor for the app*/
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialises the setting of the window size
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Reads into the game's 'config' file to place all levels' information
     * into the 'lives', 'levelEnems', 'levelOutlays', and 'levelGoals' variables
     */
    public void readConfig(){
        try{
            JSONObject json = loadJSONObject("config.json");
            int lives = json.getInt("lives");
            this.lives = lives;
            JSONArray levels = json.getJSONArray("levels");
            // Append the information for each level to their relevant level arraylists
            for (int i = 0; i < levels.size(); i++){
                JSONObject level = levels.getJSONObject(i); 
                
                levelOutlays.add(level.getString("outlay"));
                JSONArray enems = level.getJSONArray("enemies");
                // Append all the enemies
                ArrayList<String> theseEnems = new ArrayList<String>(); 
                for(int j = 0; j < enems.size(); j++){
                    JSONObject enemy = enems.getJSONObject(j);
                    theseEnems.add(Integer.toString(enemy.getInt("type")));
                    theseEnems.add(enemy.getString("spawn"));
                }
                levelEnems.add(theseEnems);
                levelGoals.add(level.getFloat("goal"));
            }
        }

        catch(Exception e){
            System.out.println(e);
        }
        
    }

    /**
     * Reads into the level's txt file
     * and initialises all the concreteTiles and places them in 'concreteTiles'
     */
    public void placeConcrete(){
        String filename = levelOutlays.get(levelNum);
        File file = new File(filename);
        try{
            Scanner scan = new Scanner(file);
            int x = 0;
            int y = 80;
            while (scan.hasNextLine()){
                x = 0;
                String row = scan.nextLine();
                for (String tile: row.split("")){
                    if (tile.equals("X")){
                        Tile newConcrete = new Tile(x, y, this.concrete, "concrete", redPath);
                        concreteTiles.add(newConcrete);
                    }
                    x += 20;
                }
                
                y += 20;
            }
            scan.close();
        }
        catch(FileNotFoundException f){
            System.out.println(f);
        }
    }

    /**
     * Looks through the 'levelEnems' variable
     * and initialise the level's enemies and places them in 'enemies'
     */
    public void spawnEnems(){
        ArrayList<String> bugs = levelEnems.get(levelNum);
        for (int i = 0; i < bugs.size(); i += 2){
            // Find type
            String type = bugs.get(i);
            PImage enemPic = null;
            // Find sprite
            if (type.equals("0")){
                enemPic = this.worm;
            }
            else{
                enemPic = this.beetle;
            }

            // Set coords, whether random or tiled
            String coords = bugs.get(i+1);

            Random xAx = new Random();
            Random yAx = new Random();
            int xCoord = 0;
            int yCoord = 0;
            Enemy newSpawn = new Enemy(xCoord, yCoord, enemPic, concreteTiles, farmTiles, type);
            if (coords.equals("random")){
                // Create a while loop that checks whether these coords spawns it in a concrete wall or not
                xCoord = xAx.nextInt(1240) + 20;
                yCoord = yAx.nextInt(600) + 100;
                newSpawn = new Enemy(xCoord, yCoord, enemPic, concreteTiles, farmTiles, type);
                while (newSpawn.collideAll(concreteTiles) != null){
                    xCoord = xAx.nextInt(1240) + 20;
                    yCoord = yAx.nextInt(600) + 100;
                }
            }
            else{
                String[] location = coords.split(",");
                xCoord = 20 * Integer.parseInt(location[0]);
                yCoord = 80 + 20 * Integer.parseInt(location[1]);
            }

            newSpawn = new Enemy(xCoord, yCoord, enemPic, concreteTiles, farmTiles, type);
            enemies.add(newSpawn);
        }  
         
    }

    /**
     * Load all resources such as images. Initialise the elements such as the 
     * player, enemies and map elements. 
     */
    public void setup() {
        frameRate(FPS);

        // Load images during setup
		this.grass = loadImage(this.getClass().getResource("grass.png").getPath());
        this.concrete = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        this.worm = loadImage(this.getClass().getResource("worm.png").getPath());
        this.beetle = loadImage(this.getClass().getResource("beetle.png").getPath());
        this.greenPath = loadImage(this.getClass().getResource("green.png").getPath());
        this.redPath = loadImage(this.getClass().getResource("red.png").getPath());
        
        // Read info from config
        this.readConfig();
        

        // Initialise the lawnlayer in the top right map corner
        this.gardener = loadImage(this.getClass().getResource("ball.png").getPath()); 
        this.lawnLayer = new LawnLayer(0, 80, this.gardener, concreteTiles, farmTiles, enemies, greenPath, redPath, grass, lives);

        // Run functions to set up variables
        this.placeConcrete();
        this.spawnEnems();

        // Font for text
        f = createFont("Arial", 16, true);
        
    }

    /**
     * Takes player input to tell the lawnlayer where to go
     */
    public void keyPressed() {
        if (this.keyCode == LEFT) {
            this.lawnLayer.setDir("left");
        }
        else if (this.keyCode == DOWN) {
            this.lawnLayer.setDir("down");
        } 
        else if (this.keyCode == RIGHT) {
            this.lawnLayer.setDir("right");
        }
        else if (this.keyCode == UP) {
            this.lawnLayer.setDir("up");
        }
    }

    /**
     * Draw all elements in the game by current frame.    
     */ 
    public void draw() {
        // Check if the lawnlayer has reached the goal. If they have, move to the next level or say good job.
        this.checkNewLevel();
        
        // Fill in the background
        fill(139,69,19);
        this.rect(-1, -1, WIDTH + 2, HEIGHT + 2);
        
        // Display game status if not continuing
        textFont(f, 50);
        fill(255);
        textAlign(CENTER);
        if (this.lawnLayer.getLives() <= 0){
            hasWon = false;
            text("Game Over", 640, 360);
            noLoop();
            return;
        }
        if (hasWon){
            text("You Won!", 640, 360);
            noLoop();
            return;
        }

        // Write the game stats
        textFont(f, 35);
        fill(255);
        text("Lives: " + this.lawnLayer.getLives(), 200, 35);
        text(Math.round(currProg) + "%/" + Math.round(levelGoals.get(levelNum) * 100) + "%", 800, 35);
        textFont(f, 27);
        text("Level " + (levelNum + 1), 1180, 60);

        // Draw the level's concrete tiles
        for (Tile concTile: concreteTiles){
            concTile.draw(this);
        }

        // for all the grass tiles
        for (Tile grass: farmTiles){
            grass.draw(this);
        }
        // Draw the lawnlayer
        this.lawnLayer.tick();
        this.lawnLayer.draw(this);
        
        // Draw the enemies
        for (Enemy bug: enemies){
            bug.tick();
            bug.draw(this);
        }
    
    }

    /**
     * Check if the player has reached the goal area percentage
     * If they have, then either process the next level or tell 'draw' to display the win screen
     */
    public void checkNewLevel(){
        // Check for the case where we've been given no input - the user should win straight away
        if (levelGoals.size() == 0){
            hasWon = true;
            return;
        }
        
        currProg = (farmTiles.size() / (2048.0 - concreteTiles.size())) * 100;

        // if the lawnlayer has met the level goal, increment the level and reset everything for the next level (unless they've won)
        if (currProg >= levelGoals.get(levelNum) * 100){
            if (levelNum == (levelGoals.size() - 1)){
                hasWon = true;
            }
            else{
                levelNum += 1;
                int oldLives = this.lawnLayer.getLives();
                this.lawnLayer = new LawnLayer(0, 80, this.gardener, concreteTiles, farmTiles, enemies, greenPath, redPath, grass, oldLives);
                enemies.clear();
                this.placeConcrete();
                this.spawnEnems();
                farmTiles.clear();
                

            }
            
        }
    }   

    /**
     * When the player has just pressed a key, give the lawnlayer that key
     * to maintain continuous movement
     */  
    public void keyReleased(){
        lawnLayer.setDir("");
        lawnLayer.giveDir(keyCode);
    }

    /** Main method
     * @param args a list of the command-line arguments. Unused
    */
    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }
}
