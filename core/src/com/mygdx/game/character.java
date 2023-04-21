// This class will work as a character or enemy
// holding all the assosiated values and the bullets that have spawn from it.

package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.LinkedList;
import java.lang.Math;
import java.util.Random;

public class character {



    // INSTANCE VARIABLES

    // Character specific stats
    private float movementSpeed;                // how much the position will increment per update
    private int AmmoCapacity;                   // the entity's number of shots before a "reload"
    private int firingRate;                     // the entity's rate of fire
    private float bulletSpeed;                  // how much the bullet's position will increment

    // character position and movement
    private float xCoordinate;                  // x coordinate of the entity's position  bottom left corner
    private float yCoordinate;                  // y coordinate of the entity's position  bottom left corner
    private int xMove, yMove;                   // modified by movement keys, used for updating player position

    // weapon variables
    private int weaponXdir, weaponYdir;
    private int powerLevel, powerMeter;

    // bullet variables
    private int coolDown;                       // a counter for tracking the space between fire.
    private LinkedList<bullet> liveShots;       // the collection to hold all of the active bullets

    // other animation triggers
    LinkedList<bullet> delayedShots;

    // damage variables
    private int hitState;                       // a counter variable for handling damage
    private boolean invincible;
    private int iFrames;                        // invincibility frames, used to defer hit detection after being hit
    boolean defeat;                             // boolean for marking when defeated, flipped when player is hit twice before healing

    // parry variables
    private int parryCounter;
    private int parryCool;
    private int parryRate;
    private boolean hasParried;


    // the various parts of the players sprite
    private String _SpriteFile;              // the sprites full path, from the assets folder just: "image.png"
    private Texture SpriteSheet;                 // the Libgdx Texture, acting as the entity's spriteSheet
    private Sprite[] spirtes = new Sprite[5];
    private Sprite body;
    private Sprite item;
    private Sprite weapon;
    private Sprite eyes;
    private Sprite meter;

    private AI control;

    private Sound hurtSound;
    private Sound healSound;
    private Sound shootSound;
    private Sound parrySound;
    private Sound parriedSound;
    private Sound winSound;





