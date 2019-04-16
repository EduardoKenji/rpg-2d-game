package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Bullet extends ZOrderableSprite {

	ParticleEffect projectileParticleEffect;
	float particleEffectOffsetX, particleEffectOffsetY;

	Light projectileLight;
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
    public Bullet(Sprite bulletSprite, Rectangle rectangle, float speed, float range, int damage, float angle, int entityId, Bullet shadow, float trueY) {
        // Used to calculate depth like a Z-axis, the projectile's shadow Y coordinate should only be slightly lower than the projectile's own Y coordinate
        super(trueY);
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

    float incrementX, incrementY;

    void update() {
        float fpsBoundMoveSpeed = speed * (Gdx.graphics.getDeltaTime() * 60);
        incrementX = (float)(fpsBoundMoveSpeed * Math.cos(Math.toRadians(angle)));
        incrementY = (float)(fpsBoundMoveSpeed * Math.sin(Math.toRadians(angle)));
        centerX += incrementX;
        centerY += incrementY;
        // Update light, if there is any
		if(projectileLight != null) {
			projectileLight.getRectangleBoundaries().setX(projectileLight.getRectangleBoundaries().getX()+ incrementX);
			projectileLight.getRectangleBoundaries().setY(projectileLight.getRectangleBoundaries().getY()+ incrementY);
			projectileLight.update();
			projectileLight.updateDistance();
		}
        setX(centerX);
        setY(centerY);
        bulletSprite.setCenter(centerX, centerY);
        bulletSprite.setOriginCenter();
        timer += Gdx.graphics.getDeltaTime();
        if(timer >= range) {
           dead = true;
        }
        // Update particle effect, is there is any
		updateParticleEffect();
    }

    public void updateParticleEffect() {
		if(projectileParticleEffect != null) {
			projectileParticleEffect.update(Gdx.graphics.getDeltaTime());
			if(projectileParticleEffect.isComplete()) {
				projectileParticleEffect.reset();
			}
			projectileParticleEffect.setPosition(centerX+particleEffectOffsetX, centerY+particleEffectOffsetY);
		}
	}

    public void checkForCollision(Player player, ArrayList<Enemie> enemieList, ArrayList<FloatingText> floatingTextList, ScreenTargetStatus screenTargetStatus) {
        // Player generated the bullet
        // If the projectile is not a shadow of another projectile
        if(shadow != null) {
            if(entityId == 0) {
                checkCollisionForPlayerProjectile(player, enemieList, floatingTextList, screenTargetStatus);
                // Enemie generated the bullet
            } else {
                checkCollisionForEnemieProjectile(player, floatingTextList);
            }
        }
    }

    public void checkCollisionForPlayerProjectile(Player player, ArrayList<Enemie> enemieList, ArrayList<FloatingText> floatingTextList, ScreenTargetStatus screenTargetStatus) {
        for(Enemie enemie : enemieList) {
            float x1 = centerX - (bulletSprite.getBoundingRectangle().getWidth() / 2);
            float y1 = centerY - (bulletSprite.getBoundingRectangle().getHeight() / 2);
            float x2 = centerX + (bulletSprite.getBoundingRectangle().getWidth() / 2);
            float y2 = centerY + (bulletSprite.getBoundingRectangle().getHeight() / 2);
            for(Rectangle bulletHitbox : enemie.getHitboxList()) {
                // The projectile is inside the hitbox
                if (checkIfPointIsInsideRectangle(x1, y1, bulletHitbox) || checkIfPointIsInsideRectangle(x2, y2, bulletHitbox)) {
                    // The projectile is still active
                    if(!dead) {
                    	screenTargetStatus.setTarget(enemie);
                        FloatingText damageText = new FloatingText(""+damage, enemie.getHitbox().getCenterX(), enemie.getHitbox().getY()+enemie.getHitbox().getHeight());
                        damageText.setTextColor(Color.WHITE);
                        floatingTextList.add(damageText);
                        enemie.setCurrentHp(enemie.getCurrentHp() - damage);
                        enemie.gotDamaged();
                        // If enemie hp is equal or lower than 0 and the enemie is not dead yet, the enemie is dead now
                        if(enemie.currentHp <= 0 && !enemie.isDead()) {
                            enemie.setDead(true);
                            FloatingText experienceText = new FloatingText(enemie.getExperience()+" EXP", player.getHitbox().getCenterX(), player.getHitbox().getY()+player.getHitbox().getHeight());
                            experienceText .setTextColor(Color.YELLOW);
                            floatingTextList.add(experienceText);
                        }
                        // Elimate this bullet
                        dead = true;
                        // Eliminate projectile's light
                        projectileLight.setDead(true);
                        // Eliminate projectile's shadow
                        shadow.setDead(true);
                    }
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
                FloatingText damageText = new FloatingText(""+damage, player.getHitbox().getCenterX(), player.getHitbox().getY()+player.getHitbox().getHeight()+10);
                if(player.getPlayerClass().getCurrentShield() > 0) {
					damageText.setTextColor(Color.BLUE);
					player.getPlayerClass().setCurrentShield(player.getPlayerClass().getCurrentShield() - damage);
				} else {
					damageText.setTextColor(Color.RED);
					player.getPlayerClass().setCurrentHp(player.getPlayerClass().getCurrentHp() - damage);
				}
				player.gotDamaged();
				floatingTextList.add(damageText);
                dead = true;
                shadow.setDead(true);
            }
        }
    }

	public void createLight(Texture lightTexture, ArrayList<Light> lightArrayList, float sizeOffset, float xOffset, float yOffset) {
		projectileLight = new Light(new Sprite(lightTexture),
				new Rectangle(bulletSprite.getX()-sizeOffset+xOffset, bulletSprite.getY()-sizeOffset+yOffset,
						bulletSprite.getWidth()+sizeOffset*2, bulletSprite.getHeight()+sizeOffset*2), true, range);
		lightArrayList.add(projectileLight);
	}

	public void createParticleEffect(String PEpath, String PEFolder, float particleEffectOffset) {
		projectileParticleEffect = new ParticleEffect();
		projectileParticleEffect.load(Gdx.files.internal(PEpath), Gdx.files.internal(PEFolder));
		this.particleEffectOffsetX = (float)(particleEffectOffset * Math.cos(Math.toRadians(angle)));
		this.particleEffectOffsetY = (float)(particleEffectOffset * Math.sin(Math.toRadians(angle)));
		projectileParticleEffect.setPosition(centerX+particleEffectOffsetX, centerY+particleEffectOffsetY);
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
        // Draw particle effect, is there is any
        if(projectileParticleEffect != null) {
			projectileParticleEffect.draw(spriteBatch);
		}

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
