package com.example.rushroyalegame;

public class Enemy {
    final int id;
    final String imagePath;
    private int row = -1, col = -1;
    private float health;
    private float speed;

    public Enemy(int id, String imagePath){
        this.id = id;
        this.imagePath = imagePath;
    }
    public Enemy(int id, String imagePath, float health, float speed){
        this.id = id;
        this.imagePath = imagePath;
        this.health = health;
        this.speed = speed;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getId() {
        return id;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
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
