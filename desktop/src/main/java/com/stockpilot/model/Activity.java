package com.stockpilot.model;


public class Activity {


    private String message;

    private String type;

    private String time;




    public Activity(
            String message,
            String type,
            String time
    ){

        this.message = message;
        this.type = type;
        this.time = time;

    }




    public String getMessage(){

        return message;

    }




    public String getType(){

        return type;

    }




    public String getTime(){

        return time;

    }




    @Override
    public String toString(){

        return message + "\n" + time;

    }


}