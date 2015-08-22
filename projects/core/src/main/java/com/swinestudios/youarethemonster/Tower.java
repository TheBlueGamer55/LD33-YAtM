package com.swinestudios.youarethemonster;

import java.util.ArrayList;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

public class Tower{

	public float x, y;
	public final int RADIUS = 80; //TODO should this be final?

	public boolean isActive;

	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite towerSprite;

	public ArrayList<Mob> mobsInRange;

	public Tower(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		type = "Tower";
		//towerSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(towerSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
		mobsInRange = new ArrayList<Mob>();
	}

	public void render(Graphics g){
		if(towerSprite != null){
			g.drawSprite(towerSprite, x, y);
		}
		else{ //TODO Temporary rectangle placeholder
			g.drawCircle(x,  y, RADIUS / 4);
			g.drawCircle(x,  y, RADIUS);
		}
	}


	public void update(float delta){		
		detectMobs();
		System.out.println(mobsInRange.size()); //TODO remove later
	}

	public void detectMobs(){
		for(int i = 0; i < level.mobs.size(); i++){
			Mob temp = level.mobs.get(i);
			if(temp != null){
				if(distanceTo(temp.hitbox) <= RADIUS){ //If a mob is within range
					System.out.println("Mob in range");
					if(!mobsInRange.contains(temp)){ //If the mob is new
						mobsInRange.add(temp);
					}
				}
				else{ //If the mob is out of range
					if(mobsInRange.contains(temp)){ //If the mob moved out of range
						mobsInRange.remove(temp);
					}
				}
			}
		}
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
