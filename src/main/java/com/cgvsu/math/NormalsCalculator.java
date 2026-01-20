package com.cgvsu.math;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;

public final class NormalsCalculator {
    private NormalsCalculator() {
    }

    public static void recalculateNormals(Model model) {
        int vertexCount = model.vertices.size();
        ArrayList<Vector3f> accumulators = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            accumulators.add(new Vector3f());
        }

        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> indices = polygon.getVertexIndices();
            if (indices.size() < 3) {
                continue;
            }
            Vector3f v0 = model.vertices.get(indices.get(0));
            Vector3f v1 = model.vertices.get(indices.get(1));
            Vector3f v2 = model.vertices.get(indices.get(2));
            Vector3f edge1 = v1.sub(v0);
            Vector3f edge2 = v2.sub(v0);
            Vector3f faceNormal = edge1.vectorProd(edge2);
            if (faceNormal.length() < 1e-7f) {
                continue;
            }
            for (Integer index : indices) {
                Vector3f current = accumulators.get(index);
                accumulators.set(index, current.add(faceNormal));
            }
        }

        ArrayList<Vector3f> normals = new ArrayList<>(vertexCount);
        for (Vector3f accumulator : accumulators) {
            normals.add(accumulator.normalize());
        }
        model.normals = normals;

        for (Polygon polygon : model.polygons) {
            polygon.setNormalIndices(new ArrayList<>(polygon.getVertexIndices()));
        }
    }
}
