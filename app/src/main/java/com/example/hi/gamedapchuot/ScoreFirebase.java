package com.example.hi.gamedapchuot;

public class ScoreFirebase {
    public String name;
    public Integer score;
    public ScoreFirebase(){

    }
    public ScoreFirebase(String name, Integer score){
        this.name=name;
        this.score=score;
    }
    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public int getscore(){return score;}
    public void setscore(int score){this.score=score;}
}
