package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class DayNightCycle {
    float minutes;
    int hours;

    final float maximumRGBValue = 0.93f;
    final float totalHours = 24;

    float timer;
    final float timeInterval = 0.5f;

    float timeFactor;
    float rgbValue;


    public DayNightCycle(int hours, float minutes) {
        this.minutes = minutes;
        this.hours = hours;
        timeFactor = (((hours+(minutes/60))-12)/11);
        if(timeFactor < 0) timeFactor *= -1;
        if(timeFactor > 0.92f) timeFactor = 0.92f;
        this.rgbValue = (1 - timeFactor) *  maximumRGBValue;
    }

    public void updateRGBValues() {
        // Update only each 1 second (timeInterval constant)
        timer += Gdx.graphics.getDeltaTime();
        if(timer >= timeInterval) {
            timeFactor = (((hours+(minutes/60))-12)/11);
            if(timeFactor < 0) timeFactor *= -1;
            if(timeFactor > 0.92f) timeFactor = 0.92f;
            rgbValue = (1 - timeFactor) *  maximumRGBValue;
        }
    }

    public void updateTime() {
        setMinutes(getMinutes() + (Gdx.graphics.getDeltaTime()));
        if(this.hours >= 24) {
            this.hours = 0;
        }
    }

    public float getMinutes() {
        return minutes;
    }

    public void setMinutes(float minutes) {
        this.minutes = minutes;
        if(this.minutes >= 59.99f) {
            this.minutes = 0;
            hours++;
        }
    }

    public int getHours() {
        return hours;
    }

    public float getRgbValue() {
        return rgbValue;
    }
}
