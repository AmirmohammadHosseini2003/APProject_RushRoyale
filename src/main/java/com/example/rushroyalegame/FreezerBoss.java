package com.example.rushroyalegame;

public class FreezerBoss extends Boss{
    private int freezeTime;
    public FreezerBoss(int id, String imagePath){
        super(id, imagePath);
        freezeTime = 5;
    }

    public int getFreezeTime() {
        return freezeTime;
    }
}
