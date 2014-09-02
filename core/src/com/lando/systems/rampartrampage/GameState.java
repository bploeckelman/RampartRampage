package com.lando.systems.rampartrampage;

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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lando.systems.rampartrampage.util.InputHandler;

/**
 * Brian Ploeckelman created on 9/1/2014.
 */
public class GameState implements Disposable {
    final Color bg = new Color(0, 0.75f, 1, 1);

    public SpriteBatch batch;
    public ModelBatch modelBatch;

    public Texture texture;

    public Environment env;
    public Model axes;
    public Model model;
    public Model frustum;
    public Model frustumWire;
    public Array<ModelInstance> modelInstances;

    public Camera camera;
    public Camera viewCam;
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
        camera.update();
    }

    public void render() {
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        for (ModelInstance inst : modelInstances) {
            modelBatch.render(inst, env);
            if (inst.getNode("frustum") != null) {
                Gdx.gl.glPolygonOffset(1, 1);
                modelBatch.render(inst, env);
                Gdx.gl.glPolygonOffset(0, 0);
            }
        }
        modelBatch.end();
    }

    @Override
    public void dispose() {
        model.dispose();
        axes.dispose();
        frustum.dispose();
        frustumWire.dispose();
        batch.dispose();
        modelBatch.dispose();
    }

    private void initializeScene() {
        camera = new PerspectiveCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
//        camera = new OrthographicCamera(Const.viewport_width, Const.viewport_height);
        camera.position.set(0, 0, 15);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 1000f;
        camera.update();

        viewCam = new PerspectiveCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
        viewCam.position.set(0, 5, 0);
        viewCam.lookAt(0, 0, 0);
        viewCam.near = 1;
        viewCam.far = 10;
        viewCam.update();

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

        // Vertices [0..3] are near plane, [4..7] are far plane
        final Color frustumColor = new Color(0.8f, 0.8f, 0.8f, 0.5f);
        final Vector3[] frustumPoints = viewCam.frustum.planePoints;
        final Material frustumMaterial = new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY), IntAttribute.createCullFace(GL20.GL_NONE), new BlendingAttribute(true, frustumColor.a));
        final long frustumAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Color;
        modelBuilder.begin();
        {
            MeshPartBuilder partBuilder = modelBuilder.part("frustum", GL20.GL_TRIANGLES, frustumAttrs, frustumMaterial);
            partBuilder.setColor(frustumColor);
            // Near and far planes
            partBuilder.rect(frustumPoints[0], frustumPoints[1], frustumPoints[2], frustumPoints[3], viewCam.frustum.planes[0].getNormal());
            partBuilder.rect(frustumPoints[4], frustumPoints[5], frustumPoints[6], frustumPoints[7], viewCam.frustum.planes[1].getNormal());

            // Left and right planes
            partBuilder.rect(frustumPoints[0], frustumPoints[3], frustumPoints[7], frustumPoints[4], viewCam.frustum.planes[2].getNormal());
            partBuilder.rect(frustumPoints[1], frustumPoints[2], frustumPoints[6], frustumPoints[5], viewCam.frustum.planes[3].getNormal());

            // Top and bottom planes
            partBuilder.rect(frustumPoints[3], frustumPoints[7], frustumPoints[6], frustumPoints[2], viewCam.frustum.planes[4].getNormal());
            partBuilder.rect(frustumPoints[0], frustumPoints[4], frustumPoints[5], frustumPoints[1], viewCam.frustum.planes[5].getNormal());
        }
        frustum = modelBuilder.end();

        final Material frustumWireMaterial = new Material(ColorAttribute.createDiffuse(Color.BLACK));
        modelBuilder.begin();
        {
            MeshPartBuilder partBuilder = modelBuilder.part("frustum_wire", GL20.GL_LINES, frustumAttrs, frustumWireMaterial);
            partBuilder.setColor(frustumColor);
            // Near and far planes
            partBuilder.rect(frustumPoints[0], frustumPoints[1], frustumPoints[2], frustumPoints[3], viewCam.frustum.planes[0].getNormal());
            partBuilder.rect(frustumPoints[4], frustumPoints[5], frustumPoints[6], frustumPoints[7], viewCam.frustum.planes[1].getNormal());

            // Left and right planes
            partBuilder.rect(frustumPoints[0], frustumPoints[3], frustumPoints[7], frustumPoints[4], viewCam.frustum.planes[2].getNormal());
            partBuilder.rect(frustumPoints[1], frustumPoints[2], frustumPoints[6], frustumPoints[5], viewCam.frustum.planes[3].getNormal());

            // Top and bottom planes
            partBuilder.rect(frustumPoints[3], frustumPoints[7], frustumPoints[6], frustumPoints[2], viewCam.frustum.planes[4].getNormal());
            partBuilder.rect(frustumPoints[0], frustumPoints[4], frustumPoints[5], frustumPoints[1], viewCam.frustum.planes[5].getNormal());
        }
        frustumWire = modelBuilder.end();

        modelInstances = new Array<ModelInstance>();
        modelInstances.add(new ModelInstance(axes));
        modelInstances.add(new ModelInstance(model));
        modelInstances.add(new ModelInstance(frustum));
        modelInstances.add(new ModelInstance(frustumWire));

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
                }
                return false;
            }
        };

        camController = new CameraInputController(camera);

        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(inputAdapter);
        inputMux.addProcessor(camController);

        Gdx.input.setInputProcessor(inputMux);
    }

}
