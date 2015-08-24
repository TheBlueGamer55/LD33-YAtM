package com.swinestudios.youarethemonster;

import java.util.ArrayList;
import java.util.List;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;
import org.mini2Dx.tiled.TiledMap;
import org.mini2Dx.tiled.TiledObject;
import org.mini2Dx.tiled.exception.TiledException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;

public class Gameplay implements GameScreen{

	public static int ID = 2;

	public ArrayList<Mob> mobs;
	public ArrayList<Tower> towers;
	public ArrayList<Projectile> projectiles;
	public ArrayList<MobProjectile> mobProjectiles;
	public ArrayList<Block> solids;

	public ArrayList<Waypoint> waypoints;

	public final int INITIAL_POINTS = 40;

	public ControllableMob player;

	public boolean gameOver = false;
	public boolean gameWin = false;
	public boolean paused = false;

	//TODO adjust later
	//The "supplies" that the player has each wave
	public final int initialMobCount = 20; //How many mobs can spawn per wave
	public int mobCount = initialMobCount;

	public int waveNum; //If a new wave starts, the player's supplies (points) reset, allowing for more mobs to spawn
	public boolean startingNewWave = false;
	public float waveTimer, maxWaveTimer = 3f; //Brief cooldown between waves

	public Rectangle cursor; //For use with UI
	public Rectangle mobSpawnButton;
	public Rectangle newWaveButton;

	//Make sure that there are no accidental double-clicks
	public boolean hasClicked = false;
	public float mouseCooldown, maxMouseCooldown = 0.1f;

	public Mob mob1, mob2, mob3;

	public TowerController towerController;
	private TiledMap map;

	public float camX, camY;

