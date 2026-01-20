package com.cgvsu.math;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NormalsCalculatorTest {

    @Test
    public void recalculatesVertexNormals() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        NormalsCalculator.recalculateNormals(model);

        assertEquals(3, model.normals.size());
        assertEquals(Arrays.asList(0, 1, 2), model.polygons.get(0).getNormalIndices());
        Vector3f normal = model.normals.get(0);
        assertEquals(0.0f, normal.getX(), 0.0001f);
        assertEquals(0.0f, normal.getY(), 0.0001f);
        assertEquals(1.0f, normal.getZ(), 0.0001f);
    }
}
