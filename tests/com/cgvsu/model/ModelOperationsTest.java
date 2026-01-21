package com.cgvsu.model;

import com.cgvsu.math.vectors.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ModelOperationsTest {

    @Test
    void testDeletePolygon() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        model.polygons.add(polygon);

        Assertions.assertTrue(ModelOperations.deletePolygon(model, 0));
        Assertions.assertEquals(0, model.polygons.size());
    }

    @Test
    void testDeleteVertexRemovesPolygons() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));
        model.vertices.add(new Vector3f(0, 0, 1));

        Polygon polygonA = new Polygon();
        polygonA.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        Polygon polygonB = new Polygon();
        polygonB.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        model.polygons.add(polygonA);
        model.polygons.add(polygonB);

        Assertions.assertTrue(ModelOperations.deleteVertex(model, 0));
        Assertions.assertEquals(3, model.vertices.size());
        Assertions.assertEquals(1, model.polygons.size());
        Assertions.assertEquals(List.of(0, 1, 2), model.polygons.get(0).getVertexIndices());
    }

    @Test
    void testCreateTransformedCopy() {
        Model model = new Model();
        model.vertices.add(new Vector3f(1, 0, 0));

        com.cgvsu.math.matrices.Matrix4 matrix = new com.cgvsu.math.matrices.Matrix4(
                1, 0, 0, 5,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );

        Model copy = ModelOperations.createTransformedCopy(model, matrix);
        Assertions.assertEquals(6.0f, copy.vertices.get(0).getX(), 1e-4);
    }
}
