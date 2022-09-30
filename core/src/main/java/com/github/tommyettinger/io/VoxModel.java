package com.github.tommyettinger.io;

import com.github.tommyettinger.Tools3D;
import com.github.tommyettinger.VoxMaterial;
import com.github.tommyettinger.ds.IntObjectMap;
import com.github.tommyettinger.ds.LongOrderedSet;

import java.util.ArrayList;
import java.util.Arrays;

public class VoxModel {
    public int[] palette;
    public ArrayList<byte[][][]> grids;
    public IntObjectMap<VoxMaterial> materials;
    public IntObjectMap<TransformChunk> transformChunks;
    public IntObjectMap<GroupChunk> groupChunks;
    public IntObjectMap<ShapeChunk> shapeChunks;
    public VoxModel(){
        palette = Arrays.copyOf(VoxIOExtended.defaultPalette, 256);
        grids = new ArrayList<>(1);
        materials = new IntObjectMap<>(256);
        transformChunks = new IntObjectMap<>(8);
        groupChunks = new IntObjectMap<>(1);
        shapeChunks = new IntObjectMap<>(8);
    }

    public VoxModel copy(){
        VoxModel next = new VoxModel();
        next.palette = Arrays.copyOf(palette, palette.length);
        ArrayList<byte[][][]> nextGrids = new ArrayList<>(grids.size());
        for (int i = 0; i < grids.size(); i++) {
            nextGrids.add(Tools3D.deepCopy(grids.get(i)));
        }
        next.grids = nextGrids;
        return next;
    }
}
