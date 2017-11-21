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
public class FramePart extends Node {
    
    
    Box framePartMesh;
    Geometry framePartGeo;
    Material framePartMat;
    AssetManager assetManager;
    
    public FramePart(float x, float y, float z,  ColorRGBA color, AssetManager assetManager){
        this.assetManager = assetManager;
        createFramePart(x,y,z,color);
       
    }
    
    private void createFramePart(float x, float y, float z, ColorRGBA color){
     
        framePartMesh = new Box(x,y,z);
        framePartGeo = new Geometry("FramePart", framePartMesh);
        framePartMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        framePartMat.setColor("Color", color);
        framePartGeo.setMaterial(framePartMat);
        this.attachChild(framePartGeo);
    }
    
    
}
