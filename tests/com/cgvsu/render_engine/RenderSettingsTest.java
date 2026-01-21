package com.cgvsu.render_engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RenderSettingsTest {

    @Test
    public void togglesAreStored() {
        RenderSettings settings = new RenderSettings(false, true, true, 0xFF112233);
        assertFalse(settings.isDrawWireframe());
        assertTrue(settings.isUseTexture());
        assertTrue(settings.isUseLighting());

        settings.setDrawWireframe(true);
        settings.setUseTexture(false);
        settings.setUseLighting(false);
        assertTrue(settings.isDrawWireframe());
        assertFalse(settings.isUseTexture());
        assertFalse(settings.isUseLighting());
    }

    @Test
    public void baseColorIsStored() {
        RenderSettings settings = new RenderSettings(false, false, false, 0xFF112233);
        assertEquals(0xFF112233, settings.getBaseColor());
        settings.setBaseColor(0xFFAABBCC);
        assertEquals(0xFFAABBCC, settings.getBaseColor());
    }
}
