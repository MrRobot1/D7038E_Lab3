/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Anton
 */
public class PositiveDisk extends Disk {
    
    private int currentReward;
    private final float MARKER_RADIUS = 1.5f;
    
    
    public PositiveDisk(float radius, float height, ColorRGBA color, Vector3f position, Vector3f velocity, AssetManager assetManager){
        
        super(radius, height, color, position, velocity, assetManager);
        
        currentReward = 5;
        addMarkers();
    }
    
    @Override
    public int reward(Disk d){
        
        if (d instanceof PlayerDisk){
            int returnedReward = currentReward;

            if(currentReward > 0){
                currentReward = currentReward - 1;
                //System.out.println("Player hitted positive");
                this.detachChildAt(returnedReward);                // removes a snowball
            }
            
            


            return returnedReward;
        }
        
        return 0;
    }
    
    @Override
    public void addToScore(int points){
       
    }
    
    
    
    private void addMarkers(){
        
        //System.out.println("ADDING MARKERS");
            
        Marker marker1 = new Marker(MARKER_RADIUS, ColorRGBA.White, this.assetManager);
        marker1.setLocalTranslation(-radius/3, radius/3, radius);
        
        Marker marker2 = new Marker(MARKER_RADIUS, ColorRGBA.White, this.assetManager);
        marker2.setLocalTranslation(radius/3, radius/3, radius);
   
        Marker marker3 = new Marker(MARKER_RADIUS, ColorRGBA.White, this.assetManager);
        marker3.setLocalTranslation(-radius/3, -radius/3, radius);
        
        Marker marker4 = new Marker(MARKER_RADIUS, ColorRGBA.White, this.assetManager);
        marker4.setLocalTranslation(radius/3, -radius/3, radius);
        
        Marker marker5 = new Marker(MARKER_RADIUS, ColorRGBA.White, this.assetManager);
        marker5.setLocalTranslation(0, 0, radius);
        
        this.attachChild(marker1);
        this.attachChild(marker2);
        this.attachChild(marker3);
        this.attachChild(marker4);
        this.attachChild(marker5);
        
       
  
    }
    
    
}
