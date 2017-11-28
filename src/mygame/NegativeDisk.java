/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author Anton
 */
public class NegativeDisk extends Disk {
    
    private final int REWARD = -3;
    
    
    public NegativeDisk(float radius, float height, ColorRGBA color, Vector3f position, Vector3f velocity, AssetManager assetManager){
        
        super(radius, height, color, position, velocity, assetManager);
    }
    
    
    
    @Override
    public int reward(Disk d){
        
        if (d instanceof PlayerDisk){
            


            return this.REWARD;
        }
        
        return 0;
    }
    
    @Override
    public void addToScore(int points){
       
    }
    
    
}
