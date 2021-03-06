package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Enemie extends ZOrderableSprite {

	ArrayList hitboxCollisionList;

    // Hitbox for the enemie
    Rectangle hitbox;
    ArrayList<Rectangle> hitboxList;
    ArrayList<Sprite> bulletHitboxSpriteList;
    // Debug hitbox sprite
    Sprite hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));
    // Map hitbox, used to calculate collision with map objects and boundaries
	Rectangle mapHitbox;
	Sprite mapHitboxSprite = new Sprite(new Texture("textures/map_hitbox.png"));

    // Enemie stats
    float moveSpeed;
    float attackDelay, attackTimer;
    int baseDamage;
    float currentHp, maximumHp;
    float currentShield, maximumShield;
    HpBar hpBar;
    int experience;
    boolean dead;
    String name;

    // Enemie projectiles properties
    float projectileSpeed, projectileLifeTime;
    float projectileWidth, projectileHeight;
    Texture projectileTexture, projectileShadowTexture;
    boolean rotateProjectile;
    // Enemie shootingPattern
    int shootingPattern;
    BulletController bulletController;

    // Enemie properties
    // AI type for this enemie
    int aiType;
    // AI variables
    float aiTimer, aiTimer2, aiSpawnTimer;
    int currentDecision;
    float aiTimeToChangeDecision;
    float aiTimeToAction;
    // Only usable for enemies that spawn others
    float aiTimeToSpawn;
    // The range for the enemie to target opponents
    float hostileRange;

    // Spawn coordinates for fixated enemies
    float originX, originY;
    // Useful to define ownership of the projectiles generated
    int enemieId;
    // If this enemie is a minion, it has a master
    Enemie master;
    // If this enemie is a spawner, this will the be the spawned enemie
    Enemie spawnedEnemie;
    // If this monster has minions
    ArrayList<Enemie> minionList;
    // Maximum amount of minions on gameMap
    int maxMinions;
    // Target player
    Player player;

    // Walking particle effect
    ParticleEffect walkingParticleEffect;
    float walkingParticleEffectScale;

    // Getting hit particle effect
    ParticleEffect gettingHitParticleEffect;
    float gettingHitParticleEffectScale;
    boolean damaged;

    // Animations from sprite sheets with enemies facing left or facing right
    MyAnimation animation;

    // Left or right
    String direction;

    // x and y offsets
    float xOffset, yOffset;
    // true height and true width for the sprite
    float spriteWidth, spriteHeight;

    // boolean to check if enemy is moving
    boolean isMoving;

    // Check collision with world map
	boolean blockedArray[];
	GameMap gameMap;

	float colorValue;

    public Enemie(Rectangle hitbox, float moveSpeed, String spriteSheetPath, String projectileTexturePath, String walkingPEPath,  String gettingHitPEPath, String particleFolder, float walkingFrameDuration, int cols) {
        super(hitbox.getY());
        // Enemie hitbox
        this.hitbox = hitbox;
        // Enemie move speed
        this.moveSpeed = moveSpeed;

        // Spawn coordinates for fixated enemies
        originX = hitbox.getX();
        originY = hitbox.getY();

        // Create enemie's animations
        animation = new MyAnimation(new Texture(spriteSheetPath), 1, cols, walkingFrameDuration);

		// Randomize enemie initial facing direction
		int random = (int)(Math.random()*2);
		if(random == 0) {
			direction = "left";
			animation.setxFlipped(direction);
		} else {
			direction = "right";
			animation.setxFlipped(direction);
		}

        // Projectile texture
        projectileTexture = new Texture(projectileTexturePath);

        // Walking particle effect
        walkingParticleEffect = new ParticleEffect();
        walkingParticleEffect.load(Gdx.files.internal(walkingPEPath), Gdx.files.internal(particleFolder));

        // Getting hit particle effect
        gettingHitParticleEffect = new ParticleEffect();
        gettingHitParticleEffect.load(Gdx.files.internal(gettingHitPEPath), Gdx.files.internal(particleFolder));

        // Boolean to check if enemy is moving
        isMoving = false;

        // Debug draw hitbox sprite
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());

        // Reset ai timer
        aiTimer = 0f;
        aiTimer2 = 0f;
        aiSpawnTimer = 0f;

        // Default direction
        currentDecision = 0;

        // Array list
		hitboxList = new ArrayList<Rectangle>();
		bulletHitboxSpriteList = new ArrayList<Sprite>();

		// Enemie is initially not dead
		dead = false;

		colorValue = 1;
    }

    public void draw(SpriteBatch spriteBatch) {

        // Draw walkingAnimation particle effect if enemie is moving
        if(isMoving) {
			//walkingParticleEffect.scaleEffect(walkingParticleEffectScale);
            walkingParticleEffect.draw(spriteBatch);
        }

        spriteBatch.setColor(1, colorValue, colorValue, 1);

        if(isMoving) {
			animation.draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
		} else {
			animation.drawStaticFrame(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
		}

		spriteBatch.setColor(1, 1, 1, 1);

        if(damaged) {
        	//gettingHitParticleEffect.scaleEffect(gettingHitParticleEffectScale);
            gettingHitParticleEffect.draw(spriteBatch);
        }

        // Draw HP bar
        hpBar.draw(spriteBatch);

        /*
        // Debug draw enemie sprite hitbox
        hitboxSprite.draw(spriteBatch);

        // Debug draw enemie bullet hitboxes sprite
        for(Sprite bulletHitbox : bulletHitboxSpriteList) {
        	bulletHitbox.draw(spriteBatch);
		}

		spriteBatch.draw(mapHitboxSprite, mapHitbox.getX(), mapHitbox.getY(), mapHitbox.getWidth(), mapHitbox.getHeight());
		*/
    }

    // Update a lot of player stats and properties
    public void update(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList) {

        // Increment AI timer
        aiTimer += Gdx.graphics.getDeltaTime();
        aiTimer2 += Gdx.graphics.getDeltaTime();
        // Increment attack timer
        attackTimer += Gdx.graphics.getDeltaTime();
        // 0: Random Walking AI: the enemie will either stay idle or walk around randomly
        if(aiType == 0) {
            // hostile: false
            randomWalkingAI(false, null, null);
        }
        /*
        1: Hostile random Walking AI: the enemie will attack the player is close enough,
        else the enemie will either stay idle or walk around randomly
        */
        else if(aiType == 1) {
            // Hostile
            randomWalkingAI(true, projectileList, zOrderableSpriteList);
        }

        // Update animations
        animation.update();

        // Update particles
        updateParticles();

        // Update enemie position
        updatePosition();

        // Update hp bar
        hpBar.update(currentHp, maximumHp, currentShield, maximumShield);
        hpBar.updatePosition(hitbox.getX(), hitbox.getY() - 7);

        // Enemie will shine red light when damaged
		if(colorValue < 1) {
			colorValue += Gdx.graphics.getDeltaTime() * 2;
		}
    }

    // Update particles, called on draw()
    public void updateParticles() {

        // Update walkingAnimation particle effect
        walkingParticleEffect.update(Gdx.graphics.getDeltaTime());
        if(walkingParticleEffect.isComplete()) {
            walkingParticleEffect.reset();
        }

        // Update getting hit particle effect
        if(damaged) {
            gettingHitParticleEffect.update(Gdx.graphics.getDeltaTime());
            if(gettingHitParticleEffect.isComplete() && !dead) {
                damaged = false;
                gettingHitParticleEffect.reset();
            }
        }
    }

    float angleToWalk;

    public void randomWalkingAI(boolean hostile, ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList) {

        // Decides if staying idle or walk randomly
        if(aiTimer > aiTimeToChangeDecision) {
            currentDecision = (int)(Math.random()*3);
            aiTimer = 0;
        }
        // Randomize angle to walk
        if(aiTimer2 > aiTimeToAction) {

            angleToWalk = (float)(Math.random()*360);
            if(angleToWalk >= 270 || angleToWalk < 90) {
                direction = "right";
            } else {
                direction = "left";
            }
			animation.setxFlipped(direction);
            aiTimer2 = 0f;
        }
        // Decisions and courses of action
        if(currentDecision == 0) {
            isMoving = false;
        } else if(currentDecision >= 1) {
            isMoving = true;
            float fpsBoundMoveSpeed = moveSpeed * (Gdx.graphics.getDeltaTime() * 60);
            float offsetX = (float)(fpsBoundMoveSpeed * Math.cos(Math.toRadians(angleToWalk)));
            float offsetY = (float)(fpsBoundMoveSpeed * Math.sin(Math.toRadians(angleToWalk)));
            if(offsetX < 0) {
            	updateMovementStepByStep(0, offsetX);
			} else if(offsetX > 0) {
				updateMovementStepByStep(2, offsetX);
			}
			if(offsetY < 0) {
				updateMovementStepByStep(1, offsetY);
			} else if(offsetY > 0) {
				updateMovementStepByStep(3, offsetY);
			}
        }

        // hostile is true fs aiType = 1, false if aiType = 0
        // Verify is player is close enough to attack
        if(hostile && euclidianDistance(hitbox.getCenterX(), hitbox.getCenterY(), player.getHitbox().getCenterX(), player.getHitbox().getCenterY()) < hostileRange) {
            // Face player
            if(player.getHitbox().getX() <= hitbox.getCenterX()) {
                direction = "left";
            } else {
                direction = "right";
            }
			animation.setxFlipped(direction);
            updateAttack(projectileList, zOrderableSpriteList);

        }
    }

    int i;

	// Update game map and blocked array
	// Get directions where there are obstacles in map
	// 0 - left, 1 - bottom, 2 - right, 3 - top
	public void updateMovementStepByStep(int direction, float fpsBoundMoveSpeed) {
		if(fpsBoundMoveSpeed < 0) fpsBoundMoveSpeed *= -1;
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
					for(Rectangle bulletHitbox : hitboxList) {
						bulletHitbox.setX(bulletHitbox.getX() + increment);
					}
				} else {
					targetY += increment;
					hitbox.setY(targetY);
					mapHitbox.setY(targetY);
					for(Rectangle bulletHitbox : hitboxList) {
						bulletHitbox.setY(bulletHitbox.getY() + increment);
					}
				}
			}
		}
	}

    public void updateAttack(ArrayList<Bullet> projectileList, ArrayList<ZOrderableSprite> zOrderableSpriteList) {
        if(attackTimer > attackDelay) {
            // Get the sprite for the bullet
            Sprite bulletSprite = new Sprite(projectileTexture);
            Sprite bulletShadowSprite = new Sprite(projectileShadowTexture);
            // Determines the angle the player is shooting to
            float angle = calculateAngle(player.getHitbox().getCenterX(), player.getHitbox().getCenterY(), hitbox.getCenterX(), hitbox.getCenterY());
            // Not actually used as hitbox, as the sprite has a bounding rectangle
            Rectangle bulletHitbox = new Rectangle(hitbox.getCenterX(), hitbox.getCenterY(), projectileWidth, projectileHeight);
            Rectangle bulletShadowHitbox = new Rectangle(hitbox.getCenterX(), hitbox.getY(), projectileWidth/2, projectileHeight/2);
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
            bulletController.setEntityId(enemieId);
            bulletController.createBullets(projectileList, zOrderableSpriteList, rotateProjectile);
            // Reset attackTimer
            attackTimer = 0;
        }
    }

    public void updatePosition() {
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        // Update walkingAnimation PE position
        walkingParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getY());
        // Update getting hit PE position
        gettingHitParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getCenterY());
        setX(hitbox.getX());
        setY(hitbox.getY());
        for(int i = 0; i < bulletHitboxSpriteList.size(); i++) {
			bulletHitboxSpriteList.get(i).setPosition(hitboxList.get(i).getX(),hitboxList.get(i).getY());
		}
    }

    // (x1, y1) is the vector target
    // (x2, y2) is the vector origin
    // Calculate angle to shoot a projectile, between 0 and ~359.99
    public float calculateAngle(float x1, float y1, float x2, float y2) {
        float difX = x1 - x2;
        float difY = y1 - y2;
        float angle = (float)(180.0 / Math.PI * Math.atan2(difY, difX));
        if(angle < 0) {
            return 360 + angle;
        }
        return angle;
    }

    public float euclidianDistance(float x1, float y1, float x2, float y2) {
        return (float)(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
    }

    public void addHitBox(float x, float y, float width, float height) {
		Sprite bulletHitboxSprite = new Sprite(new Texture("textures/bullet_hitbox.png"));
		bulletHitboxSprite.setPosition(x, y);
		bulletHitboxSprite.setSize(width, height);
		bulletHitboxSpriteList.add(bulletHitboxSprite);
		hitboxList.add(new Rectangle(x, y, width, height));
	}

    public void gotDamaged() {
    	// If enemy is not dead, it can be damaged
    	if(!dead) {
			damaged = true;
			gettingHitParticleEffect.reset();
			colorValue = 0;
		}

    }

    public float getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(float currentHp) {
        this.currentHp = currentHp;
    }

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
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

    public BulletController getBulletController() {
        return bulletController;
    }

    public void setBulletController(BulletController bulletController) {
        this.bulletController = bulletController;
    }

    public float getAiSpawnTimer() {
        return aiSpawnTimer;
    }

    public void setAiSpawnTimer(float aiSpawnTimer) {
        this.aiSpawnTimer = aiSpawnTimer;
    }

    public boolean isRotateProjectile() {
        return rotateProjectile;
    }

    public void setRotateProjectile(boolean rotateProjectile) {
        this.rotateProjectile = rotateProjectile;
    }

    public int getShootingPattern() {
        return shootingPattern;
    }

    public void setShootingPattern(int shootingPattern) {
        this.shootingPattern = shootingPattern;
    }

    public float getAiTimeToSpawn() {
        return aiTimeToSpawn;
    }

    public void setAiTimeToSpawn(float aiTimeToSpawn) {
        this.aiTimeToSpawn = aiTimeToSpawn;
    }

    public int getMaxMinions() {
        return maxMinions;
    }

    public void setMaxMinions(int maxMinions) {
        this.maxMinions = maxMinions;
    }

    public Enemie getSpawnedEnemie() {
        return spawnedEnemie;
    }

    public void setSpawnedEnemie(Enemie spawnedEnemie) {
        this.spawnedEnemie = spawnedEnemie;
    }

    public float getAiTimer2() {
        return aiTimer2;
    }

    public void setAiTimer2(float aiTimer2) {
        this.aiTimer2 = aiTimer2;
    }

    public int getCurrentDecision() {
        return currentDecision;
    }

    public void setCurrentDecision(int currentDecision) {
        this.currentDecision = currentDecision;
    }

    public float getAiTimeToChangeDecision() {
        return aiTimeToChangeDecision;
    }

    public float getAiTimeToAction() {
        return aiTimeToAction;
    }

    public float getHostileRange() {
        return hostileRange;
    }

    public void setHostileRange(float hostileRange) {
        this.hostileRange = hostileRange;
    }

    public float getOriginX() {
        return originX;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public float getAngleToWalk() {
        return angleToWalk;
    }

    public void setAngleToWalk(float angleToWalk) {
        this.angleToWalk = angleToWalk;
    }

    public float getAiTimer() {
        return aiTimer;
    }

    public void setAiTimer(float aiTimer) {
        this.aiTimer = aiTimer;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public Sprite getHitboxSprite() {
        return hitboxSprite;
    }

    public void setHitboxSprite(Sprite hitboxSprite) {
        this.hitboxSprite = hitboxSprite;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(float attackDelay) {
        this.attackDelay = attackDelay;
    }

    public float getAttackTimer() {
        return attackTimer;
    }

    public void setAttackTimer(float attackTimer) {
        this.attackTimer = attackTimer;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
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

    public float getProjectileWidth() {
        return projectileWidth;
    }

    public void setProjectileWidth(float projectileWidth) {
        this.projectileWidth = projectileWidth;
    }

    public float getProjectileHeight() {
        return projectileHeight;
    }

    public void setProjectileHeight(float projectileHeight) {
        this.projectileHeight = projectileHeight;
    }

    public Texture getProjectileTexture() {
        return projectileTexture;
    }

    public void setProjectileTexture(Texture projectileTexture) {
        this.projectileTexture = projectileTexture;
    }

    public int getAiType() {
        return aiType;
    }

    public void setAiType(int aiType) {
        this.aiType = aiType;
    }

    public int getEnemieId() {
        return enemieId;
    }

    public void setEnemieId(int enemieId) {
        this.enemieId = enemieId;
    }

    public Enemie getMaster() {
        return master;
    }

    public void setMaster(Enemie master) {
        this.master = master;
    }

    public ArrayList<Enemie> getMinionList() {
        return minionList;
    }

    public void setMinionList(ArrayList<Enemie> minionList) {
        this.minionList = minionList;
    }

    public ParticleEffect getWalkingParticleEffect() {
        return walkingParticleEffect;
    }

    public MyAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(MyAnimation animation) {
        this.animation = animation;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public float getSpriteWidth() {
        return spriteWidth;
    }

    public void setSpriteWidth(float spriteWidth) {
        this.spriteWidth = spriteWidth;
    }

    public float getSpriteHeight() {
        return spriteHeight;
    }

    public void setSpriteHeight(float spriteHeight) {
        this.spriteHeight = spriteHeight;
    }

    public void setAiTimeToChangeDecision(float aiTimeToChangeDecision) {
        this.aiTimeToChangeDecision = aiTimeToChangeDecision;
    }

    public void setAiTimeToAction(float aiTimeToAction) {
        this.aiTimeToAction = aiTimeToAction;
    }

    public Texture getProjectileShadowTexture() {
        return projectileShadowTexture;
    }

    public void setProjectileShadowTexture(Texture projectileShadowTexture) {
        this.projectileShadowTexture = projectileShadowTexture;
    }

    public ParticleEffect getGettingHitParticleEffect() {
        return gettingHitParticleEffect;
    }

    public void setGettingHitParticleEffect(ParticleEffect gettingHitParticleEffect) {
        this.gettingHitParticleEffect = gettingHitParticleEffect;
    }

    public float getWalkingParticleEffectScale() {
        return walkingParticleEffectScale;
    }

    public float getGettingHitParticleEffectScale() {
        return gettingHitParticleEffectScale;
    }

    public ArrayList<Rectangle> getHitboxList() {
        return hitboxList;
    }

    public void setHitboxList(ArrayList<Rectangle> hitboxList) {
        this.hitboxList = hitboxList;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

	public ArrayList<Sprite> getBulletHitboxSpriteList() {
		return bulletHitboxSpriteList;
	}

	public void setBulletHitboxSpriteList(ArrayList<Sprite> bulletHitboxSpriteList) {
		this.bulletHitboxSpriteList = bulletHitboxSpriteList;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public void setGettingHitParticleEffectScale(float particleEffectScale) {
		gettingHitParticleEffectScale = particleEffectScale;
		scaleParticle(gettingHitParticleEffect, particleEffectScale);
    }

	public void setWalkingParticleEffectScale(float particleEffectScale) {
    	walkingParticleEffectScale = particleEffectScale;
    	scaleParticle(walkingParticleEffect, particleEffectScale);
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

	public ArrayList getHitboxCollisionList() {
		return hitboxCollisionList;
	}

	public void setHitboxCollisionList(ArrayList hitboxCollisionList) {
		this.hitboxCollisionList = hitboxCollisionList;
	}

	public void setWalkingParticleEffect(ParticleEffect walkingParticleEffect) {
		this.walkingParticleEffect = walkingParticleEffect;
	}

	public boolean[] getBlockedArray() {
		return blockedArray;
	}

	public void setBlockedArray(boolean[] blockedArray) {
		this.blockedArray = blockedArray;
	}

	public GameMap getGameMap() {
		return gameMap;
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
		blockedArray = gameMap.updateHitbox(mapHitbox, 1);
	}

	public Rectangle getMapHitbox() {
		return mapHitbox;
	}

	public void setMapHitbox(Rectangle mapHitbox) {
		this.mapHitbox = mapHitbox;
	}

	public Sprite getMapHitboxSprite() {
		return mapHitboxSprite;
	}

	public void setMapHitboxSprite(Sprite mapHitboxSprite) {
		this.mapHitboxSprite = mapHitboxSprite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public float getColorValue() {
		return colorValue;
	}

	@Override
	public void setColorValue(float colorValue) {
		this.colorValue = colorValue;
	}
}
