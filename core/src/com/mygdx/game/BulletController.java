package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class BulletController {
    float angle;
    // Shooting pattern
    int shootingPattern;
    Texture projectileTexture, projectileShadowTexture;
    Sprite bulletSprite, bulletShadowSprite;
    Rectangle bulletHitbox, bulletShadowHitbox;
    // Projectile properties
    float projectileSpeed, projectileLifeTime;
    // Entity id that generated the bullet
    int entityId;

    public BulletController(float angle, int shootingPattern) {
        this.angle = angle;
        this.shootingPattern = shootingPattern;
    }

    public void createBullets(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList, boolean rotateProjectile) {
        if(shootingPattern == 0) {
            simpleOneBullet(projectileList, zOrderableSpriteList, rotateProjectile);
        } else if(shootingPattern == 1) {
            simpleThreeBullet(projectileList, zOrderableSpriteList, rotateProjectile);
        }

    }

    public void simpleThreeBullet(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList, boolean rotateProjectile) {
        if(rotateProjectile) {
            for(int i = -15; i < 16; i+=15) {
                Sprite bulletSprite = new Sprite(projectileTexture);
                Sprite bulletShadowSprite = new Sprite(projectileShadowTexture);
                Bullet bullet = new Bullet(bulletSprite, bulletHitbox, projectileSpeed, projectileLifeTime, angle+i, entityId);
                Bullet bulletShadow = new Bullet(bulletShadowSprite,bulletShadowHitbox, projectileSpeed, projectileLifeTime, angle+i, entityId);
                projectileList.add(bullet);
                projectileList.add(bulletShadow);
                zOrderableSpriteList.add(bullet);
                zOrderableSpriteList.add(bulletShadow);
            }
        }
    }

    public void simpleOneBullet(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList, boolean rotateProjectile) {
        if(rotateProjectile) {
            Sprite bulletSprite = new Sprite(projectileTexture);
            Sprite bulletShadowSprite = new Sprite(projectileShadowTexture);
            Bullet bullet = new Bullet(bulletSprite, bulletHitbox, projectileSpeed, projectileLifeTime, angle, entityId);
            Bullet bulletShadow = new Bullet(bulletShadowSprite,bulletShadowHitbox, projectileSpeed, projectileLifeTime, angle, entityId);
            projectileList.add(bullet);
            projectileList.add(bulletShadow);
            zOrderableSpriteList.add(bullet);
            zOrderableSpriteList.add(bulletShadow);

        }
    }

    public Texture getProjectileTexture() {
        return projectileTexture;
    }

    public void setProjectileTexture(Texture projectileTexture) {
        this.projectileTexture = projectileTexture;
    }

    public Texture getProjectileShadowTexture() {
        return projectileShadowTexture;
    }

    public void setProjectileShadowTexture(Texture projectileShadowTexture) {
        this.projectileShadowTexture = projectileShadowTexture;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public void setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
    }

    public float getProjectileLifeTime() {
        return projectileLifeTime;
    }

    public void setProjectileLifeTime(float projectileLifeTime) {
        this.projectileLifeTime = projectileLifeTime;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getShootingPattern() {
        return shootingPattern;
    }

    public void setShootingPattern(int shootingPattern) {
        this.shootingPattern = shootingPattern;
    }

    public Rectangle getBulletHitbox() {
        return bulletHitbox;
    }

    public void setBulletHitbox(Rectangle bulletHitbox) {
        this.bulletHitbox = bulletHitbox;
    }

    public Rectangle getBulletShadowHitbox() {
        return bulletShadowHitbox;
    }

    public void setBulletShadowHitbox(Rectangle bulletShadowHitbox) {
        this.bulletShadowHitbox = bulletShadowHitbox;
    }
}