	public Waypoint home;//where the creeps spawn

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		try{
			map = new TiledMap(Gdx.files.internal("testmap.tmx"));
		} catch (TiledException e) {
			e.printStackTrace();
		}
		cursor = new Rectangle(Gdx.input.getX(), Gdx.input.getY(), 1, 1);
		//TODO change width and height based on button sprite
		mobSpawnButton = new Rectangle(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60, 50, 32);
		newWaveButton = new Rectangle(60, Gdx.graphics.getHeight() - 60, 50, 32);
	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){
		gameOver = false;
		paused = false;
		gameWin = false;
		startingNewWave = false;
	}

	@Override
	public void preTransitionIn(Transition t){
		gameOver = false;
		paused = false;
		gameWin = false;
		startingNewWave = false;

		waveNum = 1;
		mobCount = initialMobCount;

		mouseCooldown = 0;

		towerController = new TowerController(this);
		TowerController.points = INITIAL_POINTS;
		mobs = new ArrayList<Mob>();
		towers = new ArrayList<Tower>();
		projectiles = new ArrayList<Projectile>();
		mobProjectiles = new ArrayList<MobProjectile>();
		solids = new ArrayList<Block>();
		waypoints = new  ArrayList<Waypoint>();

		if(map != null){
			generateSolids(map);
			generateWaypoints(map);
		}

		//TODO temporary code for testing
		//tempTower = new Tower(320, 250, this);
		//towers.add(tempTower);

		//Waypoint waypoint1 = new Waypoint(50, 0, "HOME", this);
		//Waypoint waypoint2 = new Waypoint(50, 200, "", this);
		//Waypoint waypoint3 = new Waypoint(300, 200, "", this);
		//waypoints.add(waypoint1);
		//waypoints.add(waypoint2);
		//waypoints.add(waypoint3);
		//startWaypointPairing();

		mob1 = new Mob(-64, -64, this, true);
		mob2 = new Mob(0, 0, this, true);
		mob3 = new Mob(-16, -16, this, true);
		mobs.add(mob1);
		mobs.add(mob2);
		mobs.add(mob3);

		player = new ControllableMob(320, 240, this);
		camX = player.x - Gdx.graphics.getWidth() / 2;
		camY = player.y - Gdx.graphics.getHeight() / 2;

		//Input handling
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.translate((float) Math.round(camX), (float) Math.round(camY)); //Camera movement
		map.draw(g, 0, 0);
		renderMobs(g);
		player.render(g);
		renderTowers(g);
		renderProjectiles(g);
		renderMobProjectiles(g);
		//TODO temporary code - remove later
		towerController.render(g);
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).render(g);
		}
		for(int i = 0; i < waypoints.size(); i++){
			g.setColor(Color.RED);
			g.fillCircle(waypoints.get(i).x, waypoints.get(i).y, 2);
		}

		//TODO - UI graphics using sprites
		g.drawString("Wave Number: " + waveNum, camX + 4,  camY + 4);

		g.drawRect(camX + mobSpawnButton.x, camY + mobSpawnButton.y, mobSpawnButton.width, mobSpawnButton.height);
		g.drawString("Spawn", camX + mobSpawnButton.x, camY + mobSpawnButton.y);
		g.drawString("mob", camX + mobSpawnButton.x, camY + mobSpawnButton.y + 14);

		g.drawRect(camX + newWaveButton.x, camY + newWaveButton.y, newWaveButton.width, newWaveButton.height);
		g.drawString("New", camX + newWaveButton.x, camY + newWaveButton.y);
		g.drawString("Wave", camX + newWaveButton.x, camY + newWaveButton.y + 14);
		g.drawString("Mob amount left: " + mobCount, camX + newWaveButton.x, camY + newWaveButton.y - 14);

		//TODO adjust UI for each menu
		if(gameOver){
			g.setColor(Color.WHITE);
			g.drawString("You died! Press Escape to go back to the main menu", camX + 160, camY + 240);
		}
		if(paused){
			g.setColor(Color.WHITE);
			g.drawString("Are you sure you want to quit? Y or N", camX + 220, camY + 240);
		}
		if(gameWin){
			g.setColor(Color.WHITE);
			g.drawString("Congratulations! It took you " + waveNum + " waves to destroy the land!", camX + 200, camY + 240);
		}
		if(startingNewWave){
			g.setColor(Color.WHITE);
			g.drawString("Starting new wave in " + (maxWaveTimer - waveTimer), camX + 220, camY + 240);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		//The main game's logic
		if(!paused && !gameOver && !gameWin){
			cursor.setX(Gdx.input.getX());
			cursor.setY(Gdx.input.getY());

			camX = player.x - Gdx.graphics.getWidth() / 2;
			camY = player.y - Gdx.graphics.getHeight() / 2;

			updateMobs(delta);
			player.update(delta);
			updateTowers(delta);
			updateProjectiles(delta);
			updateMobProjectiles(delta);
			towerController.update(delta);

			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
				paused = true;
			}

			//UI controls
			if(Gdx.input.justTouched()){
				if(!hasClicked){ //Prevent double-clicks
					hasClicked = true;
					//Spawn a mob
					if(cursor.overlaps(mobSpawnButton)){
						if(mobCount > 0){
							mobs.add(new Mob(home.x, home.y, this, true));
							//Costs points to spawn a mob
							mobCount--;
						}
					}
					if(cursor.overlaps(newWaveButton)){
						startingNewWave = true;
					}
				}
			}
			if(hasClicked){ //Prevent double-clicks
				mouseCooldown += delta;
				if(mouseCooldown > maxMouseCooldown){
					mouseCooldown = 0;
					hasClicked = false;
				}
			}
			
			if(startingNewWave){
				waveTimer += delta;
				if(waveTimer > maxWaveTimer){
					waveTimer = 0;
					startingNewWave = false;
					startNewWave();
				}
			}
		}
		//When the main game logic is not running
		else{
			if(gameOver){
				if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
			}
			else if(paused){
				if(Gdx.input.isKeyJustPressed(Keys.Y)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
				if(Gdx.input.isKeyJustPressed(Keys.N)){
					paused = false;
				}
			}
			else if(gameWin){
				if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}
	}

	/*
	 * TODO Starts a new wave of monsters (player's supplies reset)
	 */
	public void startNewWave(){
		waveNum++;
		mobCount = initialMobCount;
	}

	public void renderTowers(Graphics g){
		for(int i = 0; i < towers.size(); i++){
			towers.get(i).render(g);
		}
	}

	public void updateTowers(float delta){
		for(int i = 0; i < towers.size(); i++){
			towers.get(i).update(delta);
		}
	}

	public void renderMobs(Graphics g){
		for(int i = 0; i < mobs.size(); i++){
			mobs.get(i).render(g);
		}
	}

	public void updateMobs(float delta){
		for(int i = 0; i < mobs.size(); i++){
			mobs.get(i).update(delta);
		}
	}

	public void renderProjectiles(Graphics g){
		for(int i = 0; i < projectiles.size(); i++){
			projectiles.get(i).render(g);
		}
	}

	public void updateProjectiles(float delta){
		for(int i = 0; i < projectiles.size(); i++){
			projectiles.get(i).update(delta);
		}
	}

	public void renderMobProjectiles(Graphics g){
		for(int i = 0; i < mobProjectiles.size(); i++){
			mobProjectiles.get(i).render(g);
		}
	}

	public void updateMobProjectiles(float delta){
		for(int i = 0; i < mobProjectiles.size(); i++){
			mobProjectiles.get(i).update(delta);
		}
	}

	/* 
	 * Generates all solids based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateSolids(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Solids").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}

	public void generateWaypoints(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Waypoints").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);

				Waypoint w = new Waypoint(temp.getX() + temp.getWidth()/2, temp.getY() + temp.getHeight()/2, temp.getName(), this);
				if(waypoints != null){
					waypoints.add(w);
				}
				else{
					System.out.println("ArrayList Waypoints does not exist."); //error message
				}
			}
		}

		startWaypointPairing();

	}

	public void startWaypointPairing(){

		for(int i = 0; i < waypoints.size(); i++){

			if(waypoints.get(i).isHome){
				waypoints.get(i).findChildren(waypoints, 'x');//The x call means search all directions
			}
		}

	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onResize(int arg0, int arg1) {
	}

	@Override
	public void onResume() {
	}

}
