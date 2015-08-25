package com.swinestudios.youarethemonster;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Animation;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class ControllableMob implements InputProcessor{ 

	public float x, y;
	public float velX, velY;
	public float accelX, accelY;

	public final float frictionX = 0.6f;
	public final float frictionY = 0.6f;

	public final float moveSpeedX = 2.0f;
	public final float moveSpeedY = 2.0f;

	public final float maxSpeedX = 2.0f;
	public final float maxSpeedY = 2.0f;

	public boolean isActive;
	public boolean isAttacking;
	public boolean isDraining;

	//TODO adjust constants later
	public float health, maxHealth = 200;

	public final float healthBarMaxWidth = 36;
	public final float healthBarHeight = 6;
	public final float healthBarYOffset = -20;

	public boolean facingRight, facingLeft;

	public Sprite left1, left2, right1, right2;
	public Animation<Sprite> mobLeft, mobRight, mobCurrent;
	public Sprite tentacles1, tentacles2, tentacles3;
	public Animation<Sprite> tentacles;
	public float animationSpeed = 0.1f; //How many seconds a frame lasts

	public final int RADIUS = 16; 
	public final int DRAIN_RANGE = 128;

	public final float SHOT_MAGNITUDE = 4.0f; //How strong a mob shoots a projectile
	public final float SHOT_LIFETIME = 0.2f; //How long a projectile lasts

	public final float SHOT_RATE = 2; //Shots per second
	public float shotTimer, maxShotTimer;

	public boolean attackSoundPlaying = false;
	public float attackSoundTimer, maxAttackSoundTimer = 6f;

	//public final float damage = 25f; //How much damage this mob does to a tower
	//public float damageTimer;
	//public float maxDamageTimer = 1f;

	public final float drainAmount = 2f; //How much health is drained from other mobs
	public float drainTimer;
	public float maxDrainTimer = 0.1f;

	public Rectangle hitbox;
	public Circle hitbox2; //Used by towers to detect controllable mobs
	public Gameplay level;
	public String type;

	public static Sound hurt = Gdx.audio.newSound(Gdx.files.internal("Hit_Hurt13.wav"));
	public static Sound drainSound = Gdx.audio.newSound(Gdx.files.internal("DrainHealth4.wav"));
	public static Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("tentacleAttack.wav"));

	public boolean drainSoundPlaying = false;

	//Controls/key bindings
	public final int LEFT = Keys.A;
	public final int RIGHT = Keys.D;
	public final int UP = Keys.W;
	public final int DOWN = Keys.S;

	public ControllableMob(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		velX = 0;
		velY = 0;
		accelX = 0;
		accelY = 0;
		isActive = false;
		isAttacking = false;
		isDraining = false;
		this.level = level;
		health = maxHealth;
		shotTimer = 0;
		drainTimer = 0;
		attackSoundTimer = 0;
		maxShotTimer = 1f / SHOT_RATE;
		type = "ControllableMob";
		
		facingRight = true;
		facingLeft = false;
		
		right1 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceR1.png")));
		right2 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceR2.png")));
		
		left1 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceL1.png")));
		left2 = new Sprite(new Texture(Gdx.files.internal("mobFrames/babyBlackLicoriceL2.png")));
		
		tentacles1 = new Sprite(new Texture(Gdx.files.internal("tentacles1.png")));
		tentacles2 = new Sprite(new Texture(Gdx.files.internal("tentacles2.png")));
		tentacles3 = new Sprite(new Texture(Gdx.files.internal("tentacles3.png")));
		
		adjustSprite(right1, right2, left1, left2, tentacles1, tentacles2, tentacles3);
		
		mobLeft = new Animation<Sprite>(); //left animation
		mobRight = new Animation<Sprite>(); //right animation
		tentacles = new Animation<Sprite>();
		
		mobLeft.addFrame(left1, animationSpeed);
		mobLeft.addFrame(left2, animationSpeed);
		mobLeft.setLooping(true);
		mobLeft.flip(false, true);
		
		mobRight.addFrame(right1, animationSpeed);
		mobRight.addFrame(right2, animationSpeed);
		mobRight.setLooping(true);
		mobRight.flip(false, true);
		
		tentacles.addFrame(tentacles1, animationSpeed);
		tentacles.addFrame(tentacles2, animationSpeed);
		tentacles.addFrame(tentacles3, animationSpeed);
		tentacles.setLooping(true);
		tentacles.flip(false, true);
		
		Gameplay.setFrameSizes(mobLeft, left1.getWidth() * 4, left1.getHeight() * 4);
		Gameplay.setFrameSizes(mobRight, right1.getWidth() * 4, right1.getHeight() * 4);
		Gameplay.setFrameSizes(tentacles, tentacles1.getWidth() * 2, tentacles1.getHeight() * 2);
		
		mobCurrent = mobRight;
		
		hitbox = new Rectangle(x, y, left1.getWidth(), left1.getHeight() - 4); //adjust size later based on sprite
		hitbox2 = new Circle(x, y, RADIUS);
	}

	public void render(Graphics g){
		//Debug - remove later
		//g.drawRect(x, y, hitbox.width, hitbox.height);
		if(isAttacking){
			tentacles.draw(g, x - tentacles1.getWidth() / 2 + 8, y - tentacles1.getHeight() / 2 + 10);
		}
		if(mobCurrent != null){
			mobCurrent.draw(g, x, y);
		}
		/*else{
			if(velX != 0 || velY != 0){ 
				//if moving, draw animated sprites
			}
			else{ 
				//draw still images if not moving with appropriate direction
			}
		}*/
		
		//Draw health bar
		g.setColor(Color.RED);
		g.fillRect(hitbox2.getX() - healthBarMaxWidth / 2, hitbox2.getY() + healthBarYOffset, healthBarMaxWidth, healthBarHeight);
		g.setColor(Color.GREEN);
		g.fillRect(hitbox2.getX() - healthBarMaxWidth / 2, hitbox2.getY() + healthBarYOffset, healthBarMaxWidth * (health / maxHealth), healthBarHeight);
	}

	public void update(float delta){
		accelX = 0;
		accelY = 0;
		playerMovement();
		tentacles.update(delta);

		//Apply friction when not moving or when exceeding the max horizontal speed
		if(Math.abs(velX) > maxSpeedX || !Gdx.input.isKeyPressed(this.LEFT) && !Gdx.input.isKeyPressed(this.RIGHT)){
			friction(true, false);
		}
		//Apply friction when not moving or when exceeding the max vertical speed
		if(Math.abs(velY) > maxSpeedY || !Gdx.input.isKeyPressed(this.UP) && !Gdx.input.isKeyPressed(this.DOWN)){
			friction(false, true);
		}

		limitSpeed(true, true);
		if(!isAttacking && !isDraining){ //Can't move while attacking
			move();
		}

		hitbox.setX(this.x);
		hitbox.setY(this.y);
		hitbox2.setX(this.x + hitbox.getWidth() / 2);
		hitbox2.setY(this.y + hitbox.getHeight() / 2);

		checkProjectileCollision();
		
		updateSprite(delta);

		if(isAttacking){
			shotTimer += delta;
			if(shotTimer > maxShotTimer){
				attack();
				shotTimer = 0;
			}
		}
		if(isDraining){
			drainTimer += delta;
			if(drainTimer > maxDrainTimer){
				drainHealth();
				drainTimer = 0;
			}
		}

		if(attackSoundPlaying){
			attackSoundTimer += delta;
			if(attackSoundTimer > maxAttackSoundTimer){
				attackSoundTimer = 0;
				attackSoundPlaying = false;
			}
		}

		//If the controllable mob dies, game over
		if(health <= 0){
			level.gameOver = true;
		}
	}
	
	public void updateSprite(float delta){
		if(velX > 0){
			facingRight = true;
			facingLeft = false;
		}
		else if(velX < 0){
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

	/*
	 * Attack by shooting projectiles in 8 surrounding directions
	 */
	public void attack(){
		if(!attackSoundPlaying){
			attackSound.play();
		}
		for(int i = 0; i < 12; i++){
			double theta = Math.PI / 6f * i;
			float vectorX = (float) Math.cos(theta) * SHOT_MAGNITUDE;
			float vectorY = (float) Math.sin(theta) * SHOT_MAGNITUDE;
			MobProjectile p = new MobProjectile(this.hitbox2.getX(), this.hitbox2.getY(), vectorX, vectorY, SHOT_LIFETIME, level);
			level.mobProjectiles.add(p);
		}
	}

	/*
	 * Drain the health of all mobs within range
	 */
	public void drainHealth(){
		if(!drainSoundPlaying){
			drainSoundPlaying = true;
			drainSound.loop();
		}
		int amount = 0;
		for(int i = 0; i < level.mobs.size(); i++){
			//Can't drain if already at max health
			if(health == maxHealth){
				drainSound.stop();
				return;
			}
			Mob temp = level.mobs.get(i);

			if(distanceTo(temp.hitbox) <= DRAIN_RANGE){
				//If the drain amount is more than the mob's remaining health
				if(drainAmount >= temp.health){
					//Only heal for the remaining health of the mob
					amount += temp.health;
				}
				else{ //Otherwise, heal the normal drain amount
					amount += drainAmount;
				}
				temp.health -= drainAmount;
				if(!temp.isScreaming){
					temp.isScreaming = true;
					temp.playScreamSound();
				}
				//Make sure we only heal up to maxHealth
				if(health + amount > maxHealth){
					health = maxHealth;
				}
				else{
					health += amount;
				}
			}
		}
	}

	public void checkProjectileCollision(){
		for(int i = 0; i < level.projectiles.size(); i++){
			Projectile temp = level.projectiles.get(i);
			if(temp != null && temp.isActive){
				if(distanceTo(temp.hitbox) <= RADIUS * 2){ //If there is a collision
					level.projectiles.remove(temp);
					dealDamage(temp.damage);
				}
			}
		}
	}

	public void dealDamage(float amount){
		health -= amount;
		hurt.play();
	}

	public void playerMovement(){
		//Move Left
		if(Gdx.input.isKeyPressed(this.LEFT) && velX > -maxSpeedX){
			accelX = -moveSpeedX;
		}
		//Move Right
		if(Gdx.input.isKeyPressed(this.RIGHT) && velX < maxSpeedX){
			accelX = moveSpeedX;
		}
		//Move Up
		if(Gdx.input.isKeyPressed(this.UP) && velY > -maxSpeedY){
			accelY = -moveSpeedY;
		}
		//Move Down
		if(Gdx.input.isKeyPressed(this.DOWN) && velY < maxSpeedY){
			accelY = moveSpeedY;
		}
	}

	/*
	 * Checks if there is a collision if the player was at the given position.
	 */
	public boolean isColliding(Rectangle other, float x, float y){
		if(other == this.hitbox){ //Make sure solid isn't stuck on itself
			return false;
		}
		if(x < other.x + other.width && x + hitbox.width > other.x && y < other.y + other.height && y + hitbox.height > other.y){
			return true;
		}
		return false;
	}

	/*
	 * Helper method for checking whether there is a collision if the player moves at the given position
	 */
	public boolean collisionExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}

	public void move(){
		moveX();
		moveY();
	}

	/*
	 * Applies a friction force in the given axes by subtracting the respective velocity components
	 * with the given friction components.
	 */
	public void friction(boolean horizontal, boolean vertical){
		//if there is horizontal friction
		if(horizontal){
			if(velX > 0){
				velX -= frictionX; //slow down
				if(velX < 0){
					velX = 0;
				}
			}
			if(velX < 0){
				velX += frictionX; //slow down
				if(velX > 0){
					velX = 0;
				}
			}
		}
		//if there is vertical friction
		if(vertical){
			if(velY > 0){
				velY -= frictionY; //slow down
				if(velY < 0){
					velY = 0;
				}
			}
			if(velY < 0){
				velY += frictionY; //slow down
				if(velY > 0){
					velY = 0;
				}
			}
		}
	}

	/*
	 * Limits the speed of the player to a set maximum
	 */
	protected void limitSpeed(boolean horizontal, boolean vertical){
		//If horizontal speed should be limited
		if(horizontal){
			if(Math.abs(velX) > maxSpeedX){
				velX = maxSpeedX * Math.signum(velX);
			}
		}
		//If vertical speed should be limited
		if(vertical){
			if(Math.abs(velY) > maxSpeedY){
				velY = maxSpeedY * Math.signum(velY);
			}
		}
	}

	/*
	 * Returns the current tile position of the player, given the specific tile dimensions
	 */
	public float getTileX(int tileSize){
		return (int)(x / tileSize) * tileSize;
	}

	/*
	 * Returns the current tile position of the player, given the specific tile dimensions
	 */
	public float getTileY(int tileSize){
		return (int)(y / tileSize) * tileSize;
	}

	/*
	 * Returns the distance between the player and the given target
	 */
	public float distanceTo(Circle target){
		return ((float)Math.pow(Math.pow((target.getY() - this.y), 2.0) + Math.pow((target.getX() - this.x), 2.0), 0.5));
	}

	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}

	/*
	 * Move horizontally in the direction of the x-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x + velX, y)){
				while(!isColliding(solid, x + Math.signum(velX), y)){
					x += Math.signum(velX);
				}
				velX = 0;
			}
		}
		x += velX;
		velX += accelX;
	}

	/*
	 * Move vertically in the direction of the y-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x, y + velY)){
				while(!isColliding(solid, x, y + Math.signum(velY))){
					y += Math.signum(velY);
				}
				velY = 0;
			}
		}
		y += velY;
		velY += accelY;
	}

	/*
	 * Sets up any images that the player may have. Necessary because images are flipped and have the origin
	 * on the bottom-left by default.
	 */
	public void adjustSprite(Sprite... s){
		for(int i = 0; i < s.length; i++){
			s[i].setOrigin(0, 0);
			s[i].flip(false, true);
		}
	}

	//========================================Input Methods==============================================

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.SPACE){
			isAttacking = true;
		}
		if(keycode == Keys.SHIFT_LEFT){
			isDraining = true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.SPACE){
			isAttacking = false;
			attackSoundPlaying = false;
			attackSoundTimer = 0;
			attackSound.stop();
		}
		if(keycode == Keys.SHIFT_LEFT){
			isDraining = false;
			drainSound.stop();
			drainSoundPlaying = false;
		}
		if(keycode == LEFT){
			this.facingLeft = true;
			this.facingRight = false;
		}
		if(keycode == RIGHT){
			this.facingRight = true;
			this.facingLeft = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}