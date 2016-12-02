package com.seemoo.pis.fancypsiapp.Helper;

/**
 * Created by TMZ_LToP on 22.11.2016.
 */

public enum AppCategory {
    GAMES(0,"Games"),
    COMMUNICATION(1,"Communication"),
    TOOLS(2,"Tools"),
    SHOPPING(3,"Shopping"),
    SOCIAL(4,"Social"),
    NEWSEDUCATION(5,"Education"),
    FINANCE(6,"Finance"),
    ENTERTAINMENT(7,"Entertainment"),
    UNDEFINED(8,"Other");
    private int size = 7;
    private int num = 6;
    private String name;
    private AppCategory(int i,String name){
        num = i;
        this.name = name;
    }

    public static AppCategory getCategory(String s){
        if(s.contains("GAME_")){return GAMES;}
        if(s.contains("COMMUNICATION")){return COMMUNICATION;}
        if(s.contains("TOOLS")||s.contains("PHOTOGRAPHY")||s.contains("NAVIGATION")||s.contains("PRODUCTIVITY")||s.contains("WEATHER")||s.contains("VIDEO")||s.contains("MUSIC")){return TOOLS;}
        if(s.contains("NEWS_")||s.contains("EDUCATION")){return NEWSEDUCATION;}
        if(s.contains("SHOPPING")){return SHOPPING;}
        if(s.contains("SOCIAL")||s.contains("FOOD_")|| s.contains("LIFE")||s.contains("SPORTS")||s.contains("TRAVEL")){return SOCIAL;}
        if(s.contains("BUSINESS")||s.contains("FINANCE")){return FINANCE;}
        if(s.contains("ENTERTAINMENT")){return ENTERTAINMENT;}
        return UNDEFINED;
    }

    public static AppCategory[] getCategorys(){
        return new AppCategory[]{GAMES,COMMUNICATION,TOOLS,SHOPPING,SOCIAL,NEWSEDUCATION,FINANCE,ENTERTAINMENT,UNDEFINED};
    }

    public int getPos(){
        return this.num;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

