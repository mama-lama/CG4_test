package com.cgvsu.objwriter;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {
    public static void write(Model model, Path path) throws IOException {
        Files.writeString(path, write(model));
    }

    public static String write(Model model) {
        // Point 1: write OBJ with vertices, uvs, normals, and faces.
        StringBuilder builder = new StringBuilder();

        for (Vector3f vertex : model.vertices) {
            builder.append("v ")
                    .append(formatFloat(vertex.getX())).append(" ")
                    .append(formatFloat(vertex.getY())).append(" ")
                    .append(formatFloat(vertex.getZ()))
                    .append("\n");
        }

        for (Vector2f tex : model.textureVertices) {
            builder.append("vt ")
                    .append(formatFloat(tex.getX())).append(" ")
                    .append(formatFloat(tex.getY()))
                    .append("\n");
        }

        for (Vector3f normal : model.normals) {
            builder.append("vn ")
                    .append(formatFloat(normal.getX())).append(" ")
                    .append(formatFloat(normal.getY())).append(" ")
                    .append(formatFloat(normal.getZ()))
                    .append("\n");
        }

        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

            int vertexCount = vertexIndices.size();
            boolean hasTextures = textureIndices.size() == vertexCount;
            boolean hasNormals = normalIndices.size() == vertexCount;

            builder.append("f");
            for (int i = 0; i < vertexCount; i++) {
                int vIndex = vertexIndices.get(i) + 1;
                builder.append(" ").append(vIndex);
                if (hasTextures || hasNormals) {
                    builder.append("/");
                    if (hasTextures) {
                        builder.append(textureIndices.get(i) + 1);
                    }
                    if (hasNormals) {
                        builder.append("/").append(normalIndices.get(i) + 1);
                    }
                }
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private static String formatFloat(float value) {
        return String.format(Locale.US, "%s", Float.toString(value));
    }
}
