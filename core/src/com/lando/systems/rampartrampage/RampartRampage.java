package com.lando.systems.rampartrampage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class RampartRampage extends ApplicationAdapter {

    GameState gameState;

    @Override
    public void create () {
        gameState = new GameState();
    }

    @Override
    public void render () {
        gameState.update(Gdx.graphics.getDeltaTime());
        gameState.render();
    }

    @Override
    public void dispose() {
        gameState.dispose();
    }

}
