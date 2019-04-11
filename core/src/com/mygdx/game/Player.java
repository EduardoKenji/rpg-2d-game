package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// The player class
public class Player extends ZOrderableSprite {
    HashMap<String, MyAnimation> playerAnimationHashMap;
    Rectangle hitbox;
    Sprite hitboxSprite;
    HashMap<String, Boolean> playerBooleanHashMap;

    // Walking on dirty particle effect
    ParticleEffect walkingParticleEffect;
	float walkingParticleEffectScale;

    // Getting hit particle effect
    ParticleEffect gettingHitParticleEffect;
	float gettingHitParticleEffectScale;
    // Boolean to check if got hit
    boolean damaged;

    // Direction
    String direction;

    // Constants
    final float xOffset = -26.5f, yOffset = -5.5f;
    final float spriteWidth = 70, spriteHeight = 70;

    // Pointer variables
    float pointerX, pointerY;
    float angle;

    // Player stats
    int playerId;
    float moveSpeed;
    float attackDelay, attackTimer;
    int baseDamage;
    float currentHp, maximumHp;
    float currentShield, maximumShield;
    HpBar hpBar;
    int experience;

    // Player projectiles properties
    int shootingPattern = 0;
    BulletController bulletController;
    float projectileSpeed, projectileLifeTime;
    float projectileWidth, projectileHeight;
    Texture projectileTexture, projectileShadowTexture;

    // If rotate projectile is false, the angle to rotate the spawned bullet/projectile will be 0
    final boolean rotateProjectile = true;

    // Animation delay to change frames
    float attackingFrameDuration;
    float walkingFrameDuration;

