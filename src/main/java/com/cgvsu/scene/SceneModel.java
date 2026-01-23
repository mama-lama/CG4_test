package com.cgvsu.scene;

import com.cgvsu.model.Model;
import com.cgvsu.model.ModelOperations;
import com.cgvsu.math.vectors.Vector3f;

import java.nio.file.Path;

public class SceneModel {
    private final Model model;
    private final Model originalModel;
    private String name;
    private Path sourcePath;
    // Point 2/9/10: per-model transform stored in scene space.
    private final Vector3f translation = new Vector3f(0, 0, 0);
    private final Vector3f rotation = new Vector3f(0, 0, 0);
    private final Vector3f scale = new Vector3f(1, 1, 1);

    public SceneModel(Model model, String name, Path sourcePath) {
        this.model = model;
        this.originalModel = ModelOperations.createTransformedCopy(
                model, new com.cgvsu.math.matrices.Matrix4().identity());
        this.name = name;
        this.sourcePath = sourcePath;
    }

    public Model getModel() {
        return model;
    }

    public Model getOriginalModel() {
        return originalModel;
    }

    public String getName() {
        return name;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void translate(Vector3f delta) {
        translation.set(
                translation.getX() + delta.getX(),
                translation.getY() + delta.getY(),
                translation.getZ() + delta.getZ());
    }

    public void rotate(Vector3f deltaDegrees) {
        rotation.set(
                rotation.getX() + deltaDegrees.getX(),
                rotation.getY() + deltaDegrees.getY(),
                rotation.getZ() + deltaDegrees.getZ());
    }

    public void scale(Vector3f factor) {
        scale.set(
                scale.getX() * factor.getX(),
                scale.getY() * factor.getY(),
                scale.getZ() * factor.getZ());
    }

    public void resetTransform() {
        translation.set(0, 0, 0);
        rotation.set(0, 0, 0);
        scale.set(1, 1, 1);
    }

    public void resetToOriginal() {
        ModelOperations.copyInto(originalModel, model);
    }

    public void syncOriginal() {
        ModelOperations.copyInto(model, originalModel);
    }

    @Override
    public String toString() {
        return name;
    }
}
