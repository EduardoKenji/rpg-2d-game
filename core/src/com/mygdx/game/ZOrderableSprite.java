package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ZOrderableSprite {
    float x, y;
    boolean updated;
    float colorValue;

    public ZOrderableSprite(float y) {
        this.y = y;
        updated = true;
        colorValue = 1;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void draw(SpriteBatch spriteBatch) {

    }

    public float getColorValue() {
        return colorValue;
    }

    public void setColorValue(float colorValue) {
        this.colorValue = colorValue;
    }
}

