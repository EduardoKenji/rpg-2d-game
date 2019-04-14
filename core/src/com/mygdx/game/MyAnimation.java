package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyAnimation {

    // Objects used
    Animation<TextureRegion> animation; // Must declare frame type (TextureRegion)

    // A variable for tracking elapsed time for the animation
    float stateTime;
    boolean xFlipped;

    MyAnimation(Texture texture, int rows, int columns, float frameDuration) {
        // Get sprite sheet and store in tmp
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / columns,
                texture.getHeight() / rows);
        // Transform tmp matrix in 1D array tmp2
        TextureRegion[] tmp2 = new TextureRegion[rows * columns];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                tmp2[index++] = tmp[i][j];
                index++;
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        animation = new Animation<TextureRegion>(frameDuration, tmp2);
        stateTime = 0f;
    }

    MyAnimation(Texture texture, int rows, int columns, float frameDuration, int startIndex, int endIndex) {
        // Get sprite sheet and store in tmp
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / columns,
                texture.getHeight() / rows);
        // Transform tmp matrix in 1D array tmp2
        TextureRegion[] tmp2 = new TextureRegion[endIndex-startIndex+1];
        int index = 0;
        int index2 = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(index >= startIndex && index <= endIndex) {
                    tmp2[index2] = tmp[i][j];
                    index2++;
                }
                index++;
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        animation = new Animation<TextureRegion>(frameDuration, tmp2);

        stateTime = 0f;
    }

    void draw(SpriteBatch spriteBatch, float x, float y, float width, float height) {
        // Get current frame
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        spriteBatch.draw(currentFrame, x, y, width, height);
    }

    void drawStaticFrame(SpriteBatch spriteBatch, float x, float y, float width, float height) {
		TextureRegion currentFrame = animation.getKeyFrame(0, true);
		spriteBatch.draw(currentFrame, x, y, width, height);
	}

    void update() {
        // Accumulate elapsed time
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public boolean isxFlipped() {
        return xFlipped;
    }

    public void setxFlipped(String direction) {
        for(TextureRegion frame: animation.getKeyFrames()) {
            if(direction.equals("right")) {
				if(frame.isFlipX()) {
					frame.flip(true, false);
				}
            } else if(direction.equals("left")) {
                if(!frame.isFlipX()) {
                    frame.flip(true, false);
                }

            }

        }
    }
}
