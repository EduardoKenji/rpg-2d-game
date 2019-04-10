package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet extends ZOrderableSprite {
    Sprite bulletSprite;
    float speed, range, angle;
    float centerX, centerY;
    float timer;

    // Boolean to check if projectile should be removed in the next game loop with the project list iterator
    boolean dead;

    // Entity id represents the responsible for shooting the projectile
    public Bullet(Sprite bulletSprite, Rectangle rectangle, float speed, float range, float angle, int entity_id) {
        super(rectangle.getY());
        this.bulletSprite = bulletSprite;
        // Bullet properties
        this.speed = speed;
        this.range = range;
        this.angle = angle;
        // Set sprite position to origin point (center of entity hitbox)
        centerX = rectangle.getX() + (float)(10 * Math.cos(Math.toRadians(angle)));
        centerY = rectangle.getY() + (float)(10 * Math.sin(Math.toRadians(angle)));
        // Rotate sprite to conform angle
        this.bulletSprite.setRotation(angle);
        bulletSprite.setSize(rectangle.getWidth(), rectangle.getHeight());
        bulletSprite.setCenter(centerX, centerY);
        bulletSprite.setOriginCenter();
        // Check if bullet is dead
        dead = false;
    }

    void update() {
        centerX += (float)(speed * Math.cos(Math.toRadians(angle)));
        centerY += (float)(speed * Math.sin(Math.toRadians(angle)));
        setX(centerX);
        setY(centerY);
        bulletSprite.setCenter(centerX, centerY);
        bulletSprite.setOriginCenter();
        timer += Gdx.graphics.getDeltaTime();
        if(timer >= range) {
           dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void draw(SpriteBatch spriteBatch) {
        bulletSprite.draw(spriteBatch);
    }

    public Sprite getBulletSprite() {
        return bulletSprite;
    }

    public void setBulletSprite(Sprite bulletSprite) {
        this.bulletSprite = bulletSprite;
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
