package com.cgvsu.render_engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RenderSettingsTest {

    @Test
    void testSettingsRoundTrip() {
        RenderSettings settings = new RenderSettings(false, true, true, 0xFF112233);
        settings.setDrawWireframe(true);
        settings.setUseTexture(false);
        settings.setUseLighting(false);
        settings.setBaseColor(0xFFAABBCC);

        Assertions.assertTrue(settings.isDrawWireframe());
        Assertions.assertFalse(settings.isUseTexture());
        Assertions.assertFalse(settings.isUseLighting());
        Assertions.assertEquals(0xFFAABBCC, settings.getBaseColor());
    }
}
