package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenTargetStatus {
	Rectangle position;
	float hpPercentage;
	float shieldPercentage;
	Enemie target;
	StaticText targetName;
	Sprite blackBar, greenBar, blueBar;
	final float barHeight = 6;

	public ScreenTargetStatus(Rectangle position, Texture[] hpBarTextureArray) {
		this.position = position;
		blackBar = new Sprite(hpBarTextureArray[0]);
		greenBar = new Sprite(hpBarTextureArray[1]);
		blueBar = new Sprite(hpBarTextureArray[2]);
	}

	public void draw(SpriteBatch spriteBatch, BitmapFont font) {
		spriteBatch.setColor(1, 1, 1, 0.5f);
		spriteBatch.draw(blackBar, position.getX(), position.getY(), (position.getWidth()), barHeight);
		spriteBatch.draw(greenBar, position.getX(), position.getY(), (position.getWidth())*(hpPercentage), barHeight);
		spriteBatch.draw(blueBar, position.getX(), position.getY(), (position.getWidth())*(shieldPercentage), barHeight);
		spriteBatch.setColor(1, 1, 1, 1);
		if(targetName != null) {
			targetName.draw(spriteBatch, font);

		}
	}

	public void updatePosition(float x, float y) {
		position.setX(x);
		position.setY(y);
		if(targetName != null) {
			targetName.setX(x+position.getWidth()/2);
			targetName.setY(y-20);
		}
	}

	public void update() {
		if(target != null) {
			hpPercentage = target.getCurrentHp()/target.getMaximumHp();
			if(hpPercentage < 0) hpPercentage = 0;
			shieldPercentage = target.getCurrentShield()/target.getMaximumShield();
			if(shieldPercentage < 0) shieldPercentage = 0;
			if(target.getCurrentHp() <= 0) {
				target = null;
				targetName = null;
			}
		}
	}

	public Rectangle getPosition() {
		return position;
	}

	public void setPosition(Rectangle position) {
		this.position = position;
	}

	public Enemie getTarget() {
		return target;
	}

	public void setTarget(Enemie target) {
		this.target = target;
		targetName = new StaticText(target.getName(), position.getX()+position.getWidth()/2, position.getY() - 20, true);
	}

	public StaticText getTargetName() {
		return targetName;
	}

	public void setTargetName(StaticText targetName) {
		this.targetName = targetName;
	}
}