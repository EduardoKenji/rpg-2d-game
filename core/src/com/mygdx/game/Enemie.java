package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Enemie {

    // Hitbox for the enemie
    Rectangle hitbox;
    // Debug hitbox sprite
    Sprite hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));

    // Enemie stats
    float moveSpeed;
    float attackDelay, attackTimer;
    float baseDamage;

    // Enemie projectiles properties
    float projectileSpeed, projectileLifeTime;
    float projectileWidth, projectileHeight;
    Texture projectileTexture;

    // Enemie properties
    // AI type for this enemie
    int aiType;
    // AI variables
    float aiTimer;
    int currentDecision = 0;
    final float aiTimeToChangeDecision = 1.0f;

    // Spawn coordinates for fixated enemies
    float originX, originY;
    // Useful to define ownership of the projectiles generated
    int enemieId;
    // If this monster is a minion, it has a master
    Enemie master;
    // If this monster has minions
    ArrayList<Enemie> minionList;

    // Walking particle effect
    ParticleEffect walkingParticleEffect;
    float particleEffectScale;

    // Animations from sprite sheets with enemies facing left or facing right
    MyAnimation faceLeft, faceRight;

    // Left or right
    String direction;

    // x and y offsets
    float xOffset, yOffset;
    // true height and true width for the sprite
    float spriteWidth, spriteHeight;

    // boolean to check if enemy is moving
    boolean isMoving;

    public Enemie(Rectangle hitbox, float moveSpeed, String spriteSheetPath, String projectileTexturePath, String walkingPEPath, String walkingPEFolder, float walkingFrameDuration) {

        // Enemie hitbox
        this.hitbox = hitbox;
        // Enemie move speed
        this.moveSpeed = moveSpeed;

        // Randomize enemie initial facing direction
        int random = (int)(Math.random()*2);
        if(random == 0) {
            direction = "left";
        } else {
            direction = "right";
        }

        // Spawn coordinates for fixated enemies
        originX = hitbox.getX();
        originY = hitbox.getY();

        // Create enemie's animations
        faceLeft = new MyAnimation(new Texture(spriteSheetPath), 2, 2, walkingFrameDuration, 2, 3);
        faceRight = new MyAnimation(new Texture(spriteSheetPath), 2, 2, walkingFrameDuration, 0, 1);

        // Projectile texture
        projectileTexture = new Texture(projectileTexturePath);

        // Walking particle effect
        walkingParticleEffect = new ParticleEffect();
        walkingParticleEffect.load(Gdx.files.internal(walkingPEPath), Gdx.files.internal(walkingPEFolder));

        // Boolean to check if enemy is moving
        isMoving = false;

        // Debug draw hitbox sprite
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());

        // Sprite width and sprite height
        spriteWidth = 100;
        spriteHeight = 100;

        // Reset ai timer
        aiTimer = 0f;
    }

    public void draw(SpriteBatch spriteBatch) {

        // Draw walking particle effect if enemie is moving
        walkingParticleEffect.scaleEffect(particleEffectScale);
        if(isMoving) {
            walkingParticleEffect.draw(spriteBatch);
        }


        // Draw animations
        if(direction.equals("left")) {
            faceLeft.draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        } else {
            faceRight.draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        }

        // Debug draw player's hitbox sprite (a red empty rectangle)
        //hitboxSprite.draw(spriteBatch);
    }

    // Update a lot of player stats and properties
    public void update() {

        // Increment AI timer
        aiTimer += Gdx.graphics.getDeltaTime();
        // 0: Random Walking AI: the enemie will either stay idle or walk around randomly
        if(aiType == 0) {
            randomWalkingAI();
        }

        // Update animations
        faceLeft.update();
        faceRight.update();

        // Update walking particle effect
        walkingParticleEffect.update(Gdx.graphics.getDeltaTime());
        if(walkingParticleEffect.isComplete()) {
            walkingParticleEffect.reset();
        }
        updatePosition();

        // Attack timer will be incremented with elapsed time
        attackTimer += Gdx.graphics.getDeltaTime();
    }

    float angleToWalk;

    public void randomWalkingAI() {
        if(aiTimer > aiTimeToChangeDecision) {
            currentDecision = (int)(Math.random()*2);
            aiTimer = 0;
            // Randomize angle to walk
            angleToWalk = (float)(Math.random()*360);
            if(angleToWalk >= 270 || angleToWalk < 90) {
                direction = "right";
            } else {
                direction = "left";
            }
        }
        // Decisions and courses of action
        if(currentDecision == 0) {
            isMoving = false;
        } else if(currentDecision == 1) {
            isMoving = true;
            hitbox.setX(hitbox.getX() + (float)(moveSpeed * Math.cos(Math.toRadians(angleToWalk))));
            hitbox.setY(hitbox.getY() + (float)(moveSpeed * Math.sin(Math.toRadians(angleToWalk))));
        }
    }

    public void updatePosition() {
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        walkingParticleEffect.setPosition(hitbox.getCenterX(), hitbox.getY());

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

    public void setParticleEffectScale(float particleEffectScale) {
        this.particleEffectScale = particleEffectScale;
    }

    public float getParticleEffectScale() {
        return particleEffectScale;
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

    public float getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(float baseDamage) {
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

    public void setWalkingParticleEffect(ParticleEffect walkingParticleEffect) {
        this.walkingParticleEffect = walkingParticleEffect;
    }

    public MyAnimation getFaceLeft() {
        return faceLeft;
    }

    public void setFaceLeft(MyAnimation faceLeft) {
        this.faceLeft = faceLeft;
    }

    public MyAnimation getFaceRight() {
        return faceRight;
    }

    public void setFaceRight(MyAnimation faceRight) {
        this.faceRight = faceRight;
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
}
