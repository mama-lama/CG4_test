package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4;
import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.math.vectors.Vector4f;
import com.cgvsu.model.Model;
import com.cgvsu.scene.SceneModel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import java.util.ArrayList;
import java.util.List;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    // Point 7: uses custom math matrices/vectors instead of external vecmath.
    private static final int CLEAR_COLOR = 0xFF000000;
    private static final int BASE_COLOR = 0xFFB0B0B0;

    public static void renderModels(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Image texture) {
        // Point 15: default render modes (texturing + lighting on).
        renderModels(graphicsContext, camera, mesh, width, height, texture, defaultSettings());
    }

    public static void renderModels(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height,
            final Image texture) {
        renderModels(graphicsContext, camera, meshes, width, height, texture, defaultSettings());
    }

    public static void renderSceneModels(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<SceneModel> sceneModels,
            final int width,
            final int height,
            final Image texture) {
        renderSceneModels(graphicsContext, camera, sceneModels, width, height, texture, defaultSettings());
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {
        renderModels(graphicsContext, camera, mesh, width, height, null);
    }

    public static void renderModels(
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
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();
        int[] colorBuffer = new int[width * height];
        float[] depthBuffer = new float[width * height];
        Rasterizer.clearBuffers(width, height, colorBuffer, depthBuffer, CLEAR_COLOR);

        TextureSampler textureSampler = (settings != null && settings.isUseTexture() && texture != null)
                ? new ImageTextureSampler(texture)
                : null;
        Vector3f cameraPos = camera.getPosition();
        Vector3f lightPos = (settings != null && settings.isUseLighting())
                ? new Vector3f(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ())
                : null;
        int baseColor = settings != null ? settings.getBaseColor() : BASE_COLOR;

        for (Model mesh : meshes) {
            Matrix4 modelMatrix = rotateScaleTranslate(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));
            Matrix4 modelViewProjectionMatrix = projectionMatrix.mult(viewMatrix).mult(modelMatrix);
            renderSingleModel(mesh, modelMatrix, modelViewProjectionMatrix, width, height, colorBuffer, depthBuffer, textureSampler, lightPos, baseColor, settings);
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

    public static void renderSceneModels(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<SceneModel> sceneModels,
            final int width,
            final int height,
            final Image texture,
            final RenderSettings settings) {
        // Point 2/16: render full scene with multiple models and cameras.
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        int[] colorBuffer = new int[width * height];
        float[] depthBuffer = new float[width * height];
        Rasterizer.clearBuffers(width, height, colorBuffer, depthBuffer, CLEAR_COLOR);

        TextureSampler textureSampler = (settings != null && settings.isUseTexture() && texture != null)
                ? new ImageTextureSampler(texture)
                : null;
        Vector3f cameraPos = camera.getPosition();
        Vector3f lightPos = (settings != null && settings.isUseLighting())
                ? new Vector3f(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ())
                : null;
        int baseColor = settings != null ? settings.getBaseColor() : BASE_COLOR;

        for (SceneModel sceneModel : sceneModels) {
            // Point 9/10: apply per-model transforms.
            Matrix4 modelMatrix = rotateScaleTranslate(
                    sceneModel.getTranslation(),
                    sceneModel.getRotation(),
                    sceneModel.getScale());
            Matrix4 modelViewProjectionMatrix = projectionMatrix.mult(viewMatrix).mult(modelMatrix);
            renderSingleModel(sceneModel.getModel(), modelMatrix, modelViewProjectionMatrix, width, height, colorBuffer, depthBuffer, textureSampler, lightPos, baseColor, settings);
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
            RenderSettings settings) {
        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            ArrayList<Integer> vertexIndices = mesh.polygons.get(polygonInd).getVertexIndices();
            if (vertexIndices.size() != 3) {
                continue;
            }

            ArrayList<Integer> textureIndices = mesh.polygons.get(polygonInd).getTextureVertexIndices();
            ArrayList<Integer> normalIndices = mesh.polygons.get(polygonInd).getNormalIndices();
            boolean hasTexture = textureIndices.size() == 3;
            boolean hasNormals = normalIndices.size() == 3;

            Rasterizer.Vertex[] vertices = new Rasterizer.Vertex[3];
            for (int i = 0; i < 3; i++) {
                int vertexIndex = vertexIndices.get(i);
                Vector3f vertex = mesh.vertices.get(vertexIndex);
                Vector3f ndc = transformToNdc(modelViewProjectionMatrix, vertex);
                Vector2f screenPoint = vertexToPoint(ndc, width, height);
                Vector4f world4 = modelMatrix.mult(new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F));
                Vector3f worldPos = new Vector3f(world4.getX(), world4.getY(), world4.getZ());

                Vector2f texCoord = null;
                if (hasTexture) {
                    int textureIndex = textureIndices.get(i);
                    texCoord = mesh.textureVertices.get(textureIndex);
                }

                Vector3f normal = null;
                if (hasNormals) {
                    int normalIndex = normalIndices.get(i);
                    Vector3f normalLocal = mesh.normals.get(normalIndex);
                    Vector4f normalWorld = modelMatrix.mult(new Vector4f(normalLocal.getX(), normalLocal.getY(), normalLocal.getZ(), 0.0F));
                    normal = new Vector3f(normalWorld.getX(), normalWorld.getY(), normalWorld.getZ()).normalize();
                }

                vertices[i] = new Rasterizer.Vertex(
                        screenPoint.getX(),
                        screenPoint.getY(),
                        ndc.getZ(),
                        worldPos,
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

            if (settings != null && settings.isDrawWireframe()) {
                // Point 15: optional polygon wireframe overlay.
                Rasterizer.rasterizeLine(vertices[0], vertices[1], width, height, colorBuffer, depthBuffer, 0xFFFFFFFF);
                Rasterizer.rasterizeLine(vertices[1], vertices[2], width, height, colorBuffer, depthBuffer, 0xFFFFFFFF);
                Rasterizer.rasterizeLine(vertices[2], vertices[0], width, height, colorBuffer, depthBuffer, 0xFFFFFFFF);
            }
        }
    }

    private static RenderSettings defaultSettings() {
        return new RenderSettings(false, true, true, BASE_COLOR);
    }
}
