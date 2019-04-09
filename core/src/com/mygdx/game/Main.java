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
import java.util.HashMap;
import java.util.Iterator;

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

		// Initialize frame buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		// Create and configure camera
		camera=new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		// Initialize sprite batch
		spriteBatch=new SpriteBatch();

		lightArrayList.add(new Light(new Sprite(new Texture("textures/fire_light.png")), new Rectangle(0, 0, 400, 400)));
		lightArrayList.add(new Light(new Sprite(new Texture("textures/blue_light.png")), new Rectangle(600, 200, 70, 70)));

		background =new Texture("textures/background.png");

		player = new Player(new Rectangle(170, 170, 24, 40), 2);

		// Day night cycle starts at 12:00 A.M.
		dayNightCycle = new DayNightCycle(12, 0);

		// Projectile list
		projectileList = new ArrayList<Bullet>();

		// Enemie list
		enemieList = new ArrayList<Enemie>();

		// Fill enemie list
		fillEnemieList();

		Gdx.input.setInputProcessor(this);
	}

	public void fillEnemieList() {
		Texture projectileShadowTexture = new Texture("projectiles/shadow_projectile.png");
		Rectangle blueSlimeRectangle = new Rectangle(600, 200, 25, 20);
		float moveSpeed = 0.7f;
		String spriteSheetPath = "characters/enemies/blue_slime.png";
		String projectileTexturePath = "projectiles/blue_slime_projectile.png";
		String walkingPEPath = "textures/walking_on_dirty_particles.pe";
		String walkingPEFolder = "textures";
		float walkingFrameDuration = 0.3f;
		Enemie blueSlime = new Enemie(blueSlimeRectangle, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, walkingPEFolder, walkingFrameDuration);
		// Enemie sprite positioning
		blueSlime.setSpriteWidth(100);
		blueSlime.setSpriteHeight(100);
		blueSlime.setxOffset(-37.5f);
		blueSlime.setyOffset(-7);
		blueSlime.setParticleEffectScale(0.6f);
		// AI properties
		// 1: Hostile random Walking AI: the enemie will either stay idle or walk around randomly
		blueSlime.setEnemieId(1000);
		blueSlime.setAiType(1);
		blueSlime.setHostileRange(200f);
		blueSlime.setPlayer(player);
		blueSlime.setAiTimeToAction(0.25f);
		blueSlime.setAiTimeToChangeDecision(0.9f);
		// Rotate projectile
		blueSlime.setAttackDelay(2f);
		blueSlime.setRotateProjectile(true);
		blueSlime.setProjectileWidth(16);
		blueSlime.setProjectileHeight(2);
		blueSlime.setProjectileLifeTime(1);
		blueSlime.setProjectileSpeed(1.5f);
		blueSlime.setProjectileShadowTexture(projectileShadowTexture);
		enemieList.add(blueSlime);


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
			if(projectileListIterator.next().isDead()) {
				projectileListIterator.remove();
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
			enemie.update(projectileList);
		}
		// Update player
		player.update(projectileList);
		// Update camera
		camera.position.set(player.getHitbox().getCenterX(), player.getHitbox().getCenterY(), 0);
		camera.update();

	}

	// Draw player, enemies, projectiles, npcs, map, and so forth
	public void drawObjects(SpriteBatch spriteBatch) {
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		// Draw background
		spriteBatch.draw(background,0,0);
		// Draw projectiles on screen
		for(Bullet projectile : projectileList) {
			projectile.draw(spriteBatch);
		}
		// Draw enemies
		for(Enemie enemie : enemieList) {
			enemie.draw(spriteBatch);
		}
		// Draw player
		player.draw(spriteBatch);


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
