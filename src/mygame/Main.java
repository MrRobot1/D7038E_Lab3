package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.Node;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Random;

/**
 * An example using two AppStates to introduce a way to programmatically pause
 * the program (a "game") and ask the user to press a key on the keyboard to
 * play the game" again. If - when the call to press a key is visible -  P is 
 * pressed, the "game" restarts and if E is pressed the whole program instead terminates. 
 *
 * @author HÃ¥kan Jonsson
 */
public class Main extends SimpleApplication {

    private Ask ask = new Ask();
    private Game game = new Game();
    private float time = 30f;
    
    private boolean running = true;

    public Main() {
        System.out.println("RestartGameDemo: in the constructor");
        ask.setEnabled(false);
        game.setEnabled(true);
        stateManager.attach(game);
        stateManager.attach(ask);
        game.spawnPlayers(3);
    }

    public static void main(String[] args) {
        System.out.println("RestartGameDemo: main");
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        System.out.println("RestartGameDemo: simpleInitApp");
        cam.setLocation(new Vector3f(-84f, 0.0f, 720f));
        cam.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
        setDisplayStatView(false);
        setDisplayFps(false);
        
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            System.out.println("RestartGameDemo/actionlistener: onAction");
            if (isPressed) { // on the key being pressed...
                if (name.equals("Exit")) {
                    Main.this.stop(); //terminate jMonkeyEngine app
                    // System.exit(0) would also work
                } else if (name.equals("Restart")) {
                    ask.setEnabled(false);
                    // take away the text asking 
                    game.setEnabled(true); // restart the game 
                    running = true;
                    System.out.println("RestartGameDemo/actionlistener: "
                            + "(setting running to true)");
                    // disable further calls - this also removes the second 
                    // event (the key release) that otherwise would follow 
                    // after a key being (de-) pressed
                    inputManager.deleteMapping("Restart");
                    inputManager.deleteMapping("Exit");
                }
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        if (running) {
            if (time <= 0) {
                System.out.println("RestartGameDemo: simpleUpdate "
                        + "(entering when time is up)");
                game.setEnabled(false);
                inputManager.addMapping("Restart",
                        new KeyTrigger(KeyInput.KEY_P)); // enable calls
                inputManager.addMapping("Exit",
                        new KeyTrigger(KeyInput.KEY_E));
                inputManager.addListener(actionListener, "Restart", "Exit");
                ask.setEnabled(true);
                time = 0f;
                running = false;
                System.out.println("RestartGameDemo: simpleUpdate "
                        + "(leaving with running==false)");
            }
        }
    }
}

class Game extends BaseAppState {
    
    // thickness of the sides of the frame
    static final float FRAME_THICKNESS = 24f; 
    // width (and height) of the free area inside the frame, where disks move
    static final float FREE_AREA_WIDTH = 492f; 
    // total outer width (and height) of the frame
    static final float FRAME_SIZE = FREE_AREA_WIDTH + 2f * FRAME_THICKNESS; 

    // next three constants define initial positions for disks
    static final float PLAYER_COORD = FREE_AREA_WIDTH / 6;
    static final float POSNEG_MAX_COORD = FREE_AREA_WIDTH / 3;
    static final float POSNEG_BETWEEN_COORD = PLAYER_COORD;

    static final float PLAYER_R = 20f; // radius of a player's disk
    static final float POSDISK_R = 16f; // radius of a positive disk
    static final float NEGDISK_R = 16f; // radius of a negative disk
    
    static final float MIN_VELOCITY_X = -5;
    static final float MIN_VELOCITY_Y = -5;
    static final float MAX_VELOCITY_X = 5;
    static final float MAX_VELOCITY_Y = 5;
    
    static final float FRICTION_COEFFICIENT = 0.02f;
    static final float GRAVITY_ACCELERATION = 9.82f;
    
    static final float FRICTION_NUMBER = 0.998f;
    
    private final float START_TIME = 30f;
    
    public float time = START_TIME;
    
    private float lastTpf = 0f;
    
    private Node topNode;
   
    
    private SimpleApplication sapp;
    private boolean needCleaning = false;
    
