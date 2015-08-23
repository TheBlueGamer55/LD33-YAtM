package com.swinestudios.youarethemonster;

import org.mini2Dx.core.graphics.Graphics;

public class TowerController{

	public boolean isActive;

	public Gameplay level;
	public String type;
	public static float points = 0;

	public TowerController(float x, float y, float velX, float velY, float lifetime, Gameplay level){
		isActive = true;
		this.level = level;
		type = "TowerController";
	}

	public void render(Graphics g){
		//Empty for now
	}

	public void update(float delta){
		
	}

}
