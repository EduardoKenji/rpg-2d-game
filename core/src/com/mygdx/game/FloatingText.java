package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FloatingText {
	// Global variable to check text size across all floating texts instances
	static GlyphLayout glyphLayout;
	// Floating text string
	String text;
	// Reference position
	float x, y;
	// Properties
	float incrementSpeedX, incrementSpeedY;
	final float lifeTime = 0.5f;
	final float baseSpeed = 3;
	float timer;
	// Check if floating text should be removed from render
	boolean dead;
	Color textColor;

	public FloatingText(String text, float x, float y) {
		this.text = text;
		this.x = x;
		this.y = y;
		dead = false;
		timer = 0;
		if(glyphLayout == null) {
			glyphLayout = new GlyphLayout();
		}
		float angle = (float)((Math.random()*40)+70);
		incrementSpeedX = (float)(baseSpeed * Math.cos(Math.toRadians(angle)));
		incrementSpeedY = (float)(baseSpeed * Math.sin(Math.toRadians(angle)));
	}

	public void draw(SpriteBatch spriteBatch, BitmapFont font) {
		glyphLayout.setText(font, text);
		if(textColor != null) {
			Color previousColor = font.getColor();
			font.setColor(textColor);
			font.draw(spriteBatch, text, x-(glyphLayout.width/2), y);
			font.setColor(previousColor);
		} else {
			font.draw(spriteBatch, text, x-(glyphLayout.width/2), y);
		}
	}

	public void update() {
		float fpsIncrementSpeedY = incrementSpeedY * (Gdx.graphics.getDeltaTime() * 60);
		float fpsIncrementSpeedX = incrementSpeedX * (Gdx.graphics.getDeltaTime() * 60);
		x += fpsIncrementSpeedX;
		y += fpsIncrementSpeedY;
		incrementSpeedY -= (Gdx.graphics.getDeltaTime() * 10);
		timer += Gdx.graphics.getDeltaTime();
		if(timer > lifeTime) {
			dead = true;
		}
	}

	public boolean isDead() {
		return dead;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
}
