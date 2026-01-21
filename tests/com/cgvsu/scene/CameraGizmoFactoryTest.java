package com.cgvsu.scene;

import com.cgvsu.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CameraGizmoFactoryTest {

    @Test
    public void gizmoHasTriangles() {
        Model model = CameraGizmoFactory.createGizmo();
        assertFalse(model.vertices.isEmpty());
        assertFalse(model.polygons.isEmpty());
        assertTrue(model.polygons.stream().allMatch(p -> p.getVertexIndices().size() == 3));
    }
}
