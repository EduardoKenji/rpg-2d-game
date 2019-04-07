package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.Map;

public class Player {
    HashMap<String, MyAnimation> playerAnimationHashMap;
    Rectangle hitbox;
    Sprite hitboxSprite;
    float moveSpeed;
    HashMap<String, Boolean> playerBooleanHashMap;

    // Walking on dirty particle effect
    ParticleEffect walkingOnDirtyPE;

    // Direction
    String direction;

    // Constants
    final float xOffset = -40, yOffset = -10;
    final float spriteWidth = 120, spriteHeight = 120;

    // Pointer variables
    float pointerX, pointerY;

    public Player(Rectangle hitbox, float moveSpeed) {
        this.hitbox = hitbox;
        this.moveSpeed = moveSpeed;
        // Create player hitbox sprite and configure hitbox
        hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        // Create player boolean hash map
        createPlayerBooleanHashMap();
        // Create player boolean animation map
        createPlayerAnimationHashMap();
        // Default direction
        direction = "playerWalkRight";

        walkingOnDirtyPE = new ParticleEffect();
        walkingOnDirtyPE.load(Gdx.files.internal("textures/walking_on_dirty_particles.pe"), Gdx.files.internal("textures"));
    }

    // Create player boolean animation map
    public void createPlayerAnimationHashMap() {
        playerAnimationHashMap = new HashMap<String, MyAnimation>();
        float frameDuration = 0.3f;
        playerAnimationHashMap.put("playerAttackRight",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, frameDuration, 0, 1));
        playerAnimationHashMap.put("playerAttackLeft",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, frameDuration, 2, 3));
        playerAnimationHashMap.put("playerAttackUp",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, frameDuration, 6, 7));
        playerAnimationHashMap.put("playerAttackDown",
                new MyAnimation(new Texture("characters/knight_attack_spritesheet.png"), 4, 2, frameDuration, 4, 5));
        playerAnimationHashMap.put("playerWalkRight",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, frameDuration, 0, 1));
        playerAnimationHashMap.put("playerWalkLeft",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, frameDuration, 2, 3));
        playerAnimationHashMap.put("playerWalkUp",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, frameDuration, 6, 7));
        playerAnimationHashMap.put("playerWalkDown",
                new MyAnimation(new Texture("characters/knight_walking_spritesheet.png"), 4, 2, frameDuration, 4, 5));
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
        //hitboxSprite.draw(spriteBatch);

        if(playerBooleanHashMap.get(direction)) {
            walkingOnDirtyPE.draw(spriteBatch);
        }

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
        // If the player is not walking, render static frame
        if(!playerBooleanHashMap.get(direction)) {
            playerAnimationHashMap.get(direction).draw(spriteBatch, hitbox.getX()+xOffset, hitbox.getY()+yOffset, spriteWidth, spriteHeight);
        }
    }

    // x and y are pointer touch/click coordinates
    public void updateTouchDown(float x, float y) {
        playerBooleanHashMap.put("isTouchedDown", true);
        pointerX = x;
        pointerY = y;
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

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public void update() {
        for(Map.Entry<String, MyAnimation> entry : playerAnimationHashMap.entrySet()) {
            // If the player is walking into direction
            if(entry.getKey().equals(direction) && !playerBooleanHashMap.get(direction)) {
            } else {
                playerAnimationHashMap.get(entry.getKey()).update();
            }
        }
        // Update walking on dirty particle
        walkingOnDirtyPE.update(Gdx.graphics.getDeltaTime());
        if(walkingOnDirtyPE.isComplete() && playerBooleanHashMap.get(direction)) {
            walkingOnDirtyPE.reset();
        }

        updatePosition();
        calculateAngle();
    }

    public void calculateAngle() {
        // If touch/pointer is at clicked (or at screen) or is being dragged across the screen
        if(playerBooleanHashMap.get("isTouchedDown")) {

        }
    }

    public void drawParticles(SpriteBatch spriteBatch) {
        if(playerBooleanHashMap.get(direction)) {
            walkingOnDirtyPE.draw(spriteBatch);
        }
    }

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
        walkingOnDirtyPE.setPosition(hitbox.getCenterX(), hitbox.getY()-5);
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
    }

    public void updatePlayerKeydown(float keycode) {
        if(keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            playerBooleanHashMap.put("playerWalkLeft", true);
        }
        if(keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            playerBooleanHashMap.put("playerWalkUp", true);
        }
        if(keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            playerBooleanHashMap.put("playerWalkDown", true);
        }
        if(keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            playerBooleanHashMap.put("playerWalkRight", true);
        }
    }

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

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}
