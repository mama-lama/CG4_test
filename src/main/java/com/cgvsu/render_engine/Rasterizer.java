package com.cgvsu.render_engine;

import java.util.Arrays;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;

public final class Rasterizer {
    private static final float AMBIENT = 0.2f;
    private static final float DIFFUSE = 0.8f;

    private Rasterizer() {
    }

    public static final class Vertex {
        public final float x;
        public final float y;
        public final float z;
        public final Vector3f position;
        public final Vector3f normal;
        public final Vector2f texCoord;
        /**
         * 
         * @param x
         * @param y
         * @param z
         * @param position
         * @param normal
         * @param texCoord
         */
        public Vertex(float x, float y, float z, Vector3f position, Vector3f normal, Vector2f texCoord) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.position = position;
            this.normal = normal;
            this.texCoord = texCoord;
        }
    }

    public static void clearBuffers(int width, int height, int[] colorBuffer, float[] depthBuffer, int clearColor) {
        Arrays.fill(colorBuffer, clearColor);
        Arrays.fill(depthBuffer, Float.POSITIVE_INFINITY);
    }

    public static void rasterizeTriangle(
            Vertex v0,
            Vertex v1,
            Vertex v2,
            int width,
            int height,
            int[] colorBuffer,
            float[] depthBuffer,
            int baseColor,
            TextureSampler textureSampler,
            Vector3f lightPos) {
        // Ксюня: растеризация треугольника с Z-буфером, текстурой и освещением (пункты 13-14).
        float area = edgeFunction(v0.x, v0.y, v1.x, v1.y, v2.x, v2.y);
        if (Math.abs(area) < 1e-6f) {
            return;
        }

        int minX = Math.max(0, (int) Math.floor(Math.min(v0.x, Math.min(v1.x, v2.x))));
        int maxX = Math.min(width - 1, (int) Math.ceil(Math.max(v0.x, Math.max(v1.x, v2.x))));
        int minY = Math.max(0, (int) Math.floor(Math.min(v0.y, Math.min(v1.y, v2.y))));
        int maxY = Math.min(height - 1, (int) Math.ceil(Math.max(v0.y, Math.max(v1.y, v2.y))));

        float invArea = 1.0f / area;
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                float px = x + 0.5f;
                float py = y + 0.5f;
                float w0 = edgeFunction(v1.x, v1.y, v2.x, v2.y, px, py) * invArea;
                float w1 = edgeFunction(v2.x, v2.y, v0.x, v0.y, px, py) * invArea;
                float w2 = edgeFunction(v0.x, v0.y, v1.x, v1.y, px, py) * invArea;

                if (w0 < 0.0f || w1 < 0.0f || w2 < 0.0f) {
                    continue;
                }

                float depth = w0 * v0.z + w1 * v1.z + w2 * v2.z;
                int index = y * width + x;
                if (depth >= depthBuffer[index]) {
                    continue;
                }

                int sampledColor = baseColor;
                if (textureSampler != null && v0.texCoord != null && v1.texCoord != null && v2.texCoord != null) {
                    float u = w0 * v0.texCoord.getX() + w1 * v1.texCoord.getX() + w2 * v2.texCoord.getX();
                    float v = w0 * v0.texCoord.getY() + w1 * v1.texCoord.getY() + w2 * v2.texCoord.getY();
                    sampledColor = textureSampler.sample(u, v);
                }

                float intensity = 1.0f;
                if (lightPos != null && v0.normal != null && v1.normal != null && v2.normal != null) {
                    Vector3f interpolatedNormal = v0.normal.mult(w0)
                            .add(v1.normal.mult(w1))
                            .add(v2.normal.mult(w2))
                            .normalize();
                    Vector3f interpolatedPos = v0.position.mult(w0)
                            .add(v1.position.mult(w1))
                            .add(v2.position.mult(w2));
                    Vector3f lightDir = lightPos.sub(interpolatedPos).normalize();
                    float diffuse = Math.max(0.0f, interpolatedNormal.scalarProd(lightDir));
                    intensity = clamp01(AMBIENT + DIFFUSE * diffuse);
                }

                colorBuffer[index] = applyLighting(sampledColor, intensity);
                depthBuffer[index] = depth;
            }
        }
    }

    public static void rasterizeLine(
            Vertex v0,
            Vertex v1,
            int width,
            int height,
            int[] colorBuffer,
            float[] depthBuffer,
            int color) {
        // Ксюня: растеризация линий для режима сетки с Z-буфером (пункт 15).
        float dx = v1.x - v0.x;
        float dy = v1.y - v0.y;
        float dz = v1.z - v0.z;
        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) {
            return;
        }

        float stepX = dx / steps;
        float stepY = dy / steps;
        float stepZ = dz / steps;

        float x = v0.x;
        float y = v0.y;
        float z = v0.z;
        for (int i = 0; i <= steps; i++) {
            int px = Math.round(x);
            int py = Math.round(y);
            if (px >= 0 && px < width && py >= 0 && py < height) {
                int index = py * width + px;
                if (z < depthBuffer[index]) {
                    depthBuffer[index] = z;
                    colorBuffer[index] = color;
                }
            }
            x += stepX;
            y += stepY;
            z += stepZ;
        }
    }

    private static float edgeFunction(float ax, float ay, float bx, float by, float px, float py) {
        return (px - ax) * (by - ay) - (py - ay) * (bx - ax);
    }

    private static int applyLighting(int argb, float intensity) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        r = clampChannel(Math.round(r * intensity));
        g = clampChannel(Math.round(g * intensity));
        b = clampChannel(Math.round(b * intensity));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clampChannel(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > 255) {
            return 255;
        }
        return value;
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
