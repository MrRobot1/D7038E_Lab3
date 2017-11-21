/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 *
 * @author Anton
 */
public class Frame extends Node{
    
    AssetManager assetManager;
    private static final float FRONT_DEPTH = 2f; 
    
    public Frame(float thickness, float size, ColorRGBA color, ColorRGBA frontColor,AssetManager assetManager){
        
        this.assetManager = assetManager;
        assemble(thickness, size, color, frontColor);    
    }   
    
    
    private void assemble(float thickness, float size, ColorRGBA color, ColorRGBA frontColor){
        System.out.println(thickness + " thickness");
        System.out.println(size + " size");
        
        FramePart leftPart = new FramePart(thickness/2, size/2, thickness/2, color, assetManager);
        leftPart.setLocalTranslation((-size/2)+(thickness/2), 0, thickness/2);
        
        FramePart rightPart = new FramePart(thickness/2, size/2, thickness/2, color, assetManager);
        rightPart.setLocalTranslation((size/2)-(thickness/2), 0, thickness/2);
        
        FramePart upperPart = new FramePart(thickness/2, size/2, thickness/2, color, assetManager);
        upperPart.setLocalTranslation(0, (size/2)-(thickness/2), thickness/2);
        upperPart.rotate(0, 0, 1.5707963268f);
        
        FramePart lowerPart = new FramePart(thickness/2, size/2, thickness/2, color, assetManager);
        lowerPart.setLocalTranslation(0, (-size/2)+(thickness/2), thickness/2);
        lowerPart.rotate(0, 0 ,1.5707963268f);
        
        FramePart leftFrontPart = new FramePart(thickness/2, size/2, FRONT_DEPTH, frontColor, assetManager);
        leftFrontPart.setLocalTranslation((-size/2)+(thickness/2), 0, thickness);
        
        FramePart rightFrontPart = new FramePart(thickness/2, size/2, FRONT_DEPTH, frontColor, assetManager);
        rightFrontPart.setLocalTranslation((size/2)-(thickness/2), 0, thickness);
        
        FramePart upperFrontPart = new FramePart(thickness/2, size/2, FRONT_DEPTH, frontColor, assetManager);
        upperFrontPart.setLocalTranslation(0, (size/2)-(thickness/2), thickness);
        upperFrontPart.rotate(0, 0, 1.5707963268f);
        
        FramePart lowerFrontPart = new FramePart(thickness/2, size/2, FRONT_DEPTH, frontColor, assetManager);
        lowerFrontPart.setLocalTranslation(0, (-size/2)+(thickness/2), thickness);
        lowerFrontPart.rotate(0, 0 ,1.5707963268f);
        
        
        
        this.attachChild(leftPart);
        this.attachChild(rightPart);
        this.attachChild(upperPart);
        this.attachChild(lowerPart);
        this.attachChild(lowerFrontPart);
        this.attachChild(upperFrontPart);
        this.attachChild(leftFrontPart);
        this.attachChild(rightFrontPart);
    }
    
}
