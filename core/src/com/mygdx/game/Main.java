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

		player = new Player(new Rectangle(170, 170, 40, 40), 2);

		// Day night cycle starts at 12:00 A.M.
		dayNightCycle = new DayNightCycle(12, 0);

		Gdx.input.setInputProcessor(this);
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

		// Update player, map, enemies, etc
		updateObjects(spriteBatch);

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
	}

	public void updateLights() {
		for(Light light : lightArrayList) {
			light.update();
		}
	}

	public void updateObjects(SpriteBatch spriteBatch) {
		dayNightCycle.updateTime();
		dayNightCycle.updateRGBValues();
		player.update();
		// Update camera
		camera.position.set(player.getHitbox().getCenterX(), player.getHitbox().getCenterY(), 0);
		camera.update();

	}

	// Player, enemies, map character
	public void drawObjects(SpriteBatch spriteBatch) {
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		spriteBatch.draw(background,0,0);
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
