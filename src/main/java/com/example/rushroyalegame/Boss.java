package com.example.rushroyalegame;

public class Boss extends Enemy{
    private float health;
    private float speed;
    public Boss(int id, String imagePath){
        super(id, imagePath);
        this.health = 2000;
        this.speed = 4;
    }

}
