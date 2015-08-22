package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

public class Mob{
	
	public static int mobcount = 0;
	public int id;

	public float x, y, velX, velY;
	public final int RADIUS = 8; //TODO should this be final?

	public boolean isActive;

	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite mobSprite;

	public Mob(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		type = "Mob";
		//mobSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(mobSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
		//TODO temporary values
		velX = 1;
		velY = 1;
		id = ++mobcount;
	}

	public void render(Graphics g){
		if(mobSprite != null){
			g.drawSprite(mobSprite, x, y);
		}
		else{ //TODO Temporary rectangle placeholder
			g.drawCircle(x, y, RADIUS);
		}
	}


	public void update(float delta){
		//TODO may need to rewrite movement later on
		x += velX;
		y += velY;
		
		hitbox.setX(x);
		hitbox.setY(y);
	}

	/*
	 * Returns the current tile position, given the specific tile dimensions
	 */
	public float getTileX(int tileSize){
		return (int)(x / tileSize) * tileSize;
	}

	/*
	 * Returns the current tile position, given the specific tile dimensions
	 */
	public float getTileY(int tileSize){
		return (int)(y / tileSize) * tileSize;
	}

	/*
	 * Returns the distance between this and the given target
	 */
	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}

	public float distanceTo(Circle target){
		return ((float)Math.pow(Math.pow((target.getY() - this.y), 2.0) + Math.pow((target.getX() - this.x), 2.0), 0.5));
	}

	/*
	 * Sets up any images that this tower may have. Necessary because images are flipped and have the origin
	 * on the bottom-left by default.
	 */
	public void adjustSprite(Sprite... s){
		for(int i = 0; i < s.length; i++){
			if(s != null){
				s[i].setOrigin(0, 0);
				s[i].flip(false, true);
			}
		}
	}

}
