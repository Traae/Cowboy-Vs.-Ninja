// the main class for my for my game

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;

public class CvNmain extends ApplicationAdapter {
	SpriteBatch batch;			// the batch for drawing to the screen
	character player1;			// p1 and their data
	character player2;			// p2 and all their data
	Texture background;			// a plain texture for a background image
	int frameCounter = 0;		// a counter used incremented every render, used for certain functions to skip cycles
	int xDirection;				// used for player input
	int yDirection;				// used for player input

	AI ai1;
	AI ai2;
	boolean CowboyAI;
	boolean NinjaAI;
	Texture start;
	boolean starter = true;


	@Override
	public void create () {
		batch = new SpriteBatch();

		player1 = new character("cowboySpriteSheet.png", 100, 50);
		player2 = new character("ninjaSpriteSheet.png", 1100, 500);
		start = new Texture(Gdx.files.internal("start.png"));
		background = new Texture(Gdx.files.internal("background1.png"));
		//background = new Texture("D:/Code/CowboyVsNinja/core/assets/background1.png");

		ai1 = new AI(player1);
		CowboyAI = false;
		ai2 = new AI(player2);
		NinjaAI = false;

	}

	@Override
	public void render () {
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) { reset(); }					// reset the game on F1
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) { 						// exit on escape
			dispose();
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.F2)) { CowboyAI = true; }

		if (Gdx.input.isKeyPressed(Input.Keys.F3)) { NinjaAI =true; }

		if (Gdx.input.isKeyPressed(Input.Keys.F4)) { starter = false; }


		frameCounter++;															//increment frame counter
		if (frameCounter > 60) { frameCounter = 0; }							// and reset it at 60
//		Gdx.gl.glClearColor(0, 0, 0, 1);									// background color stuff
//		Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);								// unneeded at this time

		//get the Actions for player 1 and 2 or the ai if turned on.
		if (CowboyAI) {
			if (frameCounter<50) { ai1.Move(player2); }
			if (frameCounter%5==0) { ai1.Aim(player2); }
			ai1.Shoot(player2);
		}
		else {
			getP1movement();
			getPlaiming();
			getP1action();
		}
		if (NinjaAI) {
			if (frameCounter<50) { ai2.Move(player1); }
			if (frameCounter%5==0) {ai2.Aim(player1);}
			ai2.Shoot(player1);
		}
		else {
			getP2movement();
			getP2aiming();
			getP2action();
		}

		player1.update(frameCounter, player2);									// update player 1
		player2.update(frameCounter, player1);									// update player 2
		batch.begin();

		batch.draw(background, 0, 0);									// the back ground
		player1.draw(batch);													// draw player 1
		player2.draw(batch);													// draw player 2
		if (starter) { batch.draw(start, 0,0); }
		batch.end();															// finish drawing and stuff
	}

	@Override
	public void dispose(){
		batch.dispose();
		background.dispose();
		start.dispose();
		player1.getSpriteSheet().dispose();
		player2.getSpriteSheet().dispose();
		player1.disposeSounds();
		player2.disposeSounds();
	}

	public void reset(){
		batch.dispose();
		player1.getSpriteSheet().dispose();
		player2.getSpriteSheet().dispose();
		create();
	}


	public void getP1movement(){
		if (Gdx.input.isKeyPressed(Input.Keys.A)){ player1.setxMove(-1); }
		else if (Gdx.input.isKeyPressed(Input.Keys.D)){ player1.setxMove(1); }
		if (Gdx.input.isKeyPressed(Input.Keys.W)){ player1.setyMove(1); }
		else if (Gdx.input.isKeyPressed(Input.Keys.S)){ player1.setyMove(-1); }
	}
	public void getP1action(){
		if (Gdx.input.isKeyPressed(Input.Keys.U)) { player1.parry(); }
		if (Gdx.input.isKeyPressed(Input.Keys.O)) { player1.fireShot(); }
	}
	public void getPlaiming(){
		xDirection = 0;
		yDirection = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.L)) { xDirection = 1; }
		if (Gdx.input.isKeyPressed(Input.Keys.J)) { xDirection = -1; }
		if (Gdx.input.isKeyPressed(Input.Keys.I)) { yDirection = 1; }
		if (Gdx.input.isKeyPressed(Input.Keys.K)) { yDirection = -1; }
		player1.moveWeapon(xDirection, yDirection);
	}
	public void getP2movement(){
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){ player2.setxMove(-1);}
		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){ player2.setxMove(1); }
		if (Gdx.input.isKeyPressed(Input.Keys.UP)){ player2.setyMove(1); }
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){ player2.setyMove(-1);}
	}
	public void getP2action(){
		if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_7)) { player2.parry();  }
		if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)) { player2.fireShot(); }
	}
	public void getP2aiming(){
		xDirection = 0;
		yDirection = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)) { xDirection = 1; }
		else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)) { xDirection = -1; }
		if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) { yDirection = 1; }
		else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_5)) { yDirection = -1; }
		player2.moveWeapon(xDirection, yDirection);
	}




}