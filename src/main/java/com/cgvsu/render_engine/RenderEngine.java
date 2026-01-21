package com.cgvsu.render_engine;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;

import java.util.ArrayList;
import java.util.List;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    private static final int CLEAR_COLOR = 0xFF000000;
    private static final int BASE_COLOR = 0xFFB0B0B0;

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
        render(graphicsContext, camera, List.of(mesh), width, height, texture);
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height,
            final Image texture) {
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        int[] colorBuffer = new int[width * height];
        float[] depthBuffer = new float[width * height];
        Rasterizer.clearBuffers(width, height, colorBuffer, depthBuffer, CLEAR_COLOR);

        TextureSampler textureSampler = texture == null ? null : new ImageTextureSampler(texture);
        javax.vecmath.Vector3f cameraPos = camera.getPosition();
        Vector3f lightPos = new Vector3f(cameraPos.x, cameraPos.y, cameraPos.z);

        for (Model mesh : meshes) {
            renderSingleModel(mesh, modelViewProjectionMatrix, width, height, colorBuffer, depthBuffer, textureSampler, lightPos);
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
            Matrix4f modelViewProjectionMatrix,
            int width,
            int height,
            int[] colorBuffer,
            float[] depthBuffer,
            TextureSampler textureSampler,
            Vector3f lightPos) {
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
                javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(
                        vertex.getX(),
                        vertex.getY(),
                        vertex.getZ());
                javax.vecmath.Vector3f projected = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
                Point2f resultPoint = vertexToPoint(projected, width, height);

                Vector2f texCoord = null;
                if (hasTexture) {
                    int textureIndex = textureIndices.get(i);
                    texCoord = mesh.textureVertices.get(textureIndex);
                }

                Vector3f normal = null;
                if (hasNormals) {
                    int normalIndex = normalIndices.get(i);
                    normal = mesh.normals.get(normalIndex);
                }

                vertices[i] = new Rasterizer.Vertex(
                        resultPoint.x,
                        resultPoint.y,
                        projected.z,
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
                    BASE_COLOR,
                    textureSampler,
                    lightPos);
        }
    }
}
