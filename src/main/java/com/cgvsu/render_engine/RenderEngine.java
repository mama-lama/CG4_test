package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4;
import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.math.vectors.Vector4f;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import java.util.List;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    private static final int CLEAR_COLOR = 0xFF000000;
    private static final int DEFAULT_BASE_COLOR = 0xFFB0B0B0;

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {
        render(graphicsContext, camera, mesh, width, height, null);
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Image texture) {
        render(graphicsContext, camera, mesh, width, height, texture, defaultSettings());
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Image texture,
            final RenderSettings settings) {
        renderModels(graphicsContext, camera, List.of(mesh), width, height, texture, settings);
    }

    public static void renderModels(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height,
            final Image texture,
            final RenderSettings settings) {
        // Ксюня: единая точка рендера с режимами (пункт 15).
        if (meshes == null || meshes.isEmpty()) {
            return;
        }
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        int[] colorBuffer = new int[width * height];
        float[] depthBuffer = new float[width * height];
        Rasterizer.clearBuffers(width, height, colorBuffer, depthBuffer, CLEAR_COLOR);

        // Ксюня: режимы — сетка/текстура/освещение и базовый цвет (пункт 15).
        RenderSettings resolved = settings == null ? defaultSettings() : settings;
        TextureSampler textureSampler = (resolved.isUseTexture() && texture != null)
                ? new ImageTextureSampler(texture)
                : null;
        Vector3f cameraPos = camera.getPosition();
        // Ксюня: источник света привязан к активной камере (пункт 14).
        Vector3f lightPos = resolved.isUseLighting()
                ? new Vector3f(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ())
                : null;
        int baseColor = resolved.getBaseColor();

        for (Model mesh : meshes) {
            Matrix4 modelMatrix = rotateScaleTranslate(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));
            Matrix4 modelViewProjectionMatrix = projectionMatrix.mult(viewMatrix).mult(modelMatrix);

            renderSingleModel(
                    mesh,
                    modelMatrix,
                    modelViewProjectionMatrix,
                    width,
                    height,
                    colorBuffer,
                    depthBuffer,
                    textureSampler,
                    lightPos,
                    baseColor,
                    resolved.isDrawWireframe());
        }

        graphicsContext.getPixelWriter().setPixels(
                0,
                0,
                width,
                height,
                PixelFormat.getIntArgbInstance(),
                colorBuffer,
                0,
                width);
    }

    private static void renderSingleModel(
            Model mesh,
            Matrix4 modelMatrix,
            Matrix4 modelViewProjectionMatrix,
            int width,
            int height,
            int[] colorBuffer,
            float[] depthBuffer,
            TextureSampler textureSampler,
            Vector3f lightPos,
            int baseColor,
            boolean drawWireframe) {
        // Ксюня: треугольники + Z-буфер + опциональная сетка (пункты 13 и 15).
        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            List<Integer> vertexIndices = mesh.polygons.get(polygonInd).getVertexIndices();
            if (vertexIndices.size() != 3) {
                continue;
            }

            List<Integer> textureIndices = mesh.polygons.get(polygonInd).getTextureVertexIndices();
            List<Integer> normalIndices = mesh.polygons.get(polygonInd).getNormalIndices();
            boolean hasTexture = textureIndices.size() == 3;
            boolean hasNormals = normalIndices.size() == 3;

            Rasterizer.Vertex[] vertices = new Rasterizer.Vertex[3];
            for (int i = 0; i < 3; i++) {
                int vertexIndex = vertexIndices.get(i);
                Vector3f vertex = mesh.vertices.get(vertexIndex);
                Vector3f projected = transformToNdc(modelViewProjectionMatrix, vertex);
                Vector2f resultPoint = vertexToPoint(projected, width, height);

                Vector2f texCoord = null;
                if (hasTexture) {
                    int textureIndex = textureIndices.get(i);
                    texCoord = mesh.textureVertices.get(textureIndex);
                }

                Vector3f normal = null;
                if (hasNormals) {
                    int normalIndex = normalIndices.get(i);
                    Vector3f normalLocal = mesh.normals.get(normalIndex);
                    Vector4f normalWorld = modelMatrix.mult(new Vector4f(
                            normalLocal.getX(),
                            normalLocal.getY(),
                            normalLocal.getZ(),
                            0.0f));
                    normal = new Vector3f(normalWorld.getX(), normalWorld.getY(), normalWorld.getZ()).normalize();
                }

                vertices[i] = new Rasterizer.Vertex(
                        resultPoint.getX(),
                        resultPoint.getY(),
                        projected.getZ(),
                        vertex,
                        normal,
                        texCoord);
            }

            Rasterizer.rasterizeTriangle(
                    vertices[0],
                    vertices[1],
                    vertices[2],
                    width,
                    height,
                    colorBuffer,
                    depthBuffer,
                    baseColor,
                    textureSampler,
                    lightPos);

            if (drawWireframe) {
                // Ксюня: поверх заливаемой модели рисуем каркас (пункт 15).
                Rasterizer.rasterizeLine(vertices[0], vertices[1], width, height, colorBuffer, depthBuffer, 0xFFFFFFFF);
                Rasterizer.rasterizeLine(vertices[1], vertices[2], width, height, colorBuffer, depthBuffer, 0xFFFFFFFF);
                Rasterizer.rasterizeLine(vertices[2], vertices[0], width, height, colorBuffer, depthBuffer, 0xFFFFFFFF);
            }
        }
    }

    private static RenderSettings defaultSettings() {
        return new RenderSettings(false, true, true, DEFAULT_BASE_COLOR);
    }
}
