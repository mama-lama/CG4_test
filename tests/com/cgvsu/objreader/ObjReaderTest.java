package com.cgvsu.objreader;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ObjReaderTest {

    @Test
    void testParseVertex() {
        Vector3f result = ObjReader.parseVertex(List.of("1.01", "1.02", "1.03"), 5);
        Assertions.assertEquals(1.01f, result.getX());
        Assertions.assertEquals(1.02f, result.getY());
        Assertions.assertEquals(1.03f, result.getZ());
    }

    @Test
    void testParseTextureVertex() {
        Vector2f result = ObjReader.parseTextureVertex(List.of("0.25", "0.75"), 7);
        Assertions.assertEquals(0.25f, result.getX());
        Assertions.assertEquals(0.75f, result.getY());
    }

    @Test
    void testReadSimpleObj() {
        String content = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                vt 0 0
                vt 1 0
                vt 0 1
                vn 0 0 1
                vn 0 0 1
                vn 0 0 1
                f 1/1/1 2/2/2 3/3/3
                """;
        Model model = ObjReader.read(content);
        Assertions.assertEquals(3, model.vertices.size());
        Assertions.assertEquals(3, model.textureVertices.size());
        Assertions.assertEquals(3, model.normals.size());
        Assertions.assertEquals(1, model.polygons.size());
        Assertions.assertEquals(List.of(0, 1, 2), model.polygons.get(0).getVertexIndices());
        Assertions.assertEquals(List.of(0, 1, 2), model.polygons.get(0).getTextureVertexIndices());
        Assertions.assertEquals(List.of(0, 1, 2), model.polygons.get(0).getNormalIndices());
    }

    @Test
    void testReadNegativeIndices() {
        String content = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                f -3 -2 -1
                """;
        Model model = ObjReader.read(content);
        Assertions.assertEquals(1, model.polygons.size());
        Assertions.assertEquals(List.of(0, 1, 2), model.polygons.get(0).getVertexIndices());
    }

    @Test
    void testInvalidFaceThrows() {
        String content = "f 1 2";
        ObjReaderException exception = Assertions.assertThrows(ObjReaderException.class, () -> ObjReader.read(content));
        Assertions.assertTrue(exception.getMessage().contains("Face has fewer than 3 vertices."));
    }
}
