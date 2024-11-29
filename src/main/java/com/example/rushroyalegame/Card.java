package com.example.rushroyalegame;

public class Card {
    private int id;
    private String imagePath;
    public Card(int id, String imagePath){
        this.id = id;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getId() {
        return id;
    }
}
