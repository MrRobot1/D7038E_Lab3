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
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Anton
 */
public class Marker extends Node{
    
    
    Sphere markerMesh;
    Geometry markerGeo;
    Material markerMat;
    
    public Marker(float radius, ColorRGBA color, AssetManager assetManager){
        
        createMarker(radius, color, assetManager);
    }
    
    private void createMarker(float radius, ColorRGBA color, AssetManager assetManager){
        
        markerMesh = new Sphere(100, 100, radius);
        markerGeo = new Geometry("snowball", markerMesh);
        markerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        markerMat.setColor("Color", color);
        markerGeo.setMaterial(markerMat);
        this.attachChild(markerGeo);
        
    }
    
}
