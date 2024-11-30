package com.example.rushroyalegame;

public class Enemy {
    private int id;
    private String imagePath;
    private int row = -1, col = -1;

    public Enemy(int id, String imagePath){
        this.id = id;
        this.imagePath = imagePath;
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
}
