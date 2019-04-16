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
    PlayerClass playerClass;

    Rectangle mapHitbox;
	Sprite mapHitboxSprite;

	ArrayList<Rectangle> hitboxCollisionList;

    // Walking on dirty particle effect
    ParticleEffect walkingParticleEffect;
	float walkingParticleEffectScale;

    // Getting hit particle effect
    ParticleEffect gettingHitParticleEffect;
	float gettingHitParticleEffectScale;
    // Boolean to check if got hit
    boolean damaged;
    float colorValue;

    // Direction
    String direction;

    // Constants
    final float xOffset = -79.5f, yOffset = -20;
    final float spriteWidth = 192, spriteHeight = 192;

    // Pointer variables
    float pointerX, pointerY;
    float angle;

    // Player stats
    int playerId;
    HpBar hpBar;

    BulletController bulletController;

    // If rotate projectile is false, the angle to rotate the spawned bullet/projectile will be 0
    final boolean rotateProjectile = true;

    // Camera offset
	float screenToViewportX, screenToViewportY;
	float cameraCenterOffsetX, cameraCenterOffsetY;
	float halfViewportWidth, halfViewportHeight;
	boolean adjustCamera;

	boolean blockedArray[];

	GameMap gameMap;

    public Player(Rectangle hitbox, PlayerClass playerClass) {
        super(hitbox.getY());
        this.hitbox = hitbox;
        this.playerClass = playerClass;
        // Create player hitbox sprite and configure hitbox
        hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));
        mapHitboxSprite = new Sprite(new Texture("textures/map_hitbox.png"));
        // Hitbox sprite can be drawn for debug reasons
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        // Create player boolean hash map
        createPlayerBooleanHashMap();
        // Default direction
        direction = "playerWalkRight";
        // Default attack delay, attack timer will be incremented with elapsed time

        // Create player boolean animation map
        createPlayerAnimationHashMap();
        // Player id
        playerId = 0;
        // Projectile texture
        // Walking on dirt/grass particle effect
        walkingParticleEffect = new ParticleEffect();
        walkingParticleEffect.load(Gdx.files.internal("particle_effects/walking_on_dirty.pe"), Gdx.files.internal("particle_effects"));
		// Getting hit particle effect
		gettingHitParticleEffect = new ParticleEffect();
		gettingHitParticleEffect.load(Gdx.files.internal("particle_effects/player_blood.pe"), Gdx.files.internal("particle_effects"));
		damaged = false;
		walkingParticleEffectScale = 0.7f;
		gettingHitParticleEffectScale = 0.25f;

		colorValue = 1;
    }

    // Create player boolean animation map
    public void createPlayerAnimationHashMap() {
        playerAnimationHashMap = new HashMap<String, MyAnimation>();
		playerAnimationHashMap.put("playerWalking",
				new MyAnimation(playerClass.getWalkingTexture(), 1, 11, playerClass.getFrameDuration()));
		playerAnimationHashMap.put("playerAttacking",
				new MyAnimation(playerClass.getAttackingTexture(), 1, 11, playerClass.getFrameDuration()));
		playerAnimationHashMap.put("playerAttackingIdle",
				new MyAnimation(playerClass.getAttackingIdleTexture(), 1, 11, playerClass.getFrameDuration()));
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

        // Draw walkingAnimation on dirty particle effects
        if(isWalking() && !isWalkingAtSimultaneousOpposingDirections()) {
            walkingParticleEffect.scaleEffect(walkingParticleEffectScale);
            walkingParticleEffect.draw(spriteBatch);
        }

        spriteBatch.setColor(1, colorValue, colorValue, 1);
        // Draw attackingAnimation or walkingAnimation animation
        if(playerBooleanHashMap.get("isTouchedDown") && isWalking()) {
            drawPlayerAttackingAnimation(spriteBatch);

        } else if(playerBooleanHashMap.get("isTouchedDown") && !isWalking()){
			drawPlayerIdleAttackingAnimation(spriteBatch);
		} else if(isWalking()){
            drawPlayerWalkingAnimation(spriteBatch);
        }

        // If the player is not walkingAnimation nor attackingAnimation, render static frame for idle stance
        if((!playerBooleanHashMap.get("isTouchedDown") && !isWalking())) {
            //playerAnimationHashMap.get(direction).draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
			playerAnimationHashMap.get("playerWalking").drawStaticFrame(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        }

		spriteBatch.setColor(1, 1, 1, 1);

		if(damaged) {
			gettingHitParticleEffect.scaleEffect(gettingHitParticleEffectScale);
			gettingHitParticleEffect.draw(spriteBatch);
		}

        hpBar.draw(spriteBatch);

        // Debug draw player's map hitbox sprite (a red empty rectangle)
		//hitboxSprite.draw(spriteBatch);
        //spriteBatch.draw(mapHitboxSprite, mapHitbox.getX(), mapHitbox.getY(), mapHitbox.getWidth(), mapHitbox.getHeight());
    }

    public void drawPlayerIdleAttackingAnimation(SpriteBatch spriteBatch) {
		playerAnimationHashMap.get("playerAttackingIdle").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
	}

    public void drawPlayerAttackingAnimation(SpriteBatch spriteBatch) {
		playerAnimationHashMap.get("playerAttacking").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
    }


    public void drawPlayerWalkingAnimation(SpriteBatch spriteBatch) {
		playerAnimationHashMap.get("playerWalking").draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchDown(float x, float y) {
        playerBooleanHashMap.put("isTouchedDown", true);
        pointerX = x * screenToViewportX;
        pointerY = y * screenToViewportY;
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchUp(float x, float y) {
        playerBooleanHashMap.put("isTouchedDown", false);
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchDragged(float x, float y) {
        pointerX = x * screenToViewportX;
        pointerY = y * screenToViewportY;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    // Update a lot of player stats and properties
    public void update(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList,  ArrayList<Light> lightList) {
        for(Map.Entry<String, MyAnimation> entry : playerAnimationHashMap.entrySet()) {
            // If the player is walkingAnimation into direction
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
        hpBar.update(playerClass.getCurrentHp(), playerClass.getMaximumHp(), playerClass.getCurrentShield(), playerClass.getMaximumShield());
        hpBar.updatePosition(hitbox.getX(), hitbox.getY() - 7);
        // Update projectile shooting
        updateAttack(projectileList, zOrderableSpriteList, lightList);
        // Update direction if shooting
        updateDirectionIfShooting();
        // Attack timer will be incremented with elapsed time
        playerClass.setAttackTimer(playerClass.getAttackTimer() + Gdx.graphics.getDeltaTime());
    }

	// Update particles, called on draw()
    public void updateParticles() {
		// Update walkingAnimation particle effect
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

		// Player will shine red light when damaged
		if(colorValue < 1) {
			colorValue += Gdx.graphics.getDeltaTime() * 2;
		}
	}

    // Verify if player can generate another projectile and create it, if possible
    public void updateAttack(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList, ArrayList<Light> lightList) {
        // If touch/pointer is at clicked (or at screen) or is being dragged across the screen
        if(playerBooleanHashMap.get("isTouchedDown") && playerClass.getAttackTimer() > playerClass.getAttackDelay()) {
            // Determines the angle the player is shooting to
            angle = calculateAngle();
            // Not actually used as hitbox, as the sprite has a bounding rectangle
            Rectangle bulletHitbox = new Rectangle(hitbox.getCenterX(), hitbox.getCenterY(), playerClass.getProjectileWidth(), playerClass.getProjectileHeight());
            Rectangle bulletShadowHitbox = new Rectangle(hitbox.getCenterX(), hitbox.getY(),  playerClass.getProjectileWidth()/2, playerClass.getProjectileHeight()/2);
            // The player entity id is 0
            // Rotate projectile boolean controls is the new bullet should or not be rotated
            if(bulletController == null) {
                bulletController = new BulletController(angle, playerClass.getShootingPattern(), playerClass.getBaseDamage());
            }
            bulletController.setDamageRange(playerClass.getMinimumRangeOffset(), playerClass.getMaximumRangeOffset());
            bulletController.setAngle(angle);
            bulletController.setBulletHitbox(bulletHitbox);
            bulletController.setBulletShadowHitbox(bulletShadowHitbox);
            bulletController.setProjectileTexture(playerClass.getProjectileTexture());
            bulletController.setProjectileShadowTexture(playerClass.getProjectileShadowTexture());
            bulletController.setProjectileSpeed(playerClass.getProjectileSpeed());
            bulletController.setProjectileLifeTime(playerClass.getProjectileLifeTime());
            bulletController.setEntityId(playerId);
            bulletController.setLightTexture(playerClass.getLightTexture(), lightList);
            bulletController.setAttackParticleEffectPath(playerClass.getAttackParticleEffectPath());
			bulletController.setAttackParticleEffectFolderPath(playerClass.getAttackParticleEffectFolderPath());
            bulletController.createBullets(projectileList, zOrderableSpriteList, rotateProjectile);
            // Reset attack timer
            playerClass.setAttackTimer(0f);
        }
    }

	// Calculate angle to shoot a projectile, between 0 and ~359.99
	public float calculateAngle() {
		float difX = (pointerX)-(cameraCenterOffsetX) - (hitbox.getCenterX());
		float difY = (pointerY)-(cameraCenterOffsetY) - (hitbox.getCenterY());
		angle = (float)(180.0 / Math.PI * Math.atan2(difY, difX));
		if(angle < 0) {
			return 360 + angle;
		}
		return angle;
	}

    // Determine the direction that the player is facing while shooting
    public void updateDirectionIfShooting() {
        if(playerBooleanHashMap.get("isTouchedDown")) {
        	if(angle > 270 || angle < 90) {
				//playerAnimationHashMap.get("playerWalking").setxFlipped("right");
				playerAnimationHashMap.get("playerAttacking").setxFlipped("right");
				playerAnimationHashMap.get("playerAttackingIdle").setxFlipped("right");
			} else {
				//playerAnimationHashMap.get("playerWalking").setxFlipped("left");
				playerAnimationHashMap.get("playerAttacking").setxFlipped("left");
				playerAnimationHashMap.get("playerAttackingIdle").setxFlipped("left");

			}
        }
    }

    int i;
    final float sin45Deg = 0.707f;

    // Update player's position according to its move speed
    public void updatePosition() {
		float fpsBoundMoveSpeed;
		fpsBoundMoveSpeed = playerClass.getBaseMoveSpeed()* (Gdx.graphics.getDeltaTime() * 60);

		// If the player is walkingAnimation diagonally, multiply move speed by sin(45º), as there will be 0.707 for each direction
  		if(isDiagonalWalking()) {
			fpsBoundMoveSpeed *= (sin45Deg);
		}

		// If the player is attackingAnimation, reduce its speed
		if(playerBooleanHashMap.get("isTouchedDown")) {
			fpsBoundMoveSpeed *= 0.70f;
		}

		if(playerBooleanHashMap.get("playerWalkLeft")) {
			updateMovementStepByStep(0, fpsBoundMoveSpeed);
		}
		if(playerBooleanHashMap.get("playerWalkRight")) {
			updateMovementStepByStep(2, fpsBoundMoveSpeed);
		}
		if(playerBooleanHashMap.get("playerWalkUp")) {
			updateMovementStepByStep(3, fpsBoundMoveSpeed);
		}
		if(playerBooleanHashMap.get("playerWalkDown")) {
			updateMovementStepByStep(1, fpsBoundMoveSpeed);
		}

        // Update ZOrderableSprite inherited y value
        setX(hitbox.getX());
        setY(hitbox.getY());
        // Update walkingAnimation on dirt/grass particle effect position to the center of the player's hitbox
        walkingParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getY()-5);
		// Update getting hit particle effect position to the center of the player's hitbox
		gettingHitParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getCenterY());
        // Update player's debug hitbox sprite position
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        // Update map hitbox
    }

	// Update game map and blocked array
	// Get directions where there are obstacles in map
	// 0 - left, 1 - bottom, 2 - right, 3 - top
    public void updateMovementStepByStep(int direction, float fpsBoundMoveSpeed) {
		float targetX = mapHitbox.getX(), targetY = mapHitbox.getY();
		float increment;
		if(!blockedArray[direction]) {
			for(i = 0; i < Math.ceil(fpsBoundMoveSpeed); i++) {
				// Update game map and blocked array
				// Get directions where there are obstacles in map
				// 0 - left, 1 - bottom, 2 - right, 3 - top
				blockedArray = gameMap.updateHitbox(mapHitbox, 1);
				if(blockedArray[direction]) break;
				gameMap.updatePlayerPosition(mapHitbox);
				if(i != Math.ceil(fpsBoundMoveSpeed) - 1) {
					increment = 1;
				} else {
					increment = 1 - (float)(Math.ceil(fpsBoundMoveSpeed) - fpsBoundMoveSpeed);
				}
				if(direction == 0 || direction == 1) {
					increment *= -1;
				}
				if(direction == 0  || direction == 2) {
					targetX += increment;
					hitbox.setX(targetX);
					mapHitbox.setX(targetX);
				} else {
					targetY += increment;
					hitbox.setY(targetY);
					mapHitbox.setY(targetY);
				}
			}
		}
	}

	// Verify if player is moving, true if at least one is true
	public boolean isDiagonalWalking() {
		return (playerBooleanHashMap.get("playerWalkDown") && playerBooleanHashMap.get("playerWalkLeft")) ||
				(playerBooleanHashMap.get("playerWalkDown") && playerBooleanHashMap.get("playerWalkRight")) ||
				(playerBooleanHashMap.get("playerWalkUp") && playerBooleanHashMap.get("playerWalkLeft")) ||
				(playerBooleanHashMap.get("playerWalkUp") && playerBooleanHashMap.get("playerWalkRight"));
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
            playerAnimationHashMap.get("playerWalking").setxFlipped("left");
        }
        if((keycode == Input.Keys.W || keycode == Input.Keys.UP)) {
            playerBooleanHashMap.put("playerWalkUp", true);
        }
        if((keycode == Input.Keys.S || keycode == Input.Keys.DOWN)) {
            playerBooleanHashMap.put("playerWalkDown", true);
        }
        if((keycode == Input.Keys.D || keycode == Input.Keys.RIGHT)) {
            playerBooleanHashMap.put("playerWalkRight", true);
			playerAnimationHashMap.get("playerWalking").setxFlipped("right");
        }
    }

    // Process input from keyboard key up events
    public void updatePlayerKeyup(float keycode) {
        if(keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            playerBooleanHashMap.put("playerWalkLeft", false);
            if(playerBooleanHashMap.get("playerWalkRight")) {
				playerAnimationHashMap.get("playerWalking").setxFlipped("right");
			}
        }
        if(keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            playerBooleanHashMap.put("playerWalkUp", false);
        }
        if(keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            playerBooleanHashMap.put("playerWalkDown", false);
        }
        if(keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            playerBooleanHashMap.put("playerWalkRight", false);
			if(playerBooleanHashMap.get("playerWalkLeft")) {
				playerAnimationHashMap.get("playerWalking").setxFlipped("left");
			}
        }
    }

    public void gotDamaged() {
    	colorValue = 0;
    	damaged = true;
    	gettingHitParticleEffect.reset();
	}

    public HpBar getHpBar() {
        return hpBar;
    }

    public void setHpBar(HpBar hpBar) {
        this.hpBar = hpBar;
    }

    public ParticleEffect getWalkingParticleEffect() {
        return walkingParticleEffect;
    }

	public Rectangle getMapHitbox() {
		return mapHitbox;
	}

	public void setMapHitbox(Rectangle mapHitbox) {
    	this.mapHitbox = mapHitbox;
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


	public float getGettingHitParticleEffectScale() {
		return gettingHitParticleEffectScale;
	}

	public boolean isAdjustCamera() {
		return adjustCamera;
	}

	public void setAdjustCamera(boolean adjustCamera) {
		this.adjustCamera = adjustCamera;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public void setPlayerClass(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public void setScreenToViewport(float viewportWidth, float viewportHeight) {
    	screenToViewportX = viewportWidth/Gdx.graphics.getWidth();
    	screenToViewportY = viewportHeight/Gdx.graphics.getHeight();
    	halfViewportWidth = (viewportWidth/2);
    	halfViewportHeight = (viewportHeight/2);
    	cameraCenterOffsetX = halfViewportWidth-hitbox.getCenterX();
		cameraCenterOffsetY = halfViewportHeight-hitbox.getCenterY();
	}

	public void updateCameraCenterOffset(float camX, float camY) {
		cameraCenterOffsetX = halfViewportWidth - camX;
		cameraCenterOffsetY = halfViewportHeight - camY;
	}

	public float getCameraCenterOffsetX() {
		return cameraCenterOffsetX;
	}

	public void setCameraCenterOffsetX(float cameraCenterOffsetX) {
		this.cameraCenterOffsetX = cameraCenterOffsetX;
	}

	public float getCameraCenterOffsetY() {
		return cameraCenterOffsetY;
	}

	public void setCameraCenterOffsetY(float cameraCenterOffsetY) {
		this.cameraCenterOffsetY = cameraCenterOffsetY;
	}

	public void setWalkingParticleEffectScale(float particleEffectScale) {
		walkingParticleEffectScale = particleEffectScale;
		scaleParticle(walkingParticleEffect, particleEffectScale);
	}

	public void setGettingHitParticleEffectScale(float particleEffectScale) {
		gettingHitParticleEffectScale = particleEffectScale;
		scaleParticle(gettingHitParticleEffect, particleEffectScale);
	}

	public void scaleParticle(ParticleEffect particleEffect, float particleEffectScale) {
		float scaling;

		scaling = particleEffect.getEmitters().get(0).getXScale().getHighMax();
		particleEffect.getEmitters().get(0).getXScale().setHigh(scaling * particleEffectScale);

		scaling = particleEffect.getEmitters().get(0).getVelocity().getHighMax();
		particleEffect.getEmitters().get(0).getVelocity().setHigh(scaling * particleEffectScale);

		scaling = particleEffect.getEmitters().get(0).getVelocity().getLowMax();
		particleEffect.getEmitters().get(0).getVelocity().setLow(scaling * particleEffectScale);
	}

	public ArrayList<Rectangle> getHitboxCollisionList() {
		return hitboxCollisionList;
	}

	public void setHitboxCollisionList(ArrayList<Rectangle> hitboxCollisionList) {
		this.hitboxCollisionList = hitboxCollisionList;
	}

	public GameMap getGameMap() {
		return gameMap;
	}

	public void setGameMap(GameMap gameMap) {
    	this.gameMap = gameMap;
		blockedArray = gameMap.updateHitbox(mapHitbox, 1);
		gameMap.updatePlayerPosition(mapHitbox);
	}

	public float getColorValue() {
		return colorValue;
	}

	public void setColorValue(float colorValue) {
		this.colorValue = colorValue;
	}
}
