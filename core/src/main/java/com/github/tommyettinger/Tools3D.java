package com.github.tommyettinger;

import com.github.tommyettinger.ds.support.Base;
import com.github.yellowstonegames.core.Hasher;

import java.util.Arrays;

import static com.github.yellowstonegames.core.Hasher.*;

/**
 * Just laying some foundation for 3D array manipulation.
 * Created by Tommy Ettinger on 11/2/2017.
 */
public class Tools3D {
    public static byte[][][] deepCopy(byte[][][] voxels)
    {
        int xs, ys, zs;
        byte[][][] next = new byte[xs = voxels.length][ys = voxels[0].length][zs = voxels[0][0].length];
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                System.arraycopy(voxels[x][y], 0, next[x][y], 0, zs);
            }
        }
        return next;
    }

    public static byte[][][] deepCopyInto(byte[][][] voxels, byte[][][] target)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                System.arraycopy(voxels[x][y], 0, target[x][y], 0, zs);
            }
        }
        return target;
    }
    public static void fill(byte[][][] array3d, int value) {
        final int depth = array3d.length;
        final int breadth = depth == 0 ? 0 : array3d[0].length;
        final int height = breadth == 0 ? 0 : array3d[0][0].length;
        if(depth > 0 && breadth > 0) {
            Arrays.fill(array3d[0][0], (byte)value);
        }
        for (int y = 1; y < breadth; y++) {
            System.arraycopy(array3d[0][0], 0, array3d[0][y], 0, height);
        }
        for (int x = 1; x < depth; x++) {
            for (int y = 0; y < breadth; y++) {
                System.arraycopy(array3d[0][0], 0, array3d[x][y], 0, height);
            }
        }
    }
    public static void fill(float[][][] array3d, float value) {
        final int depth = array3d.length;
        final int breadth = depth == 0 ? 0 : array3d[0].length;
        final int height = breadth == 0 ? 0 : array3d[0][0].length;
        if(depth > 0 && breadth > 0) {
            Arrays.fill(array3d[0][0], value);
        }
        for (int y = 1; y < breadth; y++) {
            System.arraycopy(array3d[0][0], 0, array3d[0][y], 0, height);
        }
        for (int x = 1; x < depth; x++) {
            for (int y = 0; y < breadth; y++) {
                System.arraycopy(array3d[0][0], 0, array3d[x][y], 0, height);
            }
        }
    }

    public static byte[][][] rotate(byte[][][] voxels, int turns)
    {
        int xs, ys, zs;
        byte[][][] next = new byte[xs = voxels.length][ys = voxels[0].length][zs = voxels[0][0].length];
        switch (turns & 3)
        {
            case 0:
                return deepCopy(voxels);
            case 1:
            {
                for (int x = 0; x < xs; x++) {
                    for (int y = 0; y < ys; y++) {
                        System.arraycopy(voxels[y][xs - 1 - x], 0, next[x][y], 0, zs);
                    }
                }
            }
            break;
            case 2:
            {
                for (int x = 0; x < xs; x++) {
                    for (int y = 0; y < ys; y++) {
                        System.arraycopy(voxels[xs - 1 - x][ys - 1 - y], 0, next[x][y], 0, zs);
                    }
                }
            }
            break;
            case 3:
            {
                for (int x = 0; x < xs; x++) {
                    for (int y = 0; y < ys; y++) {
                        System.arraycopy(voxels[ys - 1 - y][x], 0, next[x][y], 0, zs);
                    }
                }
            }
            break;
        }
        return next;
    }

    public static byte[][][] clockwiseInPlace(byte[][][] data) {
        final int size = data.length - 1, halfSizeXYOdd = size + 2 >>> 1, halfSizeXYEven = size + 1 >>> 1;
        byte c;
        for (int z = 0; z <= size; z++) {
            for (int x = 0; x < halfSizeXYOdd; x++) {
                for (int y = 0; y < halfSizeXYEven; y++) {

                    c = data[x][y][z];
                    data[x][y][z] = data[y][size - x][z];
                    data[y][size - x][z] = data[size - x][size - y][z];
                    data[size - x][size - y][z] = data[size - y][x][z];
                    data[size - y][x][z] = c;
                }
            }
        }
        return data;
    }

    public static byte[][][] mirrorX(byte[][][] voxels)
    {
        int xs, ys, zs;
        byte[][][] next = new byte[(xs = voxels.length) << 1][ys = voxels[0].length][zs = voxels[0][0].length];
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                System.arraycopy(voxels[x][y], 0, next[x][y], 0, zs);
                System.arraycopy(voxels[x][y], 0, next[(xs << 1) - 1 - x][y], 0, zs);
            }
        }
        return next;
    }

    public static byte[][][] mirrorY(byte[][][] voxels)
    {
        int xs, ys, zs;
        byte[][][] next = new byte[xs = voxels.length][(ys = voxels[0].length) << 1][zs = voxels[0][0].length];
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                System.arraycopy(voxels[x][y], 0, next[x][y], 0, zs);
                System.arraycopy(voxels[x][y], 0, next[x][(ys << 1) - 1 - y], 0, zs);
            }
        }
        return next;
    }

    public static byte[][][] mirrorXY(byte[][][] voxels)
    {
        int xs, ys, zs;
        byte[][][] next = new byte[(xs = voxels.length) << 1][(ys = voxels[0].length) << 1][zs = voxels[0][0].length];
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                System.arraycopy(voxels[x][y], 0, next[x][y], 0, zs);
                System.arraycopy(voxels[x][y], 0, next[(xs << 1) - 1 - x][y], 0, zs);
                System.arraycopy(voxels[x][y], 0, next[x][(ys << 1) - 1 - y], 0, zs);
                System.arraycopy(voxels[x][y], 0, next[(xs << 1) - 1 - x][(ys << 1) - 1 - y], 0, zs);
            }
        }
        return next;
    }


    public static int countNot(byte[][][] voxels, int avoid)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        int c = 0;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if(voxels[x][y][z] != avoid) ++c;
                }
            }
        }
        return c;
    }
    public static int count(byte[][][] voxels)
    {
        return countNot(voxels, 0);
    }
    public static int count(byte[][][] voxels, int match)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        int c = 0;
        byte m = (byte)match;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if(voxels[x][y][z] == m) ++c;
                }
            }
        }
        return c;
    }
    public static byte[][][] runCA(byte[][][] voxels, int smoothLevel)
    {
        if(smoothLevel < 1)
            return voxels;
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        //Dictionary<byte, int> colorCount = new Dictionary<byte, int>();
        int[] colorCount = new int[256];
        byte[][][] vs0 = deepCopy(voxels), vs1 = new byte[xs][ys][zs];
        for(int v = 0; v < smoothLevel; v++)
        {
            if(v >= 1)
            {
                deepCopyInto(vs1, vs0);
                //fetch(vs1, (byte) 0);
            }
            for(int x = 0; x < xs; x++)
            {
                for(int y = 0; y < ys; y++)
                {
                    for(int z = 0; z < zs; z++)
                    {
                        Arrays.fill(colorCount, 0);
                        if(x == 0 || y == 0 || z == 0 || x == xs - 1 || y == ys - 1 || z == zs - 1 || vs0[x][y][z] == 2)
                        {
                            colorCount[vs0[x][y][z] & 255] = 10000;
                            colorCount[0] = -100000;
                        }
                        else
                        {
                            for(int xx = -1; xx < 2; xx++)
                            {
                                for(int yy = -1; yy < 2; yy++)
                                {
                                    for(int zz = -1; zz < 2; zz++)
                                    {
                                        byte smallColor = vs0[x + xx][y + yy][z + zz];
                                        colorCount[smallColor & 255]++;
                                    }
                                }
                            }
                        }
                        if(colorCount[0] >= 23)
                        {
                            vs1[x][y][z] = 0;
                        }
                        else
                        {
                            byte max = 0;
                            int cc = colorCount[0] / 3, tmp;
                            for(byte idx = 1; idx != 0; idx++)
                            {
                                tmp = colorCount[idx & 255];
                                if(tmp > 0 && tmp > cc)
                                {
                                    cc = tmp;
                                    max = idx;
                                }
                            }
                            vs1[x][y][z] = max;
                        }
                    }
                }
            }
        }
        return vs1;
    }

    private static void writeSlope(byte[][][] voxels, int x, int y, int z, int slope, byte color){
        voxels[x<<1][y<<1][z<<1] = ((slope & 1) != 0) ? color : 0;
        voxels[x<<1|1][y<<1][z<<1] = ((slope & 2) != 0) ? color : 0;
        voxels[x<<1][y<<1|1][z<<1] = ((slope & 4) != 0) ? color : 0;
        voxels[x<<1|1][y<<1|1][z<<1] = ((slope & 8) != 0) ? color : 0;
        voxels[x<<1][y<<1][z<<1|1] = ((slope & 16) != 0) ? color : 0;
        voxels[x<<1|1][y<<1][z<<1|1] = ((slope & 32) != 0) ? color : 0;
        voxels[x<<1][y<<1|1][z<<1|1] = ((slope & 64) != 0) ? color : 0;
        voxels[x<<1|1][y<<1|1][z<<1|1] = ((slope & 128) != 0) ? color : 0;
    }

    public static byte[][][] smoothScale(byte[][][] voxels){
        final int limitX = voxels.length - 1;
        final int limitY = voxels[0].length - 1;
        final int limitZ = voxels[0][0].length - 1;
        byte[][][] nextColors = new byte[limitX+1][limitY+1][limitZ+1];
        byte[][][] nextSlopes = new byte[limitX+1][limitY+1][limitZ+1];
        byte[][][] result = new byte[limitX+1<<1][limitY+1<<1][limitZ+1<<1];
        final int[] neighbors = new int[6];
        for (int x = 0; x <= limitX; x++) {
            for (int y = 0; y <= limitY; y++) {
                PER_CELL:
                for (int z = 0; z <= limitZ; z++) {
                    if(voxels[x][y][z] == 0)
                    {
                        int slope = 0;
                        if((neighbors[0] = x == 0 ? 0 : (voxels[x-1][y][z] & 255)) != 0) slope      |= 0x55;
                        if((neighbors[1] = y == 0 ? 0 : (voxels[x][y-1][z] & 255)) != 0) slope      |= 0x33;
                        if((neighbors[2] = z == 0 ? 0 : (voxels[x][y][z-1] & 255)) != 0) slope      |= 0x0F;
                        if((neighbors[3] = x == limitX ? 0 : (voxels[x+1][y][z] & 255)) != 0) slope |= 0xAA;
                        if((neighbors[4] = y == limitY ? 0 : (voxels[x][y+1][z] & 255)) != 0) slope |= 0xCC;
                        if((neighbors[5] = z == limitZ ? 0 : (voxels[x][y][z+1] & 255)) != 0) slope |= 0xF0;
                        if(Integer.bitCount(slope) < 5) // surrounded by empty or next to only one voxel
                        {
                            nextSlopes[x][y][z] = 0;
                            continue;
                        }
                        int bestIndex = -1;
                        for (int i = 0; i < 6; i++) {
                            if(neighbors[i] == 0) continue;
                            if(bestIndex == -1) bestIndex = i;
                            for (int j = i + 1; j < 6; j++) {
                                if(neighbors[i] == neighbors[j]){
                                    if((i == bestIndex || j == bestIndex) && neighbors[bestIndex] != 0) {
                                        nextColors[x][y][z] = (byte) neighbors[bestIndex];
                                        nextSlopes[x][y][z] = (byte) slope;
                                        continue PER_CELL;
                                    }
                                } else if(neighbors[bestIndex] < neighbors[i]) {
                                    bestIndex = i;
                                }
                            }
                        }
                        nextColors[x][y][z] = (byte) neighbors[bestIndex];
                        nextSlopes[x][y][z] = (byte) slope;
                    }
                    else
                    {
                        nextColors[x][y][z] = voxels[x][y][z];
                        nextSlopes[x][y][z] = -1;
                    }
                }
            }
        }

        for (int x = 0; x <= limitX; x++) {
            for (int y = 0; y <= limitY; y++) {
                PER_CELL:
                for (int z = 0; z <= limitZ; z++) {
                    if(nextColors[x][y][z] == 0)
                    {
                        int slope = 0;
                        if((neighbors[0] = x == 0 ? 0 : (nextColors[x-1][y][z] & 255)) != 0 && (nextSlopes[x-1][y][z] & 0xAA) != 0xAA) slope      |= (nextSlopes[x-1][y][z] & 0xAA) >>> 1;
                        if((neighbors[1] = y == 0 ? 0 : (nextColors[x][y-1][z] & 255)) != 0 && (nextSlopes[x][y-1][z] & 0xCC) != 0xCC) slope      |= (nextSlopes[x][y-1][z] & 0xCC) >>> 2;
                        if((neighbors[2] = z == 0 ? 0 : (nextColors[x][y][z-1] & 255)) != 0 && (nextSlopes[x][y][z-1] & 0xF0) != 0xF0) slope      |= (nextSlopes[x][y][z-1] & 0xF0) >>> 4;
                        if((neighbors[3] = x == limitX ? 0 : (nextColors[x+1][y][z] & 255)) != 0 && (nextSlopes[x+1][y][z] & 0x55) != 0x55) slope |= (nextSlopes[x+1][y][z] & 0x55) << 1;
                        if((neighbors[4] = y == limitY ? 0 : (nextColors[x][y+1][z] & 255)) != 0 && (nextSlopes[x][y+1][z] & 0x33) != 0x33) slope |= (nextSlopes[x][y+1][z] & 0x33) << 2;
                        if((neighbors[5] = z == limitZ ? 0 : (nextColors[x][y][z+1] & 255)) != 0 && (nextSlopes[x][y][z+1] & 0x0F) != 0x0F) slope |= (nextSlopes[x][y][z+1] & 0x0F) << 4;
                        if(Integer.bitCount(slope) < 4) // surrounded by empty or only one partial face
                        {
                            writeSlope(result, x, y, z, -1, (byte) 0);
                            continue;
                        }
                        int bestIndex = -1;
                        for (int i = 0; i < 6; i++) {
                            if(neighbors[i] == 0) continue;
                            if(bestIndex == -1) bestIndex = i;
                            for (int j = i + 1; j < 6; j++) {
                                if(neighbors[i] == neighbors[j]){
                                    if((i == bestIndex || j == bestIndex) && neighbors[bestIndex] != 0) {
                                        writeSlope(result, x, y, z, slope, (byte) neighbors[bestIndex]);
                                        continue PER_CELL;
                                    }
                                } else if(neighbors[bestIndex] < neighbors[i]) {
                                    bestIndex = i;
                                }
                            }
                        }
                        writeSlope(result, x, y, z, slope, (byte) neighbors[bestIndex]);
                    }
                    else
                    {
                        writeSlope(result, x, y, z, nextSlopes[x][y][z], nextColors[x][y][z]);
                    }
                }
            }
        }
        return result;
    }


    public static byte[][][] simpleScale(byte[][][] voxels) {
        return simpleScale(voxels, new byte[voxels.length << 1][voxels[0].length << 1][voxels[0][0].length << 1]);
    }
    public static byte[][][] simpleScale(byte[][][] voxels, byte[][][] result) {
        final int limitX = voxels.length - 1;
        final int limitY = voxels[0].length - 1;
        final int limitZ = voxels[0][0].length - 1;
        byte[][][] nextColors = new byte[limitX+1][limitY+1][limitZ+1];
        byte[][][] nextSlopes = new byte[limitX+1][limitY+1][limitZ+1];
        final int[] neighbors = new int[6];
        for (int x = 0; x <= limitX; x++) {
            for (int y = 0; y <= limitY; y++) {
                PER_CELL:
                for (int z = 0; z <= limitZ; z++) {
                    if(voxels[x][y][z] == 0)
                    {
                        int slope = 0;
                        if((neighbors[0] = x == 0 ? 0 : (voxels[x-1][y][z] & 255)) != 0) slope      |= 0x55;
                        if((neighbors[1] = y == 0 ? 0 : (voxels[x][y-1][z] & 255)) != 0) slope      |= 0x33;
                        if((neighbors[2] = z == 0 ? 0 : (voxels[x][y][z-1] & 255)) != 0) slope      |= 0x0F;
                        if((neighbors[3] = x == limitX ? 0 : (voxels[x+1][y][z] & 255)) != 0) slope |= 0xAA;
                        if((neighbors[4] = y == limitY ? 0 : (voxels[x][y+1][z] & 255)) != 0) slope |= 0xCC;
                        if((neighbors[5] = z == limitZ ? 0 : (voxels[x][y][z+1] & 255)) != 0) slope |= 0xF0;
                        if(Integer.bitCount(slope) < 5) // surrounded by empty or next to only one voxel
                        {
                            nextSlopes[x][y][z] = 0;
                            continue;
                        }
                        int bestIndex = -1;
                        for (int i = 0; i < 6; i++) {
                            if(neighbors[i] == 0) continue;
                            if(bestIndex == -1) bestIndex = i;
                            for (int j = i + 1; j < 6; j++) {
                                if(i + 3 != j && neighbors[i] == neighbors[j]){
                                    if((i == bestIndex || j == bestIndex) && neighbors[bestIndex] != 0) {
                                        nextColors[x][y][z] = (byte) neighbors[bestIndex];
                                        nextSlopes[x][y][z] = (byte) slope;
                                        continue PER_CELL;
                                    }
                                } else if(neighbors[bestIndex] < neighbors[i]) {
                                    bestIndex = i;
                                }
                            }
                        }
                        nextColors[x][y][z] = (byte) 0;
                        nextSlopes[x][y][z] = (byte) 0;
//                        nextColors[x][y][z] = (byte) neighbors[bestIndex];
//                        nextSlopes[x][y][z] = (byte) slope;
                    }
                    else
                    {
                        nextColors[x][y][z] = voxels[x][y][z];
                        nextSlopes[x][y][z] = -1;
                    }
                }
            }
        }

        for (int x = 0; x <= limitX; x++) {
            for (int y = 0; y <= limitY; y++) {
                PER_CELL:
                for (int z = 0; z <= limitZ; z++) {
                    if(nextColors[x][y][z] == 0)
                    {
                        int slope = 0;
                        if((neighbors[0] = x == 0 ? 0 : (nextColors[x-1][y][z] & 255)) != 0 && (nextSlopes[x-1][y][z] & 0xAA) != 0xAA) slope      |= (nextSlopes[x-1][y][z] & 0xAA) >>> 1;
                        if((neighbors[1] = y == 0 ? 0 : (nextColors[x][y-1][z] & 255)) != 0 && (nextSlopes[x][y-1][z] & 0xCC) != 0xCC) slope      |= (nextSlopes[x][y-1][z] & 0xCC) >>> 2;
                        if((neighbors[2] = z == 0 ? 0 : (nextColors[x][y][z-1] & 255)) != 0 && (nextSlopes[x][y][z-1] & 0xF0) != 0xF0) slope      |= (nextSlopes[x][y][z-1] & 0xF0) >>> 4;
                        if((neighbors[3] = x == limitX ? 0 : (nextColors[x+1][y][z] & 255)) != 0 && (nextSlopes[x+1][y][z] & 0x55) != 0x55) slope |= (nextSlopes[x+1][y][z] & 0x55) << 1;
                        if((neighbors[4] = y == limitY ? 0 : (nextColors[x][y+1][z] & 255)) != 0 && (nextSlopes[x][y+1][z] & 0x33) != 0x33) slope |= (nextSlopes[x][y+1][z] & 0x33) << 2;
                        if((neighbors[5] = z == limitZ ? 0 : (nextColors[x][y][z+1] & 255)) != 0 && (nextSlopes[x][y][z+1] & 0x0F) != 0x0F) slope |= (nextSlopes[x][y][z+1] & 0x0F) << 4;
                        if(Integer.bitCount(slope) < 4) // surrounded by empty or only one partial face
                        {
                            writeSlope(result, x, y, z, -1, (byte) 0);
                            continue;
                        }
                        int bestIndex = -1;
                        for (int i = 0; i < 6; i++) {
                            if(neighbors[i] == 0) continue;
                            if(bestIndex == -1) bestIndex = i;
                            for (int j = i + 1; j < 6; j++) {
                                if(i + 3 != j && neighbors[i] == neighbors[j]){
                                    if((i == bestIndex || j == bestIndex) && neighbors[bestIndex] != 0) {
                                        writeSlope(result, x, y, z, slope, (byte) neighbors[bestIndex]);
                                        continue PER_CELL;
                                    }
                                } else if(neighbors[bestIndex] < neighbors[i]) {
                                    bestIndex = i;
                                }
                            }
                        }
//                        writeSlope(result, x, y, z, slope, (byte) neighbors[bestIndex]);
                    }
                    else
                    {
                        writeSlope(result, x, y, z, nextSlopes[x][y][z], nextColors[x][y][z]);
                    }
                }
            }
        }
        return result;
    }

    public static int firstTight(byte[][][] voxels)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if (voxels[x][y][z] != 0)
                        return zs * (x * ys + y) + z;
                }
            }
        }
        return -1;
    }

    public static void findConnectors(byte[][][] voxels, int[] connectors)
    {
        Arrays.fill(connectors, -1);
        int curr;
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    curr = voxels[x][y][z] & 255;
                    if(curr >= 8 && curr < 16)
                        connectors[curr - 8] = zs * (x * ys + y) + z;
                    else if(curr >= 136 && curr < 144)
                        connectors[curr - 128] = zs * (x * ys + y) + z;
                }
            }
        }
    }
    
    public static int flood(byte[][][] base, byte[][][] bounds)
    {
        final int xs = base.length, ys = base[0].length, zs = base[0][0].length;
        int size = count(base), totalSize = 0;
        /*
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if(base[x][y][z] != 0 && bounds[x][y][z] != 0)
                        size++;
                }
            }
        }
        */

        byte[][][] nx = deepCopy(base);
        byte t;
        do {
            totalSize += size;
            size = 0;
            for (int x = 0; x < xs; x++) {
                for (int y = 0; y < ys; y++) {
                    for (int z = 0; z < zs; z++) {
                        if (nx[x][y][z] != 0 && (t = bounds[x][y][z]) != 0) {
                            nx[x][y][z] = t;
                            //++size;
                            if (x > 0 && nx[x - 1][y][z] == 0 && (t = bounds[x - 1][y][z]) != 0) {
                                nx[x - 1][y][z] = t;
                                ++size;
                            }
                            if (x < xs - 1 && nx[x + 1][y][z] == 0 && (t = bounds[x + 1][y][z]) != 0) {
                                nx[x + 1][y][z] = t;
                                ++size;
                            }
                            if (y > 0 && nx[x][y - 1][z] == 0 && (t = bounds[x][y - 1][z]) != 0) {
                                nx[x][y - 1][z] = t;
                                ++size;
                            }
                            if (y < ys - 1 && nx[x][y + 1][z] == 0 && (t = bounds[x][y + 1][z]) != 0) {
                                nx[x][y + 1][z] = t;
                                ++size;
                            }
                            if (z > 0 && nx[x][y][z - 1] == 0 && (t = bounds[x][y][z - 1]) != 0) {
                                nx[x][y][z - 1] = t;
                                ++size;
                            }
                            if (z < zs - 1 && nx[x][y][z + 1] == 0 && (t = bounds[x][y][z + 1]) != 0) {
                                nx[x][y][z + 1] = t;
                                ++size;
                            }
                        }
                    }
                }
            }
        } while (size != 0);
        deepCopyInto(nx, base);
        return totalSize + size;
    }

    public static byte[][][] largestPart(byte[][][] voxels)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        int fst = firstTight(voxels), bestSize = 0, currentSize, x, y, z;

        byte[][][] remaining = deepCopy(voxels), filled = new byte[xs][ys][zs],
                choice = new byte[xs][ys][zs];
        while (fst >= 0) {
            fill(filled, 0);
            x = fst / (ys * zs);
            y = (fst / zs) % ys;
            z = fst % zs;
            filled[x][y][z] = voxels[x][y][z];
            currentSize = flood(filled, remaining);
            if(currentSize > bestSize)
            {
                bestSize = currentSize;
                deepCopyInto(filled, choice);
            }

            for (x = 0; x < xs; x++) {
                for (y = 0; y < ys; y++) {
                    for (z = 0; z < zs; z++) {
                        if(filled[x][y][z] != 0)
                            remaining[x][y][z] = 0;
                    }
                }
            }
            fst = firstTight(remaining);
        }
        return choice;
    }

    public static byte[][][] translateCopy(byte[][][] voxels, int xMove, int yMove, int zMove)
    {
        int xs, ys, zs;
        byte[][][] next = new byte[xs = voxels.length][ys = voxels[0].length][zs = voxels[0][0].length];
        final int xLimit = xs - Math.abs(xMove), xStart = Math.max(0, -xMove);
        final int yLimit = ys - Math.abs(yMove), yStart = Math.max(0, -yMove);
        final int zLimit = zs - Math.abs(zMove), zStart = Math.max(0, -zMove), zShift = Math.max(0, zMove);
        if(zLimit <= 0)
            return next;
        for (int x = xStart, xx = 0; x < xs && xx < xLimit && xx < xs; x++, xx++) {
            for (int y = yStart, yy = 0; y < ys && yy < yLimit && yy < ys; y++, yy++) {
                System.arraycopy(voxels[x][y], zStart, next[xx][yy], zShift, zLimit);
            }
        }
        return next;

    }

    public static void translateCopyInto(byte[][][] voxels, byte[][][] into, int xMove, int yMove, int zMove) {
        int xs, ys, zs;
        xs = into.length;
        ys = into[0].length;
        zs = into[0][0].length;
        final int xLimit = voxels.length;
        final int yLimit = voxels[0].length;
        final int zLimit = voxels[0][0].length;
        for (int x = xMove, xx = 0; x < xs && xx < xLimit && xx < xs; x++, xx++) {
            if(x < 0) continue;
            for (int y = yMove, yy = 0; y < ys && yy < yLimit && yy < ys; y++, yy++) {
                if(y < 0) continue;
                for (int z = zMove, zz = 0; z < zs && zz < zLimit && zz < zs; z++, zz++) {
                    if(z < 0) continue;
                    if (into[x][y][z] == 0 && voxels[xx][yy][zz] != 0)
                        into[x][y][z] = voxels[xx][yy][zz];
                }
            }
        }
    }

    private static int isSurface(byte[][][] voxels, int x, int y, int z) {
        int v;
        if(x < 0 || y < 0 || z < 0 ||
                x >= voxels.length || y >= voxels[x].length || z >= voxels[x][y].length ||
                voxels[x][y][z] == 0)
            return 0;
        if(x <= 0 || (v = voxels[x-1][y][z] & 255) == 0 || VoxIO.lastMaterials.get(v).getTrait(VoxMaterial.MaterialTrait._alpha) >= 1f) return 1;
        if(y <= 0 || (v = voxels[x][y-1][z] & 255) == 0 || VoxIO.lastMaterials.get(v).getTrait(VoxMaterial.MaterialTrait._alpha) >= 1f) return 2;
        if(z <= 0 || (v = voxels[x][y][z-1] & 255) == 0 || VoxIO.lastMaterials.get(v).getTrait(VoxMaterial.MaterialTrait._alpha) >= 1f) return 3;
        if(x >= voxels.length - 1       || (v = voxels[x+1][y][z] & 255) == 0 || VoxIO.lastMaterials.get(v).getTrait(VoxMaterial.MaterialTrait._alpha) >= 1f) return 4;
        if(y >= voxels[x].length - 1    || (v = voxels[x][y+1][z] & 255) == 0 || VoxIO.lastMaterials.get(v).getTrait(VoxMaterial.MaterialTrait._alpha) >= 1f) return 5;
        if(z >= voxels[x][y].length - 1 || (v = voxels[x][y][z+1] & 255) == 0 || VoxIO.lastMaterials.get(v).getTrait(VoxMaterial.MaterialTrait._alpha) >= 1f) return 6;
        return -1;
    }

    public static void soakInPlace(byte[][][] voxels)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        byte b;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if(isSurface(voxels, x, y, z) > 0){
                        b = voxels[x][y][z];
                        if(isSurface(voxels, x, y, z-1) == -1) voxels[x][y][z-1] = b;
                        if(isSurface(voxels, x-1, y, z) == -1) voxels[x-1][y][z] = b;
                        if(isSurface(voxels, x, y-1, z) == -1) voxels[x][y-1][z] = b;
                        if(isSurface(voxels, x+1, y, z) == -1) voxels[x+1][y][z] = b;
                        if(isSurface(voxels, x, y+1, z) == -1) voxels[x][y+1][z] = b;
                        if(isSurface(voxels, x, y, z+1) == -1) voxels[x][y][z+1] = b;
                    }
                }
            }
        }
    }

    public static byte[][][] hollowInPlace(byte[][][] voxels)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if(isSurface(voxels, x, y, z) < 0) {
                        voxels[x][y][z] = 0;
                    }
                }
            }
        }
        return voxels;
    }

    public static byte[][][] soak(byte[][][] voxels)
    {
        final int xs = voxels.length, ys = voxels[0].length, zs = voxels[0][0].length;
        byte[][][] next = new byte[xs][ys][zs];
        byte b;
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                for (int z = 0; z < zs; z++) {
                    if(isSurface(voxels, x, y, z) > 0){
                        next[x][y][z] = b = voxels[x][y][z];
                        if(isSurface(voxels, x, y, z-1) == -1) next[x][y][z-1] = b;
                        if(isSurface(voxels, x-1, y, z) == -1) next[x-1][y][z] = b;
                        if(isSurface(voxels, x, y-1, z) == -1) next[x][y-1][z] = b;
                        if(isSurface(voxels, x+1, y, z) == -1) next[x+1][y][z] = b;
                        if(isSurface(voxels, x, y+1, z) == -1) next[x][y+1][z] = b;
                        if(isSurface(voxels, x, y, z+1) == -1) next[x][y][z+1] = b;
                    }
                }
            }
        }
        return next;
    }

    public static byte[][][] scaleAndSoak(byte[][][] voxels) {
        voxels = simpleScale(voxels);
        soakInPlace(voxels);
        return voxels;
    }

    public static int hash(final byte[][] data) {
        if (data == null) return 0;
        long seed = 0x9E3779B97F4A7C15L;//0xfc637ed1a0c7a964L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(Hasher.beleth.hash(data[i-3]) ^ b1, Hasher.beleth.hash(data[i-2]) ^ b2) + seed,
                    mum(Hasher.beleth.hash(data[i-1]) ^ b3, Hasher.beleth.hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = Hasher.beleth.hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ Hasher.beleth.hash(data[len-2]), b0 ^ Hasher.beleth.hash(data[len-1])); break;
            case 3: seed = mum(seed ^ Hasher.beleth.hash(data[len-3]), b2 ^ Hasher.beleth.hash(data[len-2])) ^ mum(seed ^ Hasher.beleth.hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }

    public static int hash(final byte[][][] data) {
        if (data == null) return 0;
        long seed = 0xC13FA9A902A6328FL;//0xfc637ed1a0c7a964L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(Hasher.beleth.hash(data[i-3]) ^ b1, Hasher.beleth.hash(data[i-2]) ^ b2) + seed,
                    mum(Hasher.beleth.hash(data[i-1]) ^ b3, Hasher.beleth.hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = Hasher.beleth.hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ Hasher.beleth.hash(data[len-2]), b0 ^ Hasher.beleth.hash(data[len-1])); break;
            case 3: seed = mum(seed ^ Hasher.beleth.hash(data[len-3]), b2 ^ Hasher.beleth.hash(data[len-2])) ^ mum(seed ^ Hasher.beleth.hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }

    public static long hash64(final byte[][][] data) {
        if (data == null) return 0;
        long seed = 0x7ddc1606c2a753b9L;
        final int len = data.length;
        for (int i = 3; i < len; i += 4) {
            seed = mum(
                    mum(hash(data[i - 3]) ^ b1, hash(data[i - 2]) ^ b2) + seed,
                    mum(hash(data[i - 1]) ^ b3, hash(data[i]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0:
                seed = mum(b1 ^ seed, b4 + seed);
                break;
            case 1:
                seed = mum(seed ^ ((t = hash(data[len - 1])) >>> 16), b3 ^ (t & 0xFFFFL));
                break;
            case 2:
                seed = mum(seed ^ hash(data[len - 2]), b0 ^ hash(data[len - 1]));
                break;
            case 3:
                seed = mum(seed ^ hash(data[len - 3]), b2 ^ hash(data[len - 2])) ^ mum(seed ^ hash(data[len - 1]), b4);
                break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }
    
    public static long hash64(final byte[][][][] data) {
        if (data == null) return 0;
        long seed = 0xD1B54A32D192ED03L;
        final int len = data.length;
        for (int i = 3; i < len; i += 4) {
            seed = mum(
                    mum(hash(data[i - 3]) ^ b1, hash(data[i - 2]) ^ b2) + seed,
                    mum(hash(data[i - 1]) ^ b3, hash(data[i]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0:
                seed = mum(b1 ^ seed, b4 + seed);
                break;
            case 1:
                seed = mum(seed ^ ((t = hash(data[len - 1])) >>> 16), b3 ^ (t & 0xFFFFL));
                break;
            case 2:
                seed = mum(seed ^ hash(data[len - 2]), b0 ^ hash(data[len - 1]));
                break;
            case 3:
                seed = mum(seed ^ hash(data[len - 3]), b2 ^ hash(data[len - 2])) ^ mum(seed ^ hash(data[len - 1]), b4);
                break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static int hash(Hasher h, byte[][] data) {
        if (data == null) return 0;
        long result = 0xBEEF1E57DADL ^ data.length * 0x9E3779B97F4A7C15L;
        int i = 0;
        for (; i + 7 < data.length; i += 8) {
            result = 0xEBEDEED9D803C815L * result
                    + 0xD96EB1A810CAAF5FL * h.hash(data[i])
                    + 0xC862B36DAF790DD5L * h.hash(data[i + 1])
                    + 0xB8ACD90C142FE10BL * h.hash(data[i + 2])
                    + 0xAA324F90DED86B69L * h.hash(data[i + 3])
                    + 0x9CDA5E693FEA10AFL * h.hash(data[i + 4])
                    + 0x908E3D2C82567A73L * h.hash(data[i + 5])
                    + 0x8538ECB5BD456EA3L * h.hash(data[i + 6])
                    + 0xD1B54A32D192ED03L * h.hash(data[i + 7])
            ;
        }
        for (; i < data.length; i++) {
            result = 0x9E3779B97F4A7C15L * result + h.hash(data[i]);
        }
        result *= 0x94D049BB133111EBL;
        result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
        result *= 0x369DEA0F31A53F85L;
        result ^= result >>> 31;
        result *= 0xDB4F0B9175AE2165L;
        return (int) (result ^ result >>> 28);
    }
    

    public static int hash(Hasher h, byte[][][] data) {
        return (int) hash64(h, data);
    }
    public static long hash64(Hasher h, byte[][][] data) {
        if (data == null) return 0;
        long result = 0xBEEF1E57DADD1E5L ^ data.length * 0x9E3779B97F4A7C15L;
        int i = 0;
        for (; i + 7 < data.length; i += 8) {
            result =  0xEBEDEED9D803C815L * result
                    + 0xD96EB1A810CAAF5FL * hash(h, data[i])
                    + 0xC862B36DAF790DD5L * hash(h, data[i + 1])
                    + 0xB8ACD90C142FE10BL * hash(h, data[i + 2])
                    + 0xAA324F90DED86B69L * hash(h, data[i + 3])
                    + 0x9CDA5E693FEA10AFL * hash(h, data[i + 4])
                    + 0x908E3D2C82567A73L * hash(h, data[i + 5])
                    + 0x8538ECB5BD456EA3L * hash(h, data[i + 6])
                    + 0xD1B54A32D192ED03L * hash(h, data[i + 7])
            ;
        }
        for (; i < data.length; i++) {
            result = 0x9E3779B97F4A7C15L * result + hash(h, data[i]);
        }
        result *= 0x94D049BB133111EBL;
        result ^= (result << 41 | result >>> 23) ^ (result << 17 | result >>> 47);
        result *= 0x369DEA0F31A53F85L;
        result ^= result >>> 31;
        result *= 0xDB4F0B9175AE2165L;
        return (result ^ result >>> 28);
    }

    /**
     * Returns a hashed float value given four longs; the result should be uniform between 0 (inclusive) and 1
     * (exclusive).
     * @param x position as a long
     * @param y position as a long
     * @param z position as a long
     * @param s state or frame as a long
     * @return a uniform float between 0.0f (inclusive) and 1.0f (exclusive)
     */
    public static float randomizePoint(long x, long y, long z, long s){
        s =      (0xEBEDEED9D803C815L * x
                + 0xD96EB1A810CAAF5FL * y
                + 0xC862B36DAF790DD5L * z
                + 0xB8ACD90C142FE10BL * s);
        return ((s ^ s >>> 41 ^ s >>> 32) & 0x7FFFFFL) * 0x1p-23f;
    }

    /**
     * Returns a hashed float value given four longs; the result is between 0 and 1.5 but is more often low.
     * @param x position as a long
     * @param y position as a long
     * @param z position as a long
     * @param s state or frame as a long
     * @return a float between 0.0f (inclusive) and 1.5f (exclusive), more often low than high
     */
    public static float randomizePointRare(long x, long y, long z, long s){
        s =      (0xEBEDEED9D803C815L * x
                + 0xD96EB1A810CAAF5FL * y
                + 0xC862B36DAF790DD5L * z
                + 0xB8ACD90C142FE10BL * s);
        return ((s >>> 41 ^ s >>> 32) & s & 0x7FFFFFL) * 0x1.8p-23f;
    }

    public static StringBuilder show(byte[][][] data){
        final int sizeX = data.length, sizeY = data[0].length, sizeZ = data[0][0].length;
        StringBuilder sb = new StringBuilder(sizeX * (1+sizeY) * (1+sizeZ) << 2);
        for (int z = 0; z < sizeZ; z++) {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    Base.BASE16.appendUnsigned(sb, data[x][y][z]).append(", ");
                }
                sb.append('\n');
            }
            sb.append("...\n");
        }
        return sb;
    }

}
