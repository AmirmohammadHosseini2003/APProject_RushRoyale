package com.example.rushroyalegame;

public class Soldier extends Enemy{
    private int health;
    private int speed;
    public Soldier(int id, String imagePath){
        super(id, imagePath);
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
