/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Anton
 */
public abstract class Disk extends Node{
    
    static final int AXIS_SAMPLES = 100;
    static final int RADIAL_SAMPLES = 100;
    
    protected Vector3f position; 
    protected Vector3f velocity;
    protected Vector3f acceleration;
    public Vector3f desiredVelocity;

    protected int score;
    protected float radius;
    private float height;
    private float mass;
    private ColorRGBA color;
    
    BitmapText velocityText;
   
    
 
    
    Cylinder cylinderMesh;
    Geometry cylinderGeo;
    Material cylinderMat;
    Node cylinderNode;
    AssetManager assetManager;
    
    public Disk(float radius, float height, ColorRGBA color, Vector3f position, Vector3f velocity,AssetManager assetManager){
        this.assetManager = assetManager;
        this.radius = radius;
        this.height = height;
        this.color = color;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector3f(0,0,0);
        this.mass = (float) Math.PI * radius*radius;
        this.score = 0;
        this.desiredVelocity = new Vector3f(this.getVelocity().x, this.getVelocity().y, 0);

        createDisk(position, color);
        
    }
    
    private void createDisk(Vector3f position, ColorRGBA color){
        
        cylinderMesh = new Cylinder(AXIS_SAMPLES, RADIAL_SAMPLES, radius, height, true);
        cylinderGeo = new Geometry("Cylinder", cylinderMesh);
      
        cylinderMat = new Material(assetManager,
        "Common/MatDefs/Light/Lighting.j3md");
       
        cylinderMat.setBoolean("UseMaterialColors",true);
        cylinderMat.setColor("Diffuse", color);
        cylinderMat.setColor("Specular", color);
        cylinderMat.setFloat("Shininess", 64f);  // [0,128]
        cylinderGeo.setMaterial(cylinderMat);
    
        this.attachChild(cylinderGeo);
        this.setLocalTranslation(position.getX(),position.getY(),height);
    }
    
    
    public void setVelocity(Vector3f velocity){
        this.velocity = velocity;

    }
    
    public void setPosition(Vector3f position){
        this.position = position;
        
        
        this.setLocalTranslation(position.getX(), position.getY(), height);
    }
    
    public void setAcceleration(Vector3f acceleration){
        this.acceleration = acceleration;
        
    }
    
    
    public Vector3f getVelocity(){
        return this.velocity;
    }
    
    public Vector3f getPosition(){
        return this.position;
    }
    
    public Vector3f getAcceleration(){
        return this.acceleration;
    }
    
    public float getMass(){
        return this.mass;
    }
    
    public float getRadius(){
        return this.radius;
    }
    
    public int getScore(){
        return this.score;
    }
    
    public String scorePrint(){
        return ""+this.score;
    }
    
        
    public void checkCollide(Vector3f minCoordinates, Vector3f maxCoordinates){
        
        
        // Lower
        if((this.position.getY()-this.radius) < minCoordinates.getY()){         // negate velocity if lower part of disk is under the lowest part of given parameter
            this.setPosition(new Vector3f(this.position.getX(), minCoordinates.getY()+this.radius,0));
            this.velocity.setY(-this.velocity.getY());
            this.desiredVelocity.y = this.velocity.y;

        }
        // Upper
        else if((this.position.getY()+this.radius) > maxCoordinates.getY()){
            this.setPosition(new Vector3f(this.position.getX(), maxCoordinates.getY()-this.radius,0));
            this.velocity.setY(-this.velocity.getY());
            this.desiredVelocity.y = this.velocity.y;

        }
        // Left
        else if((this.position.getX()-this.radius) < minCoordinates.getX()){
            this.setPosition(new Vector3f(minCoordinates.getX()+this.radius, this.position.getY(),0));
            this.velocity.setX(-this.velocity.getX());
            this.desiredVelocity.x = this.velocity.x;

        }
        // Right
        else if((this.position.getX()+this.radius) > maxCoordinates.getX()){
            this.setPosition(new Vector3f(maxCoordinates.getX()-this.radius, this.position.getY(),0));
            this.velocity.setX(-this.velocity.getX());
            this.desiredVelocity.x = this.velocity.x;
          
        }
        
    }
    public void diskCollision(Disk disk, Vector3f prevVelocityDisk1, Vector3f prevVelocityDisk2, Vector3f positionDisk1, Vector3f positionDisk2){
       
        Vector3f disk1Position = positionDisk1;
        Vector3f disk2Position = positionDisk2;
      

                 
        Vector3f normalVector = new Vector3f(disk2Position.getX()- disk1Position.getX(), disk2Position.getY() -disk1Position.getY(),0);
        Vector3f unitNormalVector = normalVector.normalize();
        Vector3f unitTangentVector = new Vector3f(-unitNormalVector.getY(), unitNormalVector.getX(),0);

     
        float disk1NormalVelocityScalar = unitNormalVector.dot(this.getVelocity());
        float disk1TangentVelocityScalar = unitTangentVector.dot(this.getVelocity());
        float disk2NormalVelocityScalar = unitNormalVector.dot(disk.getVelocity());
        float disk2TangentVelocityScalar = unitTangentVector.dot(disk.getVelocity());

       
        float disk1NormalVelocityScalarPrime = ((disk1NormalVelocityScalar*(this.getMass()-disk.getMass())) + (2*disk.getMass()*disk2NormalVelocityScalar)) / (this.getMass()+disk.getMass());
        float disk2NormalVelocityScalarPrime = ((disk2NormalVelocityScalar*(disk.getMass()- this.getMass())) + (2*this.getMass()*disk1NormalVelocityScalar)) /(this.getMass()+disk.getMass());


        Vector3f disk1NormalVelocityVectorPrime = unitNormalVector.mult(disk1NormalVelocityScalarPrime);
        Vector3f disk1TangentVelocityVectorPrime = unitTangentVector.mult(disk1TangentVelocityScalar);
        Vector3f disk2NormalVelocityVectorPrime = unitNormalVector.mult(disk2NormalVelocityScalarPrime);
        Vector3f disk2TangentVelocityVectorPrime = unitTangentVector.mult(disk2TangentVelocityScalar);


        Vector3f disk1VelocityVectorPrime = disk1NormalVelocityVectorPrime.add(disk1TangentVelocityVectorPrime);
        Vector3f disk2VelocityVectorPrime = disk2NormalVelocityVectorPrime.add(disk2TangentVelocityVectorPrime);


        this.setVelocity(disk1VelocityVectorPrime);
        disk.setVelocity(disk2VelocityVectorPrime);
        
        this.desiredVelocity.x = this.velocity.x;
        this.desiredVelocity.y = this.velocity.y;
        
        disk.desiredVelocity.x = disk.velocity.x;
        disk.desiredVelocity.y = disk.velocity.y;
     
        this.addToScore(disk.reward(this));
        disk.addToScore(this.reward(disk));

    
        
        
    }
    
    public boolean collidesWithAnotherDisk(Disk disk){
        float dX = this.position.getX()- disk.getPosition().getX();
        float dY = this.position.getY() - disk.getPosition().getY();
        
        float sumOfRadius = this.radius + disk.getRadius();
        
        return dX*dX + dY*dY <= sumOfRadius*sumOfRadius;
    }
   
    
    abstract public void addToScore(int points);
    
    abstract public int reward(Disk d);

    
    
    
    
}
