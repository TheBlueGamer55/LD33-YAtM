package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

public class MobProjectile{

	public float x, y, velX, velY;
	public float damage = 1; //How much damage this projectile does to a mob
	public float lifeTimer = 0, maxLifeTimer; //For how long does this projectile last
	public final int RADIUS = 2; 

	public boolean isActive;

	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite projectileSprite;

	public MobProjectile(float x, float y, float velX, float velY, float lifetime, Gameplay level){
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		maxLifeTimer = lifetime;
		isActive = true;
		this.level = level;
		type = "MobProjectile";
		//projectileSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(projectileSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
	}

	public void render(Graphics g){
		if(isActive){
			if(projectileSprite != null){
				g.drawSprite(projectileSprite, x, y);
			}
			else{ //TODO Temporary shape placeholder
				g.drawCircle(x, y, RADIUS);
			}
		}
	}

	public void update(float delta){
		if(isActive){
			if(lifeTimer < maxLifeTimer){
				lifeTimer += delta;
				if(lifeTimer > maxLifeTimer){
					isActive = false;
					level.projectiles.remove(this);
				}
			}

			x += velX;
			y += velY;

			hitbox.setX(x);
			hitbox.setY(y);
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
