package com.cgvsu.scene;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SceneModelTest {

    @Test
    void testTransformOperations() {
        SceneModel model = new SceneModel(new Model(), "Test", null);
        model.translate(new Vector3f(1, 2, 3));
        model.rotate(new Vector3f(10, 20, 30));
        model.scale(new Vector3f(2, 3, 4));

        Assertions.assertEquals(1.0f, model.getTranslation().getX(), 1e-4);
        Assertions.assertEquals(2.0f, model.getTranslation().getY(), 1e-4);
        Assertions.assertEquals(3.0f, model.getTranslation().getZ(), 1e-4);

        Assertions.assertEquals(10.0f, model.getRotation().getX(), 1e-4);
        Assertions.assertEquals(20.0f, model.getRotation().getY(), 1e-4);
        Assertions.assertEquals(30.0f, model.getRotation().getZ(), 1e-4);

        Assertions.assertEquals(2.0f, model.getScale().getX(), 1e-4);
        Assertions.assertEquals(3.0f, model.getScale().getY(), 1e-4);
        Assertions.assertEquals(4.0f, model.getScale().getZ(), 1e-4);
    }

    @Test
    void testResetTransform() {
        SceneModel model = new SceneModel(new Model(), "Test", null);
        model.translate(new Vector3f(1, 2, 3));
        model.rotate(new Vector3f(10, 20, 30));
        model.scale(new Vector3f(2, 3, 4));

        model.resetTransform();
        Assertions.assertEquals(0.0f, model.getTranslation().getX(), 1e-4);
        Assertions.assertEquals(0.0f, model.getRotation().getX(), 1e-4);
        Assertions.assertEquals(1.0f, model.getScale().getX(), 1e-4);
    }
}