    //private Node topNode;
    //private Frame frame;
    //private FreeArea freeArea;
    //private Disk negativeDisk;
    private HudText hudText;
    private ArrayList<Disk> diskStore = new ArrayList<Disk>();
    protected ArrayList<PlayerDisk> players = new ArrayList<PlayerDisk>();
    private ArrayList<ArrayList<KeyTrigger>> keyTriggers = new ArrayList<ArrayList<KeyTrigger>>();
   // private ArrayList<KeyTrigger> player1Keys = new ArrayList<KeyTrigger>();
    //private ArrayList<KeyTrigger> player2Keys = new ArrayList<KeyTrigger>();
    //private ArrayList<KeyTrigger> player3Keys = new ArrayList<KeyTrigger>();
    
    private ArrayList<Vector3f> playerStartPositions = new ArrayList<Vector3f>();
    
    private Random random = new Random();
    
    
   

    @Override
    protected void initialize(Application app) {
        sapp = (SimpleApplication) app;
        System.out.println("Game: initialize");
        
    }

    @Override
    protected void cleanup(Application app) {
        System.out.println("Game: cleanup");
    }

    @Override
    protected void onEnable() {
        System.out.println("Game: onEnable");
        if (needCleaning) {
            System.out.println("(Cleaning up)");
            diskStore.clear();
            players.clear();
            sapp.getRootNode().detachAllChildren();
            needCleaning = false;
        }
        System.out.println("(Creating the scenegraph etc from scratch)");

            
        Frame frame = new Frame(FRAME_THICKNESS, FRAME_SIZE, ColorRGBA.DarkGray, ColorRGBA.Brown, sapp.getAssetManager());
        FreeArea freeArea = new FreeArea(FREE_AREA_WIDTH, ColorRGBA.LightGray, sapp.getAssetManager());
        hudText = new HudText(-FRAME_SIZE, FRAME_SIZE/2, ColorRGBA.White, sapp.getAssetManager());
        
        ArrayList<Vector3f> negStartPositions = new ArrayList<Vector3f>();
        ArrayList<Vector3f> posStartPositions = new ArrayList<Vector3f>();
       
        
        negStartPositions.add(new Vector3f(-POSNEG_BETWEEN_COORD, POSNEG_MAX_COORD,0));
        negStartPositions.add(new Vector3f(POSNEG_BETWEEN_COORD, POSNEG_MAX_COORD,0));
        negStartPositions.add(new Vector3f(POSNEG_MAX_COORD, POSNEG_BETWEEN_COORD,0));
        negStartPositions.add(new Vector3f(POSNEG_MAX_COORD, -POSNEG_BETWEEN_COORD,0));
        negStartPositions.add(new Vector3f(POSNEG_BETWEEN_COORD,-POSNEG_MAX_COORD,0));
        negStartPositions.add(new Vector3f(-POSNEG_BETWEEN_COORD, -POSNEG_MAX_COORD,0));
        negStartPositions.add(new Vector3f(-POSNEG_MAX_COORD, -POSNEG_BETWEEN_COORD,0));
        negStartPositions.add(new Vector3f(-POSNEG_MAX_COORD, POSNEG_BETWEEN_COORD,0));
        
        posStartPositions.add(new Vector3f(-POSNEG_MAX_COORD, POSNEG_MAX_COORD,0));
        posStartPositions.add(new Vector3f(0, POSNEG_MAX_COORD,0));
        posStartPositions.add(new Vector3f(POSNEG_MAX_COORD, POSNEG_MAX_COORD,0));
        posStartPositions.add(new Vector3f(POSNEG_MAX_COORD, 0,0));
        posStartPositions.add(new Vector3f(POSNEG_MAX_COORD,-POSNEG_MAX_COORD,0));
        posStartPositions.add(new Vector3f(0, -POSNEG_MAX_COORD,0));
        posStartPositions.add(new Vector3f(-POSNEG_MAX_COORD, -POSNEG_MAX_COORD,0));
        posStartPositions.add(new Vector3f(-POSNEG_MAX_COORD, 0,0));

        
        topNode = new Node("top");
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        topNode.addLight(sun);
        
        for(int i=0; i<8; i++){
            Disk negativeDisk = new NegativeDisk(NEGDISK_R, FRAME_THICKNESS - 3, ColorRGBA.Red, negStartPositions.get(i), getRandomVelocity(), sapp.getAssetManager());
            diskStore.add(negativeDisk);
            topNode.attachChild(negativeDisk);
        }
        
        for(int i=0; i<8; i++){
            Disk positiveDisk = new PositiveDisk(NEGDISK_R, FRAME_THICKNESS - 3, ColorRGBA.Green, posStartPositions.get(i),getRandomVelocity(), sapp.getAssetManager());
            diskStore.add(positiveDisk);
            topNode.attachChild(positiveDisk);
        }

        
        topNode.attachChild(frame);
        topNode.attachChild(freeArea);
        topNode.attachChild(hudText);
        
      
        sapp.getRootNode().attachChild(topNode);
       
       
    }
    public void spawnPlayers(int numberOfPlayers) {
        for(int i=0; i<numberOfPlayers;i++){
            float x = (float) Math.random()*2 - 1f;
            float y = (float) Math.random()*2 - 1f;

            x = Math.round(x);
            y = Math.round(y); //x and y should now be -1, 0, or 1
        
            Vector3f startPosition = new Vector3f(x*PLAYER_COORD, y*PLAYER_COORD,0);
            
            Disk playerDisk = new PlayerDisk(PLAYER_R, FRAME_THICKNESS-3, ColorRGBA.Blue,
                    startPosition, getRandomVelocity(),
                    sapp.getAssetManager(), "Player"+Integer.toString(i));
            diskStore.add(playerDisk);
            players.add((PlayerDisk) playerDisk);

            topNode.attachChild(playerDisk);
        }
        System.out.println("players added");
    }
    public PlayerDisk getPlayerById(String id) {
        for (int i=0; i<players.size(); i++) {
            if (players.get(i).id.equals(id)) {
                return players.get(i);
            }
            
        }
        Util.print("Cannot find player");
        return null;
    }
    
    
    @Override
    protected void onDisable() {
        System.out.println("Game: onDisable");
        sapp.getRootNode().detachAllChildren();
        needCleaning = true;
    }

