package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.Map;

public class Player {
    HashMap<String, MyAnimation> playerAnimationsHashMap;
    Rectangle hitbox;
    Sprite hitboxSprite;
    float moveSpeed;
    HashMap<String, Boolean> playerBooleanHashMap;

    public Player(Rectangle hitbox, float moveSpeed) {
        this.hitbox = hitbox;
        this.moveSpeed = moveSpeed;
        // Player hitbox sprite
        hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));
        // Player animations hash map
        playerAnimationsHashMap = new HashMap<String, MyAnimation>();
        playerAnimationsHashMap.put("playerAttackLeft", new MyAnimation(new Texture("characters/knight_attack_layout_2x_shadows.png"), 4, 2, 0.4f));
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        playerBooleanHashMap = createPlayerBooleanHashMap();
    }

    public HashMap createPlayerBooleanHashMap() {
        playerBooleanHashMap = new HashMap<String, Boolean>();
        playerBooleanHashMap.put("isMovingLeft", false);
        playerBooleanHashMap.put("isMovingRight", false);
        playerBooleanHashMap.put("isMovingUp", false);
        playerBooleanHashMap.put("isMovingDown", false);
        return playerBooleanHashMap;
    }

    public void draw(SpriteBatch spriteBatch) {
        hitboxSprite.draw(spriteBatch);
        for(Map.Entry<String, MyAnimation> entry : playerAnimationsHashMap.entrySet()) {
            playerAnimationsHashMap.get(entry.getKey()).draw(spriteBatch, hitbox.getX(), hitbox.getY());
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public void update() {
        for(Map.Entry<String, MyAnimation> entry : playerAnimationsHashMap.entrySet()) {
            playerAnimationsHashMap.get(entry.getKey()).update();
        }
        updatePosition();
    }

    public void updatePosition() {
        if(playerBooleanHashMap.get("isMovingLeft")) {
            hitbox.setX(hitbox.getX() - moveSpeed);
        }
        if(playerBooleanHashMap.get("isMovingRight")) {
            hitbox.setX(hitbox.getX() + moveSpeed);
        }
        if(playerBooleanHashMap.get("isMovingUp")) {
            hitbox.setY(hitbox.getY() + moveSpeed);
        }
        if(playerBooleanHashMap.get("isMovingDown")) {
            hitbox.setY(hitbox.getY() - moveSpeed);
        }
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
    }

    public void updatePlayerKeydown(float keycode) {
        if(keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            playerBooleanHashMap.put("isMovingLeft", true);
        }
        if(keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            playerBooleanHashMap.put("isMovingUp", true);
        }
        if(keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            playerBooleanHashMap.put("isMovingDown", true);
        }
        if(keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            playerBooleanHashMap.put("isMovingRight", true);
        }
    }

    public void updatePlayerKeyup(float keycode) {
        if(keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            playerBooleanHashMap.put("isMovingLeft", false);
        }
        if(keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            playerBooleanHashMap.put("isMovingUp", false);
        }
        if(keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            playerBooleanHashMap.put("isMovingDown", false);
        }
        if(keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            playerBooleanHashMap.put("isMovingRight", false);
        }
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}
