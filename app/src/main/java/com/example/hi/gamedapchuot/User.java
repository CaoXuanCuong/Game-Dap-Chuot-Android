package com.example.hi.gamedapchuot;

public class User {
    private String name;
    private int score;

    public User(){
    }

    public User(String name,int score){
        this.name=name;
        this.score=score;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
