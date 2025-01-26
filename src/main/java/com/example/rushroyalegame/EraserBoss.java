package com.example.rushroyalegame;

public class EraserBoss extends Boss{
    private int eraseTime;
    public EraserBoss(int id, String imagePath){
        super(id,imagePath);
        eraseTime = 7;
    }
    public Boolean isDead(){
        return super.getHealth() <= 0;
    }

    public int getEraseTime() {
        return eraseTime;
    }
}
