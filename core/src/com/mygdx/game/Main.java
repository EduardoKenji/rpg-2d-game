package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main extends ApplicationAdapter implements InputProcessor {

	// Frame buffer to update and draw lights
	FrameBuffer frameBuffer;
	// Spritebatch to draw sprites, texts (with fonts), textures, and so forth
	SpriteBatch spriteBatch;
	// Camera to follow the player
	OrthographicCamera camera;
	// Textures
	Texture background;
	// Class that takes care of day-time calculations
	DayNightCycle dayNightCycle;
	// Player
	Player player;
	// Font used to draw text on screen
	BitmapFont font;
	// Map with colors (k: Color name, v: RGBA values)
	HashMap<String, Color> colorHashMap;
	// Array list with lights
	ArrayList<Light> lightArrayList;
	// Array list with bullets (projectiles from player, monsters, npcs, and so forth)
	ArrayList<Bullet> projectileList;
	// Array list with enemies
	ArrayList<Enemie> enemieList;
	// Enemie dictionary
	HashMap<String, Enemie> enemieDict;
	// Static map objects list (such as trees and houses
	ArrayList<MapObject> mapObjectList;
	// ZOrderableSprite list: All sprites from static and dynamic (player, enemies, items, projectiles) ordered according to y value
	ArrayList<ZOrderableSprite> zOrderableSpriteList;
	// Comparator used to compare the Z axis for sprites via Y coordinate comparisons
	ZAxisComparator zAxisComparator;
 	// Font size
	final int FONT_SIZE = 30;

	@Override
	public void create() {

		// Create and instantiate color hash map
		colorHashMap = createColorHashMap();

		// Create font
		font = createFont("dungeonFont.ttf", FONT_SIZE);

		// Initialize light array list
		lightArrayList = new ArrayList<Light>();

		// Initialize map object list
		mapObjectList = new ArrayList<MapObject>();

		// Initialize z-orderable sprite list
		zOrderableSpriteList = new ArrayList<ZOrderableSprite>();
		zAxisComparator = new ZAxisComparator();

		// Initialize frame buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		// Create and configure camera
		camera=new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		// Initialize sprite batch
		spriteBatch=new SpriteBatch();

		lightArrayList.add(new Light(new Sprite(new Texture("textures/fire_light.png")), new Rectangle(0, 0, 400, 400)));
		lightArrayList.add(new Light(new Sprite(new Texture("textures/blue_light.png")), new Rectangle(600, 200, 70, 70)));

		background = new Texture("textures/background.png");
		// Debug sprite to test depth calculation for sprites

		// Debug tree to test depth
		MapObject testTree;

		for(int i = 0; i < 40000; i++) {
			testTree = new MapObject(new Texture("test_tree_128.png"), (int)(Math.random()*10000), (int)(Math.random()*10000), 128, 128);
			mapObjectList.add(testTree);
			zOrderableSpriteList.add(testTree);
		}

		player = new Player(new Rectangle(300, 300, 17, 28), 2);
		zOrderableSpriteList.add(player);

		// Day night cycle starts at 12:00 A.M.
		dayNightCycle = new DayNightCycle(12, 0);

		// Projectile list
		projectileList = new ArrayList<Bullet>();

		// Enemie list
		enemieList = new ArrayList<Enemie>();

		// Initiate enemie dictionary
		enemieDict = new HashMap<String, Enemie>();

		// Fill enemie list
		fillEnemieList();

		Gdx.input.setInputProcessor(this);
	}

	public void fillEnemieList() {
		Texture projectileShadowTexture = new Texture("projectiles/shadow_projectile.png");

		// Small blue slime
		Rectangle hitbox = new Rectangle(600, 200, 25, 20);
		float moveSpeed = 0.7f;
		String spriteSheetPath = "characters/enemies/blue_slime.png";
		String projectileTexturePath = "projectiles/blue_slime_projectile.png";
		String walkingPEPath = "textures/walking_on_dirty_particles.pe";
		String walkingPEFolder = "textures";
		float walkingFrameDuration = 0.3f;
		Enemie enemie = new Enemie(hitbox, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, walkingPEFolder, walkingFrameDuration);
		// Enemie sprite positioning
		enemie.setSpriteWidth(100);
		enemie.setSpriteHeight(100);
		enemie.setxOffset(-37.5f);
		enemie.setyOffset(-8);
		enemie.setParticleEffectScale(0.6f);
		// AI properties
		// 1: Hostile random walking AI: the enemie will either stay idle or walk around randomly
		enemie.setEnemieId(1000);
		enemie.setAiType(1);
		enemie.setHostileRange(200f);
		enemie.setPlayer(player);
		enemie.setAiTimeToAction(0.25f);
		enemie.setAiTimeToChangeDecision(0.9f);
		// Projectile properties
		// 2: 3 bullets with 15 degree distance between
		enemie.setShootingPattern(0);
		enemie.setRotateProjectile(true);
		enemie.setAttackDelay(2f);
		enemie.setProjectileWidth(16);
		enemie.setProjectileHeight(2);
		enemie.setProjectileLifeTime(1);
		enemie.setProjectileSpeed(1.5f);
		enemie.setProjectileShadowTexture(projectileShadowTexture);
		enemieDict.put("blueSlime", enemie);
		enemieList.add(enemie);
		zOrderableSpriteList.add(enemie);

		// Mother blue slime
		hitbox = new Rectangle(600, 200, 50, 40);
		moveSpeed = 0.7f;
		spriteSheetPath = "characters/enemies/blue_slime.png";
		projectileTexturePath = "projectiles/blue_slime_projectile.png";
		walkingPEPath = "textures/walking_on_dirty_particles.pe";
		walkingPEFolder = "textures";
		walkingFrameDuration = 0.3f;
		enemie = new Enemie(hitbox, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, walkingPEFolder, walkingFrameDuration);
		// Enemie sprite positioning
		enemie.setSpriteWidth(200);
		enemie.setSpriteHeight(200);
		enemie.setxOffset(-75);
		enemie.setyOffset(-16);
		enemie.setParticleEffectScale(1f);
		// AI properties
		// 2: Hostile spawner random walking AI: the enemie will either stay idle or walk around randomly
		enemie.setEnemieId(1000);
		enemie.setAiType(1);
		enemie.setHostileRange(250f);
		enemie.setPlayer(player);
		enemie.setAiTimeToAction(0.25f);
		enemie.setAiTimeToChangeDecision(0.9f);
		enemie.setAiTimeToSpawn(1f);
		enemie.setMaxMinions(5);
		enemie.setSpawnedEnemie(enemieDict.get("blueSlime"));
		// Projectile properties
		// 1: 3 bullets with 15 degree distance between
		enemie.setShootingPattern(1);
		enemie.setAttackDelay(2f);
		enemie.setRotateProjectile(true);
		enemie.setProjectileWidth(28);
		enemie.setProjectileHeight(3.5f);
		enemie.setProjectileLifeTime(1.2f);
		enemie.setProjectileSpeed(1.5f);
		enemie.setProjectileShadowTexture(projectileShadowTexture);
		for(int i = 0; i < 100; i++) {

		}
		enemieList.add(enemie);
		zOrderableSpriteList.add(enemie);
	}

	// Create and return a new font
	public BitmapFont createFont(String fontName, int fontSize) {
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontName));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = fontSize;
		fontParameter.color = colorHashMap.get("BLUE");
		return fontGenerator.generateFont(fontParameter);
	}

	// Create and return a new color hash map
	HashMap createColorHashMap() {
		HashMap<String, Color> colorHashMapInstance = new HashMap<String, Color>();
		colorHashMapInstance.put("WHITE", new Color(1, 1, 1, 1));
		colorHashMapInstance.put("BLACK", new Color(0, 0, 0, 1));
		colorHashMapInstance.put("BLUE", new Color(0, 0, 1, 1));
		colorHashMapInstance.put("GREEN", new Color(0, 1, 0, 1));
		return colorHashMapInstance;
	}

	@Override
	public void render() {

		// Update player, map, enemies, projectiles, and so forth
		updateObjects();

		// Update lights
		updateLights();

		// Update light frame buffer
		updateLightFrameBuffer(spriteBatch);

		// Draw player, map, enemies and other objects
		drawObjects(spriteBatch);

		// Update sprite batch projection matrix
		spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix().idt());

		// Draw lights
		drawLight(spriteBatch);

		// Remove projectiles
		removeProjectiles();
	}

	public void removeProjectiles() {
		// Projectile list to remove bullets/projectiles without concurrence problems
		Iterator<Bullet> projectileListIterator = projectileList.iterator();
		while(projectileListIterator.hasNext()) {
			Bullet projectile = projectileListIterator.next();
			if(projectile.isDead()) {
				projectileListIterator.remove();
				zOrderableSpriteList.remove(projectile);
			}
		}
	}

	// Update lights
	public void updateLights() {
		for(Light light : lightArrayList) {
			light.update();
		}
	}

	// Update player, map, enemies, projectiles, and so forth
	public void updateObjects() {
		// Update Day/Night cycle
		dayNightCycle.updateTime();
		dayNightCycle.updateRGBValues();
		// Update projectiles
		for(Bullet projectile : projectileList) {
			projectile.update();
		}
		// Update enemies
		for(Enemie enemie : enemieList) {
			enemie.update(projectileList, zOrderableSpriteList);
		}
		// Update player
		player.update(projectileList, zOrderableSpriteList);
		// Update camera
		camera.position.set(player.getHitbox().getCenterX(), player.getHitbox().getCenterY(), 0);
		camera.update();

		// Update sprites in Z order
		Collections.sort(zOrderableSpriteList, zAxisComparator);

		//System.out.println(zOrderableSpriteList.size());
	}

	// Draw player, enemies, projectiles, npcs, map, and so forth
	public void drawObjects(SpriteBatch spriteBatch) {
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		// Draw background
		spriteBatch.draw(background,0,0, 10000, 10000);
		// Draw all static and dynamic map objects and entities
		for(ZOrderableSprite sprite : zOrderableSpriteList) {
			if(euclidianDistance(sprite.getX(), sprite.getY(), player.getHitbox().getCenterX(), player.getHitbox().getCenterY()) < 600) {
				sprite.draw(spriteBatch);
			}
		}
		spriteBatch.end();
	}

	public void updateLightFrameBuffer(SpriteBatch spriteBatch) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		frameBuffer.begin();

		Gdx.gl.glClearColor(dayNightCycle.getRgbValue(),dayNightCycle.getRgbValue(),dayNightCycle.getRgbValue(),1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE);
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		for(Light light : lightArrayList) {
			light.draw(spriteBatch);
		}
		spriteBatch.end();
		frameBuffer.end();
	}

	public void drawLight(SpriteBatch spriteBatch) {
		spriteBatch.setBlendFunction( GL20.GL_ZERO,GL20.GL_SRC_COLOR);
		spriteBatch.begin();

		spriteBatch.draw(frameBuffer.getColorBufferTexture(),-1,1,2,-2);
		spriteBatch.end();
	}

	public float euclidianDistance(float x1, float y1, float x2, float y2) {
		return (float)(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}

	@Override
	public void dispose () {
		frameBuffer.dispose();
		font.dispose();
		spriteBatch.dispose();
		background.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		player.updatePlayerKeydown(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		player.updatePlayerKeyup(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;
		player.updateTouchDown(screenX, screenY);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;
		player.updateTouchUp(screenX, screenY);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenY = Gdx.graphics.getHeight() - screenY;
		player.updateTouchDragged(screenX, screenY);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
