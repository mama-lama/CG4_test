package com.cgvsu.scene;

import com.cgvsu.model.Model;

import java.nio.file.Path;

public class SceneModel {
    private final Model model;
    private String name;
    private Path sourcePath;

    public SceneModel(Model model, String name, Path sourcePath) {
        this.model = model;
        this.name = name;
        this.sourcePath = sourcePath;
    }

    public Model getModel() {
        return model;
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

    @Override
    public String toString() {
        return name;
    }
}
