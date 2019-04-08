package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {
    Sprite bulletSprite;
    Rectangle rectangle;
    float speed, range, angle;
    float originX, originY;

    public Bullet(Sprite bulletSprite, Rectangle rectangle, float speed, float range, float angle) {
        this.bulletSprite = bulletSprite;
        this.rectangle = rectangle;
        // Bullet properties
        this.speed = speed;
        this.range = range;
        this.angle = angle;
        // Set sprite position to origin point (center of entity hitbox)
        originX = rectangle.getX();
        originY = rectangle.getY();

        // Rotate sprite to conform angle
        this.bulletSprite.rotate(angle);
        bulletSprite.setSize(rectangle.getWidth(), rectangle.getHeight());
        bulletSprite.setCenter(originX, originY);
        bulletSprite.setOriginCenter();
        this.rectangle.setX(bulletSprite.getX()+bulletSprite.getWidth()/2);
        this.rectangle.setY(bulletSprite.getY()+bulletSprite.getHeight()/2);
    }

    void update() {

        rectangle.setX(rectangle.getX() + (float)(speed * Math.cos(Math.toRadians(angle))));
        rectangle.setY(rectangle.getY() + (float)(speed * Math.sin(Math.toRadians(angle))));
        bulletSprite.setCenter(rectangle.getX(), rectangle.getY());
        bulletSprite.setOriginCenter();

    }

    void draw(SpriteBatch spriteBatch) {
        bulletSprite.draw(spriteBatch);
    }

    public Sprite getBulletSprite() {
        return bulletSprite;
    }

    public void setBulletSprite(Sprite bulletSprite) {
        this.bulletSprite = bulletSprite;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
