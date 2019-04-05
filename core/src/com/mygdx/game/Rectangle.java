package com.mygdx.game;

public class Rectangle {
    float x, y, width, height;
    float centerX, centerY;

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        centerX = this.x+(this.width/2);
        centerY = this.y+(this.height/2);
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        centerX = this.x+(this.width/2);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        centerY = this.y+(this.height/2);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
