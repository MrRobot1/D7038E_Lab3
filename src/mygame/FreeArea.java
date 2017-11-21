/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Anton
 */
public class FreeArea extends Node{
    
    
    Box freeAreaMesh;
    Geometry freeAreaGeo;
    Material freeAreaMat;
    AssetManager assetManager;
    private static final float DEPTH = 2f; 
    
    public FreeArea(float width, ColorRGBA color, AssetManager assetManager){
        this.assetManager = assetManager;
        createFreeArea(width, color);
    }
    
    
    private void createFreeArea(float width, ColorRGBA color){
        freeAreaMesh = new Box(width/2,width/2, DEPTH);
        freeAreaGeo = new Geometry("FramePart", freeAreaMesh);
        freeAreaMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        freeAreaMat.setColor("Color", color);
        freeAreaGeo.setMaterial(freeAreaMat);
        this.attachChild(freeAreaGeo);
        //this.setLocalTranslation(0,-100,0);
        
    }
    
}
