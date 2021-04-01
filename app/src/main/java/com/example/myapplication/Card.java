package com.example.myapplication;


public class Card {

    private String mText1;
    private String mText2;
    public Card(String text1, String text2) {

        mText1 = text1;
        mText2 = text2;
    }

    public String getText1() {
        return mText1;
    }
    public String getText2() {
        return mText2;
    }
}