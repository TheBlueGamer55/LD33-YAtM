package com.swinestudios.youarethemonster;

import java.util.ArrayList;
import java.util.Random;

import org.mini2Dx.core.geom.Point;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class TowerController{

	public boolean isActive;

	public ArrayList<Point> spawnPoints;
	public Random random;

	public Gameplay level;
	public String type;
	public static float points = 40;
	public final int towerCost = 10; //TODO adjust later

	public TowerController(Gameplay level){
		isActive = true;
		this.level = level;
		type = "TowerController";
		spawnPoints = new ArrayList<Point>();
		random = new Random();
		//TODO hard-coded spawnpoints - change later
		spawnPoints.add(new Point(10, 10));
		spawnPoints.add(new Point(200, 10));
		spawnPoints.add(new Point(400, 10));
		spawnPoints.add(new Point(10, 200));
		spawnPoints.add(new Point(200, 200));
		spawnPoints.add(new Point(400, 200));
	}

	public void render(Graphics g){
		//TODO debug code - remove later
		g.drawString(points + "", 0, 0);
	}

	public void update(float delta){
		//TODO adjust later so that building is not simply instantaneous
		if(points >= towerCost){
			spawnTower(towerCost);
		}
		//TODO debug code - remove later
		if(Gdx.input.isKeyJustPressed(Keys.P)){
			TowerController.points++;
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
			level.towers.add(new Tower(pos.getX(), pos.getY(), level));
			points -= cost;
		}
	}

}
