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
            }
        }
        // Initialize the Animation with the frame interval and array of frames
        animation = new Animation<TextureRegion>(frameDuration, tmp2);

        stateTime = 0f;
    }

    void draw(SpriteBatch spriteBatch, float x, float y) {
        // Get current frame
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        spriteBatch.draw(currentFrame, x, y);
    }

    void update() {
        // Accumulate elapsed time
        stateTime += Gdx.graphics.getDeltaTime();
    }
}
