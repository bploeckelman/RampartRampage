package com.lando.systems.rampartrampage.scene;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lando.systems.rampartrampage.Const;
import com.lando.systems.rampartrampage.GameState;
import com.lando.systems.rampartrampage.Global;
import com.lando.systems.rampartrampage.accessors.Vector3Accessor;
import com.lando.systems.rampartrampage.util.DebugCamera;

/**
 * Brian Ploeckelman created on 9/7/2014.
 */
public class Scene implements Disposable {

    private final GameState gameState;

    public Texture texture;

    public Camera currentCamera;
    public Camera transitionCamera;

    public DebugCamera camera1;
    public DebugCamera camera2;
    public DebugCamera camera3;

    public CameraInputController camController;

    public Environment env;
    public Model axes;
    public Model box;
    public Model ground;
    public Model selectorModel;
    public ModelInstance selectorInstance;
    public Array<ModelInstance> modelInstances;

    public Plane groundPlane;


    public Scene(GameState gameState) {
        this.gameState = gameState;

        initializeCameras();
        initializeGeometry();
    }

    public final Vector3 mouseWorldIntersection = new Vector3();
    public void update(float delta) {
        camController.update();

        camera1.update(true);
        camera2.update(true);
        camera3.update(true);

        camera1.updateFrustumModel();
        camera2.updateFrustumModel();
        camera3.updateFrustumModel();

        transitionCamera.update(true);

        Intersector.intersectRayPlane(
                currentCamera.getPickRay(Gdx.input.getX(), Gdx.input.getY()),
                groundPlane, mouseWorldIntersection);
        selectorInstance.nodes.get(0).translation.set(
                (int) mouseWorldIntersection.x,
                (int) mouseWorldIntersection.y,
                (int) mouseWorldIntersection.z);
        selectorInstance.calculateTransforms();
    }

    public void render(ModelBatch modelBatch, SpriteBatch spriteBatch) {
        modelBatch.begin(currentCamera);
        {
            modelBatch.render(modelInstances, env);
            modelBatch.render(selectorInstance, env);

            camera1.render(modelBatch);
            camera2.render(modelBatch);
            camera3.render(modelBatch);
        }
        modelBatch.end();
    }

    @Override
    public void dispose() {
        box.dispose();
        axes.dispose();
        ground.dispose();
        selectorModel.dispose();
        camera3.dispose();
        camera2.dispose();
        camera1.dispose();
    }

