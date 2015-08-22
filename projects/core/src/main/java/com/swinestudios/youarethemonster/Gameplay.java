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
	
	public Tower tempTower;
	public Mob mob1, mob2, mob3;

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
		
		mobs = new ArrayList<Mob>();
		towers = new ArrayList<Tower>();
		projectiles = new ArrayList<Projectile>();
		solids = new ArrayList<Block>();
		//TODO temporary code for testing
		tempTower = new Tower(320, 250, this);
		towers.add(tempTower);
		mob1 = new Mob(-64, -64, this);
		mob2 = new Mob(0, 0, this);
		mob3 = new Mob(-16, -16, this);
		mobs.add(mob1);
		mobs.add(mob2);
		mobs.add(mob3);
	}

	@Override
	public void postTransitionIn(Transition t){
		
	}

	@Override
	public void postTransitionOut(Transition t){
		
	}

	@Override
	public void preTransitionIn(Transition t){
		//TODO change values later
		camX = 0;
		camY = 0;
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.translate((float) Math.round(camX), (float) Math.round(camY)); //Camera movement
		g.drawString("This is the gameplay", 320, 240);
		renderMobs(g);
		renderTowers(g);
		renderProjectiles(g);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		updateMobs(delta);
		updateTowers(delta);
		updateProjectiles(delta);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
		}
		//TODO debug code to move camera
		if(Gdx.input.isKeyPressed(Keys.D)){
			camX++;
		}
		if(Gdx.input.isKeyPressed(Keys.A)){
			camX--;
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
