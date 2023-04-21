package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Random;

public class AI {

    private Random randy;
    private character player;
    private int xDirection, yDirection;
    private Sprite weapon;
    private int moveCounter;
    private int goalCounter;
    private int moveTask, previous;
    private float xGoalCoord, yGoalCoord;
    private int xGoalDir, yGoalDir;
    private boolean moveTaskComplete;



    public AI(character p){
        player = p;
        p.setControl(this);
        weapon = player.getWeapon();
        randy= new Random();
        moveCounter = 360;
        moveTask = 0;
        previous = 0;
        moveTaskComplete = false;


    }


    public void Move(character enemy) {
        // Create all the variables for the functions calculations
        float X = player.getX();                                             // sprite's left X value
        float W = player.getX() + weapon.getWidth();                         // right X value
        float Y = player.getY();                                             // bottom Y value
        float H = player.getY() + weapon.getHeight();

        // top Y value
        float eX = enemy.getX();                              // bullet's left X value
        float eW = enemy.getX() + 50 ;   // right X value
        float eY = enemy.getY();                              // bottom Y value
        float eH = enemy.getY() + 40;  // top Y value


        for (bullet b: enemy.getLiveShots()) {
            float xDistance = Math.abs(X - b.getxCoord());
            float yDistance = Math.abs(Y - b.getyCoord());
            if ((xDistance + yDistance) < 200) {
                eX = b.getxCoord();
                eY = b.getyCoord();
                break;
            }
        }

        int xDir = 0;
        int yDir = 0;

        float xDistance = Math.abs(X - eX);
        float yDistance = Math.abs(Y - eY);


        moveCounter--;
        goalCounter--;
        if (goalCounter <= 0) { moveTaskComplete = true; }
        if (moveCounter <= 0) { // if the move counter resets it switches to a brief hunt
            moveCounter = 360;
            moveTaskComplete = false;
            goalCounter = 30;
            moveTask = 0;
        }
        if ((xDistance + yDistance) < 200) {
            moveTaskComplete = false;
            goalCounter = 30;
            moveTask = 1;
        }



        if (moveTaskComplete) { // choose new goal
            moveTaskComplete = false;
            previous = moveTask;

            if ((xDistance + yDistance > 1000)) { moveTask = 0; }
            else if ((xDistance < 150) & (yDistance < 150)) { moveTask = 1; }
            else if ((W <= 200) | (X >= 1000) | (H <= 100) | (Y >= 500 )) { moveTask = 2; }
            else { moveTask = randy.nextInt(6); }

            if (moveTask == previous) { moveTask++; }
            if (moveTask > 4) { moveTask = 0; }



            if (moveTask == 0) { goalCounter = 60; }         // Code: HUNT will pursue opponent
            else if (moveTask == 1) { goalCounter = 30; }    // Code: EVADE will retreat from opponent
            else if (moveTask == 2) {
                xGoalCoord = (eX * -1) + 1200;
                yGoalCoord = (eY * -1) + 600;
                goalCounter = randy.nextInt(50) + 30;
            }                                               // Code: JUKE will move to the opposite position of opponent;
            else if (moveTask == 3) {
                xGoalCoord = X;
                yGoalCoord = Y;
                goalCounter = randy.nextInt(20) + 30;
            }                                               // Code: DEFEND will hover around point
            else if (moveTask == 4) {
                xGoalCoord = eX;
                yGoalCoord = eY;
                goalCounter = randy.nextInt(20) + 30;
            }                                               // Code: FOLLOW will move to a position based on opponent;
            else if (moveTask == 5) {
                xGoalDir = randy.nextInt(3) - 2;
                yGoalDir = randy.nextInt(3) - 2;
                goalCounter = randy.nextInt(20) + 30;
            }

        }

        if (moveTask == 0) {
            if ((eX >= W) & ((eY < Y) | (eH > H))) { xDir = 1; }
            else if ((eW < X) & ((eY < Y) | (eH > H))) { xDir = -1; }
            if ((eY >= H) & ((eX < X) | (eW > W))) { yDir = 1; }
            else if ((eH < Y) & ((eX < X) | (eW > W))) { yDir = -1; }
        }
        else if (moveTask == 1) {
            if ((eX >= W) & ((eY < Y) | (eH > H))) { xDir = -1; }
            else if ((eW < X) & ((eY < Y) | (eH > H))) { xDir = 1; }
            if ((eY >= H) & ((eX < X) | (eW > W))) { yDir = -1; }
            else if ((eH < Y) & ((eX < X) | (eW > W))) { yDir = 1; }
        }
        else if (moveTask == 5) {
            xDir = xGoalDir;
            yDir = xGoalDir;
        }
        else {
            if (X < xGoalCoord) { xDir = 1; }
            else { xDir = -1; }
            if ( Y < yGoalCoord) { yDir = 1; }
            else { yDir = -1; }
            double goalDistance = Math.abs(X - xGoalCoord) + Math.abs(Y - yGoalCoord);
            if (goalDistance < 10) {
                int toAdd = randy.nextInt(100);
                if (randy.nextInt(2) == 0) { toAdd *= -1; }
                xGoalCoord += toAdd;
                toAdd = randy.nextInt(100);
                if (randy.nextInt(2) == 0) { toAdd *= -1; }
                yGoalCoord += toAdd;
            }
        }
        player.setxMove(xDir);
        player.setyMove(yDir);
    }

    public void Aim(character enemy){
        xDirection = 0;
        yDirection = 0;
        float X = player.getX() + 25;
        float Y = player.getY() + 25;

        float eX = enemy.getX() + 25;
        float eY = enemy.getY() + 25;

        float opposite = Y - eY;
        float adjacent = X - eX;

        if (eX > X) { xDirection = 1; }
        else if (eX <= X) { xDirection = -1; }
        if (eY > Y) { yDirection = 1; }
        else if (eY <= Y) { yDirection = -1; }

        if (Math.abs(opposite/adjacent) < .57735f) { yDirection = 0; }
        if (Math.abs(opposite/adjacent) > 1.732f) { xDirection = 0; }

        player.moveWeapon(xDirection, yDirection);
    }
    public void Shoot(character enemy){
        boolean shouldParry = false;
        for (bullet b: enemy.getLiveShots()) {
            float sX = weapon.getX();                                             // sprite's left X value
            float sW = weapon.getX() + weapon.getWidth();                         // right X value
            float sY = weapon.getY();                                             // bottom Y value
            float sH = weapon.getY() + weapon.getHeight();                        // top Y value
            float bX = b.getxCoord();                              // bullet's left X value
            float bW = b.getxCoord() + b.getSprite().getWidth();   // right X value
            float bY = b.getyCoord();                              // bottom Y value
            float bH = b.getyCoord() + b.getSprite().getHeight();  // top Y value
            if ((bX <= sW) & (bX >= sX) & (bY <= sH) & (bY >= sY)) { shouldParry = true; }
            else if ((bX <= sW) & (bX >= sX) & (bH <= sH) & (bH >= sY)) { shouldParry = true; }
            else if ((bW <= sW) & (bW >= sX) & (bH <= sH) & (bH >= sY)) { shouldParry = true; }
            else if ((bW <= sW) & (bW >= sX) & (bY <= sH) & (bY >= sY)) { shouldParry = true; }
        }
        if (shouldParry) { player.parry(); }
        else { player.fireShot(); }
    }
    public void gotHit() {
        moveCounter = 0;
    }







}
