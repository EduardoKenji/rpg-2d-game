package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PlayerClass {
	float frameDuration;
	Texture walkingTexture, attackingTexture, attackingIdleTexture;
	Texture lightTexture;
	String attackParticleEffectPath, attackParticleEffectFolderPath;

	// Player projectiles properties
	int shootingPattern = 0;
	float projectileSpeed, projectileLifeTime;
	float projectileWidth, projectileHeight;
	Texture projectileTexture, projectileShadowTexture;

	int level;
	int experience;
	int baseDamage;
	int minimumRangeOffset, maximumRangeOffset;
	float baseMoveSpeed;
	float currentHp, maximumHp;
	float currentShield, maximumShield;
	float currentMana, maximumMana;

	float attackTimer, attackDelay;

	public PlayerClass(String walkingPath, String attackingPath, String attackingIdlePath, int shootingPattern,
					   String projectileTexturePath, String projectileShadowTexturePath, String lightTexturePath,
					   String attackParticleEffectPath, String attackParticleEffectFolderPath,
					   float projectileSpeed, float projectileLifeTime,
					   float projectileWidth, float projectileHeight,
					   float baseMoveSpeed, int baseDamage,
					   int minimumRangeOffset, int maximumRangeOffset, float attackDelay,
					   float currentHp, float maximumHp,
					   float currentShield, float maximumShield,
					   float currentMana, float maximumMana) {

		this.walkingTexture = new Texture(walkingPath);
		this.attackingTexture = new Texture(attackingPath);
		this.attackingIdleTexture = new Texture(attackingIdlePath);
		if(lightTexturePath != null) this.lightTexture = new Texture(lightTexturePath);
		if(attackParticleEffectPath != null) this.attackParticleEffectPath = attackParticleEffectPath;
		if(attackParticleEffectFolderPath != null) this.attackParticleEffectFolderPath = attackParticleEffectFolderPath;

		this.shootingPattern = shootingPattern;
		this.projectileTexture = new Texture(projectileTexturePath);
		this.projectileShadowTexture = new Texture(projectileShadowTexturePath);
		this.projectileSpeed = projectileSpeed;
		this.projectileLifeTime = projectileLifeTime;
		this.projectileWidth = projectileWidth;
		this.projectileHeight = projectileHeight;
		this.baseMoveSpeed = baseMoveSpeed;
		this.baseDamage = baseDamage;
		this.attackDelay = attackDelay;
		this.maximumRangeOffset = maximumRangeOffset;
		this.minimumRangeOffset = minimumRangeOffset;
		this.currentHp = currentHp;
		this.maximumHp = maximumHp;
		this.currentShield = currentShield;
		this.maximumShield = maximumShield;
		this.currentMana = currentMana;
		this.maximumMana = maximumMana;

		// Player spawns with a normal attack ready
		attackTimer = attackDelay;
		// Player spawns with 0 exp and level 0
		experience = 0;
		level = 0;
		// Animation delay to change frames
		frameDuration = 0.02f;
	}

	public String getAttackParticleEffectPath() {
		return attackParticleEffectPath;
	}

	public void setAttackParticleEffectPath(String attackParticleEffectPath) {
		this.attackParticleEffectPath = attackParticleEffectPath;
	}

	public String getAttackParticleEffectFolderPath() {
		return attackParticleEffectFolderPath;
	}

	public void setAttackParticleEffectFolderPath(String attackParticleEffectFolderPath) {
		this.attackParticleEffectFolderPath = attackParticleEffectFolderPath;
	}

	public Texture getLightTexture() {
		return lightTexture;
	}

	public void setLightTexture(Texture lightTexture) {
		this.lightTexture = lightTexture;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getMinimumRangeOffset() {
		return minimumRangeOffset;
	}

	public void setMinimumRangeOffset(int minimumRangeOffset) {
		this.minimumRangeOffset = minimumRangeOffset;
	}

	public int getMaximumRangeOffset() {
		return maximumRangeOffset;
	}

	public void setMaximumRangeOffset(int maximumRangeOffset) {
		this.maximumRangeOffset = maximumRangeOffset;
	}

	public float getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(float currentHp) {
		this.currentHp = currentHp;
	}

	public float getMaximumHp() {
		return maximumHp;
	}

	public void setMaximumHp(float maximumHp) {
		this.maximumHp = maximumHp;
	}

	public float getCurrentShield() {
		return currentShield;
	}

	public void setCurrentShield(float currentShield) {
		this.currentShield = currentShield;
	}

	public float getMaximumShield() {
		return maximumShield;
	}

	public void setMaximumShield(float maximumShield) {
		this.maximumShield = maximumShield;
	}

	public float getCurrentMana() {
		return currentMana;
	}

	public void setCurrentMana(float currentMana) {
		this.currentMana = currentMana;
	}

	public float getMaximumMana() {
		return maximumMana;
	}

	public void setMaximumMana(float maximumMana) {
		this.maximumMana = maximumMana;
	}

	public float getAttackTimer() {
		return attackTimer;
	}

	public void setAttackTimer(float attackTimer) {
		this.attackTimer = attackTimer;
	}

	public float getAttackDelay() {
		return attackDelay;
	}

	public void setAttackDelay(float attackDelay) {
		this.attackDelay = attackDelay;
	}

	public float getBaseMoveSpeed() {
		return baseMoveSpeed;
	}

	public void setBaseMoveSpeed(float baseMoveSpeed) {
		this.baseMoveSpeed = baseMoveSpeed;
	}

	public float getFrameDuration() {
		return frameDuration;
	}

	public void setFrameDuration(float frameDuration) {
		this.frameDuration = frameDuration;
	}

	public Texture getWalkingTexture() {
		return walkingTexture;
	}

	public void setWalkingTexture(Texture walkingTexture) {
		this.walkingTexture = walkingTexture;
	}

	public Texture getAttackingTexture() {
		return attackingTexture;
	}

	public void setAttackingTexture(Texture attackingTexture) {
		this.attackingTexture = attackingTexture;
	}

	public Texture getAttackingIdleTexture() {
		return attackingIdleTexture;
	}

	public void setAttackingIdleTexture(Texture attackingIdleTexture) {
		this.attackingIdleTexture = attackingIdleTexture;
	}

	public int getShootingPattern() {
		return shootingPattern;
	}

	public void setShootingPattern(int shootingPattern) {
		this.shootingPattern = shootingPattern;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	public float getProjectileLifeTime() {
		return projectileLifeTime;
	}

	public void setProjectileLifeTime(float projectileLifeTime) {
		this.projectileLifeTime = projectileLifeTime;
	}

	public float getProjectileWidth() {
		return projectileWidth;
	}

	public void setProjectileWidth(float projectileWidth) {
		this.projectileWidth = projectileWidth;
	}

	public float getProjectileHeight() {
		return projectileHeight;
	}

	public void setProjectileHeight(float projectileHeight) {
		this.projectileHeight = projectileHeight;
	}

	public Texture getProjectileTexture() {
		return projectileTexture;
	}

	public void setProjectileTexture(Texture projectileTexture) {
		this.projectileTexture = projectileTexture;
	}

	public Texture getProjectileShadowTexture() {
		return projectileShadowTexture;
	}

	public void setProjectileShadowTexture(Texture projectileShadowTexture) {
		this.projectileShadowTexture = projectileShadowTexture;
	}

	public int getBaseDamage() {
		return baseDamage;
	}

	public void setBaseDamage(int baseDamage) {
		this.baseDamage = baseDamage;
	}
}
