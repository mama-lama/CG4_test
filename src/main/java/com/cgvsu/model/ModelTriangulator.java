package com.cgvsu.model;

import java.util.ArrayList;

public final class ModelTriangulator {
    private ModelTriangulator() {
    }

    public static void triangulate(Model model) {
        // Ксюня: триангуляция полигонов для подготовки к растеризации (пункт 12).
        ArrayList<Polygon> triangulated = new ArrayList<>();
        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            int vertexCount = vertexIndices.size();
            if (vertexCount < 3) {
                continue;
            }
            if (vertexCount == 3) {
                triangulated.add(polygon);
                continue;
            }

            ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();
            boolean hasTexture = textureIndices.size() == vertexCount;
            boolean hasNormals = normalIndices.size() == vertexCount;

            for (int i = 1; i < vertexCount - 1; i++) {
                Polygon triangle = new Polygon();
                triangle.setVertexIndices(triangleIndices(vertexIndices, 0, i, i + 1));
                if (hasTexture) {
                    triangle.setTextureVertexIndices(triangleIndices(textureIndices, 0, i, i + 1));
                }
                if (hasNormals) {
                    triangle.setNormalIndices(triangleIndices(normalIndices, 0, i, i + 1));
                }
                triangulated.add(triangle);
            }
        }
        model.polygons = triangulated;
    }

    private static ArrayList<Integer> triangleIndices(ArrayList<Integer> indices, int a, int b, int c) {
        ArrayList<Integer> result = new ArrayList<>(3);
        result.add(indices.get(a));
        result.add(indices.get(b));
        result.add(indices.get(c));
        return result;
    }
}
