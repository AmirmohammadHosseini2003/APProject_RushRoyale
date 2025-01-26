package com.example.rushroyalegame;

public class ShielderSoldier extends Soldier{
    public ShielderSoldier(int id, String imagePath){
        super(id, imagePath);
        super.setHealth(100);
        super.setSpeed(2);
    }
}
