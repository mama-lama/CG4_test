package com.cgvsu.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraManager {
    private final List<SceneCamera> cameras = new ArrayList<>();
    private int activeIndex = -1;

    public SceneCamera add(SceneCamera camera) {
        cameras.add(camera);
        if (activeIndex < 0) {
            activeIndex = 0;
        }
        return camera;
    }

    public boolean removeActive() {
        if (cameras.size() <= 1 || activeIndex < 0) {
            return false;
        }
        cameras.remove(activeIndex);
        if (activeIndex >= cameras.size()) {
            activeIndex = cameras.size() - 1;
        }
        return true;
    }

    public SceneCamera getActive() {
        if (activeIndex < 0) {
            return null;
        }
        return cameras.get(activeIndex);
    }

    public SceneCamera setActive(SceneCamera camera) {
        int index = cameras.indexOf(camera);
        if (index >= 0) {
            activeIndex = index;
        }
        return getActive();
    }

    public SceneCamera cycleActive() {
        if (cameras.isEmpty()) {
            return null;
        }
        activeIndex = (activeIndex + 1) % cameras.size();
        return getActive();
    }

    public List<SceneCamera> getCameras() {
        return Collections.unmodifiableList(cameras);
    }
}
