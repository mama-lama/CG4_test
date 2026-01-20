package com.cgvsu.render_engine;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class ImageTextureSampler implements TextureSampler {
    private final PixelReader pixelReader;
    private final int width;
    private final int height;

    public ImageTextureSampler(Image image) {
        this.pixelReader = image.getPixelReader();
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
    }

    @Override
    public int sample(float u, float v) {
        if (pixelReader == null || width == 0 || height == 0) {
            return 0xFFFFFFFF;
        }
        float clampedU = clamp01(u);
        float clampedV = clamp01(v);
        int x = Math.min(width - 1, Math.max(0, Math.round(clampedU * (width - 1))));
        int y = Math.min(height - 1, Math.max(0, Math.round((1.0f - clampedV) * (height - 1))));
        return pixelReader.getArgb(x, y);
    }

    private static float clamp01(float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }
}
