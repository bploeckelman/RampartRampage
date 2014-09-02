package com.lando.systems.rampartrampage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lando.systems.rampartrampage.util.DebugCamera;
import com.lando.systems.rampartrampage.util.InputHandler;

/**
 * Brian Ploeckelman created on 9/1/2014.
 */
public class GameState implements Disposable {
//    final Color bg = new Color(0, 0.75f, 1, 1);
    final Color bg = new Color(0.3f, 0.3f, 0.3f, 1);

    public SpriteBatch batch;
    public ModelBatch modelBatch;

    public Texture texture;

    public Environment env;
    public Model axes;
    public Model model;
    public Model ground;
    public Array<ModelInstance> modelInstances;

    public Camera currentCamera;
    public DebugCamera camera1;
    public DebugCamera camera2;
    public DebugCamera camera3;
    public InputHandler inputHandler;
    public InputAdapter inputAdapter;
    public CameraInputController camController;


    public GameState() {
        batch = new SpriteBatch();
        modelBatch = new ModelBatch();

        initializeScene();
        initializeInput();
    }

    public void update(float delta) {
        inputHandler.handleInput(delta);
        camController.update();
        camera1.update(true);
        camera2.update(true);
        camera3.update(true);
        camera1.updateFrustumModel();
        camera2.updateFrustumModel();
        camera3.updateFrustumModel();
    }

    public void render() {
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(currentCamera);
        for (ModelInstance inst : modelInstances) {
            modelBatch.render(inst, env);
        }
        camera1.render(modelBatch);
        camera2.render(modelBatch);
        camera3.render(modelBatch);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        camera3.dispose();
        camera2.dispose();
        camera1.dispose();
        model.dispose();
        axes.dispose();
        batch.dispose();
        modelBatch.dispose();
    }

    private void initializeScene() {
        camera1 = new DebugCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
        camera1.position.set(10, 5, 10);
        camera1.lookAt(0, 0, 0);
        camera1.near = 1;
        camera1.far = 100;
        camera1.update();
        camera1.setFrustumColor(new Color(1, 1, 0, 0.5f));
        camera1.updateFrustumModel();

        camera2 = new DebugCamera(Const.default_fov, 100, 100);
        camera2.position.set(15, 5, 0);
        camera2.lookAt(0, 0, 0);
        camera2.near = 1;
        camera2.far = 100;
        camera2.show();
        camera2.update();
        camera2.setFrustumColor(new Color(1, 0, 1, 0.5f));
        camera2.updateFrustumModel();

        camera3 = new DebugCamera(Const.default_fov, 100, 100);
        camera3.position.set(0, 5, 15);
        camera3.lookAt(0, 0, 0);
        camera3.near = 1;
        camera3.far = 100;
        camera3.show();
        camera3.update();
        camera3.setFrustumColor(new Color(0, 1, 1, 0.5f));
        camera3.updateFrustumModel();

        currentCamera = camera1;
        currentCamera.near = 1;
        currentCamera.far = 1000;

        final ModelBuilder modelBuilder = new ModelBuilder();

        final float axisLength = 10;
        final Material axisMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        final long axisAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        axes = modelBuilder.createXYZCoordinates(axisLength, axisMaterial, axisAttrs);

        texture = new Texture("badlogic.jpg");
        final float boxSize = 1;
        final Material boxMaterial = new Material(TextureAttribute.createDiffuse(texture));
        final long boxAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        model = modelBuilder.createBox(boxSize, boxSize, boxSize, boxMaterial, boxAttrs);

        final int xDivisions = 100;
        final int zDivisions = 100;
        final float xSize = 1;
        final float zSize = 1;
        final Material groundMaterial = new Material(ColorAttribute.createDiffuse(bg.cpy()));
        final long groundAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.Color;
        ground = modelBuilder.createLineGrid(xDivisions, zDivisions, xSize, zSize, groundMaterial, groundAttrs);

        modelInstances = new Array<ModelInstance>();
        modelInstances.add(new ModelInstance(axes));
        modelInstances.add(new ModelInstance(model));
        modelInstances.add(new ModelInstance(ground));

        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
        env.add(new DirectionalLight().set(Color.PURPLE, -5, 5, 5));
        // ...
    }

    private void initializeInput() {
        inputHandler = new InputHandler(this);

        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyUp (int keycode) {
                switch (keycode) {
                    case Keys.ESCAPE: Gdx.app.exit(); break;
                    case Keys.SPACE: switchCameras(); break;
                    case Keys.TAB:
                        camera1.updateFrustumModel();
                        camera2.updateFrustumModel();
                        break;
                }
                return false;
            }
        };

        camController = new CameraInputController(camera1);

        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(inputAdapter);
        inputMux.addProcessor(camController);

        Gdx.input.setInputProcessor(inputMux);
    }

    private void switchCameras() {
        if (currentCamera == camera1) {
            currentCamera = camera2;
            camera1.show();
            camera2.hide();
            camera3.show();
            camera1.near = 1;
            camera1.far = 100;
            camera3.near = 1;
            camera3.far = 100;
        } else if (currentCamera == camera2) {
            currentCamera = camera3;
            camera1.show();
            camera2.show();
            camera3.hide();
            camera1.near = 1;
            camera1.far = 100;
            camera2.near = 1;
            camera2.far = 100;
        } else if (currentCamera == camera3) {
            currentCamera = camera1;
            camera1.hide();
            camera2.show();
            camera3.show();
            camera2.near = 1;
            camera2.far = 100;
            camera3.near = 1;
            camera3.far = 100;
        }

        camera1.update(true);
        camera2.update(true);
        camera3.update(true);
        camController.camera = currentCamera;

        // So that other cameras are fully visible
        currentCamera.near = 1;
        currentCamera.far = 1000;
    }

}
