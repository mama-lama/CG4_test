package com.cgvsu.scene;

import com.cgvsu.render_engine.Camera;
import org.junit.jupiter.api.Test;

import javax.vecmath.Vector3f;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CameraManagerTest {

    @Test
    public void addCycleAndRemove() {
        CameraManager manager = new CameraManager();
        SceneCamera cam1 = new SceneCamera(
                new Camera(new Vector3f(0, 0, 1), new Vector3f(), 1, 1, 0.1f, 10),
                "Camera 1",
                CameraGizmoFactory.createGizmo());
        SceneCamera cam2 = new SceneCamera(
                new Camera(new Vector3f(0, 0, 2), new Vector3f(), 1, 1, 0.1f, 10),
                "Camera 2",
                CameraGizmoFactory.createGizmo());

        manager.add(cam1);
        manager.add(cam2);
        assertEquals(cam1, manager.getActive());

        manager.cycleActive();
        assertEquals(cam2, manager.getActive());

        assertTrue(manager.removeActive());
        assertNotNull(manager.getActive());
        assertEquals(1, manager.getCameras().size());
    }
}
