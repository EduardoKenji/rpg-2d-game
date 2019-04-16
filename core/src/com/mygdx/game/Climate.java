package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Climate {
	ParticleEffect snow;
	String mode;
	Climate(String mode, float x, float y) {
		this.mode = mode;
		if(mode.equals("snow") && snow == null) {
			snow = new ParticleEffect();
			snow.load(Gdx.files.internal("particle_effects/snow_v2.pe"), Gdx.files.internal("particle_effects"));
		}
		snow.setPosition(x, y);
	}

	public void update(float x, float y) {
		if(mode.equals("snow")) {
			snow.setPosition(x, y);
			snow.update(Gdx.graphics.getDeltaTime());
			if(snow.isComplete()) {
				snow.reset();
			}
		}
	}

	public void draw(SpriteBatch spriteBatch) {
		if(mode.equals("snow")) {
			snow.draw(spriteBatch);
		}
	}
}
