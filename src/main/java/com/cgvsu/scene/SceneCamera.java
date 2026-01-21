package com.cgvsu.scene;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;

public class SceneCamera {
    private final Camera camera;
    private final String name;
    private final Model gizmoModel;

    public SceneCamera(Camera camera, String name, Model gizmoModel) {
        // Point 16: camera as a scene entity with optional gizmo model.
        this.camera = camera;
        this.name = name;
        this.gizmoModel = gizmoModel;
    }

    public Camera getCamera() {
        return camera;
    }

    public String getName() {
        return name;
    }

    public Model getGizmoModel() {
        return gizmoModel;
    }

    public Vector3f getPosition() {
        return camera.getPosition();
    }

    @Override
    public String toString() {
        return name;
    }
}
