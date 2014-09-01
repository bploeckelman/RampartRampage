package com.lando.systems.rampartrampage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

import javax.swing.text.AttributeSet;

public class RampartRampage extends ApplicationAdapter {
    final Color bg = new Color(0, 0.75f, 1, 1);

    SpriteBatch batch;
    ModelBatch modelBatch;

    Environment env;
    Model axes;
    Model model;
    Array<ModelInstance> modelInstances;

    Texture img;

    Camera camera;
    InputAdapter inputAdapter;

    @Override
    public void create () {
        batch = new SpriteBatch();
        modelBatch = new ModelBatch();

        img = new Texture("badlogic.jpg");

        camera = new PerspectiveCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
//        camera = new OrthographicCamera(Const.viewport_width, Const.viewport_height);
        camera.position.set(0, 0, 15);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 1000f;
        camera.update();

        initializeScene();

        initializeInput();
    }

    @Override
    public void render () {
        camera.update();

        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        for (ModelInstance inst : modelInstances) {
            modelBatch.render(inst, env);
        }
        modelBatch.end();

//        final float x = -img.getWidth() / 2;
//        final float y = -img.getHeight() / 2;
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//        batch.draw(img, x, y);
//        batch.end();
    }

    @Override
    public void dispose() {
        model.dispose();
        axes.dispose();
        img.dispose();
        batch.dispose();
        modelBatch.dispose();
    }

    private void initializeScene() {
        final ModelBuilder modelBuilder = new ModelBuilder();

        final float axisLength = 10;
        final Material axisMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        final long axisAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        axes = modelBuilder.createXYZCoordinates(axisLength, axisMaterial, axisAttrs);

        final float boxSize = 1;
        final Material boxMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        final long boxAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        model = modelBuilder.createBox(boxSize, boxSize, boxSize, boxMaterial, boxAttrs);

        modelInstances = new Array<ModelInstance>();
        modelInstances.add(new ModelInstance(axes));
        modelInstances.add(new ModelInstance(model));

        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1));
        env.add(new DirectionalLight().set(Color.PURPLE, -10, 10, 10));
        // ...
    }

    private void initializeInput() {
        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                return false;
            }

            @Override
            public boolean keyUp (int keycode) {
                switch (keycode) {
                    case Keys.ESCAPE: Gdx.app.exit(); break;
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

            @Override
            public boolean scrolled (int amount) {
                return false;
            }
        };

        Gdx.input.setInputProcessor(inputAdapter);
    }
}
