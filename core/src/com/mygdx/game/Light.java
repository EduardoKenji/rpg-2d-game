package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Light {
    Sprite lightSprite;
    Rectangle rectangleBoundaries;

    float timer;
    final float timeInterval = 0.09f;
    final float animationSpeed = 2;
    float animationAmount = 0;
    final float animationThreshold = 10;
    int mode = 0;

    public Light(Sprite lightSprite, Rectangle rectangleBoundaries) {
        this.lightSprite = lightSprite;
        this.rectangleBoundaries = rectangleBoundaries;
        // Set light sprite to respect rectangle boundaries position
        lightSprite.setPosition(rectangleBoundaries.getX(), rectangleBoundaries.getY());
        lightSprite.setSize(rectangleBoundaries.getWidth(), rectangleBoundaries.getHeight());
    }

    public void draw(SpriteBatch spriteBatch) {
        lightSprite.draw(spriteBatch);
    }

    public void update() {
        timer += Gdx.graphics.getDeltaTime();
        if(timer > timeInterval) {
            if(mode == 0) {
                rectangleBoundaries.setX(rectangleBoundaries.getX() - animationSpeed);
                rectangleBoundaries.setY(rectangleBoundaries.getY() - animationSpeed);
                rectangleBoundaries.setWidth(rectangleBoundaries.getWidth() + (animationSpeed*2));
                rectangleBoundaries.setHeight(rectangleBoundaries.getHeight() + (animationSpeed*2));
                animationAmount += animationSpeed;
                if(animationAmount >= animationThreshold) {
                    animationAmount = 0;
                    mode = 1;
                }
            } else if(mode == 1) {
                rectangleBoundaries.setX(rectangleBoundaries.getX() + animationSpeed);
                rectangleBoundaries.setY(rectangleBoundaries.getY() + animationSpeed);
                rectangleBoundaries.setWidth(rectangleBoundaries.getWidth() - (animationSpeed*2));
                rectangleBoundaries.setHeight(rectangleBoundaries.getHeight() - (animationSpeed*2));
                animationAmount += animationSpeed;
                if(animationAmount >= animationThreshold) {
                    animationAmount = 0;
                    mode = 0;
                }
            }
            timer = 0f;
            lightSprite.setPosition(rectangleBoundaries.getX(), rectangleBoundaries.getY());
            lightSprite.setSize(rectangleBoundaries.getWidth(), rectangleBoundaries.getHeight());
        }
    }

    public Sprite getLightSprite() {
        return lightSprite;
    }

    public void setLightSprite(Sprite lightSprite) {
        this.lightSprite = lightSprite;
    }

    public Rectangle getRectangleBoundaries() {
        return rectangleBoundaries;
    }

    public void setRectangleBoundaries(Rectangle rectangleBoundaries) {
        this.rectangleBoundaries = rectangleBoundaries;
    }
}
