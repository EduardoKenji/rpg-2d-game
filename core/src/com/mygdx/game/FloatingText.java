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
	final float incrementSpeed = 1.8f;
	final float maxHeightOffset = 50;
	float maxHeight;
	// Check if floating text should be removed from render
	boolean dead;
	Color textColor;

	public FloatingText(String text, float x, float y) {
		this.text = text;
		this.x = x;
		this.y = y;
		maxHeight = y+maxHeightOffset;
		dead = false;
		if(glyphLayout == null) {
			glyphLayout = new GlyphLayout();
		}
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
		float fpsIncrementSpeed = incrementSpeed * (Gdx.graphics.getDeltaTime() * 60);
		y += fpsIncrementSpeed;
		if(y > maxHeight) {
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
