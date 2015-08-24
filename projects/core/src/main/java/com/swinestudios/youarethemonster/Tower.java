package com.swinestudios.youarethemonster;

import java.util.Random;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Tower{

	public float x, y;

	public float health, maxHealth = 100; //TODO adjust later
	
	public final float healthBarMaxWidth = 52;
	public final float healthBarHeight = 6;
	public final float healthBarYOffset = -20;
	
	public final int RADIUS = 96; //TODO should this be final?
	public final int HITBOX_RADIUS = 20;
	public final float SHOT_MAGNITUDE = 4.0f; //How strong a tower shoots a projectile
	public final float SHOT_LIFETIME = 0.2f; //How long a projectile lasts

	public final float SHOT_RATE = 6; //Shots per second
	public float shotTimer, maxShotTimer;

	public boolean isBeingBuilt;
	public float buildingTimer, maxBuildingTime = 2; //How long it takes to build a tower

	public boolean isActive;
	public boolean autoMobsOnly;
	
	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite towerSprite;
	
	public Random random = new Random();
	
	public static Sound shotSound = Gdx.audio.newSound(Gdx.files.internal("LessExplosiveLaunch.wav"));
	//public static Sound towerHit = Gdx.audio.newSound(Gdx.files.internal("TowerHit.wav"));
	public static Sound destroyedSound = Gdx.audio.newSound(Gdx.files.internal("TowerDestroyed.wav"));
	public static Sound constructionSound = Gdx.audio.newSound(Gdx.files.internal("TowerConstruction2.wav"));
	public static Sound builtSound = Gdx.audio.newSound(Gdx.files.internal("TowerBuilt.wav"));
	
	public Mob nearestMob;
	public ControllableMob nearestControllableMob;

	public Tower(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		isBeingBuilt = true;
		constructionSound.loop();
		buildingTimer = 0;
		this.level = level;
		type = "Tower";
		health = maxHealth;
		shotTimer = 0;
		maxShotTimer = 1f / SHOT_RATE;
		nearestControllableMob = level.player;
		
		setAutoMobsOnly(random.nextBoolean());
		
		//adjustSprite(towerSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
	}

	public void render(Graphics g){
		if(isActive){
			if(towerSprite != null){
				g.drawSprite(towerSprite, x - towerSprite.getWidth() / 2, y - towerSprite.getHeight() / 2);
			}
			//TODO Temporary shape placeholder
			g.setColor(Color.GREEN);
			g.drawCircle(x,  y, RADIUS / 4);
			g.drawCircle(x,  y, RADIUS);
			
			//Draw health bar
			g.setColor(Color.RED);
			g.fillRect(x - healthBarMaxWidth / 2, y + healthBarYOffset, healthBarMaxWidth, healthBarHeight);
			g.setColor(Color.GREEN);
			g.fillRect(x - healthBarMaxWidth / 2, y + healthBarYOffset, healthBarMaxWidth * (health / maxHealth), healthBarHeight);
			if(isBeingBuilt){
				g.drawString("Building... " + (maxBuildingTime - buildingTimer), x, y + 6);
			}
		}
	}

	public void update(float delta){	
		if(isActive){
			//Take maxBuildingTime seconds to build a tower
			if(isBeingBuilt){
				buildingTimer += delta;
				if(buildingTimer > maxBuildingTime){
					isBeingBuilt = false;
					buildingTimer = 0;
					constructionSound.stop();
					builtSound.play();
				}
				return; //Skip all the logic until a tower is finished building
			}
			
			checkProjectileCollision();

			//Tower only targets path-following mobs
			if(autoMobsOnly){
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
			//Tower only targets controllable mobs
			else{
				//Only start shooting if controllable mob is in range
				if(nearestControllableMob != null && distanceTo(nearestControllableMob.hitbox2) <= RADIUS){ 
					shotTimer += delta;
					if(shotTimer > maxShotTimer){
						shootControllableMob();
						shotTimer = 0;
					}
				}
			}

			//If a tower is destroyed
			if(health <= 0){
				isActive = false;
				destroyedSound.play();
				//Until the tower is completely removed, move it far away
				x = -500;
				y = -500;
				level.towers.remove(this);
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
	
	/*
	 * Check for projectiles from any mobs
	 */
	public void checkProjectileCollision(){
		for(int i = 0; i < level.mobProjectiles.size(); i++){
			MobProjectile temp = level.mobProjectiles.get(i);
			if(temp != null && temp.isActive){
				//TODO adjust hitboxes when sprites are done
				if(distanceTo(temp.hitbox) <= HITBOX_RADIUS){ //If there is a collision
					temp.isActive = false;
					level.mobProjectiles.remove(temp);
					dealDamage(temp.damage);
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
		shotSound.play(1, 1.5f, 0);
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
		shotSound.play(1, 1.8f, 0);
	}
	
	public void dealDamage(float amount){
		health -= amount;
		//towerHit.play();
	}

	public void setAutoMobsOnly(boolean flag){
		autoMobsOnly = flag;
		if(autoMobsOnly){
			towerSprite = new Sprite(new Texture(Gdx.files.internal("candyTower1.png")));
		}
		else{
			towerSprite = new Sprite(new Texture(Gdx.files.internal("candyTower2.png")));
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