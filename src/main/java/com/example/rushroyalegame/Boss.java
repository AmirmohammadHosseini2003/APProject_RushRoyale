package com.example.rushroyalegame;

public class Boss extends Enemy{
    private int health;
    private int speed;
    public Boss(int id, String imagePath){
        super(id, imagePath);
        health = 2000;
        speed = 4;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }
}
