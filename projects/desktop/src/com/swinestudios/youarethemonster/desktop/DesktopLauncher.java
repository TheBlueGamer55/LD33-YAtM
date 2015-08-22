package com.swinestudios.youarethemonster.desktop;

import org.mini2Dx.desktop.DesktopMini2DxGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.swinestudios.youarethemonster.YouAreMonster;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.stencil = 8;
		config.width = 640;
		config.height = 480;
		config.vSyncEnabled = true;
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;
		new LwjglApplication(new DesktopMini2DxGame(YouAreMonster.GAME_IDENTIFIER, new YouAreMonster()), config);
	}
}
