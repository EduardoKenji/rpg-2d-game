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
    // Bullet damage
    int damage;
    int minDamageOffset, maxDamageOffset;
    int range;
    // Light texture
	Texture lightTexture;
	ArrayList<Light> lightList;
	// Particle effect
	String attackParticleEffectPath, attackParticleEffectFolderPath;

    public BulletController(float angle, int shootingPattern, int damage) {
        this.angle = angle;
        this.shootingPattern = shootingPattern;
        this.damage = damage;
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
                Bullet bulletShadow = new Bullet(bulletShadowSprite,bulletShadowHitbox, projectileSpeed, projectileLifeTime,
                        (damage+minDamageOffset)+(int)(Math.random()*range),angle+i, entityId, null, bulletHitbox.getY()-2);
				Bullet bullet = new Bullet(bulletSprite, bulletHitbox, projectileSpeed, projectileLifeTime, (damage+minDamageOffset)+(int)(Math.random()*range),
                        angle+i, entityId, bulletShadow, bulletHitbox.getY());
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
			Bullet bulletShadow = new Bullet(bulletShadowSprite,bulletShadowHitbox, projectileSpeed, projectileLifeTime, (damage+minDamageOffset)+(int)(Math.random()*range),
                    angle, entityId, null, bulletHitbox.getY()-2);
            Bullet bullet = new Bullet(bulletSprite, bulletHitbox, projectileSpeed, projectileLifeTime, (damage+minDamageOffset)+(int)(Math.random()*range),
                    angle, entityId, bulletShadow, bulletHitbox.getY());
			if(lightTexture != null) {
				bullet.createLight(lightTexture, lightList, 25, 0, 0);
			}
			if(attackParticleEffectPath != null && attackParticleEffectFolderPath != null) {
				bullet.createParticleEffect(attackParticleEffectPath, attackParticleEffectFolderPath, 13f);
			}
            projectileList.add(bullet);
            projectileList.add(bulletShadow);
            zOrderableSpriteList.add(bullet);
            zOrderableSpriteList.add(bulletShadow);
        }
    }

	public void setAttackParticleEffectPath(String attackParticleEffectPath) {
		this.attackParticleEffectPath = attackParticleEffectPath;
	}

	public void setDamageRange(int minDamageOffset, int maxDamageOffset) {
        this.minDamageOffset = minDamageOffset;
        this.maxDamageOffset = maxDamageOffset;
        range = (maxDamageOffset+damage)-(minDamageOffset+damage)+1;
    }

	public String getAttackParticleEffectPath() {
		return attackParticleEffectPath;
	}

	public void setAttackParticleEffect(String attackParticleEffectPath) {
		this.attackParticleEffectPath = attackParticleEffectPath;
	}

	public String getAttackParticleEffectFolderPath() {
		return attackParticleEffectFolderPath;
	}

	public void setAttackParticleEffectFolderPath(String attackParticleEffectFolderPath) {
		this.attackParticleEffectFolderPath = attackParticleEffectFolderPath;
	}

	public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getMinDamageOffset() {
        return minDamageOffset;
    }

    public void setMinDamageOffset(int minDamageOffset) {
        this.minDamageOffset = minDamageOffset;
    }

    public int getMaxDamageOffset() {
        return maxDamageOffset;
    }

	public Sprite getBulletSprite() {
		return bulletSprite;
	}

	public void setBulletSprite(Sprite bulletSprite) {
		this.bulletSprite = bulletSprite;
	}

	public Sprite getBulletShadowSprite() {
		return bulletShadowSprite;
	}

	public void setBulletShadowSprite(Sprite bulletShadowSprite) {
		this.bulletShadowSprite = bulletShadowSprite;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public Texture getLightTexture() {
		return lightTexture;
	}

	public void setLightTexture(Texture lightTexture, ArrayList<Light> lightList) {
		this.lightTexture = lightTexture;
		this.lightList = lightList;
	}

	public void setMaxDamageOffset(int maxDamageOffset) {
        this.maxDamageOffset = maxDamageOffset;
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
