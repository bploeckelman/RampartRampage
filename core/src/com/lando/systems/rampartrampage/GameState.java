package com.lando.systems.rampartrampage;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.lando.systems.rampartrampage.accessors.ColorAccessor;
import com.lando.systems.rampartrampage.accessors.Vector2Accessor;
import com.lando.systems.rampartrampage.accessors.Vector3Accessor;
import com.lando.systems.rampartrampage.scene.Scene;
import com.lando.systems.rampartrampage.util.InputHandler;

/**
 * Brian Ploeckelman created on 9/1/2014.
 */
public class GameState implements Disposable {
    public final Color bg = new Color(0.3f, 0.3f, 0.3f, 1);

    public SpriteBatch spriteBatch;
    public ModelBatch modelBatch;

    public Scene scene;

    public InputHandler inputHandler;
    public InputAdapter inputAdapter;


    public GameState() {
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());

        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();

        scene = new Scene(this);

        initializeInput();
    }

    public void update(float delta) {
        Global.tweenManager.update(delta);
        inputHandler.handleInput(delta);
        scene.update(delta);
    }

    public void render() {
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        scene.render(modelBatch, spriteBatch);
    }

    @Override
    public void dispose() {
        scene.dispose();
        spriteBatch.dispose();
        modelBatch.dispose();
    }

    private void initializeInput() {
        inputHandler = new InputHandler(this);

        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyUp (int keycode) {
                switch (keycode) {
                    case Keys.ESCAPE: Gdx.app.exit(); break;
                    case Keys.TAB: scene.switchCameras(); break;
                    case Keys.SPACE:
                        scene.spawnBox(new Vector3(inputHandler.mouse_world.x, 0, inputHandler.mouse_world.z));
                        break;
                }
                return false;
            }

            @Override
            public boolean touchDown (int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp (int screenX, int screenY, int pointer, int button) {
                return false;
            }
        };

        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(inputAdapter);
        inputMux.addProcessor(scene.camController);

        Gdx.input.setInputProcessor(inputMux);
    }

}
