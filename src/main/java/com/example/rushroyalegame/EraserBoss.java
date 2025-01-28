package com.example.rushroyalegame;

public class EraserBoss extends Boss{
    private int eraseTime;
    public EraserBoss(int id, String imagePath){
        super(id,imagePath);
        eraseTime = 7;
    }

    public int getEraseTime() {
        return eraseTime;
    }
}
