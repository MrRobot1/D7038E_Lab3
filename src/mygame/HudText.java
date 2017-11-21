/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 *
 * @author Anton
 */
public class HudText extends Node{
    
    
    BitmapText hudText;
    BitmapFont guiFont;
    AssetManager assetManager;
    
    public HudText(float x, float y, ColorRGBA color, AssetManager assetManager){
        
        this.assetManager = assetManager;
        createHudText(x, y, color, assetManager);
        
    }
    
    private void createHudText(float x, float y, ColorRGBA color, AssetManager assetManager){
        guiFont = assetManager.loadFont("Interface/Fonts/Console.fnt");
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 2);      // font size
        hudText.setColor(color);                             // font color
        hudText.setText("Game started");             // the text
        hudText.setLocalTranslation(x, y, 0); // position
        this.attachChild(hudText);
    }
    
    public void setText(String text){
        
        this.hudText.setText(text);
        
    }
    
    
}
