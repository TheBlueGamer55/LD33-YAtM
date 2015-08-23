package com.swinestudios.youarethemonster;
import java.util.*;


public class Waypoint {

	public float x, y;//Location
	public char myFoundDirection;//What direction to this object's parent. You don't look this direction because that would cause infinite loops.

	public ArrayList<Waypoint> children = new ArrayList<Waypoint>();//All subsequent waypoints
	public ArrayList<Character> directions = new ArrayList<Character>();//What direction from this waypoint those children waypoints are

	public boolean isHome = false;//Whether or not it is ready to be paired
	public boolean paired = false;//Whether or not it has been paired.

	private Gameplay level;

	public Waypoint(float x, float y, String name, Gameplay level){

		this.x=x;
		this.y=y;
		this.level=level;

		if(name != null){
			if(name.equals("HOME")){
				level.home=this;

				this.isHome = true;
			}
		}
	}

	public void findChildren(ArrayList<Waypoint> field, char foundDirection){
		this.paired = true;

		for(int i = 0; i < field.size(); i++){

			if(foundDirection!='L'){//Left
				if(field.get(i).y==this.y && field.get(i).x < this.x){
					Block tester = new Block(this.x, this.y, 1, 1, level);
					boolean reachable = true;
					while(tester.x>=field.get(i).x){
						for(int j = 0; j < level.solids.size() && reachable; j++){
							if(tester.intersects(level.solids.get(j))){
								reachable = false;
							}
						}
						tester.x-=1;
					}
					if(reachable){
						System.out.println("Found Left Child");
						children.add(field.get(i));
						directions.add('L');
						if(field.get(i).paired==false){
							field.get(i).findChildren(field, 'R');
						}
					}
				}

			}
			if(foundDirection!='R'){//Right
				if(field.get(i).y==this.y && field.get(i).x > this.x){
					Block tester = new Block(this.x, this.y, 1, 1, level);
					boolean reachable = true;
					while(tester.x<=field.get(i).x){
						for(int j = 0; j < level.solids.size() && reachable; j++){
							if(tester.intersects(level.solids.get(j))){
								reachable = false;
							}
						}
						tester.x+=1;
					}
					if(reachable){
						System.out.println("Found Right Child");
						children.add(field.get(i));
						directions.add('R');
						if(field.get(i).paired==false){
							field.get(i).findChildren(field, 'L');
						}
					}
				}


			}
			if(foundDirection!='U'){//If you weren't found from above, search up
				if(field.get(i).x==this.x && field.get(i).y < this.y){
					Block tester = new Block(this.x, this.y, 1, 1, level);
					boolean reachable = true;
					while(tester.y>=field.get(i).y){
						for(int j = 0; j < level.solids.size() && reachable; j++){
							if(tester.intersects(level.solids.get(j))){
								reachable = false;
							}
						}
						tester.y-=1;
					}
					if(reachable){
						System.out.println("Found Up Child");
						children.add(field.get(i));
						directions.add('U');
						if(field.get(i).paired==false){
							field.get(i).findChildren(field, 'D');
						}
					}
				}


			}
			if(foundDirection!='D'){//If you weren't found from below, search down
				if(field.get(i).x==this.x && field.get(i).y > this.y){
					Block tester = new Block(this.x, this.y, 1, 1, level);
					boolean reachable = true;
					while(tester.y<=field.get(i).y){


						for(int j = 0; j < level.solids.size() && reachable; j++){
							if(tester.intersects(level.solids.get(j))){
								reachable = false;
							}
						}
						tester.y+=1;
					}
					if(reachable){
						System.out.println("Found Down Child");
						children.add(field.get(i));
						directions.add('D');
						if(field.get(i).paired==false){
							field.get(i).findChildren(field, 'U');
						}
					}
				}


			}
		}

	}

	public int getRandomChildIndex(){
		return((int)((double)children.size()*Math.random()));
	}

	public boolean hasChildren(){
		if(children.size()>0){
			return true;

		}
		else{
			return false;
		}
	}

}
