package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StaticText {
	// Global variable to check text size across all static texts instances
	static GlyphLayout glyphLayout;
	// Static text string
	String text;
	// Reference position
	float x, y;
	// Check if floating text should be removed from render
	boolean dead;
	Color textColor;
	boolean glyphLayoutUsed;

	public StaticText(String text, float x, float y, boolean glyphLayoutUsed) {
		this.text = text;
		this.x = x;
		this.y = y;
		dead = false;
		this.glyphLayoutUsed = glyphLayoutUsed;
		if(glyphLayout == null) {
			glyphLayout = new GlyphLayout();
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	float halfWidth;

	public void draw(SpriteBatch spriteBatch, BitmapFont font) {
		glyphLayout.setText(font, text);
		if(glyphLayoutUsed) {
			halfWidth = glyphLayout.width/2;
		} else {
			halfWidth = 0;
		}

		if(textColor != null) {
			Color previousColor = font.getColor();
			font.setColor(textColor);
			font.draw(spriteBatch, text, x-halfWidth, y);
			font.setColor(previousColor);
		} else {
			font.setColor(Color.WHITE);
			font.draw(spriteBatch, text, x-halfWidth, y);
		}
	}


}
