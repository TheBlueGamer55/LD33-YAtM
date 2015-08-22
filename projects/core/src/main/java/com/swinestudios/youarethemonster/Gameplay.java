package com.swinestudios.youarethemonster;

import java.util.ArrayList;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Gameplay implements GameScreen{
	
	public static int ID = 2;
	
	public ArrayList<Mob> mobs;
	public ArrayList<Tower> towers;
	public Tower tempTower;
	public Mob mob1, mob2, mob3;
	
	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		mobs = new ArrayList<Mob>();
		towers = new ArrayList<Tower>();
		//TODO temporary code for testing
		tempTower = new Tower(320, 250, this);
		towers.add(tempTower);
		mob1 = new Mob(-64, -64, this);
		mob2 = new Mob(0, 0, this);
		mob3 = new Mob(5, 5, this);
		mobs.add(mob1);
		mobs.add(mob2);
		mobs.add(mob3);
	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}
	
	@Override
	public void onPause() {
	}

	@Override
	public void onResize(int arg0, int arg1) {
	}

	@Override
	public void onResume() {
	}

	@Override
	public void postTransitionIn(Transition t){
		
	}

	@Override
	public void postTransitionOut(Transition t){
		
	}

	@Override
	public void preTransitionIn(Transition t){
		
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawString("This is the gameplay", 320, 240);
		renderMobs(g);
		renderTowers(g);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		updateMobs(delta);
		updateTowers(delta);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
		}
	}
	
	public void renderTowers(Graphics g){
		for(int i = 0; i < towers.size(); i++){
			towers.get(i).render(g);
		}
	}
	
	public void updateTowers(float delta){
		for(int i = 0; i < towers.size(); i++){
			towers.get(i).update(delta);
		}
	}
	
	public void renderMobs(Graphics g){
		for(int i = 0; i < mobs.size(); i++){
			mobs.get(i).render(g);
		}
	}
	
	public void updateMobs(float delta){
		for(int i = 0; i < mobs.size(); i++){
			mobs.get(i).update(delta);
		}
	}

}
