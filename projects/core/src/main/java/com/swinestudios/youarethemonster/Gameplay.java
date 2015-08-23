package com.swinestudios.youarethemonster;

import java.util.ArrayList;
import java.util.List;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;
import org.mini2Dx.tiled.TiledMap;
import org.mini2Dx.tiled.TiledObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Gameplay implements GameScreen{

	public static int ID = 2;

	public ArrayList<Mob> mobs;
	public ArrayList<Tower> towers;
	public ArrayList<Projectile> projectiles;
	public ArrayList<Block> solids;
	public ArrayList<Waypoint> waypoints;

	public ControllableMob player;

	//public Tower tempTower;
	public Mob mob1, mob2, mob3;

	public TowerController towerController;
	private TiledMap map;

	public float camX, camY;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		/*try{
			map = new TiledMap(Gdx.files.internal("someMap.tmx"));
		}catch (IOException e){
			e.printStackTrace();
		}*/
		waypoints = new  ArrayList<Waypoint>();
		
		Waypoint waypoint1 = new Waypoint(0, 0, "HOME", this);
		Waypoint waypoint2 = new Waypoint(0, 10, "", this);
		Waypoint waypoint3 = new Waypoint(5, 10, "", this);
		waypoints.add(waypoint1);
		waypoints.add(waypoint2);
		waypoints.add(waypoint3);
		
		startWaypointPairing();
		
	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){

	}

	@Override
	public void preTransitionIn(Transition t){		
		towerController = new TowerController(this);
		mobs = new ArrayList<Mob>();
		towers = new ArrayList<Tower>();
		projectiles = new ArrayList<Projectile>();
		solids = new ArrayList<Block>();

		//TODO temporary code for testing
		//tempTower = new Tower(320, 250, this);
		//towers.add(tempTower);
		Block block1 = new Block(500, 30, 20, 80, this);
		Block block2 = new Block(450, 30, 50, 20, this);
		solids.add(block1);
		solids.add(block2);
		mob1 = new Mob(-64, -64, this);
		mob2 = new Mob(0, 0, this);
		mob3 = new Mob(-16, -16, this);
		mobs.add(mob1);
		mobs.add(mob2);
		mobs.add(mob3);

		player = new ControllableMob(320, 240, this);
		camX = player.x - Gdx.graphics.getWidth() / 2;
		camY = player.y - Gdx.graphics.getHeight() / 2;
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.translate((float) Math.round(camX), (float) Math.round(camY)); //Camera movement
		g.drawString("This is the gameplay", 320, 240);
		renderMobs(g);
		player.render(g);
		renderTowers(g);
		renderProjectiles(g);
		//TODO temporary code - remove later
		towerController.render(g);
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).render(g);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		camX = player.x - Gdx.graphics.getWidth() / 2;
		camY = player.y - Gdx.graphics.getHeight() / 2;

		updateMobs(delta);
		player.update(delta);
		updateTowers(delta);
		updateProjectiles(delta);
		//TODO temporary code - remove later
		towerController.update(delta);

		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
		}
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
