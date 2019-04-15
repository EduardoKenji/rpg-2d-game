package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DayNightCycle {
    float minutes;
    int hours;

    final float maximumRGBValue = 0.89f;
    final float totalHours = 24;

    float timer;
    final float timeInterval = 0.5f;

    float timeFactor;
    float rgbValue;

    // Will be drawn to screen
    StaticText currentTimeText;

    public DayNightCycle(int hours, float minutes) {
        this.minutes = minutes;
        this.hours = hours;
        timeFactor = (((hours+(minutes/60))-12)/11);
        if(timeFactor < 0) timeFactor *= -1;
        if(timeFactor > 0.92f) timeFactor = 0.92f;
        this.rgbValue = (1 - timeFactor) *  maximumRGBValue;
        /*
		String twoDigitMinutes = ""+(int)this.minutes;
		if(this.minutes < 10) {
			twoDigitMinutes = "0" + twoDigitMinutes;
		}
        currentTimeText = new StaticText(this.hours+":"+twoDigitMinutes,70, Gdx.graphics.getHeight()-20);
        */
    }

    public void draw(SpriteBatch spriteBatch, BitmapFont font) {
    	if(currentTimeText != null)
        currentTimeText.draw(spriteBatch, font);
    }

    public void updateText(float x, float y) {
		if(currentTimeText != null) {
			String twoDigitHours = ""+hours;
			if(this.hours < 10) {
				twoDigitHours = "0" + twoDigitHours;
			}
			String twoDigitMinutes = ""+(int)this.minutes;
			if(this.minutes < 10) {
				twoDigitMinutes = "0" + twoDigitMinutes;
			}
			currentTimeText.setText(twoDigitHours+":"+twoDigitMinutes);
		}
		currentTimeText.setX(x);
		currentTimeText.setY(y);
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

	public StaticText getCurrentTimeText() {
		return currentTimeText;
	}

	public void setCurrentTimeText(StaticText currentTimeText) {
		this.currentTimeText = currentTimeText;
	}
}
