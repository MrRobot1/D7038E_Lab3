/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
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
    
    public PlayerDisk(float radius, float height, ColorRGBA color, Vector3f position, Vector3f velocity,AssetManager assetManager, String id){
        
        super(radius, height, color, position, velocity, assetManager);
        this.id=id;
        playersCreated++;
        objectNumber = playersCreated;
        Box markerMesh = new Box(1,8,1);
        Geometry markerGeo = new Geometry("FramePart", markerMesh);
        Material markerMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        markerMat.setColor("Color", ColorRGBA.White);
        markerGeo.setMaterial(markerMat);
        markerGeo.setLocalTranslation(0,0,radius);
        
        this.attachChild(markerGeo);
        
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
    
    
}
