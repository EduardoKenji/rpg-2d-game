package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Bullet extends ZOrderableSprite {
    Sprite bulletSprite;
    float speed, range, angle;
    float centerX, centerY;
    float timer;
    int entityId;
    int damage;

    // Boolean to check if projectile should be removed in the next game loop with the project list iterator
    boolean dead;
    // No damage for bullet shadow sprites
    Bullet shadow;

    // Entity id represents the responsible for shooting the projectile
    public Bullet(Sprite bulletSprite, Rectangle rectangle, float speed, float range, int damage, float angle, int entityId, Bullet shadow) {
        super(rectangle.getY());
        this.bulletSprite = bulletSprite;
        // Bullet properties
        this.speed = speed;
        this.range = range;
        this.angle = angle;
        this.damage = damage;
        // Entity that generated the bullets
        this.entityId = entityId;
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
        this.shadow = shadow;
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

    public void checkForCollision(Player player, ArrayList<Enemie> enemieList, ArrayList<FloatingText> floatingTextList) {
        // Player generated the bullet
        // If the projectile is not a shadow of another projectile
        if(shadow != null) {
            if(entityId == 0) {
                checkCollisionForPlayerProjectile(enemieList, floatingTextList);
                // Enemie generated the bullet
            } else {
                checkCollisionForEnemieProjectile(player, floatingTextList);
            }
        }
    }

    public void checkCollisionForPlayerProjectile(ArrayList<Enemie> enemieList, ArrayList<FloatingText> floatingTextList) {
        for(Enemie enemie : enemieList) {
            float x1 = centerX - (bulletSprite.getBoundingRectangle().getWidth() / 2);
            float y1 = centerY - (bulletSprite.getBoundingRectangle().getHeight() / 2);
            float x2 = centerX + (bulletSprite.getBoundingRectangle().getWidth() / 2);
            float y2 = centerY + (bulletSprite.getBoundingRectangle().getHeight() / 2);
            // The projectile is inside the hitbox
            if (checkIfPointIsInsideRectangle(x1, y1, enemie.getHitbox()) || checkIfPointIsInsideRectangle(x2, y2, enemie.getHitbox())) {
                // The projectile is still active
                if(!dead) {
                    FloatingText damageText = new FloatingText("-"+damage, enemie.getHitbox().getCenterX(), enemie.getHitbox().getCenterY()+20);
                    damageText.setTextColor(Color.RED);
                    floatingTextList.add(damageText);
                    enemie.setCurrentHp(enemie.getCurrentHp() - damage);
                    enemie.gotDamaged();
                    dead = true;
                    shadow.setDead(true);
                }
            }
        }
    }

    public void checkCollisionForEnemieProjectile(Player player, ArrayList<FloatingText> floatingTextList) {
        float x1 = centerX - (bulletSprite.getBoundingRectangle().getWidth() / 2);
        float y1 = centerY - (bulletSprite.getBoundingRectangle().getHeight() / 2);
        float x2 = centerX + (bulletSprite.getBoundingRectangle().getWidth() / 2);
        float y2 = centerY + (bulletSprite.getBoundingRectangle().getHeight() / 2);
        // The projectile is inside the hitbox
        if (checkIfPointIsInsideRectangle(x1, y1, player.getHitbox()) || checkIfPointIsInsideRectangle(x2, y2, player.getHitbox())) {
            // The projectile is still active
            if(!dead) {
                FloatingText damageText = new FloatingText("-"+damage, player.getHitbox().getCenterX(), player.getHitbox().getCenterY()+20);
                damageText.setTextColor(Color.RED);
                floatingTextList.add(damageText);
                player.setCurrentHp(player.getCurrentHp() - damage);
                player.gotDamaged();
                dead = true;
                shadow.setDead(true);
            }
        }
    }

    public boolean checkIfPointIsInsideRectangle(float x, float y, Rectangle rectangle) {
        if(x > rectangle.getX() && x < rectangle.getX()+rectangle.getWidth() &&
        y > rectangle.getY() && y < rectangle.getY()+rectangle.getHeight()) {
            return true;
        }
        return false;
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
