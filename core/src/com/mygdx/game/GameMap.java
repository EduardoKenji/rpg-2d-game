package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMap {
	// Square size in pixels;
	float squareSize;
	// Number of rows and columns
	int rows, columns;
	int map[][];
	float originX, originY;
	Sprite mapSquare = new Sprite(new Texture("textures/map_square.png"));
	Sprite filledMapSquare = new Sprite(new Texture("textures/map_square_filled.png"));

	int lowerLimitX, upperLimitX, lowerLimitY, upperLimitY;

	public GameMap(float squareSize, int rows, int columns, float originX, float originY) {
		this.squareSize = squareSize;
		this.rows = rows;
		this.columns = columns;
		this.originX = originX;
		this.originY = originY;
		map = new int[rows][columns];
	}

	int i, j;

	// Debug draw gameMap
	public void draw(SpriteBatch spriteBatch, Player player) {

		for(i = 0; i < rows; i++) {
			for(j = 0; j < columns; j++) {
				if(euclidianDistance(originX +(j*squareSize), originY +(i*squareSize), player.getHitbox().getCenterX(), player.getHitbox().getCenterY()) < 100) {
					if(map[i][j] == 0) {
						spriteBatch.draw(mapSquare, originX +(j*squareSize), originY +(i*squareSize), squareSize, squareSize);
					} else {
						spriteBatch.draw(filledMapSquare, originX +(j*squareSize), originY +(i*squareSize), squareSize, squareSize);
					}
				}
			}
		}
	}

	public void resetMatrix() {
		for(i = lowerLimitY; i < upperLimitY; i++) {
			for(j = lowerLimitX; j < upperLimitX; j++) {
				map[i][j] = 0;
			}
		}
	}

	public void updatePlayerPosition(Rectangle hitbox) {
		int x = (int)((hitbox.getCenterX()-originX)/squareSize);
		int y = (int)((hitbox.getCenterX()-originX)/squareSize);
		lowerLimitX = x - 500;
		if(lowerLimitX < 0) {
			lowerLimitX = 0;
		}
		lowerLimitY = y - 500;
		if(lowerLimitY < 0) {
			lowerLimitY = 0;
		}
		upperLimitX = x + 500;
		if(upperLimitX >= columns) {
			upperLimitX = columns - 1;
		}
		upperLimitY = y + 500;
		if(upperLimitY >= rows) {
			upperLimitY = rows - 1;
		}
	}

	// Check collision with this hitbox
	public boolean[] updateHitbox(Rectangle hitbox, int type) {
		int startX = (int)((hitbox.getX()-originX)/squareSize);
		int startY = (int)((hitbox.getY()-originY)/squareSize);
		int endX = (int)((hitbox.getX()+hitbox.getWidth()-originX)/squareSize);
		int endY = (int)((hitbox.getY()+hitbox.getHeight() -originY)/squareSize);
		for(i = startY; i <= endY; i++) {
			for(j = startX; j <= endX; j++) {
				map[i][j] = type;
			}
		}
		boolean blockedArray[] = new boolean[4];
		boolean leftBlocked = isfLeftBlocked(startX, startY, endX, endY);
		boolean bottomBlocked = isfBottomBlocked(startX, startY, endX, endY);
		boolean rightBlocked = isRightBlocked(startX, startY, endX, endY);
		boolean topBlocked = isfTopBlocked(startX, startY, endX, endY);
		blockedArray[0] = leftBlocked;
		blockedArray[1] = bottomBlocked;
		blockedArray[2] = rightBlocked;
		blockedArray[3] = topBlocked;
		return blockedArray;

	}

	public boolean isfLeftBlocked(int startX, int startY, int endX, int endY) {
		if((startX-1) == -1) {
			return true;
		}
		for(i = startY; i <= endY; i++) {
			if(map[i][(startX-1)] == 2) {
				return true;
			}
		}
		return false;
 	}

	public boolean isRightBlocked(int startX, int startY, int endX, int endY) {
		if((endX+1) == columns) {
			return true;
		}
		for(i = startY; i <= endY; i++) {
			if(map[i][(endX+1)] == 2) {
				return true;
			}
		}
		return false;
	}

	public boolean isfTopBlocked(int startX, int startY, int endX, int endY) {
		if((endY+1) == rows) {
			return true;
		}
		for(i = startX; i <= endX; i++) {
			if(map[endY+1][i] == 2) {
				return true;
			}
		}
		return false;
	}

	public boolean isfBottomBlocked(int startX, int startY, int endX, int endY) {
		if((startY-1) == -1) {
			return true;
		}
		for(i = startX; i <= endX; i++) {
			if(map[startY-1][i] == 2) {
				return true;
			}
		}
		return false;
	}

	public float euclidianDistance(float x1, float y1, float x2, float y2) {
		return (float)(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}

	public float getSquareSize() {
		return squareSize;
	}

	public void setSquareSize(float squareSize) {
		this.squareSize = squareSize;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}
}
