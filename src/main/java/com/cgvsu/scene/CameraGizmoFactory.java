package com.cgvsu.scene;

import com.cgvsu.math.NormalsCalculator;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class CameraGizmoFactory {
    public static Model createGizmo() {
        // Point 16: simple model used to visualize camera positions.
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));   // tip
        model.vertices.add(new Vector3f(-0.5f, -0.3f, -1f));
        model.vertices.add(new Vector3f(0.5f, -0.3f, -1f));
        model.vertices.add(new Vector3f(0.5f, 0.3f, -1f));
        model.vertices.add(new Vector3f(-0.5f, 0.3f, -1f));

        model.polygons.add(triangle(0, 1, 2));
        model.polygons.add(triangle(0, 2, 3));
        model.polygons.add(triangle(0, 3, 4));
        model.polygons.add(triangle(0, 4, 1));
        model.polygons.add(triangle(1, 4, 3));
        model.polygons.add(triangle(1, 3, 2));

        NormalsCalculator.recalculateNormals(model);
        return model;
    }

    private static Polygon triangle(int a, int b, int c) {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(a, b, c)));
        return polygon;
    }
}
