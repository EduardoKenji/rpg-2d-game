package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Main;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 960; // 960 - 1200
		config.height = 720; // 540 - 675
		config.title = "Pixel RPG";
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.backgroundFPS = 30;
		try {
			new LwjglApplication(new Main(), config);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
		}
	}
}
