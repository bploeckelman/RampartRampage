package com.lando.systems.rampartrampage.util;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * Brian Ploeckelman created on 9/1/2014.
 */
public class DebugCamera extends PerspectiveCamera implements Disposable {

    private static final ModelBuilder model_builder = new ModelBuilder();
    private static final long frustum_attrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Color;

    private boolean visible = false;
    private Color frustumColor = new Color(0.8f, 0.8f, 0.8f, 0.5f);

    private Model frustumModel;
    private Model frustumWireModel;
    private ModelInstance frustumModelInstance;
    private ModelInstance frustumWireModelInstance;

    public DebugCamera(float fov, int viewportWidth, int viewportHeight) {
        super(fov, viewportWidth, viewportHeight);
    }

    @Override
    public void dispose() {
        frustumModel.dispose();
        frustumWireModel.dispose();
    }

    public void render(ModelBatch batch) {
        if (!visible) return;
        batch.render(frustumWireModelInstance);
        batch.render(frustumModelInstance);
    }

    public boolean isVisible() { return visible; }
    public void show() { visible = true; }
    public void hide() { visible = false; }
    public void toggleVisibility() { visible = !visible; }
    public void setFrustumColor(Color color) {
        frustumColor = color.cpy();
    }

    public void updateFrustumModel() {
        // Frustum vertices [0..3] are near plane, [4..7] are far plane
        // Frustum planes [0..5] are near, far, left, right, top, bottom
        if (frustumModel != null) dispose();

        final Vector3[] frustumPoints = frustum.planePoints;
        final Material frustumMaterial = new Material(
                ColorAttribute.createDiffuse(frustumColor),
                IntAttribute.createCullFace(GL20.GL_NONE),
                new BlendingAttribute(true, frustumColor.a));

        model_builder.begin();
        {
            MeshPartBuilder partBuilder = model_builder.part("frustum", GL20.GL_TRIANGLES, frustum_attrs, frustumMaterial);
            partBuilder.setColor(frustumColor);
            // Near and far planes
            partBuilder.rect(frustumPoints[0], frustumPoints[1], frustumPoints[2], frustumPoints[3], frustum.planes[0].getNormal());
            partBuilder.rect(frustumPoints[4], frustumPoints[7], frustumPoints[6], frustumPoints[5], frustum.planes[1].getNormal());

            // Left and right planes
            partBuilder.rect(frustumPoints[0], frustumPoints[3], frustumPoints[7], frustumPoints[4], frustum.planes[2].getNormal());
            partBuilder.rect(frustumPoints[1], frustumPoints[5], frustumPoints[6], frustumPoints[2], frustum.planes[3].getNormal());

            // Top and bottom planes
            partBuilder.rect(frustumPoints[3], frustumPoints[2], frustumPoints[6], frustumPoints[7], frustum.planes[4].getNormal());
            partBuilder.rect(frustumPoints[0], frustumPoints[4], frustumPoints[5], frustumPoints[1], frustum.planes[5].getNormal());
        }
        frustumModel = model_builder.end();

        final Material frustumWireMaterial = new Material(ColorAttribute.createDiffuse(Color.BLACK));
        model_builder.begin();
        {
            MeshPartBuilder partBuilder = model_builder.part("frustum_wire", GL20.GL_LINES, frustum_attrs, frustumWireMaterial);
            partBuilder.setColor(frustumColor);
            // Near and far planes
            partBuilder.rect(frustumPoints[0], frustumPoints[1], frustumPoints[2], frustumPoints[3], frustum.planes[0].getNormal());
            partBuilder.rect(frustumPoints[4], frustumPoints[7], frustumPoints[6], frustumPoints[5], frustum.planes[1].getNormal());

            // Left and right planes
            partBuilder.rect(frustumPoints[0], frustumPoints[3], frustumPoints[7], frustumPoints[4], frustum.planes[2].getNormal());
            partBuilder.rect(frustumPoints[1], frustumPoints[5], frustumPoints[6], frustumPoints[2], frustum.planes[3].getNormal());

            // Top and bottom planes
            partBuilder.rect(frustumPoints[3], frustumPoints[2], frustumPoints[6], frustumPoints[7], frustum.planes[4].getNormal());
            partBuilder.rect(frustumPoints[0], frustumPoints[4], frustumPoints[5], frustumPoints[1], frustum.planes[5].getNormal());
        }
        frustumWireModel = model_builder.end();

        frustumModelInstance = new ModelInstance(frustumModel);
        frustumWireModelInstance = new ModelInstance(frustumWireModel);
    }

}
