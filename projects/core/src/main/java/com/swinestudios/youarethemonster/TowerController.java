package com.swinestudios.youarethemonster;

import java.util.ArrayList;
import java.util.Random;

import org.mini2Dx.core.geom.Point;
import org.mini2Dx.core.graphics.Graphics;

public class TowerController{

	public boolean isActive;

	public ArrayList<Point> spawnPoints;
	public Random random;

	public Gameplay level;
	public String type;
	public static float points = 0;
	public static final int towerCost = 30; 

	public TowerController(Gameplay level){
		isActive = true;
		this.level = level;
		type = "TowerController";
		spawnPoints = new ArrayList<Point>();
		random = new Random();
		//hard-coded spawnpoints - change later based on map
		/*spawnPoints.add(new Point(10, 10));
		spawnPoints.add(new Point(200, 10));
		spawnPoints.add(new Point(400, 10));
		spawnPoints.add(new Point(10, 200));
		spawnPoints.add(new Point(200, 200));
		spawnPoints.add(new Point(400, 200));*/
	}

	public void render(Graphics g){
		
	}

	public void update(float delta){
		if(points >= towerCost){
			spawnTower(towerCost);
		}
	}

	public void spawnTower(int cost){
		Point pos = spawnPoints.get(random.nextInt(spawnPoints.size()));
		boolean isEmpty = true;
		//Check whether the spawnpoint is empty or not
		for(int i = 0; i < level.towers.size(); i++){
			Tower temp = level.towers.get(i);
			if(temp.x == pos.getX() && temp.y == pos.getY()){
				isEmpty = false;
				break;
			}
		}
		if(isEmpty){
			Tower t = new Tower(pos.getX(), pos.getY(), level);
			//esting code - remove later
			//t.setAutoMobsOnly(random.nextBoolean());
			level.towers.add(t);
			points -= cost;
		}
	}

}
