package com.lando.systems.rampartrampage.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.lando.systems.rampartrampage.GameState;

/**
 * Brian Ploeckelman created on 9/1/2014.
 */
public class InputHandler {

    private GameState gameState;

    public Vector3 mouse_screen = new Vector3();
    public Vector3 mouse_world = new Vector3();

    public InputHandler(GameState gameState) {
        this.gameState = gameState;
    }

    public void handleInput(float delta) {
        final float screenX = gameState.scene.currentCamera.viewportWidth / 2f;
        final float screenY = gameState.scene.currentCamera.viewportHeight / 2f;
        // TODO : raytrace from screenZ = 0 to 1 and pick nearest intersected scene object
        final float screenZ = 0.85f;
//        final float screenX = Gdx.input.getX();
//        final float screenY = Gdx.input.getY();
//        final float screenH = Gdx.graphics.getHeight();
//        final float screenZ = (screenH - screenY) / screenH;
//        Gdx.app.log("SCREEN_Z", "" + screenZ);

        mouse_screen.set(screenX, screenY, screenZ);
        mouse_world.set(mouse_screen);
        gameState.scene.currentCamera.unproject(mouse_world);
    }

}