    public void switchCameras() {
        currentCamera.far = 100;
        transitionCamera.position.set(currentCamera.position);
        transitionCamera.direction.set(currentCamera.direction);
        transitionCamera.up.set(currentCamera.up);

        final TweenEquation easing = Back.INOUT;
        final float duration = 3;

        if (currentCamera == camera1) {
//            camera1.show();
//            camera3.show();

            Timeline.createParallel()
                    .push(Tween.to(transitionCamera.position,  Vector3Accessor.XYZ, duration).target(camera2.position.x,  camera2.position.y,  camera2.position.z) .ease(easing))
                    .push(Tween.to(transitionCamera.direction, Vector3Accessor.XYZ, duration).target(camera2.direction.x, camera2.direction.y, camera2.direction.z).ease(easing))
                    .push(Tween.to(transitionCamera.up, Vector3Accessor.XYZ, duration).target(camera2.up.x,        camera2.up.y,        camera2.up.z)       .ease(easing))
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            camera2.hide();
                            currentCamera = camera2;
                            camController.camera = currentCamera;
                        }
                    })
                    .start(Global.tweenManager);
        } else if (currentCamera == camera2) {
//            camera1.show();
//            camera2.show();

            Timeline.createParallel()
                    .push(Tween.to(transitionCamera.position,  Vector3Accessor.XYZ, duration).target(camera3.position.x,  camera3.position.y,  camera3.position.z) .ease(easing))
                    .push(Tween.to(transitionCamera.direction, Vector3Accessor.XYZ, duration).target(camera3.direction.x, camera3.direction.y, camera3.direction.z).ease(easing))
                    .push(Tween.to(transitionCamera.up,        Vector3Accessor.XYZ, duration).target(camera3.up.x,        camera3.up.y,        camera3.up.z)       .ease(easing))
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            camera3.hide();
                            currentCamera = camera3;
                            camController.camera = currentCamera;
                        }
                    })
                    .start(Global.tweenManager);
        } else if (currentCamera == camera3) {
//            camera2.show();
//            camera3.show();

            Timeline.createParallel()
                    .push(Tween.to(transitionCamera.position,  Vector3Accessor.XYZ, duration).target(camera1.position.x,  camera1.position.y,  camera1.position.z) .ease(easing))
                    .push(Tween.to(transitionCamera.direction, Vector3Accessor.XYZ, duration).target(camera1.direction.x, camera1.direction.y, camera1.direction.z).ease(easing))
                    .push(Tween.to(transitionCamera.up,        Vector3Accessor.XYZ, duration).target(camera1.up.x,        camera1.up.y,        camera1.up.z)       .ease(easing))
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            camera1.hide();
                            currentCamera = camera1;
                            camController.camera = currentCamera;
                        }
                    })
                    .start(Global.tweenManager);
        }

        currentCamera = transitionCamera;
        transitionCamera.update();

        camera1.update(true);
        camera2.update(true);
        camera3.update(true);

        camController.camera = currentCamera;

        // So that other cameras are fully visible
        currentCamera.far = 1000;
    }

    private void initializeCameras() {
        final float frustum_alpha = 0.6f;

        camera1 = new DebugCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
        camera1.position.set(10, 2, 0);
        camera1.lookAt(0, 0, 0);
        camera1.near = 1;
        camera1.far = 25;
        camera1.update();
        camera1.setFrustumColor(new Color(1, 0, 0, frustum_alpha));
        camera1.updateFrustumModel();

        camera2 = new DebugCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
        camera2.position.set(10, 1, 10);
        camera2.lookAt(0, 0, 0);
        camera2.near = 1;
        camera2.far = 50;
        camera2.update();
        camera2.setFrustumColor(new Color(0, 1, 0, frustum_alpha));
        camera2.updateFrustumModel();

        camera3 = new DebugCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
        camera3.position.set(0, 3, 10);
        camera3.lookAt(0, 0, 0);
        camera3.near = 1;
        camera3.far = 75;
        camera3.update();
        camera3.setFrustumColor(new Color(0, 0, 1, frustum_alpha));
        camera3.updateFrustumModel();

        transitionCamera = new DebugCamera(Const.default_fov, Const.viewport_width, Const.viewport_height);
        transitionCamera.near = 1;
        transitionCamera.far = 1000;
        transitionCamera.update();

        currentCamera = camera1;

        camController = new CameraInputController(camera1);
    }

    private void initializeGeometry() {
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
        final Material groundMaterial = new Material(ColorAttribute.createDiffuse(gameState.bg.cpy()));
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

        final Vector3 groundNormal = new Vector3(0,1,0);
        final Vector3 groundCenter = new Vector3(0,0,0);
        groundPlane = new Plane(groundNormal, groundCenter);
    }

    final Quaternion unit_quaternion = new Quaternion();
    final Vector3 unit_vector3 = new Vector3(1,1,1);
    final Vector3 temp_vector3 = new Vector3();
    // TODO :
    public void spawnBox(float x, float y, float z) {
        this.spawnBox(temp_vector3.set(x,y,z));
    }
    public void spawnBox(Vector3 position) {
        this.spawnBox(position, unit_vector3);
    }

    public void spawnBox(Vector3 position, Vector3 scale) {
        this.spawnBox(position, scale, unit_quaternion);
    }

    public void spawnBox(Vector3 position, Vector3 scale, Quaternion rotation) {
        Gdx.app.log("SPAWN_BOX", "pos: " + position.toString() + ", scale: " + scale.toString() + ", rot: " + rotation.toString());

        ModelInstance boxInstance = new ModelInstance(box);
        boxInstance.nodes.get(0).translation.set(position.x + .5f, position.y + 0.5f, position.z + 0.5f);
        boxInstance.nodes.get(0).scale.set(scale);
        boxInstance.nodes.get(0).rotation.set(rotation);
        boxInstance.calculateTransforms();
        modelInstances.add(boxInstance);
    }

    public void spawnBoxFromClick() {
        spawnBox((int) mouseWorldIntersection.x, (int) mouseWorldIntersection.y, (int) mouseWorldIntersection.z);
    }

    final Vector3 intersection = new Vector3();
    public void spawnBoxFromScreenOrigin() {
        Intersector.intersectRayPlane(
                currentCamera.getPickRay(currentCamera.viewportWidth / 2f, currentCamera.viewportHeight / 2f),
                groundPlane, intersection);
        spawnBox(intersection);
    }
}
