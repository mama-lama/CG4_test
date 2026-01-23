package com.cgvsu.model;

import java.util.ArrayList;
import java.util.Iterator;

public class ModelOperations {
    public static boolean deletePolygon(Model model, int polygonIndex) {
        // Point 3: remove a polygon by index.
        if (polygonIndex < 0 || polygonIndex >= model.polygons.size()) {
            return false;
        }
        model.polygons.remove(polygonIndex);
        return true;
    }

    public static boolean deleteVertex(Model model, int vertexIndex) {
        // Point 3: remove a vertex and clean affected polygons.
        if (vertexIndex < 0 || vertexIndex >= model.vertices.size()) {
            return false;
        }

        model.vertices.remove(vertexIndex);

        Iterator<Polygon> iterator = model.polygons.iterator();
        while (iterator.hasNext()) {
            Polygon polygon = iterator.next();
            ArrayList<Integer> indices = polygon.getVertexIndices();
            boolean usesRemoved = false;
            for (int index : indices) {
                if (index == vertexIndex) {
                    usesRemoved = true;
                    break;
                }
            }
            if (usesRemoved) {
                iterator.remove();
                continue;
            }

            for (int i = 0; i < indices.size(); i++) {
                int oldIndex = indices.get(i);
                if (oldIndex > vertexIndex) {
                    indices.set(i, oldIndex - 1);
                }
            }
        }

        return true;
    }

    public static Model createTransformedCopy(Model source, com.cgvsu.math.matrices.Matrix4 matrix) {
        // Point 10: export with model transforms applied.
        Model copy = new Model();

        for (com.cgvsu.math.vectors.Vector3f vertex : source.vertices) {
            com.cgvsu.math.vectors.Vector4f transformed = matrix.mult(
                    new com.cgvsu.math.vectors.Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F));
            copy.vertices.add(new com.cgvsu.math.vectors.Vector3f(
                    transformed.getX(), transformed.getY(), transformed.getZ()));
        }

        copy.textureVertices.addAll(source.textureVertices);

        for (com.cgvsu.math.vectors.Vector3f normal : source.normals) {
            com.cgvsu.math.vectors.Vector4f transformed = matrix.mult(
                    new com.cgvsu.math.vectors.Vector4f(normal.getX(), normal.getY(), normal.getZ(), 0.0F));
            copy.normals.add(new com.cgvsu.math.vectors.Vector3f(
                    transformed.getX(), transformed.getY(), transformed.getZ()).normalize());
        }

        for (Polygon polygon : source.polygons) {
            Polygon copyPolygon = new Polygon();
            copyPolygon.setVertexIndices(new ArrayList<>(polygon.getVertexIndices()));
            copyPolygon.setTextureVertexIndices(new ArrayList<>(polygon.getTextureVertexIndices()));
            copyPolygon.setNormalIndices(new ArrayList<>(polygon.getNormalIndices()));
            copy.polygons.add(copyPolygon);
        }

        return copy;
    }

    public static void copyInto(Model source, Model target) {
        target.vertices = new ArrayList<>();
        for (com.cgvsu.math.vectors.Vector3f vertex : source.vertices) {
            target.vertices.add(new com.cgvsu.math.vectors.Vector3f(
                    vertex.getX(), vertex.getY(), vertex.getZ()));
        }

        target.textureVertices = new ArrayList<>();
        for (com.cgvsu.math.vectors.Vector2f tex : source.textureVertices) {
            target.textureVertices.add(new com.cgvsu.math.vectors.Vector2f(
                    tex.getX(), tex.getY()));
        }

        target.normals = new ArrayList<>();
        for (com.cgvsu.math.vectors.Vector3f normal : source.normals) {
            target.normals.add(new com.cgvsu.math.vectors.Vector3f(
                    normal.getX(), normal.getY(), normal.getZ()));
        }

        target.polygons = new ArrayList<>();
        for (Polygon polygon : source.polygons) {
            Polygon copyPolygon = new Polygon();
            copyPolygon.setVertexIndices(new ArrayList<>(polygon.getVertexIndices()));
            copyPolygon.setTextureVertexIndices(new ArrayList<>(polygon.getTextureVertexIndices()));
            copyPolygon.setNormalIndices(new ArrayList<>(polygon.getNormalIndices()));
            target.polygons.add(copyPolygon);
        }
    }
}
