package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class MapObject extends ZOrderableSprite {
    Texture objectTexture;
    Rectangle hitbox;
    // Coordinates to draw gameMap object
    float x, y, width, height;
    // Y coord offset, different for each object
    float yOffset;
    // Debug hitbox sprite
    Sprite hitboxSprite = new Sprite(new Texture("textures/hitbox.png"));
    ParticleEffect particleEffect;
    float timer;
    float timeToResetPE;
    Light light;

    public MapObject(Texture objectTexture, float x, float y, float width, float height) {
        super(y);
        this.objectTexture = objectTexture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        timer = 0;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(objectTexture, x, y+yOffset, width, height);
        if(hitbox != null) {
            //hitboxSprite.draw(spriteBatch);
        }
        if(particleEffect != null) {
            particleEffect.draw(spriteBatch);
        }
    }

    // Update particle effects
    public void update() {
        if(particleEffect != null) {
        	timer += Gdx.graphics.getDeltaTime();
            particleEffect.update(Gdx.graphics.getDeltaTime());
            if(particleEffect.isComplete() || timer >= timeToResetPE) {
				particleEffect.reset();
				timer = 0;
			}
        }
    }

    public void createParticleEffect(String PEpath, String PEFolder, float x, float y) {
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal(PEpath), Gdx.files.internal(PEFolder));
        particleEffect.setPosition(x, y);
        float randomOffsetToFinish = (float)(Math.random()*1500)+150;
        timeToResetPE = (particleEffect.getEmitters().get(0).getDuration().getLowMin() - randomOffsetToFinish)/1000f;
        System.out.println(particleEffect.getEmitters().get(0).getDuration().getLowMin());
		System.out.println(timeToResetPE);
    }

    public void createLight(String lightTexture, ArrayList<Light> lightArrayList, float yOffset) {
		light = new Light(new Sprite(new Texture(lightTexture)), new Rectangle(x, y+yOffset, width, height));
		lightArrayList.add(light);
	}

    public Texture getObjectTexture() {
        return objectTexture;
    }

    public void setObjectTexture(Texture objectTexture) {
        this.objectTexture = objectTexture;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle hitbox) {
        hitboxSprite.setPosition(hitbox.getX(), hitbox.getY());
        hitboxSprite.setSize(hitbox.getWidth(), hitbox.getHeight());
        this.hitbox = hitbox;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public Sprite getHitboxSprite() {
        return hitboxSprite;
    }

    public void setHitboxSprite(Sprite hitboxSprite) {
        this.hitboxSprite = hitboxSprite;
    }
}