    @Override
    public void update(float tpf) {
        
        //System.out.println(tpf + " tpf");
        
        
        
        
        for(int i=0; i<diskStore.size();i++){
            Disk disk1 = diskStore.get(i);
            Vector3f velocity = disk1.getVelocity();
            Vector3f desiredVelocity = disk1.desiredVelocity;

            float actualX = velocity.x;
            float actualY = velocity.y;

            float desiredX = desiredVelocity.x;
            float desiredY = desiredVelocity.y;

            float differenceX = desiredX-actualX;
            float differenceY = desiredY-actualY;

            disk1.setVelocity(new Vector3f(actualX+0.6f*differenceX, 
                            actualY+0.6f*differenceY, 0));
            

            
            float velocityX = (disk1.getVelocity().getX())*FRICTION_NUMBER;
            float velocityY = (disk1.getVelocity().getY())*FRICTION_NUMBER; 
            disk1.setVelocity(new Vector3f(velocityX, velocityY,0));
            
            
            
         
            float positionX = disk1.getPosition().getX() + tpf*disk1.getVelocity().getX();
            float positionY = disk1.getPosition().getY() + tpf*disk1.getVelocity().getY();
            disk1.setPosition(new Vector3f(positionX, positionY,0));
            disk1.checkCollide(new Vector3f(-FREE_AREA_WIDTH/2, -FREE_AREA_WIDTH/2,0), new Vector3f(FREE_AREA_WIDTH/2,FREE_AREA_WIDTH/2,0));
            for(int j=i+1; j<diskStore.size();j++){
                Disk disk2 = diskStore.get(j);
                if(disk1 != disk2){
                   
                    
                    if(disk1.collidesWithAnotherDisk(disk2)){
                        Vector3f velocityDisk1 = disk1.getVelocity();
                        Vector3f velocityDisk2 = disk2.getVelocity();
        
                        
                        Vector3f positionDisk1 = disk1.getPosition();
                        Vector3f positionDisk2 = disk2.getPosition();
                        
                 
                        
                        // Delta positions and Delta velocities
                        
                        
                        float dPX = positionDisk2.getX() - positionDisk1.getX();
                        float dPY = positionDisk2.getY() - positionDisk1.getY();
                       
                        float dVX = velocityDisk1.getX() - velocityDisk2.getX();
                        float dVY = velocityDisk1.getY() - velocityDisk2.getY();
                        
                        // Time to collision
                        
                        float radiusSum = disk1.getRadius()+disk2.getRadius();
                        float radiusMult = radiusSum*radiusSum;
                        
                        float p = ((2*dPX*dVX+2*dPY*dVY)/(dVX*dVX+dVY*dVY)); 
                        float q = ((dPX*dPX+dPY*dPY-(radiusMult))/(dVX*dVX+dVY*dVY));
                        
                        
               
                        
                        float t = -(p/2) + (float) sqrt((p/2)*(p/2) - q);
                        float t2 = -(p/2) - (float) sqrt((p/2)*(p/2) - q);

                        

                       
                        float newPositionXDisk1 = disk1.getPosition().getX() - t*velocityDisk1.getX();
                        float newPositionYDisk1 = disk1.getPosition().getY() - t*velocityDisk1.getY();
                        float newPositionXDisk2 = disk2.getPosition().getX() - t*velocityDisk2.getX();
                        float newPositionYDisk2 = disk2.getPosition().getY() - t*velocityDisk2.getY();
                        
                        
                        Vector3f newPositionDisk1 = new Vector3f(newPositionXDisk1, newPositionYDisk1,0);
                        Vector3f newPositionDisk2 = new Vector3f(newPositionXDisk2, newPositionYDisk2,0);


                        
                        
                        disk1.setPosition(newPositionDisk1);
                        disk2.setPosition(newPositionDisk2);
                        
                      
                        diskStore.get(i).diskCollision(diskStore.get(j), velocityDisk1, velocityDisk2, newPositionDisk1, newPositionDisk2);
                        
                        float positiondisk1X = disk1.getPosition().getX() + (t)*disk1.getVelocity().getX();
                        float positiondisk1Y = disk1.getPosition().getY() + (t)*disk1.getVelocity().getY();
                        float positiondisk2X = disk2.getPosition().getX() + (t)*disk2.getVelocity().getX();
                        float positiondisk2Y = disk2.getPosition().getY() + (t)*disk2.getVelocity().getY();
                        
                        disk1.setPosition(new Vector3f(positiondisk1X, positiondisk1Y,0));
                        disk2.setPosition(new Vector3f(positiondisk2X, positiondisk2Y,0));
                    
                    }
                    
                    
                }
            }
        }
        
        
        String text = "I : ";
        
        for(int i=0; i<diskStore.size();i++){ 
            if(diskStore.get(i) instanceof PlayerDisk){
                text = text + diskStore.get(i).scorePrint() + "\n";
                
             
            }
            
        }
        text = text + time + "\n";
        hudText.setText(text);
                   
        
        
        
     
    }
    
