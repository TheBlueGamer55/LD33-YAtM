package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

public class Block extends Rectangle{

	public boolean isActive;
	public String type;
	private Gameplay level;

	public Block(float x, float y, float width, float height, Gameplay level){
		super(x, y, width, height);
		isActive = true;
		type = "Block";
		this.level = level;
	}

	public void render(Graphics g){
		g.fillRect(x, y, width, height);
	}

	public void update(float delta){
		//Empty for now
	}

}
