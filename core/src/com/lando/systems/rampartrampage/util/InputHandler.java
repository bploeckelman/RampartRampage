package com.lando.systems.rampartrampage.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.lando.systems.rampartrampage.GameState;

/**
 * Brian Ploeckelman created on 9/1/2014.
 */
public class InputHandler {

    GameState gameState;

    public InputHandler(GameState gameState) {
        this.gameState = gameState;
    }

    public void handleInput(float delta) {
        final float move_step = 1;
        final float move_amount = move_step * delta;

        if (Gdx.input.isKeyPressed(Keys.S)) {
            gameState.camera.position.add(0, 0, move_amount);
        }
        if (Gdx.input.isKeyPressed(Keys.W)) {
            gameState.camera.position.sub(0, 0, move_amount);
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            gameState.camera.position.sub(move_amount, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            gameState.camera.position.add(move_amount, 0, 0);
        }
    }

}

