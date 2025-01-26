package com.example.rushroyalegame;

public class RunnerSoldier extends Soldier{
    public RunnerSoldier(int id, String imagePath){
        super(id, imagePath);
        super.setHealth(50);
        super.setSpeed(1);
    }
    public Boolean isDead(){
        return super.getHealth() <= 0;
    }
}
