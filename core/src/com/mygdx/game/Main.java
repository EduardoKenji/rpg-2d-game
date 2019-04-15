package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
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
	Texture background, ground;
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
	// Floating text list
	ArrayList<FloatingText> floatingTextList;
	// Comparator used to compare the Z axis for sprites via Y coordinate comparisons
	ZAxisComparator zAxisComparator;
	// GameMap to check collisions and some hitboxes
	GameMap gameMap;


	@Override
	public void create() {

		// Create and instantiate color hash map
		colorHashMap = createColorHashMap();

		// Create font
		//font = createFont("dungeon_font.ttf", FONT_SIZE);
		//font.setUseIntegerPositions(false);
		//font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		font = new BitmapFont(Gdx.files.internal("arcade.fnt"), false);
		font.getData().setScale(1, 1);

		// Initialize light array list
		lightArrayList = new ArrayList<Light>();

		// Initialize map object list
		mapObjectList = new ArrayList<MapObject>();

		// Initialize floating text list
		floatingTextList = new ArrayList<FloatingText>();

		// Initialize z-orderable sprite list
		zOrderableSpriteList = new ArrayList<ZOrderableSprite>();
		zAxisComparator = new ZAxisComparator();

		// Initialize frame buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		// Create and configure camera
		camera=new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.viewportHeight = 600;
		camera.viewportWidth = 800;

		// Initialize sprite batch
		spriteBatch=new SpriteBatch();

		lightArrayList.add(new Light(new Sprite(new Texture("textures/fire_light.png")), new Rectangle(0, 0, 400, 400)));
		lightArrayList.add(new Light(new Sprite(new Texture("textures/blue_light.png")), new Rectangle(600, 200, 70, 70)));

		background = new Texture("textures/background.png");
		// Debug sprite to test depth calculation for sprites

		// Create gameMap
		gameMap = new GameMap(1 , 2000, 2000, 0, 0);

		// Fill map object list
		fillMapObjectList();

        Texture hpBarsTextures[] = new Texture[3];
        hpBarsTextures[0] = new Texture("hp_bars/black_bar.png");
        hpBarsTextures[1] = new Texture("hp_bars/green_bar.png");
        hpBarsTextures[2] = new Texture("hp_bars/blue_bar.png");

		player = new Player(new Rectangle(315, 212.5f, 33, 56), 5);
		player.setScreenToViewport(camera.viewportWidth, camera.viewportHeight);
		player.setGettingHitParticleEffectScale(1.3f);
		player.setMapHitbox(new Rectangle(315, 212.5f, 33, 22));
        HpBar hpBar = new HpBar( new Rectangle(315, 205.5f, 33, 56), hpBarsTextures);
        player.setHpBar(hpBar);
        player.setCurrentHp(40);
        player.setMaximumHp(40);
        player.setBaseDamage(20);
		zOrderableSpriteList.add(player);
		// Add player hitbox to the gameMap
		//gameMap.updateHitbox(player.getMapHitbox(), 1);
		//gameMap.updatePlayerPosition(player.getMapHitbox());
		player.setGameMap(gameMap);


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

		for(Enemie enemie: enemieList) {
			enemie.setGameMap(gameMap);
		}

		Gdx.input.setInputProcessor(this);

		// Pixmap debug
		Pixmap pixmap = new Pixmap(new FileHandle("textures/ground.png"));
		System.out.println("Image resolution: "+pixmap.getWidth()+" "+pixmap.getHeight());
		float centerX = pixmap.getWidth()/2;
		float centerY = pixmap.getHeight()/2;
		int i, j, k, l, count = 0;
		int r, g, b, a;
		float maxDistance = Integer.MIN_VALUE;
		float minDistance = Integer.MAX_VALUE;
		for(i = 0; i < pixmap.getHeight(); i++) {
			for(j = 0; j < pixmap.getWidth(); j++) {
				Color color = new Color(pixmap.getPixel(j, i));
				//System.out.println(color.r * (255) + " " + color.g * (255) + " " + color.b * (255) + " " + color.a * (255));
				//count++;
				float euclidianDistance = euclidianDistance(j, i, centerX, centerY);
				float result;
				if(euclidianDistance == 0) {
					result = 1;
				} else {
					result = 1/euclidianDistance;
				}
				if(result > maxDistance) maxDistance = result;
				if(result < minDistance) minDistance = result;
				float variation = 20;
				if(Math.random() < result) {
					int result2 = (int)(Math.random() * 3);
					int squareSize;
					if(euclidianDistance < 200) {
						squareSize = (int)(Math.random()*20+20);
					} else if(euclidianDistance >= 200 && euclidianDistance < 400) {
						squareSize = (int)(Math.random()*6+4);
					} else if(euclidianDistance >= 400 && euclidianDistance < 600) {
						squareSize = (int)(Math.random()*5+2);
					} else {
						squareSize = (int)(Math.random()*5);
					}

					for(k = i; k < i+squareSize; k++) {
						for(l = j; l < j+squareSize; l++) {
							if(result2 == 0 || euclidianDistance < 200) {
								pixmap.setColor(new Color(47f/255f+(float)(Math.random())/variation- (1/variation),
										74f/255f+(float)(Math.random())/variation- (1/variation),
										114f/255f+(float)(Math.random())/variation- (1/variation), 1));
							} else if (result2 == 1){
								pixmap.setColor(new Color(0f/255f+(float)(Math.random())/variation- (1/variation),
										127f/255f+(float)(Math.random())/variation- (1/variation),
										70f/255f+(float)(Math.random())/variation- (1/variation), 1));
							} else {
								pixmap.setColor(new Color(0.5f+(float)(Math.random())/variation- (1/variation),
										0.5f+(float)(Math.random())/variation- (1/variation),
										0.5f+(float)(Math.random())/variation- (1/variation), 1));
							}
							//color = new Color(pixmap.getPixel(l, k));
							pixmap.drawPixel(l, k);
						}
					}
				} else {
					pixmap.setColor(new Color(0, 0, 0, 0));
				}
			}
		}
		System.out.println("Min: "+minDistance+", max: "+maxDistance);
		FileHandle fh = new FileHandle("textures/new_ground.png");
		PixmapIO.writePNG(fh, pixmap);
		pixmap.dispose();


		ground = new Texture("textures/new_ground.png");
	}

	public static void paintWithMossAspect() {
		// Pixmap debug
		Pixmap pixmap = new Pixmap(new FileHandle("C:/cygwin64/home/Eduardo/rpg-2d-game/android/assets/textures/ground.png"));
		System.out.println(pixmap.getWidth()+" "+pixmap.getHeight());
		int i, j, k, l, count = 0;
		int r, g, b, a;
		for(i = 0; i < pixmap.getHeight(); i++) {
			for(j = 0; j < pixmap.getWidth(); j++) {
				Color color = new Color(pixmap.getPixel(j, i));
				if(color.r* (255) == 64.0f && color.g* (255) == 64.0f &&
						color.b* (255) == 64.0f && color.a* (255) == 255.0f) {
					//System.out.println(color.r * (255) + " " + color.g * (255) + " " + color.b * (255) + " " + color.a * (255));
					//count++;
					int result = (int)(Math.random()*6);
					float variation = 20;

					if(result == 1) {
						pixmap.setColor(new Color(0.5f+(float)(Math.random())/variation- (1/variation),
								0.5f+(float)(Math.random())/variation- (1/variation),
								0.5f+(float)(Math.random())/variation- (1/variation), 1));

					} else if (result == 2){
						pixmap.setColor(new Color(0f+(float)(Math.random())/variation,
								0.5f+(float)(Math.random())/variation- (1/variation),
								0.05f+(float)(Math.random())/variation, 1));
					} else if (result == 3){
						pixmap.setColor(new Color(182f/255f+(float)(Math.random())/variation - (1/variation),
								1-(float)(Math.random())/variation,
								0+(float)(Math.random())/variation, 1));
					} else if (result == 4){
						pixmap.setColor(new Color(192f/255f+(float)(Math.random())/variation - (1/variation),
								192f/255f+(float)(Math.random())/variation- (1/variation),
								192f/255f+(float)(Math.random())/variation- (1/variation), 1));
					} else {
						pixmap.setColor(0.25f, 0.25f, 0.25f, 1);
					}
					int squareSize = 2;
					for(k = i; k < i+squareSize; k++) {
						for(l = j; l < j+squareSize; l++) {
							color = new Color(pixmap.getPixel(l, k));
							if(color.r* (255) == 0.0f && color.g* (255) == 0.0f &&
									color.b* (255) == 0.0f && color.a* (255) == 255f) {
								break;
							}
							if(color.a*(255) < 255f) {
								break;
							}
							pixmap.drawPixel(l, k);
						}
					}
					//pixmap.drawPixel(j, i);
					//pixmap.drawPixel(j, i+1);
					//pixmap.drawPixel(j+1, i);
					//pixmap.drawPixel(j+1, i+1);
				}
			}
		}
		System.out.println(count);
		FileHandle fh = new FileHandle("textures/new_ground.png");
		PixmapIO.writePNG(fh, pixmap);
		pixmap.dispose();
	}

	public void fillMapObjectList() {
		// Debug tree to test depth
		MapObject pillar;

		float mapObjectHitboxWidth = 17;
		float mapObjectHitboxHeight = 24;

		pillar = new MapObject(new Texture("textures/new_pillar.png"), 300, 300, 256, 256);
		pillar.setyOffset(-38);
		pillar.setHitbox(new Rectangle(428-mapObjectHitboxWidth/2, 300, mapObjectHitboxWidth, mapObjectHitboxHeight));
		gameMap.updateHitbox(pillar.getHitbox(), 2);
		mapObjectList.add(pillar);
		zOrderableSpriteList.add(pillar);
		for(int i = 0; i < 20; i++) {
			float randomX = (float)(Math.random()*600);
			float randomY = (float)(Math.random()*600);
			pillar = new MapObject(new Texture("textures/new_pillar.png"), randomX, randomY, 256, 256);
			pillar.setyOffset(-38);
			pillar.setHitbox(new Rectangle(randomX+128-mapObjectHitboxWidth/2, randomY, mapObjectHitboxWidth, mapObjectHitboxHeight));
			gameMap.updateHitbox(pillar.getHitbox(), 2);
			mapObjectList.add(pillar);
			zOrderableSpriteList.add(pillar);
		}
	}

	public void fillEnemieList() {
		Texture projectileShadowTexture = new Texture("projectiles/shadow_projectile.png");
		Texture hpBarsTextures[] = new Texture[3];
		hpBarsTextures[0] = new Texture("hp_bars/black_bar.png");
		hpBarsTextures[1] = new Texture("hp_bars/green_bar.png");
		hpBarsTextures[2] = new Texture("hp_bars/blue_bar.png");

		float startY = 400;

		// Small blue slime
		Rectangle hitbox = new Rectangle(600, startY, 25, 20);
		HpBar hpBar = new HpBar( new Rectangle(600, startY-7, 25, 20), hpBarsTextures);
		float moveSpeed = 0.7f;
		String spriteSheetPath = "characters/enemies/blue_slime.png";
		String projectileTexturePath = "projectiles/blue_slime_projectile.png";
		String walkingPEPath = "particle_effects/walking_on_dirty.pe";
		String gettingHitPEPath = "particle_effects/blue_slime_blood.pe";
		// Particles folder
		String particlesFolder = "particle_effects";
		float frameDuration = 0.3f;
		Enemie enemie = new Enemie(hitbox, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, gettingHitPEPath, particlesFolder, frameDuration, 2);
		enemie.setMapHitBox(new Rectangle(600, startY, 25, 5));
		enemie.addHitBox(599, startY, 8, 17);
		enemie.addHitBox(607, startY, 11, 21);
		enemie.addHitBox(618, startY, 8, 17);
		// HP bar and some stats
		enemie.setHpBar(hpBar);
		enemie.setCurrentHp(20);
		enemie.setMaximumHp(20);
		enemie.setBaseDamage(2);
		enemie.setExperience(1);
		// Enemie sprite positioning
		enemie.setSpriteWidth(100);
		enemie.setSpriteHeight(100);
		enemie.setxOffset(-37.5f);
		enemie.setyOffset(-8);
		enemie.setWalkingParticleEffectScale(0.4f);
		enemie.setGettingHitParticleEffectScale(0.4f);
		// AI properties
		// 1: Hostile random walking AI: the enemie will either stay idle or walk around randomly
		enemie.setEnemieId(1000);
		enemie.setAiType(1);
		enemie.setHostileRange(200f);
		enemie.setPlayer(player);
		enemie.setAiTimeToAction(0.25f);
		enemie.setAiTimeToChangeDecision(0.9f);
		// Projectile properties
		// 0: 1 simple bullet
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


		for(int i = 0; i < 2; i++) {
			// Medium blue slime
			hitbox = new Rectangle(600, startY, 50, 40);
			hpBar = new HpBar( new Rectangle(600, startY-7, 50, 40), hpBarsTextures);
			moveSpeed = 0.7f;
			spriteSheetPath = "characters/enemies/blue_slime.png";
			projectileTexturePath = "projectiles/blue_slime_projectile.png";
			walkingPEPath = "particle_effects/walking_on_dirty.pe";
			gettingHitPEPath = "particle_effects/blue_slime_blood.pe";
			particlesFolder = "particle_effects";
			frameDuration = 0.3f;
			enemie = new Enemie(hitbox, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, gettingHitPEPath, particlesFolder, frameDuration, 2);
			enemie.setMapHitBox(new Rectangle(600, startY, 50, 10));
			enemie.addHitBox(600, startY, 14, 28);
			enemie.addHitBox(614, startY, 22, 40);
			enemie.addHitBox(636, startY, 14, 28);
			// HP bar and some stats
			enemie.setHpBar(hpBar);
			enemie.setCurrentHp(50);
            enemie.setMaximumHp(50);
			enemie.setBaseDamage(3);
			enemie.setExperience(3);
			// Enemie sprite positioning
			enemie.setSpriteWidth(200);
			enemie.setSpriteHeight(200);
			enemie.setxOffset(-75);
			enemie.setyOffset(-16);
			enemie.setWalkingParticleEffectScale(1f);
			enemie.setGettingHitParticleEffectScale(1f);
			// AI properties
			// 2: Hostile spawner random walking AI: the enemie will either stay idle or walk around randomly
			enemie.setEnemieId(1001);
			enemie.setAiType(1);
			enemie.setHostileRange(250f);
			enemie.setPlayer(player);
			enemie.setAiTimeToAction(0.25f);
			enemie.setAiTimeToChangeDecision(0.9f);
			enemie.setAiTimeToSpawn(1f);
			enemie.setMaxMinions(1);
			enemie.setSpawnedEnemie(enemieDict.get("blueSlime"));
			// Projectile properties
			// 0: 1 simple bullet
			enemie.setShootingPattern(0);
			enemie.setAttackDelay(2f);
			enemie.setRotateProjectile(true);
			enemie.setProjectileWidth(24);
			enemie.setProjectileHeight(3);
			enemie.setProjectileLifeTime(1.2f);
			enemie.setProjectileSpeed(1.5f);
			enemie.setProjectileShadowTexture(projectileShadowTexture);
			enemieList.add(enemie);
			zOrderableSpriteList.add(enemie);
		}

		// Mother blue slime
		hitbox = new Rectangle(600, startY, 100, 80);
		hpBar = new HpBar( new Rectangle(600, startY-7, 100, 80), hpBarsTextures);
		moveSpeed = 0.7f;
		spriteSheetPath = "characters/enemies/blue_slime.png";
		projectileTexturePath = "projectiles/blue_slime_projectile.png";
		walkingPEPath = "particle_effects/walking_on_dirty.pe";
		gettingHitPEPath = "particle_effects/blue_slime_blood.pe";
		particlesFolder = "particle_effects";
		frameDuration = 0.3f;
		enemie = new Enemie(hitbox, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, gettingHitPEPath, particlesFolder, frameDuration, 2);
		enemie.setMapHitBox(new Rectangle(600, startY, 100, 20));
		enemie.addHitBox(600, startY, 28, 56);
		enemie.addHitBox(628, startY, 44, 80);
		enemie.addHitBox(672, startY, 28, 56);
		// HP bar and some stats
		enemie.setHpBar(hpBar);
		enemie.setCurrentHp(150);
		enemie.setMaximumHp(150);
		enemie.setBaseDamage(6);
		enemie.setExperience(10);
		// Enemie sprite positioning
		enemie.setSpriteWidth(400);
		enemie.setSpriteHeight(400);
		enemie.setxOffset(-150);
		enemie.setyOffset(-32);
		enemie.setWalkingParticleEffectScale(1.7f);
		enemie.setGettingHitParticleEffectScale(1.7f);
		// AI properties
		// 2: Hostile spawner random walking AI: the enemie will either stay idle or walk around randomly
		enemie.setEnemieId(1002);
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
		enemie.setProjectileWidth(40);
		enemie.setProjectileHeight(5);
		enemie.setProjectileLifeTime(1.4f);
		enemie.setProjectileSpeed(1.8f);
		enemie.setProjectileShadowTexture(projectileShadowTexture);
		enemieList.add(enemie);
		zOrderableSpriteList.add(enemie);

		for(int i = 0; i < 2; i++) {
			// Skeleton
			hitbox = new Rectangle(600, startY, 34, 64);
			hpBar = new HpBar( new Rectangle(600, startY-7, 34, 64), hpBarsTextures);
			moveSpeed = 2f;
			spriteSheetPath = "characters/enemies/skeleton.png";
			projectileTexturePath = "projectiles/skeleton_projectile.png";
			walkingPEPath = "particle_effects/walking_on_dirty.pe";
			gettingHitPEPath = "particle_effects/skeleton_blood.pe";
			particlesFolder = "particle_effects";
			frameDuration = 0.02f;
			enemie = new Enemie(hitbox, moveSpeed, spriteSheetPath, projectileTexturePath, walkingPEPath, gettingHitPEPath, particlesFolder, frameDuration, 11);
			enemie.setMapHitBox(new Rectangle(600, startY, 34, 20));
			enemie.addHitBox(600, startY, 34, 64);

			// HP bar and some stats
			enemie.setHpBar(hpBar);
			enemie.setCurrentHp(1000);
			enemie.setMaximumHp(1000);
			enemie.setBaseDamage(10);
			enemie.setExperience(10);
			// Enemie sprite positioning
			enemie.setSpriteWidth(200);
			enemie.setSpriteHeight(200);
			enemie.setxOffset(-83);
			enemie.setyOffset(-17);
			enemie.setWalkingParticleEffectScale(1.7f);
			enemie.setGettingHitParticleEffectScale(1.7f);
			// AI properties
			// 2: Hostile spawner random walking AI: the enemie will either stay idle or walk around randomly
			enemie.setEnemieId(1002);
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
			enemie.setAttackDelay(1.5f);
			enemie.setRotateProjectile(true);
			enemie.setProjectileWidth(40);
			enemie.setProjectileHeight(5);
			enemie.setProjectileLifeTime(1.4f);
			enemie.setProjectileSpeed(2.6f);
			enemie.setProjectileShadowTexture(projectileShadowTexture);
			enemieList.add(enemie);
			zOrderableSpriteList.add(enemie);
		}

	}

	// Create and return a new font
	public BitmapFont createFont(String fontName, int fontSize) {
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontName));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = fontSize;
		fontParameter.borderColor = colorHashMap.get("BLACK");
		fontParameter.borderWidth = 1;
		fontParameter.color = colorHashMap.get("WHITE");
		//fontParameter.genMipMaps = true;
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
	public void resize(int width, int height) {
		player.setScreenToViewport(camera.viewportWidth, camera.viewportHeight);
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
		removeObjects();
	}

	public void removeObjects() {
		// Projectile list to remove bullets/projectiles without concurrence problems
		Iterator<Bullet> projectileListIterator = projectileList.iterator();
		while(projectileListIterator.hasNext()) {
			Bullet projectile = projectileListIterator.next();
			if(projectile.isDead()) {
				projectileListIterator.remove();
				zOrderableSpriteList.remove(projectile);
			}
		}
		// Remove floating texts
		Iterator<FloatingText> floatingTextListIterator = floatingTextList.iterator();
		while(floatingTextListIterator.hasNext()) {
			FloatingText floatingText = floatingTextListIterator.next();
			if(floatingText.isDead()) {
				floatingTextListIterator.remove();
				floatingTextList.remove(floatingText);
			}
		}

		// Remove dead enemies
		Iterator<Enemie> enemieIterator= enemieList.iterator();
		while(enemieIterator.hasNext()) {
			Enemie enemie = enemieIterator.next();
			if(enemie.isDead() && enemie.getGettingHitParticleEffect().isComplete()) {
				enemieIterator.remove();
				zOrderableSpriteList.remove(enemie);
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
		// Update floating texts
		for(FloatingText floatingText : floatingTextList) {
			floatingText.update();
		}
		// Update projectiles
		for(Bullet projectile : projectileList) {
			// Update position
			projectile.update();
			// Check for collision
			projectile.checkForCollision(player, enemieList, floatingTextList);
		}
		// Update enemies
		for(Enemie enemie : enemieList) {
			enemie.update(projectileList, zOrderableSpriteList);
		}
		// Update player
		player.update(projectileList, zOrderableSpriteList);
		// Update camera
		camera.position.set(player.getHitbox().getCenterX(), player.getHitbox().getCenterY(), 0);
		if(camera.position.x < camera.viewportWidth/2) {
			camera.position.x = camera.viewportWidth/2;
		}
		if(camera.position.y <  camera.viewportHeight/2) {
			camera.position.y = camera.viewportHeight/2;
		}
		player.updateCameraCenterOffset(camera.position.x, camera.position.y);
		camera.update();

		// Update map
		gameMap.resetMatrix();
		for(MapObject mapObject : mapObjectList) {
			gameMap.updateHitbox(mapObject.getHitbox(), 2);
		}

		// Update sprites in Z order
		Collections.sort(zOrderableSpriteList, zAxisComparator);

	}

	// Draw player, enemies, projectiles, npcs, map, and so forth
	public void drawObjects(SpriteBatch spriteBatch) {
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.begin();
		// Draw background
		spriteBatch.draw(background,0,0, 10000, 10000);
		// Draw test ground
		spriteBatch.draw(ground, 0, 0, 1024, 1024);
		// Draw all static and dynamic map objects and entities
		for(ZOrderableSprite sprite : zOrderableSpriteList) {
			if(euclidianDistance(sprite.getX(), sprite.getY(), player.getHitbox().getCenterX(), player.getHitbox().getCenterY()) < 850) {
				sprite.draw(spriteBatch);
			}
		}
		// Debug draw map
		//gameMap.draw(spriteBatch, player);
		// Draw floating texts
		for(FloatingText floatingText : floatingTextList) {
			floatingText.draw(spriteBatch, font);
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
