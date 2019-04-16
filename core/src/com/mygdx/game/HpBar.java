package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HpBar {
    Rectangle position;
    float hpPercentage;
    float shieldPercentage;
    Sprite blackBar, healthBar, shieldBar;
    float difWidth, halfDifWidth;
    final float barHeight = 6;

    public HpBar(Rectangle position, Texture[] hpBarTextureArray) {
        this.position = position;
        blackBar = new Sprite(hpBarTextureArray[0]);
        healthBar = new Sprite(hpBarTextureArray[1]);
        shieldBar = new Sprite(hpBarTextureArray[2]);
        float minimumWidth = 22;
        if(position.width < minimumWidth) {
            difWidth = minimumWidth - position.width;
            halfDifWidth = difWidth/2;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.setColor(1, 1, 1 , 0.8f);
        spriteBatch.draw(blackBar, position.getX()-halfDifWidth, position.getY(), (position.getWidth()+difWidth), barHeight);
        spriteBatch.draw(healthBar, position.getX()-halfDifWidth, position.getY(), (position.getWidth()+difWidth)*(hpPercentage), barHeight);
		spriteBatch.setColor(1, 1, 1 , 0.6f);
        spriteBatch.draw(shieldBar, position.getX()-halfDifWidth, position.getY(), (position.getWidth()+difWidth)*(shieldPercentage), barHeight);
        spriteBatch.setColor(1, 1, 1 , 1);
    }

    public void updatePosition(float x, float y) {
        position.setX(x);
        position.setY(y);
    }

    public void update(float currentHp, float maximumHp, float currentShield, float maximumShield) {
        hpPercentage = currentHp/maximumHp;
        if(hpPercentage < 0) hpPercentage = 0;
        shieldPercentage = currentShield/maximumHp;
        if(shieldPercentage < 0) shieldPercentage = 0;
    }

    public Rectangle getPosition() {
        return position;
    }

    public void setPosition(Rectangle position) {
        this.position = position;
    }
}
