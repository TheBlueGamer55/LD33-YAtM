package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

public class Tower{

	public float x, y;
	
	public final int RADIUS = 80; //TODO should this be final?
	public final float SHOT_MAGNITUDE = 4.0f; //How strong a tower shoots a projectile
	public final float SHOT_LIFETIME = 0.2f; //How long a projectile lasts
	
	public final float SHOT_RATE = 6; //Shots per second
	public float shotTimer, maxShotTimer;

	public boolean isActive;

	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite towerSprite;

	public Mob nearestMob;

	public Tower(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		type = "Tower";
		shotTimer = 0;
		maxShotTimer = 1f / SHOT_RATE;
		//towerSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(towerSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
	}

	public void render(Graphics g){
		if(towerSprite != null){
			g.drawSprite(towerSprite, x, y);
		}
		else{ //TODO Temporary shape placeholder
			g.drawCircle(x,  y, RADIUS / 4);
			g.drawCircle(x,  y, RADIUS);
		}
	}


	public void update(float delta){		
		findNearestMob();
		//Only start shooting if nearest mob is in range
		if(nearestMob != null && distanceTo(nearestMob.hitbox) <= RADIUS){ 
			shotTimer += delta;
			if(shotTimer > maxShotTimer){
				shootNearestMob();
				shotTimer = 0;
			}
		}
	}

	public void findNearestMob(){
		for(int i = 0; i < level.mobs.size(); i++){
			Mob temp = level.mobs.get(i);
			if(distanceTo(temp.hitbox) <= RADIUS){ //If a mob is within range
				if(nearestMob == null){ //The first mob that becomes the nearest mob
					nearestMob = temp;
					continue;
				}
				if(distanceTo(temp.hitbox) < distanceTo(nearestMob.hitbox)){ //If this mob is the new closest mob
					nearestMob = temp;
				}
			}
		}
	}
	
	public void shootNearestMob(){
		float targetX = nearestMob.x;
		float targetY = nearestMob.y;
		float deltaX = targetX - this.x;
		float deltaY = targetY - this.y;
		float theta = (float) Math.atan2(deltaY, deltaX); //angle from player to mouse
		
		float vectorX = (float) Math.cos(theta) * SHOT_MAGNITUDE;
		float vectorY = (float) Math.sin(theta) * SHOT_MAGNITUDE;
		Projectile p = new Projectile(x, y, vectorX, vectorY, SHOT_LIFETIME, level);
		level.projectiles.add(p);
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
