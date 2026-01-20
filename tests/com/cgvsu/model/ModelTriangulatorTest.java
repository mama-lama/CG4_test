package com.cgvsu.model;

import com.cgvsu.math.vectors.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTriangulatorTest {

    @Test
    public void triangulatesQuadIntoTwoTriangles() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(1, 1, 0));
        model.vertices.add(new Vector3f(0, 1, 0));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2, 3)));
        model.polygons.add(polygon);

        ModelTriangulator.triangulate(model);

        assertEquals(2, model.polygons.size());
        assertEquals(Arrays.asList(0, 1, 2), model.polygons.get(0).getVertexIndices());
        assertEquals(Arrays.asList(0, 2, 3), model.polygons.get(1).getVertexIndices());
    }

    @Test
    public void preservesTextureAndNormalIndices() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(1, 1, 0));
        model.vertices.add(new Vector3f(0, 1, 0));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2, 3)));
        polygon.setTextureVertexIndices(new ArrayList<>(Arrays.asList(10, 11, 12, 13)));
        polygon.setNormalIndices(new ArrayList<>(Arrays.asList(20, 21, 22, 23)));
        model.polygons.add(polygon);

        ModelTriangulator.triangulate(model);

        assertEquals(Arrays.asList(10, 11, 12), model.polygons.get(0).getTextureVertexIndices());
        assertEquals(Arrays.asList(20, 21, 22), model.polygons.get(0).getNormalIndices());
        assertEquals(Arrays.asList(10, 12, 13), model.polygons.get(1).getTextureVertexIndices());
        assertEquals(Arrays.asList(20, 22, 23), model.polygons.get(1).getNormalIndices());
    }
}
