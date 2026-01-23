package com.cgvsu.scene;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;

public class SceneCamera {
    // Ксюня: камера сцены с именем и гизмосом для визуализации (пункт 16).
    private final Camera camera;
    private final String name;
    private final Model gizmoModel;

    public SceneCamera(Camera camera, String name, Model gizmoModel) {
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
}
