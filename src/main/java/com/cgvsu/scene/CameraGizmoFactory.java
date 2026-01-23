package com.cgvsu.scene;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;

public final class CameraGizmoFactory {
    private CameraGizmoFactory() {
    }

    public static Model createGizmo() {
        // Ксюня: гизмос камеры — простая модель для отображения на сцене (пункт 16).
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(-0.5f, -0.3f, 1.0f));
        model.vertices.add(new Vector3f(0.5f, -0.3f, 1.0f));
        model.vertices.add(new Vector3f(0.5f, 0.3f, 1.0f));
        model.vertices.add(new Vector3f(-0.5f, 0.3f, 1.0f));

        model.polygons.add(triangle(0, 1, 2));
        model.polygons.add(triangle(0, 2, 3));
        model.polygons.add(triangle(0, 3, 4));
        model.polygons.add(triangle(0, 4, 1));
        model.polygons.add(triangle(1, 4, 3));
        model.polygons.add(triangle(1, 3, 2));

        return model;
    }

    public static Model createGizmoAt(Vector3f position, float scale) {
        Model base = createGizmo();
        Model transformed = new Model();
        for (Vector3f vertex : base.vertices) {
            transformed.vertices.add(new Vector3f(
                    vertex.getX() * scale + position.getX(),
                    vertex.getY() * scale + position.getY(),
                    vertex.getZ() * scale + position.getZ()));
        }
        transformed.polygons = new ArrayList<>(base.polygons);
        return transformed;
    }

    private static Polygon triangle(int a, int b, int c) {
        Polygon polygon = new Polygon();
        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(a);
        indices.add(b);
        indices.add(c);
        polygon.setVertexIndices(indices);
        return polygon;
    }
}
