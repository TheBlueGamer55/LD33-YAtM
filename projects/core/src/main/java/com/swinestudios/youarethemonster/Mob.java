package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.graphics.Color;

public class Mob{
	
	public static int mobcount = 0;
	public int id;

	public float x, y, velX, velY;
	public float damage = 50; //How much damage a mob does if it kills itself when attacking the candy base
	public float health, maxHealth = 50; //TODO adjust later
	
	public final float healthBarMaxWidth = 20;
	public final float healthBarHeight = 4;
	public final float healthBarYOffset = -8;
	
	public char moveDirection;
	public final int RADIUS = 8; //TODO should this be final?
	public final int POINT_VALUE = 10; //TODO adjust later, how many points the hero player gets if a mob dies
	
	public final float SHOT_MAGNITUDE = 4.0f; //How strong a mob shoots a projectile
	public final float SHOT_LIFETIME = 0.2f; //How long a projectile lasts
	public final float SHOT_RANGE = RADIUS * 3; //The range that a mob can detect towers

	public final float SHOT_RATE = 3; //Shots per second
	public float shotTimer, maxShotTimer;
	public boolean isShooting = false;
	
	public Tower nearestTower;
	
	float moveSpeed;//The speed at which this mob moves in the UDLR directions
	
	//PATHING VARIABLES
	public Waypoint target;//Where this is aiming to go
	
	public boolean isActive;

	public Circle hitbox;
	public Gameplay level;
	public String type;
	public Sprite mobSprite;

	public Mob(float x, float y, Gameplay level, boolean spawnAtHome){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		type = "Mob";
		health = maxHealth;
		shotTimer = 0;
		maxShotTimer = 1f / SHOT_RATE;
		//mobSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(mobSprite);
		hitbox = new Circle(x, y, (int) RADIUS);
		//TODO temporary values
		velX = 0.5f;
		velY = 0.5f;
		id = ++mobcount;
		
		if(spawnAtHome==true){
			this.x = level.home.x;
			this.y = level.home.y;
			
			if(level.home.hasChildren()){
				int temp = level.home.getRandomChildIndex();
				this.target=level.home.children.get(temp);
				this.moveDirection=level.home.directions.get(temp);
				
			}
			
		}
		
		moveSpeed=0.5f;//TODO CHANGE LATER
	}

	public void render(Graphics g){
		if(mobSprite != null){
			g.drawSprite(mobSprite, x, y);
		}
		else{ //TODO Temporary shape placeholder
			g.setColor(Color.DARK_GRAY);
			g.fillCircle(x, y, RADIUS);
		}
		//Draw health bar
		g.setColor(Color.RED);
		g.fillRect(x - healthBarMaxWidth / 2, y + healthBarYOffset, healthBarMaxWidth, healthBarHeight);
		g.setColor(Color.GREEN);
		g.fillRect(x - healthBarMaxWidth / 2, y + healthBarYOffset, healthBarMaxWidth * (health / maxHealth), healthBarHeight);
	}
	
	
	public void update(float delta){
		//TODO what happens while a mob is shooting?
		if(isShooting){
			//TODO shoot tower
		}
		//Move along the waypoints
		else{
			waypointPathingUpdate();
			directedMovement();
			
			x += velX;
			y += velY;
			
			hitbox.setX(x);
			hitbox.setY(y);
		}
		
		checkProjectileCollision();
		
		//When a mob dies
		if(health <= 0){
			//Until the mob is completely removed, move it far away
			x = -100;
			y = -100;
			hitbox.setX(x);
			hitbox.setY(y);
			level.mobs.remove(this);
			TowerController.points += POINT_VALUE;
		}
	}
	
	public void waypointPathingUpdate(){
		if(this.target!=null){
			if(Math.abs(this.x-target.x) + Math.abs(this.y-target.y) < moveSpeed){
				if(this.target.hasChildren()){
					int temp = this.target.getRandomChildIndex();
					this.moveDirection = this.target.directions.get(temp);//CRUCIAL that this comes before the next
					this.target = this.target.children.get(temp);

				}
			}
		}
	}
	
	public void directedMovement(){//movement as directed by UDLR directions
		if(this.moveDirection=='L'){//Left
			this.velX = -1 * moveSpeed;
			this.velY = 0;
			
		}
		else if(this.moveDirection=='R'){//Right
			this.velX = 1 * moveSpeed;
			this.velY = 0;
		}
		else if(this.moveDirection=='U'){//Up
			this.velX = 0;
			this.velY = -1 * moveSpeed;
		}
		else if(this.moveDirection=='D'){//Down
			this.velX = 0;
			this.velY = 1 * moveSpeed;
		}
		else if(this.moveDirection=='S'){//S is for stop. Avoid using this because you can't continue on to other waypoints if you do.
			this.velX = 0;
			this.velY = 0;
		}
	}
	
	/*
	 * Check for projectiles from any tower
	 */
	public void checkProjectileCollision(){
		for(int i = 0; i < level.projectiles.size(); i++){
			Projectile temp = level.projectiles.get(i);
			if(temp != null && temp.isActive){
				if(distanceTo(temp.hitbox) <= RADIUS * 2){ //If there is a collision
					temp.isActive = false;
					level.projectiles.remove(temp);
					health -= temp.damage;
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
