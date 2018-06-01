package com.vakuor.knightsandgoldmines.utilities;

public class Timers {

    private double time;
    private boolean on;
    private double endtime;

    public Timers(){
        on = true;
        time = 0;
        endtime = -1;
    }

    public Timers(double time,boolean on){
        this.on = on;
        this.time = time;
    }

    public Timers(double time,double endtime,boolean on){
        this.on = on;
        this.time = time;
        this.endtime = endtime;
    }

    public boolean update(float deltaTime){
        if(on){time+=deltaTime;
        if(time>=endtime) {time=0;return true;}
        else return false;}
        else return false;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        if(time>0)
        this.time = time;
        else {
            System.out.println("Timers.setTime");throw new ExceptionInInitializerError();}

    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public double getEndtime() {
        return endtime;
    }

    public void setEndtime(double endtime) {
        if(endtime>time)
        this.endtime = endtime;
        else {
            System.out.println("Timers.setEndtime");throw new ExceptionInInitializerError();}
    }
}
