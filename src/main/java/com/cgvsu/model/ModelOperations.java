package com.cgvsu.model;

import java.util.ArrayList;
import java.util.Iterator;

public class ModelOperations {
    public static boolean deletePolygon(Model model, int polygonIndex) {
        if (polygonIndex < 0 || polygonIndex >= model.polygons.size()) {
            return false;
        }
        model.polygons.remove(polygonIndex);
        return true;
    }

    public static boolean deleteVertex(Model model, int vertexIndex) {
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
}
