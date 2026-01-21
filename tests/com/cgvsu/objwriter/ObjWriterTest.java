package com.cgvsu.objwriter;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ObjWriterTest {

    @Test
    void testWriteWithTexturesAndNormals() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));
        model.textureVertices.add(new Vector2f(0, 0));
        model.textureVertices.add(new Vector2f(1, 0));
        model.textureVertices.add(new Vector2f(0, 1));
        model.normals.add(new Vector3f(0, 0, 1));
        model.normals.add(new Vector3f(0, 0, 1));
        model.normals.add(new Vector3f(0, 0, 1));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon.setNormalIndices(new ArrayList<>(List.of(0, 1, 2)));
        model.polygons.add(polygon);

        String output = ObjWriter.write(model);
        Assertions.assertTrue(output.contains("v 0.0 0.0 0.0"));
        Assertions.assertTrue(output.contains("vt 1.0 0.0"));
        Assertions.assertTrue(output.contains("vn 0.0 0.0 1.0"));
        Assertions.assertTrue(output.contains("f 1/1/1 2/2/2 3/3/3"));
    }

    @Test
    void testWriteWithoutTexturesOrNormals() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));
        model.vertices.add(new Vector3f(1, 0, 0));
        model.vertices.add(new Vector3f(0, 1, 0));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        model.polygons.add(polygon);

        String output = ObjWriter.write(model);
        Assertions.assertTrue(output.contains("f 1 2 3"));
        Assertions.assertFalse(output.contains("/"));
    }
}
