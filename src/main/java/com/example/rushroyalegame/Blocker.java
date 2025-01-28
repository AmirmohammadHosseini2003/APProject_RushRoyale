package com.example.rushroyalegame;

public class Blocker extends Agent{
    final int elixir;
    public int destroyEnemyNum;
    public Blocker(int id, String imagePath, int level,  int elixir) {
        super(id, imagePath, level);
        this.elixir = elixir;
        this.destroyEnemyNum = 2;
    }
}
