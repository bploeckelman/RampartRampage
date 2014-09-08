package com.lando.systems.rampartrampage;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lando.systems.rampartrampage.accessors.ColorAccessor;
import com.lando.systems.rampartrampage.accessors.Vector2Accessor;
import com.lando.systems.rampartrampage.accessors.Vector3Accessor;
import com.lando.systems.rampartrampage.scene.Scene;
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
    public Model box;
    public Model ground;
    public Model selectorModel;
    public ModelInstance selectorInstance;
    public Array<ModelInstance> modelInstances;

    public Scene scene;

    public InputHandler inputHandler;
    public InputAdapter inputAdapter;


    public GameState() {
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());

        batch = new SpriteBatch();
        modelBatch = new ModelBatch();

        initializeScene();
        initializeInput();
    }

    public void update(float delta) {
        Global.tweenManager.update(delta);

        inputHandler.handleInput(delta);

        selectorInstance.nodes.get(0).translation.set(inputHandler.mouse_world);
        selectorInstance.nodes.get(0).translation.y = 0;
        selectorInstance.calculateTransforms();

        scene.update(delta);
    }

    public void render() {
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(scene.currentCamera);
        modelBatch.render(modelInstances, env);
        modelBatch.render(selectorInstance, env);

        scene.camera1.render(modelBatch);
        scene.camera2.render(modelBatch);
        scene.camera3.render(modelBatch);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        scene.dispose();
        box.dispose();
        axes.dispose();
        ground.dispose();
        selectorModel.dispose();
        batch.dispose();
        modelBatch.dispose();
    }

    private void initializeScene() {
        scene = new Scene();

        final ModelBuilder modelBuilder = new ModelBuilder();

        final float axisLength = 10;
        final Material axisMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        final long axisAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        axes = modelBuilder.createXYZCoordinates(axisLength, axisMaterial, axisAttrs);

        texture = new Texture("badlogic.jpg");
        final float boxSize = 1;
        final Material boxMaterial = new Material(TextureAttribute.createDiffuse(texture));
        final long boxAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        box = modelBuilder.createBox(boxSize, boxSize, boxSize, boxMaterial, boxAttrs);

        final int xDivisions = 100;
        final int zDivisions = 100;
        final float xSize = 1;
        final float zSize = 1;
        final Material groundMaterial = new Material(ColorAttribute.createDiffuse(bg.cpy()));
        final long groundAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.Color;
        ground = modelBuilder.createLineGrid(xDivisions, zDivisions, xSize, zSize, groundMaterial, groundAttrs);

        final Material selectorMaterial = new Material(
                ColorAttribute.createDiffuse(1, 1, 1, 0.7f),
                TextureAttribute.createDiffuse(texture),
                IntAttribute.createCullFace(GL20.GL_NONE));//,
//                new BlendingAttribute(true, 0.7f));
        final long selectorAttrs = VertexAttributes.Usage.Position
//                                 | VertexAttributes.Usage.Normal
                                 | VertexAttributes.Usage.TextureCoordinates
                                 | VertexAttributes.Usage.Color;
        selectorModel = modelBuilder.createRect(
                0, 0, 0,
                1, 0, 0,
                1, 0, 1,
                0, 0, 1,
                0, 1, 0,
                selectorMaterial,
                selectorAttrs);
        selectorInstance = new ModelInstance(selectorModel);

        modelInstances = new Array<ModelInstance>();
        modelInstances.add(new ModelInstance(axes));
        modelInstances.add(new ModelInstance(box));
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
                    case Keys.TAB: scene.switchCameras(); break;
                    case Keys.SPACE:
                        spawnBox(new Vector3(inputHandler.mouse_world.x, 0, inputHandler.mouse_world.z));
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

    final Quaternion unit_quaternion = new Quaternion();
    final Vector3 unit_vector3 = new Vector3(1,1,1);
    // TODO :
    private void spawnBox(Vector3 position) {
        this.spawnBox(position, unit_vector3);
    }

    private void spawnBox(Vector3 position, Vector3 scale) {
        this.spawnBox(position, scale, unit_quaternion);
    }

    private void spawnBox(Vector3 position, Vector3 scale, Quaternion rotation) {
        Gdx.app.log("SPAWN_BOX", "pos: " + position.toString() + ", scale: " + scale.toString() + ", rot: " + rotation.toString());

        ModelInstance boxInstance = new ModelInstance(box);
        boxInstance.nodes.get(0).translation.set(position);
        boxInstance.nodes.get(0).scale.set(scale);
        boxInstance.nodes.get(0).rotation.set(rotation);
        boxInstance.calculateTransforms();
        modelInstances.add(boxInstance);
    }
}
