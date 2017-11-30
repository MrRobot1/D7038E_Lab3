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
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author Anton
 */
public class PlayerDisk extends Disk{
    
    //private int score;
    public final float MAX_SPEED = 150f;
    public String id;
    public static int playersCreated;
    public int objectNumber;
    BitmapText playerText;
    BitmapFont playerFont;
    
    public PlayerDisk(float radius, float height, ColorRGBA color, Vector3f position, Vector3f velocity,AssetManager assetManager, String id){
        
        super(radius, height, color, position, velocity, assetManager);
        this.id=id;
        playersCreated++;
        objectNumber = playersCreated;
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

    
    public void accLeft(float tpf, float acceleration){
        
        if(this.getVelocity().getX() > -this.MAX_SPEED){
            //velocity.setX(this.getVelocity().getX() - acceleration*0.1f*tpf);
            desiredVelocity.x = (float) (this.desiredVelocity.x - acceleration*tpf);
        }
        

    }
    
    public void accDown(float tpf, float acceleration){
  
        if(this.getVelocity().getY() > -this.MAX_SPEED){
            //velocity.setY(this.getVelocity().getY() - acceleration*0.1f*tpf);
            desiredVelocity.y = (float) (this.desiredVelocity.y - acceleration*tpf);
           
        }

    }
    
    public void accRight(float tpf, float acceleration){
  
        if(this.getVelocity().getX() < this.MAX_SPEED){
           // velocity.setX(this.getVelocity().getX() +acceleration*0.1f*tpf);
            desiredVelocity.x = (float) (this.desiredVelocity.x + acceleration*tpf);
          
        }
        
    }
    
    public void accUp(float tpf, float acceleration){
      
        if(this.getVelocity().getY() < this.MAX_SPEED){
            //velocity.setY(this.getVelocity().getY() + acceleration*0.1f*tpf);
            desiredVelocity.y = (float) (this.desiredVelocity.y + acceleration*tpf);
           
        }
        

    }
        private void playerNumber(String id, Float height){
            playerFont = assetManager.loadFont("Interface/Fonts/Console.fnt");
            playerText = new BitmapText(playerFont, false);
            playerText.setSize(playerFont.getCharSet().getRenderedSize() * 2);      // font size
            playerText.setColor(ColorRGBA.White);                             // font color
            String text = this.id.split("Player")[1];
            int i = Integer.parseInt(text);
            i++;
            text = Integer.toString(i);
            playerText.setText(text);             // the text
            playerText.setLocalTranslation(-radius/4f, radius/2f, height); // position
            this.attachChild(playerText);
    }
    @Override
    public String scorePrint(){
        String number = this.id.split("Player")[1];
        int n = Integer.parseInt(number);
        n++;
        number = Integer.toString(n);
        
        return "Player " + number + ": " + Integer.toString(this.score);
       
    }
}
