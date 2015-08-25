package com.swinestudios.youarethemonster;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class MainMenu implements GameScreen{
	
	public static int ID = 1;
	
	public static Sound menuTheme, select, violin;
	
	public boolean isSelected;
	public boolean showFace = false;
	public float selectTimer, maxSelectTimer = 2.0f;
	
	public Sprite background, face;
	
	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		menuTheme = Gdx.audio.newSound(Gdx.files.internal("menuTheme.ogg"));
		select = Gdx.audio.newSound(Gdx.files.internal("Select.wav"));
		violin = Gdx.audio.newSound(Gdx.files.internal("violin.wav"));
		background = new Sprite(new Texture(Gdx.files.internal("mainMenuBG.png")));
		background.setSize(640, 480);
		face = new Sprite(new Texture(Gdx.files.internal("blackLicoriceFace.png")));
		face.setSize(640, 480);
	}

	@Override
	public void postTransitionIn(Transition t){
		menuTheme.loop();
	}

	@Override
	public void postTransitionOut(Transition t){
		menuTheme.stop();
		isSelected = false;
		showFace = false;
		selectTimer = 0;
	}

	@Override
	public void preTransitionIn(Transition t){
		selectTimer = 0;
		isSelected = false;
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawSprite(background, 0, 0);
		if(showFace){
			g.drawSprite(face, 0, 0);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta) {
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
			if(!isSelected){
				isSelected = true;
				showFace = true;
				menuTheme.stop();
				//select.play();
				violin.play();
			}
		}
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		if(isSelected){
			selectTimer += delta;
			if(selectTimer > maxSelectTimer){
				sm.enterGameScreen(Gameplay.ID, new FadeOutTransition(), new FadeInTransition());
			}
		}
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

}
