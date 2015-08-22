package com.swinestudios.youarethemonster;

import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.graphics.Graphics;

public class YouAreMonster extends BasicGame {
	public static final String GAME_IDENTIFIER = "com.swinestudios.youarethemonster";

	private Texture texture;
	
	@Override
    public void initialise() {
    	texture = new Texture("mini2Dx.png");
    }
    
    @Override
    public void update(float delta) {
    
    }
    
    @Override
    public void interpolate(float alpha) {
    
    }
    
    @Override
    public void render(Graphics g) {
		g.drawTexture(texture, 0f, 0f);
    }
}