    private Vector3f getVelocityBeforeCollision(Vector3f velocity, Vector3f position){
        return velocity;
    }
    
    public ArrayList<Disk> getDisks() {
        return this.diskStore;
    }
    private Vector3f getRandomVelocity(){
        float velocityX = MIN_VELOCITY_X + random.nextFloat() * (MAX_VELOCITY_X - MIN_VELOCITY_X);
        float velocityY = MIN_VELOCITY_Y + random.nextFloat() * (MAX_VELOCITY_Y - MIN_VELOCITY_Y);
        Vector3f velocity = new Vector3f(velocityX, velocityY,0);
        
        return velocity;
    }
    
    private Vector3f getRandomPosition(){
        float positionX = -POSNEG_BETWEEN_COORD + random.nextFloat() * (POSNEG_BETWEEN_COORD + POSNEG_BETWEEN_COORD);
        float positionY = -POSNEG_BETWEEN_COORD + random.nextFloat() * (POSNEG_BETWEEN_COORD + POSNEG_BETWEEN_COORD);
        Vector3f position = new Vector3f(positionX, positionY,0);
        
        return position;
    }
    
    public float getTime(){
        return this.time;
    }


    
}

class Ask extends BaseAppState {

    private SimpleApplication sapp;

    @Override
    protected void initialize(Application app) {
        System.out.println("Ask: initialize");
        sapp = (SimpleApplication) app;
    }

    @Override
    protected void cleanup(Application app) {
        System.out.println("Ask: cleanup");

    }

    @Override
    protected void onEnable() {
        System.out.println("Ask: onEnable (asking)");
        // create a text in the form of a bitmap, and add it to the GUI pane
        BitmapFont myFont
                = sapp.getAssetManager()
                        .loadFont("Interface/Fonts/Console.fnt");
        BitmapText hudText = new BitmapText(myFont, false);
        hudText.setSize(myFont.getCharSet().getRenderedSize() * 2);
        hudText.setColor(ColorRGBA.White);
        hudText.setText("PRESS P TO RESTART AND E TO EXIT");
        hudText.setLocalTranslation(120, hudText.getLineHeight(), 0);
        sapp.getGuiNode().attachChild(hudText);
    }

    @Override
    protected void onDisable() {
        System.out.println("Ask: onDisable (user pressed P)");
        sapp.getGuiNode().detachAllChildren();
    }

}