/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author Anton
 */
public class PlayerDisk extends Disk{
    
    //private int score;
    private ArrayList<KeyTrigger> keys = new ArrayList<KeyTrigger>();
    public final float MAX_SPEED = 150f;
    String id;
    BitmapText playerText;
    BitmapFont playerFont;
    
    public PlayerDisk(float radius, float height, ColorRGBA color, Vector2f position, Vector2f velocity, ArrayList<KeyTrigger> keys,AssetManager assetManager, String id){
        
        super(radius, height, color, position, velocity, assetManager);
        this.keys = keys;
        this.id = id;
        playerNumber(this.id, height);
        
    }
    
    
    @Override
    public int reward(Disk d){
        
        return 0;
    }
    
    @Override
    public void addToScore(int points){
        score = score + points;
    }
    
    public ArrayList<KeyTrigger> getKeys(){
        return this.keys;
    }
    
    public void accLeft(float tpf, float acceleration){
        
        if(this.getVelocity().getX() > -this.MAX_SPEED){
            velocity.setX(this.getVelocity().getX() - acceleration*tpf);
          
        }
        

    }
    
    public void accDown(float tpf, float acceleration){
  
        if(this.getVelocity().getY() > -this.MAX_SPEED){
            velocity.setY(this.getVelocity().getY() - acceleration*tpf);
           
        }

    }
    
    public void accRight(float tpf, float acceleration){
  
        if(this.getVelocity().getX() < this.MAX_SPEED){
            velocity.setX(this.getVelocity().getX() +acceleration*tpf);
          
        }
        
    }
    
    public void accUp(float tpf, float acceleration){
      
        if(this.getVelocity().getY() < this.MAX_SPEED){
            velocity.setY(this.getVelocity().getY() + acceleration*tpf);
           
        }
        

    }
    public String scorePrint(){
        return "Player "+ id+ " : "+ this.score;
    }
    
    private void playerNumber(String id, Float height){
        playerFont = assetManager.loadFont("Interface/Fonts/Console.fnt");
        playerText = new BitmapText(playerFont, false);
        playerText.setSize(playerFont.getCharSet().getRenderedSize() * 2);      // font size
        playerText.setColor(ColorRGBA.White);                             // font color
        playerText.setText(this.id);             // the text
        playerText.setLocalTranslation(-radius/4f, radius/2f, height); // position
        this.attachChild(playerText);
    }
    
    
}
