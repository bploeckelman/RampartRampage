package com.lando.systems.rampartrampage.scene;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Disposable;
import com.lando.systems.rampartrampage.Const;
import com.lando.systems.rampartrampage.Global;
import com.lando.systems.rampartrampage.accessors.Vector3Accessor;
import com.lando.systems.rampartrampage.util.DebugCamera;

/**
 * Brian Ploeckelman created on 9/7/2014.
 */
public class Scene implements Disposable {

    public Camera currentCamera;
    public Camera transitionCamera;

    public DebugCamera camera1;
    public DebugCamera camera2;
    public DebugCamera camera3;

    public CameraInputController camController;

    public Scene() {
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

    public void update(float delta) {
        camController.update();

        camera1.update(true);
        camera2.update(true);
        camera3.update(true);

        camera1.updateFrustumModel();
        camera2.updateFrustumModel();
        camera3.updateFrustumModel();

        transitionCamera.update(true);
    }

    @Override
    public void dispose() {
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



}
