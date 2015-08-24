package com.swinestudios.youarethemonster;

import java.util.Random;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Animation;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Mob{
	
	public static int mobcount = 0;
	public int id;
	
	public Random random = new Random();

	public float x, y, velX, velY;
	public float damage = 50; //How much damage a mob does if it kills itself when attacking the candy base
	public float health, maxHealth = 30; //TODO adjust later
	
	public final float healthBarMaxWidth = 20;
	public final float healthBarHeight = 4;
	public final float healthBarYOffset = -12;
	
	public boolean facingRight, facingLeft;
	
	public Sprite left1, left2, right1, right2;
	public Animation<Sprite> mobLeft, mobRight, mobCurrent;
	public float animationSpeed = 0.1f; //How many seconds a frame lasts
	
	public char moveDirection;
	public final int RADIUS = 8; //TODO should this be final?
	public final int POINT_VALUE = 10; //TODO adjust later, how many points the hero player gets if a mob dies
	
	public final float SHOT_MAGNITUDE = 4.0f; //How strong a mob shoots a projectile
	public final float SHOT_LIFETIME = 0.2f; //How long a projectile lasts
	public final float SHOT_RANGE = RADIUS * 3; //The range that a mob can detect towers

	public final float SHOT_RATE = 3; //Shots per second
	public float shotTimer, maxShotTimer;
	public boolean isShooting = false;
	
	public boolean isScreaming = false;
	public float screamTimer, maxScreamTimer = 1f;
	
	public Tower nearestTower;
	
	float moveSpeed;//The speed at which this mob moves in the UDLR directions
	
	//PATHING VARIABLES
	public Waypoint target;//Where this is aiming to go
	
	public boolean isActive;
	
	public static Sound scream1 = Gdx.audio.newSound(Gdx.files.internal("scream1.wav"));
	public static Sound scream2 = Gdx.audio.newSound(Gdx.files.internal("scream2.wav"));
	public static Sound scream3 = Gdx.audio.newSound(Gdx.files.internal("scream3.wav"));
	public static Sound scream4 = Gdx.audio.newSound(Gdx.files.internal("scream4.wav"));

	public Circle hitbox;
	public Gameplay level;
	public String type;

	public Mob(float x, float y, Gameplay level, boolean spawnAtHome){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		type = "Mob";
		health = maxHealth;
		screamTimer = 0;
		shotTimer = 0;
		maxShotTimer = 1f / SHOT_RATE;
		
		facingRight = true;
		facingLeft = false;
		
		right1 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceR1.png")));
		right2 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceR2.png")));
		
		left1 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceL1.png")));
		left2 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceL2.png")));
		
		adjustSprite(right1, right2, left1, left2);
		
		mobLeft = new Animation<Sprite>(); //left animation
		mobRight = new Animation<Sprite>(); //right animation
		
		mobLeft.addFrame(left1, animationSpeed);
		mobLeft.addFrame(left2, animationSpeed);
		mobLeft.setLooping(true);
		mobLeft.flip(false, true);
		
		mobRight.addFrame(right1, animationSpeed);
		mobRight.addFrame(right2, animationSpeed);
		mobRight.setLooping(true);
		mobRight.flip(false, true);
		
		Gameplay.setFrameSizes(mobLeft, left1.getWidth() * 2, left1.getHeight() * 2);
		Gameplay.setFrameSizes(mobRight, right1.getWidth() * 2, right1.getHeight() * 2);
		
		mobCurrent = mobRight;
		
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
		if(mobCurrent != null){
			mobCurrent.draw(g, x - left1.getWidth() / 2, y - left1.getHeight() / 2);
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
		
		updateSprite(delta);
		
		if(isScreaming){
			screamTimer += delta;
			if(screamTimer > maxScreamTimer){
				screamTimer = 0;
				isScreaming = false;
			}
		}
		
		//When a mob dies
		if(health <= 0){
			//Until the mob is completely removed, move it far away
			x = -100;
			y = -100;
			hitbox.setX(-100);
			hitbox.setY(-100);
			playScreamSound();
			
			level.mobs.remove(this);
			TowerController.points += POINT_VALUE;
		}
	}
	
	public void playScreamSound(){
		int choice = random.nextInt(4);
		if(choice == 0){
			scream1.play();
		}
		else if(choice == 1){
			scream2.play();
		}
		else if(choice == 2){
			scream3.play();
		}
		else if(choice == 3){
			scream4.play();
		}
	}
	
	public void updateSprite(float delta){
		if(velX >= 0 || moveDirection == 'R'){
			facingRight = true;
			facingLeft = false;
		}
		else if(velX < 0 || moveDirection == 'L'){
			facingRight = false;
			facingLeft = true;
		}
		//change the direction the mob is facing
		if(facingRight){
			mobCurrent = mobRight;
		}
		else{
			mobCurrent = mobLeft;
		}
		mobCurrent.update(delta);
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
