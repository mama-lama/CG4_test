package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.math.vectors.Vector4f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GraphicConveyorTest {

    @Test
    void testModelMatrixTranslation() {
        Vector3f translation = new Vector3f(2, 3, 4);
        Matrix4 model = GraphicConveyor.rotateScaleTranslate(translation, new Vector3f(), new Vector3f(1, 1, 1));
        Vector4f result = model.mult(new Vector4f(1, 1, 1, 1));
        Assertions.assertEquals(3.0f, result.getX(), 1e-4);
        Assertions.assertEquals(4.0f, result.getY(), 1e-4);
        Assertions.assertEquals(5.0f, result.getZ(), 1e-4);
    }

    @Test
    void testModelMatrixScale() {
        Vector3f scale = new Vector3f(2, 3, 4);
        Matrix4 model = GraphicConveyor.rotateScaleTranslate(new Vector3f(), new Vector3f(), scale);
        Vector4f result = model.mult(new Vector4f(1, 1, 1, 1));
        Assertions.assertEquals(2.0f, result.getX(), 1e-4);
        Assertions.assertEquals(3.0f, result.getY(), 1e-4);
        Assertions.assertEquals(4.0f, result.getZ(), 1e-4);
    }

    @Test
    void testLookAtMatrix() {
        Matrix4 view = GraphicConveyor.lookAt(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
        Assertions.assertEquals(1.0f, view.get(0, 0), 1e-4);
        Assertions.assertEquals(1.0f, view.get(1, 1), 1e-4);
        Assertions.assertEquals(1.0f, view.get(2, 2), 1e-4);
        Assertions.assertEquals(-1.0f, view.get(2, 3), 1e-4);
    }

    @Test
    void testPerspectiveMatrix() {
        float fov = (float) (Math.PI / 2);
        Matrix4 projection = GraphicConveyor.perspective(fov, 1.0f, 1.0f, 3.0f);
        Assertions.assertEquals(1.0f, projection.get(0, 0), 1e-4);
        Assertions.assertEquals(1.0f, projection.get(1, 1), 1e-4);
        Assertions.assertEquals(-2.0f, projection.get(2, 2), 1e-4);
        Assertions.assertEquals(-3.0f, projection.get(2, 3), 1e-4);
        Assertions.assertEquals(-1.0f, projection.get(3, 2), 1e-4);
    }

    @Test
    void testTransformToNdc() {
        Matrix4 identity = new Matrix4().identity();
        Vector3f ndc = GraphicConveyor.transformToNdc(identity, new Vector3f(0.5f, -0.5f, 0.25f));
        Assertions.assertEquals(0.5f, ndc.getX(), 1e-4);
        Assertions.assertEquals(-0.5f, ndc.getY(), 1e-4);
        Assertions.assertEquals(0.25f, ndc.getZ(), 1e-4);
    }
}
