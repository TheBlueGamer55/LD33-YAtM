package com.swinestudios.youarethemonster;

import org.mini2Dx.core.game.ScreenBasedGame;

public class YouAreMonster extends ScreenBasedGame{
	
	public static final String GAME_IDENTIFIER = "com.swinestudios.youarethemonster";
	
	@Override
	public void initialise() {
		this.addScreen(new MainMenu());
		this.addScreen(new Gameplay());
	}
	
	@Override
	public int getInitialScreenId() {
		return MainMenu.ID;
	}
}
