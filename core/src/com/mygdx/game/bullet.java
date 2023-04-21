package com.mygdx.game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import jdk.nashorn.internal.ir.WhileNode;

public class bullet {

    private static int ballSpeed = 4;
    private static int arrowSpeed = 6;

    // instance variables
    private float xDirection;         // the bullet's direction on the x axis -1, 0, 1
    private float yDirection;         // the bullet's direction on the y axis
    private float speed;
    private int level;          // the bullet's level; 1, 2, 3, 4(MAX)
    private int type;           // the bullet's type; 0 = ball, 1 = arrow
    private Sprite sprite;      // the bullet's sprite
    private boolean hit;
    private boolean active;

    private int delayTime;
    private float xDelayed, yDelayed;

    // constructor
    public bullet(float xCoord, float yCoord, float xDir, float yDir, int level, int type, Texture sheet){
        xDirection = xDir;
        yDirection = yDir;
        this.level = level;
        this.type = type;
        setSpeed();
        int srcX;                           // source X for the sprite
        if (this.type == 0){ srcX = 134; }  // 0 = ball, srcX = 134
        else { srcX = 159;  }               // 1 = arrow, srcY = 159
        int srcY = 50 + (level * 25);;      // set srcY to fifty
                                            // then add another 25 per level to get the appropriate y value
        sprite = new Sprite(sheet, srcX, srcY, 25, 25);
        sprite.setPosition(xCoord, yCoord);
        hit = false;
        Activate();

        delayTime = 0;
    }

    // getters and setter
    private float setSpeed() {
        if (type == 0) { speed = ballSpeed; }
        else { speed = arrowSpeed; }
        if ((xDirection != 0) & (yDirection != 0)) {
            if ((xDirection == 1) | ( xDirection == -1)) { xDirection *= (Math.sqrt(2)/2); }
            if ((yDirection == 1) | ( yDirection == -1)) { yDirection *= (Math.sqrt(2)/2); }
        }
        if (xDirection > 1) { xDirection = 1; }
        else if (xDirection < -1) { xDirection = -1; }
        if (yDirection > 1) { yDirection = 1; }
        else if (yDirection < -1) { yDirection = -1; }


        return speed;
    }
    public float getxCoord() { return sprite.getX(); }
    public float getyCoord() { return sprite.getY(); }
    public int getLevel() { return level; }
    public int getType() { return type; }
    public Sprite getSprite() { return sprite; }
    public float getXDir() { return xDirection; }
    public float getYDir() { return yDirection; }

    public void setxDirection(float xDir) { xDirection = xDir; }
    public void setyDirection(float yDir) { yDirection = yDir; }

    // modifier functions
    public void levelUP() {
        if (level < 4) {
            level++;
            int srcX = 0;
            int srcY = 0;
            if (this.type == 0){ srcX = 134; }
            else { srcX = 159; }
            srcY += (level * 25);
            sprite.setRegion(srcX, srcY ,25,25);
        }
    }
    public void levelDown() {
        if (level > 1) {
            level--;
            int srcX;
            int srcY = 0;
            if (this.type == 0){ srcX = 134; }
            else { srcX = 159; }
            srcY += (level * 25);
            sprite.setRegion(srcX, srcY ,25,25);
        }
        else { hit = true; }
    }
    public void typeChange(){
        int srcX, srcY;
        if (type == 0) {
            type++;
            srcX = 159;
        }
        else {
            type--;
            srcX = 134;
        }
        srcY = 50 + (level * 25);
        sprite.setRegion(srcX, srcY ,25,25);
        setSpeed();
    }


    // nessecary functions
    public void update(){
        if (delayTime > 0) { updateDelay(); }
        sprite.setX(sprite.getX() + (speed * xDirection));
        sprite.setY(sprite.getY() + (speed * yDirection));
    }
    public void hitTarget() { hit = true; }
    public boolean hasHit() { return hit; }
    public boolean isOffScreen() {
        if ((sprite.getX() < 0) || (sprite.getY() < 0)) { return true; }
        else if ((sprite.getX() > 1200) || (sprite.getY() > 600)){ return true; }
        else { return false; }
    }

    public boolean isActive() { return active; }

    private void Deactivate() {
        active = false;
        sprite.setColor(Color.GRAY);
    }
    private void Activate(){
        active = true;
        sprite.setColor(Color.WHITE);
    }


    public void setDelay(int delay) {
        Deactivate();
        delayTime = delay;
        speed = 0;
    }
    public int getDelayTime() { return delayTime; }
    public void updateDelay(){
        delayTime--;
        if (delayTime == 0) {
            setSpeed();
            Activate();
        }
    }



}
