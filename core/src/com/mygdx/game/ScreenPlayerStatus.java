package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ScreenPlayerStatus {
	float hpPercentage;
	float shieldPercentage;
	float manaPercentage;
	Rectangle position;
	Sprite blackBar, healthBar, shieldBar, manaBar;

	public ScreenPlayerStatus(Rectangle position, Texture[] hpBarTextureArray) {
		this.position = position;
		blackBar = new Sprite(hpBarTextureArray[0]);
		healthBar = new Sprite(hpBarTextureArray[1]);
		shieldBar = new Sprite(hpBarTextureArray[2]);
		manaBar = new Sprite(hpBarTextureArray[3]);
	}
}
