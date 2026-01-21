package com.cgvsu.scene;

import com.cgvsu.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CameraGizmoFactoryTest {

    @Test
    void testGizmoHasNormals() {
        Model model = CameraGizmoFactory.createGizmo();
        Assertions.assertFalse(model.vertices.isEmpty());
        Assertions.assertEquals(model.vertices.size(), model.normals.size());
    }
}
