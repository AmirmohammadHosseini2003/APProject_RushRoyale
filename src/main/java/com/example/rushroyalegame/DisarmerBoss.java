package com.example.rushroyalegame;

public class DisarmerBoss extends Boss {

    public DisarmerBoss(int id, String imagePath){
        super(id, imagePath);
    }
    public Boolean isDead(){
        return super.getHealth() <= 0;
    }
}
