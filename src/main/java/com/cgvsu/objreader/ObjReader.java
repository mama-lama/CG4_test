package com.cgvsu.objreader;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ObjReader {
    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";

    public static Model read(final String fileContent) {
        // Point 1: robust OBJ parsing with validation and line-aware errors.
        Model result = new Model();
        try (BufferedReader reader = new BufferedReader(new StringReader(fileContent))) {
            String line;
            int lineInd = 0;
            while ((line = reader.readLine()) != null) {
                lineInd++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] tokens = trimmed.split("\\s+");
                String token = tokens[0];
                List<String> args = new ArrayList<>();
                for (int i = 1; i < tokens.length; i++) {
                    args.add(tokens[i]);
                }

                switch (token) {
                    case OBJ_VERTEX_TOKEN -> result.vertices.add(parseVertex(args, lineInd));
                    case OBJ_TEXTURE_TOKEN -> result.textureVertices.add(parseTextureVertex(args, lineInd));
                    case OBJ_NORMAL_TOKEN -> result.normals.add(parseNormal(args, lineInd));
                    case OBJ_FACE_TOKEN -> result.polygons.add(parseFace(args, lineInd, result));
                    default -> {
                        // Ignore unsupported tokens.
                    }
                }
            }
        } catch (IOException e) {
            throw new ObjReaderException("Failed to read OBJ content.", 0);
        }
        return result;
    }

    protected static Vector3f parseVertex(final List<String> args, int lineInd) {
        if (args.size() < 3) {
            throw new ObjReaderException("Too few vertex arguments.", lineInd);
        }
        try {
            return new Vector3f(
                    Float.parseFloat(args.get(0)),
                    Float.parseFloat(args.get(1)),
                    Float.parseFloat(args.get(2)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);
        }
    }

    protected static Vector2f parseTextureVertex(final List<String> args, int lineInd) {
        if (args.size() < 2) {
            throw new ObjReaderException("Too few texture vertex arguments.", lineInd);
        }
        try {
            return new Vector2f(
                    Float.parseFloat(args.get(0)),
                    Float.parseFloat(args.get(1)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);
        }
    }

    protected static Vector3f parseNormal(final List<String> args, int lineInd) {
        if (args.size() < 3) {
            throw new ObjReaderException("Too few normal arguments.", lineInd);
        }
        try {
            return new Vector3f(
                    Float.parseFloat(args.get(0)),
                    Float.parseFloat(args.get(1)),
                    Float.parseFloat(args.get(2)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);
        }
    }

    protected static Polygon parseFace(final List<String> args, int lineInd, Model model) {
        // Point 1: faces can reference vertices/uvs/normals with flexible formats.
        if (args.size() < 3) {
            throw new ObjReaderException("Face has fewer than 3 vertices.", lineInd);
        }

        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Integer> textureIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();

        for (String word : args) {
            parseFaceWord(word, vertexIndices, textureIndices, normalIndices, lineInd, model);
        }

        Polygon result = new Polygon();
        result.setVertexIndices(vertexIndices);
        if (!textureIndices.isEmpty()) {
            result.setTextureVertexIndices(textureIndices);
        }
        if (!normalIndices.isEmpty()) {
            result.setNormalIndices(normalIndices);
        }
        return result;
    }

    protected static void parseFaceWord(
            String wordInLine,
            ArrayList<Integer> vertexIndices,
            ArrayList<Integer> textureIndices,
            ArrayList<Integer> normalIndices,
            int lineInd,
            Model model) {
        String[] wordIndices = wordInLine.split("/", -1);
        if (wordIndices.length < 1 || wordIndices.length > 3) {
            throw new ObjReaderException("Invalid face element size.", lineInd);
        }

        int vertexIndex = parseIndex(wordIndices[0], model.vertices.size(), lineInd);
        vertexIndices.add(vertexIndex);

        if (wordIndices.length >= 2 && !wordIndices[1].isEmpty()) {
            int textureIndex = parseIndex(wordIndices[1], model.textureVertices.size(), lineInd);
            textureIndices.add(textureIndex);
        }
        if (wordIndices.length == 3 && !wordIndices[2].isEmpty()) {
            int normalIndex = parseIndex(wordIndices[2], model.normals.size(), lineInd);
            normalIndices.add(normalIndex);
        }
    }

    private static int parseIndex(String rawIndex, int size, int lineInd) {
        try {
            int index = Integer.parseInt(rawIndex);
            if (index == 0) {
                throw new ObjReaderException("Index must be non-zero.", lineInd);
            }
            if (index < 0) {
                index = size + index;
            } else {
                index = index - 1;
            }
            if (index < 0 || index >= size) {
                throw new ObjReaderException("Index out of bounds.", lineInd);
            }
            return index;
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse int value.", lineInd);
        }
    }
}
