package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

public class CandyBase{

	public float x, y;

	public float health, maxHealth = 500; //TODO adjust later

	public final float healthBarMaxWidth = 64;
	public final float healthBarHeight = 6;
	public final float healthBarYOffset = -20;

	public final int RADIUS = 32;
	public final int SHOT_RADIUS = 100;
	
	public final float SHOT_MAGNITUDE = 4.0f; //How strong a tower shoots a projectile
	public final float SHOT_LIFETIME = 0.2f; //How long a projectile lasts

	public final float SHOT_RATE = 6; //Shots per second
	public float shotTimer, maxShotTimer;
	
	public Mob nearestMob;
	public ControllableMob nearestControllableMob;

	public boolean isActive;

	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite baseSprite;

	public CandyBase(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		type = "CandyBase";
		health = maxHealth;
		shotTimer = 0;
		maxShotTimer = 1f / SHOT_RATE;
		nearestControllableMob = level.player;
		//towerSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(baseSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
	}

	public void render(Graphics g){
		if(isActive){
			if(baseSprite != null){
				g.drawSprite(baseSprite, x, y);
			}
			else{ //TODO Temporary shape placeholder
				g.setColor(Color.PURPLE);
				g.drawCircle(x,  y, RADIUS / 4);
				g.drawCircle(x,  y, RADIUS);
			}
			//Draw health bar
			g.setColor(Color.RED);
			g.fillRect(x - healthBarMaxWidth / 2, y + healthBarYOffset, healthBarMaxWidth, healthBarHeight);
			g.setColor(Color.GREEN);
			g.fillRect(x - healthBarMaxWidth / 2, y + healthBarYOffset, healthBarMaxWidth * (health / maxHealth), healthBarHeight);
		}
	}

	public void update(float delta){	
		if(isActive){			
			checkProjectileCollision();
			checkMobCollision();

			//Tower can target auto mobs
			findNearestMob();
			//Only start shooting if nearest mob is in range
			if(nearestMob != null && distanceTo(nearestMob.hitbox) <= SHOT_RADIUS){ 
				shotTimer += delta;
				if(shotTimer > maxShotTimer){
					shootNearestMob();
					shotTimer = 0;
				}
			}
			//Tower can target controllable mobs
			//Only start shooting if controllable mob is in range
			if(nearestControllableMob != null && distanceTo(nearestControllableMob.hitbox2) <= SHOT_RADIUS){ 
				shotTimer += delta;
				if(shotTimer > maxShotTimer){
					shootControllableMob();
					shotTimer = 0;
				}
			}

			//If the base is destroyed, win the game
			if(health <= 0){
				isActive = false;
				level.gameWin = true;
			}
			//TODO debug code - remove later
			if(Gdx.input.isKeyJustPressed(Keys.T)){
				dealDamage(10);
			}
		}
	}
	
	public void findNearestMob(){
		for(int i = 0; i < level.mobs.size(); i++){
			Mob temp = level.mobs.get(i);
			if(distanceTo(temp.hitbox) <= SHOT_RADIUS){ //If a mob is within range
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
		float theta = (float) Math.atan2(deltaY, deltaX); 

		float vectorX = (float) Math.cos(theta) * SHOT_MAGNITUDE;
		float vectorY = (float) Math.sin(theta) * SHOT_MAGNITUDE;
		Projectile p = new Projectile(x, y, vectorX, vectorY, SHOT_LIFETIME, level);
		level.projectiles.add(p);
	}

	public void shootControllableMob(){
		float targetX = nearestControllableMob.hitbox2.getX();
		float targetY = nearestControllableMob.hitbox2.getY();
		float deltaX = targetX - this.x;
		float deltaY = targetY - this.y;
		float theta = (float) Math.atan2(deltaY, deltaX); 

		float vectorX = (float) Math.cos(theta) * SHOT_MAGNITUDE;
		float vectorY = (float) Math.sin(theta) * SHOT_MAGNITUDE;
		Projectile p = new Projectile(x, y, vectorX, vectorY, SHOT_LIFETIME, level);
		level.projectiles.add(p);
	}

	/*
	 * Check for projectiles from any mobs
	 */
	public void checkProjectileCollision(){
		for(int i = 0; i < level.mobProjectiles.size(); i++){
			MobProjectile temp = level.mobProjectiles.get(i);
			if(temp != null && temp.isActive){
				//TODO adjust hitboxes when sprites are done
				if(distanceTo(temp.hitbox) <= RADIUS * 2){ //If there is a collision
					temp.isActive = false;
					level.mobProjectiles.remove(temp);
					dealDamage(temp.damage);
				}
			}
		}
	}

	/*
	 * Check for collision from any mobs
	 */
	public void checkMobCollision(){
		for(int i = 0; i < level.mobs.size(); i++){
			Mob temp = level.mobs.get(i);
			if(temp != null && temp.isActive){
				//TODO adjust hitboxes when sprites are done
				if(distanceTo(temp.hitbox) <= RADIUS * 2){ //If there is a collision
					temp.isActive = false;
					temp.health = 0;
					temp.x = -100;
					temp.y = -100;
					temp.hitbox.setX(-100);
					temp.hitbox.setY(-100);
					level.mobs.remove(temp);
					dealDamage(temp.damage);
				}
			}
		}
	}
	
	public void dealDamage(float amount){
		health -= amount;
		//Tower.towerHit.play();
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