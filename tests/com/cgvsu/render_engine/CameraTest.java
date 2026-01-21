package com.cgvsu.render_engine;

import com.cgvsu.math.vectors.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CameraTest {

    @Test
    void testMovePosition() {
        Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1.0f, 1.0f, 0.1f, 100f);
        camera.movePosition(new Vector3f(1, 2, 3));
        Assertions.assertEquals(1.0f, camera.getPosition().getX(), 1e-4);
        Assertions.assertEquals(2.0f, camera.getPosition().getY(), 1e-4);
        Assertions.assertEquals(3.0f, camera.getPosition().getZ(), 1e-4);
    }

    @Test
    void testMoveTarget() {
        Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1.0f, 1.0f, 0.1f, 100f);
        camera.moveTarget(new Vector3f(0, 1, 0));
        Assertions.assertEquals(0.0f, camera.getTarget().getX(), 1e-4);
        Assertions.assertEquals(1.0f, camera.getTarget().getY(), 1e-4);
        Assertions.assertEquals(0.0f, camera.getTarget().getZ(), 1e-4);
    }

    @Test
    void testGetters() {
        Camera camera = new Camera(new Vector3f(1, 2, 3), new Vector3f(0, 0, 0), 1.2f, 1.5f, 0.5f, 50f);
        Assertions.assertEquals(1.2f, camera.getFov(), 1e-4);
        Assertions.assertEquals(1.5f, camera.getAspectRatio(), 1e-4);
        Assertions.assertEquals(0.5f, camera.getNearPlane(), 1e-4);
        Assertions.assertEquals(50f, camera.getFarPlane(), 1e-4);
    }
}