    public Player(Rectangle hitbox, float moveSpeed) {
        super(hitbox.getY());
        this.hitbox = hitbox;
        this.moveSpeed = moveSpeed;
        // Create player hitbox sprite and configure hitbox
        hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));
        // Hitbox sprite can be drawn for debug reasons
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        // Create player boolean hash map
        createPlayerBooleanHashMap();
        // Default direction
        direction = "playerWalkRight";
        // Default attack delay, attack timer will be incremented with elapsed time
        attackDelay = 0.3f;
        attackTimer = attackDelay;
        // Player projectile stats
        projectileSpeed = 4f;
        projectileLifeTime = 0.38f;
        projectileWidth = 20;
        projectileHeight = 2.5f;
        // Create player boolean animation map
        String attackSpriteSheetPath = "characters/knight_attack_spritesheet.png";
        String walkSpriteSheetPath = "characters/knight_walking_spritesheet.png";
        createPlayerAnimationHashMap(walkSpriteSheetPath, attackSpriteSheetPath);
        // Player id
        playerId = 0;
        // Projectile texture
        projectileTexture = new Texture("projectiles/wood_projectile.png");
        projectileShadowTexture = new Texture("projectiles/shadow_projectile.png");
        // Walking on dirt/grass particle effect
        walkingParticleEffect = new ParticleEffect();
        walkingParticleEffect.load(Gdx.files.internal("particle_effects/walking_on_dirty.pe"), Gdx.files.internal("particle_effects"));
		// Getting hit particle effect
		gettingHitParticleEffect = new ParticleEffect();
		gettingHitParticleEffect.load(Gdx.files.internal("particle_effects/player_blood.pe"), Gdx.files.internal("particle_effects"));
		damaged = false;
		walkingParticleEffectScale = 0.7f;
		gettingHitParticleEffectScale = 0.25f;
    }

    // Create player boolean animation map
    public void createPlayerAnimationHashMap(String walkSpriteSheetPath, String attackSpriteSheetPath) {
        playerAnimationHashMap = new HashMap<String, MyAnimation>();
        walkingFrameDuration = 0.3f;
        attackingFrameDuration = 0.15f;
        playerAnimationHashMap.put("playerAttackRight",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, attackingFrameDuration, 0, 1));
        playerAnimationHashMap.put("playerAttackLeft",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, attackingFrameDuration, 2, 3));
        playerAnimationHashMap.put("playerAttackUp",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, attackingFrameDuration, 6, 7));
        playerAnimationHashMap.put("playerAttackDown",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, attackingFrameDuration, 4, 5));
        playerAnimationHashMap.put("playerWalkRight",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, walkingFrameDuration, 0, 1));
        playerAnimationHashMap.put("playerWalkLeft",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, walkingFrameDuration, 2, 3));
        playerAnimationHashMap.put("playerWalkUp",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, walkingFrameDuration, 6, 7));
        playerAnimationHashMap.put("playerWalkDown",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, walkingFrameDuration, 4, 5));
    }

    // Create player boolean hash map
    public void createPlayerBooleanHashMap() {
        playerBooleanHashMap = new HashMap<String, Boolean>();
        playerBooleanHashMap.put("playerWalkLeft", false);
        playerBooleanHashMap.put("playerWalkRight", false);
        playerBooleanHashMap.put("playerWalkUp", false);
        playerBooleanHashMap.put("playerWalkDown", false);
        playerBooleanHashMap.put("isTouchedDown", false);
        playerBooleanHashMap.put("drawPEWalkInGrass", false);
    }

    public void draw(SpriteBatch spriteBatch) {

        // Draw walking on dirty particle effects
        if(isWalking() && !isWalkingAtSimultaneousOpposingDirections()) {
            walkingParticleEffect.scaleEffect(walkingParticleEffectScale);
            walkingParticleEffect.draw(spriteBatch);
        }

        // Draw attacking or walking animation
        if(playerBooleanHashMap.get("isTouchedDown") ) {
            drawPlayerAttackingAnimation(spriteBatch);
        } else if(!isWalkingAtSimultaneousOpposingDirections()){
            drawPlayerWalkingAnimation(spriteBatch);
        }

        // If the player is not walking nor attacking, render static frame for idle stance
        if((!playerBooleanHashMap.get("isTouchedDown") && isWalkingAtSimultaneousOpposingDirections()) || (!playerBooleanHashMap.get("isTouchedDown") && !isWalking())) {
            playerAnimationHashMap.get(direction).draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        }

		if(damaged) {
			gettingHitParticleEffect.scaleEffect(gettingHitParticleEffectScale);
			gettingHitParticleEffect.draw(spriteBatch);
		}

        hpBar.draw(spriteBatch);

        // Debug draw player's hitbox sprite (a red empty rectangle)
        //hitboxSprite.draw(spriteBatch);
    }

    public void drawPlayerAttackingAnimation(SpriteBatch spriteBatch) {
        if(angle >= 135 && angle < 225) {
            playerAnimationHashMap.get("playerAttackLeft").draw(spriteBatch, hitbox.getX()+xOffset,
                    hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        } else if(angle >= 315 || angle < 45) {
            playerAnimationHashMap.get("playerAttackRight").draw(spriteBatch, hitbox.getX()+xOffset,
                    hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        } else if(angle >= 45 && angle < 135) {
            playerAnimationHashMap.get("playerAttackUp").draw(spriteBatch, hitbox.getX()+xOffset,
                    hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        } else if(angle >= 225 && angle < 315) {
            playerAnimationHashMap.get("playerAttackDown").draw(spriteBatch, hitbox.getX()+xOffset,
                    hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        }
    }


    public void drawPlayerWalkingAnimation(SpriteBatch spriteBatch) {
        if(playerBooleanHashMap.get("playerWalkLeft")) {
            playerAnimationHashMap.get("playerWalkLeft").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
            direction = "playerWalkLeft";
        }
        else if(playerBooleanHashMap.get("playerWalkRight")) {
            playerAnimationHashMap.get("playerWalkRight").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
            direction = "playerWalkRight";
        }
        else if(playerBooleanHashMap.get("playerWalkUp")) {
            playerAnimationHashMap.get("playerWalkUp").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
            direction = "playerWalkUp";
        }
        else if(playerBooleanHashMap.get("playerWalkDown")) {
            playerAnimationHashMap.get("playerWalkDown").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
            direction = "playerWalkDown";
        }
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchDown(float x, float y) {
        playerBooleanHashMap.put("isTouchedDown", true);
        pointerX = x;
        pointerY = y;
        playerAnimationHashMap.get("playerAttackRight").setStateTime(attackingFrameDuration);
        playerAnimationHashMap.get("playerAttackLeft").setStateTime(attackingFrameDuration);
        playerAnimationHashMap.get("playerAttackUp").setStateTime(attackingFrameDuration);
        playerAnimationHashMap.get("playerAttackDown").setStateTime(attackingFrameDuration);
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchUp(float x, float y) {
        playerBooleanHashMap.put("isTouchedDown", false);
        pointerX = x;
        pointerY = y;
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchDragged(float x, float y) {
        pointerX = x;
        pointerY = y;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    // Update a lot of player stats and properties
    public void update(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList) {
        for(Map.Entry<String, MyAnimation> entry : playerAnimationHashMap.entrySet()) {
            // If the player is walking into direction
            if(entry.getKey().equals(direction) && !playerBooleanHashMap.get(direction)) {
            } else {
                playerAnimationHashMap.get(entry.getKey()).update();
            }
        }

        // Update particles
		updateParticles();

        // Update player position
        updatePosition();
        // Update HP bar
        hpBar.update(currentHp, maximumHp, currentShield, maximumShield);
        hpBar.updatePosition(hitbox.getX(), hitbox.getY() - 7);
        // Update projectile shooting
        updateAttack(projectileList, zOrderableSpriteList);
        // Update direction if shooting
        updateDirectionIfShooting();
        // Attack timer will be incremented with elapsed time
        attackTimer += Gdx.graphics.getDeltaTime();
    }

	// Update particles, called on draw()
    public void updateParticles() {
		// Update walking particle effect
		walkingParticleEffect.update(Gdx.graphics.getDeltaTime());
		if(walkingParticleEffect.isComplete() && isWalking()) {
			walkingParticleEffect.reset();
		}

		// Update getting hit particle effect
		if(damaged) {
			gettingHitParticleEffect.update(Gdx.graphics.getDeltaTime());
			if(gettingHitParticleEffect.isComplete()) {
				damaged = false;
				gettingHitParticleEffect.reset();
			}
		}
	}

    // Verify if player can generate another projectile and create it, if possible
    public void updateAttack(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList) {
        // If touch/pointer is at clicked (or at screen) or is being dragged across the screen
        if(playerBooleanHashMap.get("isTouchedDown") && attackTimer > attackDelay) {
            // Get the sprite for the bullet
            Sprite bulletSprite = new Sprite(projectileTexture);
            Sprite bulletShadowSprite = new Sprite(projectileShadowTexture);
            // Determines the angle the player is shooting to
            angle = calculateAngle();
            // Not actually used as hitbox, as the sprite has a bounding rectangle
            Rectangle bulletHitbox = new Rectangle(hitbox.getCenterX(), hitbox.getCenterY(), projectileWidth, projectileHeight);
            Rectangle bulletShadowHitbox = new Rectangle(hitbox.getCenterX(), hitbox.getCenterY()-11, projectileWidth, projectileHeight);
            // The player entity id is 0
            // Rotate projectile boolean controls is the new bullet should or not be rotated
            if(bulletController == null) {
                bulletController = new BulletController(angle, shootingPattern, baseDamage);
            }
            bulletController.setAngle(angle);
            bulletController.setBulletHitbox(bulletHitbox);
            bulletController.setBulletShadowHitbox(bulletShadowHitbox);
            bulletController.setProjectileTexture(projectileTexture);
            bulletController.setProjectileShadowTexture(projectileShadowTexture);
            bulletController.setProjectileSpeed(projectileSpeed);
            bulletController.setProjectileLifeTime(projectileLifeTime);
            bulletController.setEntityId(playerId);
            bulletController.createBullets(projectileList, zOrderableSpriteList, rotateProjectile);
            // Reset attack timer
            attackTimer = 0f;
        }
    }

    // Determine the direction that the player is facing while shooting
    public void updateDirectionIfShooting() {
        if(playerBooleanHashMap.get("isTouchedDown")) {
            if(angle >= 135 && angle < 225) {
                direction = "playerWalkLeft";
            } else if(angle >= 315 || angle < 45) {
                direction = "playerWalkRight";
            } else if(angle >= 45 && angle < 135) {
                direction = "playerWalkUp";
            } else if(angle >= 225 && angle < 315) {
                direction = "playerWalkDown";
            }
        }
    }

    // Calculate angle to shoot a projectile, between 0 and ~359.99
    public float calculateAngle() {
        float difX = pointerX - (Gdx.graphics.getWidth()/2f);
        float difY = pointerY - (Gdx.graphics.getHeight()/2f);
        angle = (float)(180.0 / Math.PI * Math.atan2(difY, difX));
        if(angle < 0) {
            return 360 + angle;
        }
        return angle;
    }

    // Update player's position according to its move speed
    public void updatePosition() {
        if(playerBooleanHashMap.get("playerWalkLeft")) {
            hitbox.setX(hitbox.getX() - moveSpeed);
        }
        if(playerBooleanHashMap.get("playerWalkRight")) {
            hitbox.setX(hitbox.getX() + moveSpeed);
        }
        if(playerBooleanHashMap.get("playerWalkUp")) {
            hitbox.setY(hitbox.getY() + moveSpeed);
        }
        if(playerBooleanHashMap.get("playerWalkDown")) {
            hitbox.setY(hitbox.getY() - moveSpeed);
        }
        // Update ZOrderableSprite inherited y value
        setX(hitbox.getX());
        setY(hitbox.getY());
        // Update walking on dirt/grass particle effect position to the center of the player's hitbox
        walkingParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getY()-5);
		// Update getting hit particle effect position to the center of the player's hitbox
		gettingHitParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getCenterY());
        // Update player's debug hitbox sprite position
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
    }

    // Verify if player is moving, true if at least one is true
    public boolean isWalking() {
        return playerBooleanHashMap.get("playerWalkDown") ||
                playerBooleanHashMap.get("playerWalkUp") ||
                playerBooleanHashMap.get("playerWalkLeft") ||
                playerBooleanHashMap.get("playerWalkRight");
    }

    // Verify if player is pressing opposing directions simultaneously, ex.: left and right or up and down
    public boolean isWalkingAtSimultaneousOpposingDirections() {
        return (playerBooleanHashMap.get("playerWalkDown") &&
                playerBooleanHashMap.get("playerWalkUp")) ||
                (playerBooleanHashMap.get("playerWalkLeft") &&
                playerBooleanHashMap.get("playerWalkRight"));
    }

    // Process input from keyboard key down events
    public void updatePlayerKeydown(float keycode) {
        if((keycode == Input.Keys.A || keycode == Input.Keys.LEFT)) {
            playerBooleanHashMap.put("playerWalkLeft", true);
        }
        if((keycode == Input.Keys.W || keycode == Input.Keys.UP)) {
            playerBooleanHashMap.put("playerWalkUp", true);
        }
        if((keycode == Input.Keys.S || keycode == Input.Keys.DOWN)) {
            playerBooleanHashMap.put("playerWalkDown", true);
        }
        if((keycode == Input.Keys.D || keycode == Input.Keys.RIGHT)) {
            playerBooleanHashMap.put("playerWalkRight", true);
        }
    }

    // Process input from keyboard key up events
    public void updatePlayerKeyup(float keycode) {
        if(keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            playerBooleanHashMap.put("playerWalkLeft", false);
        }
        if(keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            playerBooleanHashMap.put("playerWalkUp", false);
        }
        if(keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            playerBooleanHashMap.put("playerWalkDown", false);
        }
        if(keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            playerBooleanHashMap.put("playerWalkRight", false);
        }
    }

    public void gotDamaged() {
    	damaged = true;
    	gettingHitParticleEffect.reset();
	}

    public float getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(float currentHp) {
        this.currentHp = currentHp;
    }

    public float getMaximumHp() {
        return maximumHp;
    }

    public void setMaximumHp(float maximumHp) {
        this.maximumHp = maximumHp;
    }

    public float getCurrentShield() {
        return currentShield;
    }

    public void setCurrentShield(float currentShield) {
        this.currentShield = currentShield;
    }

    public float getMaximumShield() {
        return maximumShield;
    }

    public void setMaximumShield(float maximumShield) {
        this.maximumShield = maximumShield;
    }

    public HpBar getHpBar() {
        return hpBar;
    }

    public void setHpBar(HpBar hpBar) {
        this.hpBar = hpBar;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public ParticleEffect getWalkingParticleEffect() {
        return walkingParticleEffect;
    }

    public void setWalkingParticleEffect(ParticleEffect walkingParticleEffect) {
        this.walkingParticleEffect = walkingParticleEffect;
    }

    public ParticleEffect getGettingHitParticleEffect() {
        return gettingHitParticleEffect;
    }

    public void setGettingHitParticleEffect(ParticleEffect gettingHitParticleEffect) {
        this.gettingHitParticleEffect = gettingHitParticleEffect;
    }

	public boolean isDamaged() {
		return damaged;
	}

	public float getWalkingParticleEffectScale() {
		return walkingParticleEffectScale;
	}

	public void setWalkingParticleEffectScale(float walkingParticleEffectScale) {
		this.walkingParticleEffectScale = walkingParticleEffectScale;
	}

	public float getGettingHitParticleEffectScale() {
		return gettingHitParticleEffectScale;
	}

	public void setGettingHitParticleEffectScale(float gettingHitParticleEffectScale) {
		this.gettingHitParticleEffectScale = gettingHitParticleEffectScale;
	}

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
