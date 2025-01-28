package com.example.rushroyalegame;

public class Soldier extends Enemy{
    private float health;
    private float speed;

    public Soldier(int id, String imagePath, float health, float speed){
        super(id, imagePath);
        this.health = health;
        this.speed = speed;
    }
    public Boolean isDead(){
        return health <= 0;
    }
    public float getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