    // constructor
    public character(String file, float xCoord, float yCoord) {



        //character stats
        movementSpeed = 5;
        AmmoCapacity = 10;
        firingRate = 50;

        // Other variables
        liveShots = new LinkedList<bullet>();
        delayedShots = new LinkedList<bullet>();
        hitState = 0;
        defeat = false;
        iFrames = 20;
        invincible = false;
        parryCool = 0;
        parryRate = 60;
        hasParried = false;
        coolDown = 0;
        powerLevel = 1;
        powerMeter = 0;

        // spawn coordinates
        xCoordinate = xCoord;
        yCoordinate = yCoord;

        weaponXdir = 1;
        weaponYdir = 0;





        // SPRITES
        _SpriteFile = file;
        SpriteSheet = new Texture(Gdx.files.internal(_SpriteFile));
        // create all the sprites of the the players, in order of rendering, and add them to array, for ease
        body = new Sprite(SpriteSheet, 0, 160, 50, 40);
        body.setPosition(xCoordinate, yCoordinate);
        spirtes[0] = body;
        weapon = new Sprite(SpriteSheet, 0, 95, 50, 50);
        weapon.setPosition(xCoordinate + 50, yCoordinate);
        spirtes[1] = weapon;
        item = new Sprite(SpriteSheet, 0, 145, 50, 15);
        item.setPosition(xCoordinate, yCoordinate+40);
        spirtes[2] = item;
        eyes = new Sprite(SpriteSheet, 121, 191, 12, 9);
        eyes.setPosition(xCoordinate+19, yCoordinate + 20);
        spirtes[3] = eyes;
        meter = new Sprite(SpriteSheet, 184, 174, 25, 25);
        meter.setPosition(xCoordinate-27, yCoordinate+15);
        spirtes[4] = meter;

        hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.wav"));
        healSound = Gdx.audio.newSound(Gdx.files.internal("heal.wav"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
        parrySound = Gdx.audio.newSound(Gdx.files.internal("parry.wav"));
        parriedSound = Gdx.audio.newSound(Gdx.files.internal("parried.wav"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("win.wav"));

    }


    // getters and setters
    public void setX(float xCoordinate) { this.xCoordinate = xCoordinate; }
    public void setY(float yCoordinate) { this.yCoordinate = yCoordinate; }
    public float getX() { return xCoordinate; }
    public float getY() { return yCoordinate; }
    public Texture getSpriteSheet() {
        return SpriteSheet;
    }
    public Sprite[]  getSprites(){
        return spirtes;
    }

    public Sprite getWeapon() { return weapon; }

    public LinkedList<bullet> getLiveShots() {
        return liveShots;
    }
    public boolean isDefeat() { return defeat; }
    public void defeated() {
        defeat = true;
        Sprite loss = new Sprite(SpriteSheet, 0, 0, 300, 50);
        loss.setPosition(body.getX(), body.getY());
        body = loss;
        spirtes[0] = loss;
        winSound.play();
    }

    // actions
    public void setxMove(int xDir) { if ((xCoordinate+xDir > 0) & (xCoordinate+xDir+50 < 1200)) { xMove = xDir; }}
    public void setyMove(int yDir) { if ((yCoordinate+yDir > 0) & (yCoordinate+yDir+40 < 600)) { yMove = yDir; }}
    public void moveWeapon(int xDir, int yDir) {
        if ((xDir!=0) | (yDir!=0)) {
            weaponXdir = xDir;
            weaponYdir = yDir;
            if (weaponXdir > 0) {
                weapon.setX(xCoordinate+50);
                meter.setX(xCoordinate-50);
            }
            else if (weaponXdir < 0) {
                weapon.setX(xCoordinate-50);

                meter.setX(xCoordinate+50);
            }
            else {
                weapon.setX(xCoordinate);
                meter.setX(xCoordinate);
            }
            if (weaponYdir > 0) {
                weapon.setY(yCoordinate+50);
                meter.setY(yCoordinate-50);
            }
            else if (weaponYdir < 0) {
                weapon.setY(yCoordinate-50);
                meter.setY(yCoordinate+50);

            }
            else {
                weapon.setY(yCoordinate);
                meter.setY(yCoordinate);
            }
            if ((weaponXdir>0) & (weaponYdir==0)){ weapon.setRotation(0); }
            else if ((weaponXdir>0) & (weaponYdir>0)){ weapon.setRotation(45); }
            else if ((weaponXdir>0) & (weaponYdir<0)){ weapon.setRotation(-45); }
            else if ((weaponXdir==0) & (weaponYdir>0)){ weapon.setRotation(90); }
            else if ((weaponXdir==0) & (weaponYdir<0)){ weapon.setRotation(-90); }
            else if ((weaponXdir<0) & (weaponYdir==0)){ weapon.setRotation(180); }
            else if ((weaponXdir<0) & (weaponYdir>0)){ weapon.setRotation(135); }
            else if ((weaponXdir<0) & (weaponYdir<0)){ weapon.setRotation(-135); }


        }
    }
    public void levelUP(){
        if (powerLevel < 4) {
            powerLevel++;
            meter.setRegion(159+(powerLevel*25),174,25,25);
        }
    }
    public void fireShot(){
        if (coolDown == 0) {
            float xSp = weaponXdir;
            float ySp = weaponYdir;

            int type;
            if ((xMove==0) & (yMove==0)) { type = 1; }
            else { type = 0; }


            bullet b = new bullet(weapon.getX(), weapon.getY(), xSp, ySp, powerLevel, type, SpriteSheet);
            shootSound.play();
            setBulletDirection(b);
            liveShots.add(b);
            coolDown = firingRate;
        }
    }
    private void setBulletDirection(bullet b) {
        if ((weaponXdir > 0) & (weaponYdir == 0)) { b.getSprite().rotate(270); }
        else if ((weaponXdir < 0) & (weaponYdir == 0)) { b.getSprite().rotate(90); }
        else if ((weaponXdir == 0) & (weaponYdir < 0)) { b.getSprite().rotate(180); }
        else if ((weaponXdir > 0) & (weaponYdir > 0)) { b.getSprite().rotate(315); }
        else if ((weaponXdir < 0) & (weaponYdir > 0)) { b.getSprite().rotate(45); }
        else if ((weaponXdir < 0) & (weaponYdir < 0)) { b.getSprite().rotate(135); }
        else if ((weaponXdir > 0) & (weaponYdir < 0)) { b.getSprite().rotate(225); }
    }
    private bullet reflectShot(bullet b){
        b.levelUP();
        bullet c = new bullet(b.getxCoord(), b.getyCoord(),
                b.getXDir()*-1, b.getYDir()*-1,
                b.getLevel(), b.getType(), SpriteSheet);
        shootSound.play();
        c.getSprite().rotate(b.getSprite().getRotation() + 180);
        liveShots.add(c);
        return c;
    }
    private void straight(bullet b){
        int count = powerLevel - 1;
        int delay = 10;
        float xCincrement = 5 * b.getXDir() * -1;
        float yCincriment = 5 * b.getYDir() * -1;
        while (count > 0) {
            bullet c = new bullet(b.getxCoord() + xCincrement,
                    b.getyCoord() + yCincriment,
                    b.getXDir()*-1, b.getYDir()*-1,
                    1, b.getType(), SpriteSheet);
            c.getSprite().rotate(b.getSprite().getRotation() + 180);
            liveShots.add(c);
            c.setDelay(delay);
            count--;
            delay+=10;
            xCincrement += 5 * b.getXDir();
            yCincriment += 5 * b.getYDir();
        }
        reflectShot(b);
        shootSound.play();
    }
    private void wall(bullet b) {
        int count = powerLevel;
        float xIncrement = 20 * b.getYDir();
        float yIncrement = 20 * b.getXDir();
        if ((b.getYDir() != 0) & ( b.getXDir() != 0)) { yIncrement*=-1; }

        reflectShot(b);
        while (count > 1) {
            bullet c = new bullet(b.getxCoord() + xIncrement,
                    b.getyCoord() + yIncrement,
                    b.getXDir()*-1, b.getYDir()*-1,
                    1, 0, SpriteSheet);
            liveShots.add(c);
            xIncrement *= -1;
            yIncrement *= -1;
            bullet d = new bullet(b.getxCoord() + xIncrement,
                    b.getyCoord() + yIncrement,
                    b.getXDir()*-1, b.getYDir()*-1,
                    1, 0, SpriteSheet);
            liveShots.add(d);
            if (xIncrement > 1) { xIncrement+=20; }
            else if (xIncrement < -1 ) { xIncrement-=20; }
            if (yIncrement > 1) { yIncrement+=20; }
            else if (yIncrement < -1) { yIncrement-=20; }
            count--;

        }
        reflectShot(b);
        shootSound.play();
    }
    private void deflect(bullet b){
        int count = powerLevel;
        int delay = 15;
        float xCincrement = 5 * b.getXDir() * -1;
        float yCincriment = 5 * b.getYDir() * -1;
        while (count > 0) {
            bullet c = new bullet(b.getxCoord() + xCincrement,
                    b.getyCoord() + yCincriment,
                    weaponXdir, weaponYdir,
                    1, 1, SpriteSheet);
            setBulletDirection(c);
            liveShots.add(c);
            c.setDelay(delay);
            count--;
            delay+=15;
            xCincrement += 5 * weaponXdir;
            yCincriment += 5 * weaponYdir;
        }
        shootSound.play();
    }
    private void pop(bullet b){
        Random r = new Random();
        int count = powerLevel;
        float xDir = b.getXDir()*-1;
        float yDir = b.getYDir()*-1;
        boolean xChange = false;
        if (xDir == 0) { xChange = true;}
        while (count > 0) {
            bullet c = new bullet(b.getxCoord(), b.getyCoord(),
                    xDir, yDir,
                    1, 0, SpriteSheet);
            c.setDelay(count*2);
            shootSound.play();
            liveShots.add(c);
            if (xChange) {
                xDir += r.nextFloat()/2;
                xDir*=-1;
            }
            else {
                yDir += r.nextFloat()/2;
                if (count%2 == 0) { yDir*=-1; }
            }
            count--;

        }
    }

    public void parry() {
        if (parryCool == 0) {
            parrySound.play();
            parryCounter++;
            parryCool = parryRate;
            weapon.setColor(Color.RED);

        }
    }
    private void riposte(bullet b){
        if (b.getType() == 1) { straight(b); }
        else  { wall(b); }
    }
    private void absorb(bullet b) {
        powerMeter += b.getLevel();
        if ((powerMeter >= 4) & (powerLevel < 2)) { levelUP(); }
        else if ((powerMeter >= 9) & (powerLevel < 3)) { levelUP(); }
        else if ((powerMeter >= 16) & (powerLevel < 4)) { levelUP(); }
        else if ( powerLevel == 4) {
            if (b.getType() == 1) { deflect(b); }
            else { pop(b); }
        }
    }

    public void setControl(AI control) {
        this.control = control;
    }

    // collions detection stuff
    public boolean collison(bullet b, Sprite s) {
        if (b.isActive()) {
            float sX = s.getX();                                    // sprite's left X value
            float sW = s.getX() + s.getWidth();                     // right X value
            float sY = s.getY();                                    // bottom Y value
            float sH = s.getY() + s.getHeight();                    // top Y value
            float bX = b.getxCoord();                              // bullet's left X value
            float bW = b.getxCoord() + b.getSprite().getWidth();   // right X value
            float bY = b.getyCoord();                              // bottom Y value
            float bH = b.getyCoord() + b.getSprite().getHeight();  // top Y value

            if ((bX <= sW) & (bX >= sX) & (bY <= sH) & (bY >= sY)) { return true; }
            if ((bX <= sW) & (bX >= sX) & (bH <= sH) & (bH >= sY)) { return true; }
            if ((bW <= sW) & (bW >= sX) & (bH <= sH) & (bH >= sY)) { return true; }
            if ((bW <= sW) & (bW >= sX) & (bY <= sH) & (bY >= sY)) { return true; }
        }
        return false;
    }



    public boolean isHit(LinkedList<bullet> enemyFire, Sprite s){
        for (bullet b : enemyFire) {
            if (collison(b,body) & (!invincible)) {
                b.hitTarget();
                hurtSound.play();
                control.gotHit();
                return true;
            }
        }
        return false;
    }
    public boolean isParried(bullet b){
        if (collison(b, weapon)) {
            if ((xMove==0) & (yMove==0) & (!hasParried)) {
                hasParried = true;
                riposte(b);
            }
            else { absorb(b); }
            b.hitTarget();
            parriedSound.play();
            return true;
        }
        return false;
    }

    // updaters
    private void updateParry(LinkedList<bullet> enemyfire) {
        if (parryCool > 0) {
            parryCool--;
            if (parryCool == 0) { weapon.setColor(Color.WHITE); }
        }
        if (parryCounter > 0) {
            weapon.rotate90(false);
            parryCounter++;
            for (bullet b: enemyfire) { isParried(b); };
            if (parryCounter > 8) {
                parryCounter = 0;
                hasParried = false;

            }
        }
    }
    private void updateDamage(){
        if (hitState > 0) {
            if (hitState == 1) {
                invincible = true;
                body.setColor(Color.GOLD);
            }
            else if (hitState == iFrames) {
                invincible = false;
                body.setColor(Color.WHITE);
            }
            hitState++;
            if (hitState == 120) {
                hitState = 0;
                healSound.play();
            }
        }
    }
    private void updatePosition(){
        float Xincrement = movementSpeed * xMove;
        xCoordinate += Xincrement;
        float Yincrement = movementSpeed * yMove;
        yCoordinate += Yincrement;
        for (Sprite s : spirtes) {
            s.setX(s.getX() + Xincrement);
            s.setY(s.getY() + Yincrement);
        }
        xMove = 0;
        yMove = 0;
    }
    private void updateBullets() {
        // check all the bullets for off screens and update their positions
        LinkedList<bullet> toRemove = new LinkedList<>();
        for (bullet b : liveShots) {
            b.update();
            if (b.isOffScreen() | b.hasHit()) { toRemove.add(b); }
        }
        liveShots.removeAll(toRemove);
        if (coolDown > 0 ) { coolDown--; }                                         // update cool down variable
    }

    private void updateEyes(int counter, character enemy){
        if ((counter%5) == 0) {
            float adjacent = enemy.getX() - xCoordinate;
            float opposite = enemy.getY() - yCoordinate;
            if (adjacent == 0) {
                adjacent += .5f;
            }
            if (opposite == 0) {
                opposite += .5f;
            }
            float hypotenuse = (float) Math.sqrt((adjacent * adjacent) + (opposite * opposite));
            float toChangeX = (adjacent / hypotenuse) + eyes.getX();
            float toChangeY = (opposite / hypotenuse) + eyes.getY();

            while (toChangeX < (xCoordinate + 15)) {
                toChangeX++;
            }
            while (toChangeX > (xCoordinate + 25)) {
                toChangeX--;
            }
            while (toChangeY < (yCoordinate + 15)) {
                toChangeY++;
            }
            while (toChangeY > (yCoordinate + 30)) {
                toChangeY--;
            }
            eyes.setX(toChangeX);
            eyes.setY(toChangeY);
        }
    }
    // THE update function
    public void update(int counter, character enemy){
        updateBullets();
        updateParry(enemy.getLiveShots());
        updateEyes(counter, enemy);
        updatePosition();
        if (!enemy.isDefeat()) {
            if (isHit(enemy.getLiveShots(), body)) {
                if (hitState == 0)  { hitState++; }
                else { defeated(); }
            }
            updateDamage();
        }
    }

    // Handles the Sprite.draw() calls for the various sprites.
    public void draw(Batch b){
        body.draw(b);
        for (bullet shot : liveShots) { shot.getSprite().draw(b); }
        if (!defeat) {
            if (hitState == 0) { item.draw(b); }
            eyes.draw(b);
            weapon.draw(b);
            meter.draw(b);
        }
    }
    public void disposeSounds(){
        hurtSound.dispose();
        healSound.dispose();
        shootSound.dispose();
        parrySound.dispose();
        parriedSound.dispose();
        winSound.dispose();
    }

}
