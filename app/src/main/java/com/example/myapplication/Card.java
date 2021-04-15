package com.example.myapplication;


public class Card {

    private String name;
    private String categorie;
    private Task task;
    public Card(String text1, String text2) {

        name = text1;
        categorie = text2;
    }

    public String getText1() {
        return name;
    }
    public String getText2() {
        return categorie;
    }
}