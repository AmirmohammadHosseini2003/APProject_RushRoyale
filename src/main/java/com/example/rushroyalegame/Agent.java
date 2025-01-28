package com.example.rushroyalegame;

public class Agent {
    final int id;
    final String imagePath;

    private int level;
    private int row = -1, col = -1;
    protected Boolean freeze = false;

    public Agent(int id, String imagePath, int level){
        this.id = id;
        this.imagePath = imagePath;
        this.level = level;
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

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    public void setFreeze(Boolean freeze) {
        this.freeze = freeze;
    }
}
