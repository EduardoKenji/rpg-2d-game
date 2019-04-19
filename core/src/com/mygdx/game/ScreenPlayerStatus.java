package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenPlayerStatus {
	static GlyphLayout glyphLayout;
	float hpPercentage;
	float shieldPercentage;
	float manaPercentage;
	Rectangle position;
	Sprite blackBar, healthBar, shieldBar, manaBar;
	Player player;
	StaticText hpText;
	StaticText manaText;

	public ScreenPlayerStatus(Rectangle position, Texture[] hpBarTextureArray, Player player) {
		this.position = position;
		blackBar = new Sprite(hpBarTextureArray[0]);
		healthBar = new Sprite(hpBarTextureArray[1]);
		shieldBar = new Sprite(hpBarTextureArray[2]);
		manaBar = new Sprite(hpBarTextureArray[3]);
		hpPercentage = 1;
		manaPercentage = 1;
		this.player = player;
		if(glyphLayout == null) {
			glyphLayout = new GlyphLayout();
		}
		float xOffset = 6;
		float yOffset = 19;
		hpText = new StaticText((int)player.getPlayerClass().getCurrentHp()+"", position.getX()+xOffset, position.getY()+position.getHeight()+yOffset, false);
		yOffset = -6;
		manaText = new StaticText((int)player.getPlayerClass().getCurrentMana()+"", position.getX()+xOffset, position.getY()+position.getHeight()+yOffset, false);
	}

	public void draw(SpriteBatch spriteBatch, BitmapFont hpFont, BitmapFont manaFont) {
		spriteBatch.setColor(1, 1, 1 , 0.4f);
		spriteBatch.draw(blackBar, position.getX(), position.getY()+position.getHeight(), (position.getWidth()), position.getHeight());
		spriteBatch.draw(healthBar, position.getX(), position.getY()+position.getHeight(), (position.getWidth())*(hpPercentage), position.getHeight());
		spriteBatch.setColor(1, 1, 1 , 0.3f);
		spriteBatch.draw(shieldBar, position.getX(), position.getY()+position.getHeight(), (position.getWidth())*(shieldPercentage), position.getHeight());


		spriteBatch.setColor(1, 1, 1 , 0.4f);
		spriteBatch.draw(blackBar, position.getX(), position.getY(), (position.getWidth()), position.getHeight());
		spriteBatch.draw(manaBar, position.getX(), position.getY(), (position.getWidth())*(manaPercentage), position.getHeight());

		spriteBatch.setColor(1, 1, 1 , 1);

		hpText.draw(spriteBatch, hpFont);
		manaText.draw(spriteBatch, manaFont);
	}

	public void update() {
		if(player != null) {
			hpPercentage = player.getPlayerClass().getCurrentHp()/player.getPlayerClass().getMaximumHp();
			if(hpPercentage < 0) hpPercentage = 0;
			shieldPercentage = player.getPlayerClass().getCurrentShield()/player.getPlayerClass().getMaximumShield();
			if(shieldPercentage < 0) shieldPercentage = 0;
			manaPercentage = player.getPlayerClass().getCurrentMana()/player.getPlayerClass().getMaximumMana();
			if(manaPercentage < 0) manaPercentage = 0;

			hpText.setText((int)player.getPlayerClass().getCurrentHp()+"");
			manaText.setText((int)player.getPlayerClass().getCurrentMana()+"");
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
