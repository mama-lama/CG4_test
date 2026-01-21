package com.cgvsu.scene;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SceneCameraTest {

    @Test
    void testSceneCameraAccessors() {
        Camera camera = new Camera(new Vector3f(1, 2, 3), new Vector3f(0, 0, 0), 1.0f, 1.0f, 0.1f, 100f);
        Model gizmo = new Model();
        SceneCamera sceneCamera = new SceneCamera(camera, "Cam A", gizmo);

        Assertions.assertEquals("Cam A", sceneCamera.getName());
        Assertions.assertEquals(1.0f, sceneCamera.getPosition().getX(), 1e-4);
        Assertions.assertSame(camera, sceneCamera.getCamera());
        Assertions.assertSame(gizmo, sceneCamera.getGizmoModel());
    }
}
