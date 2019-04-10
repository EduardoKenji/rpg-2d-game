package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HpBar {
    Rectangle position;
    float hpPercentage;
    float shieldPercentage;
    Sprite blackBar, greenBar, blueBar;
    float difWidth, halfDifWidth;

    public HpBar(Rectangle position, Texture[] hpBarTextureArray) {
        this.position = position;
        blackBar = new Sprite(hpBarTextureArray[0]);
        greenBar = new Sprite(hpBarTextureArray[1]);
        blueBar = new Sprite(hpBarTextureArray[2]);
        float minimumWidth = 22;
        if(position.width < minimumWidth) {
            difWidth = minimumWidth - position.width;
            halfDifWidth = difWidth/2;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(blackBar, position.getX()-halfDifWidth, position.getY(), position.getWidth()+difWidth, 5);
        spriteBatch.draw(greenBar, position.getX()-halfDifWidth, position.getY(), position.getWidth()*(hpPercentage)+difWidth, 5);
        spriteBatch.draw(blueBar, position.getX()-halfDifWidth, position.getY(), position.getWidth()*(shieldPercentage)+difWidth, 5);
    }

    public void updatePosition(float x, float y) {
        position.setX(x);
        position.setY(y);
    }

    public void update(float currentHp, float maximumHp, float currentShield, float maximumShield) {
        hpPercentage = currentHp/maximumHp;
        shieldPercentage = currentShield/maximumShield;
    }

    public Rectangle getPosition() {
        return position;
    }

    public void setPosition(Rectangle position) {
        this.position = position;
    }
}
