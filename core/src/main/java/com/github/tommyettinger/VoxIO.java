package com.github.tommyettinger;

import com.github.tommyettinger.ds.IntFloatMap;
import com.github.tommyettinger.ds.IntObjectMap;
import com.github.tommyettinger.io.VoxIOExtended;

import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * Handles reading MagicaVoxel .vox files from file to byte[][][], and vice versa.
 * The palette and, if present, materials of the latest .vox file read are available
 * in {@link VoxIOExtended#lastPalette} and {@link VoxIOExtended#lastMaterials}.
 * <br>
 * Created by Tommy Ettinger on 12/12/2017.
 */
public class VoxIO {

    public static byte[][][] readVox(InputStream stream) {
        return readVox(new LittleEndianDataInputStream(stream));
    }
    public static byte[][][] readVox(LittleEndianDataInputStream stream) {
        // check out https://github.com/ephtracy/voxel-model/blob/master/MagicaVoxel-file-format-vox.txt for the file format used below
        byte[][][] voxelData = null;
        VoxIOExtended.lastMaterials.clear();
        try {
            byte[] chunkId = new byte[4];
            if (4 != stream.read(chunkId))
                return null;
            //int version = 
            stream.readInt();
            int sizeX = 16, sizeY = 16, size = 16, sizeZ = 16, offX = 0, offY = 0;
            byte[] key = new byte[6]; // used for MaterialTrait
            byte[] val = new byte[10]; // used for MaterialType and numbers
            // a MagicaVoxel .vox file starts with a 'magic' 4 character 'VOX ' identifier
            if (chunkId[0] == 'V' && chunkId[1] == 'O' && chunkId[2] == 'X' && chunkId[3] == ' ') {
                while (stream.available() > 0) {
                    // each chunk has an ID, size and child chunks
                    stream.read(chunkId);
                    int chunkSize = stream.readInt();
                    //int childChunks = 
                    stream.readInt();
                    String chunkName = new String(chunkId, StandardCharsets.ISO_8859_1);

                    // there are only 4 chunks we care about, and they are SIZE, XYZI, RGBA, and MATL
                    if (chunkName.equals("SIZE")) {
                        sizeX = stream.readInt();
                        sizeY = stream.readInt();
                        sizeZ = stream.readInt();
                        size = Math.max(sizeZ, Math.max(sizeX, sizeY));
                        System.out.printf("sizeX: %d, sizeY: %d, sizeZ: %d, size: %d\n", sizeX, sizeY, sizeZ, size);
                        offX = size - sizeX >> 1;
                        offY = size - sizeY >> 1;
                        voxelData = new byte[size][size][size];
                        stream.skipBytes(chunkSize - 4 * 3);
                    } else if (chunkName.equals("XYZI") && voxelData != null) {
                        // XYZI contains n voxels
                        int numVoxels = stream.readInt();
                        // each voxel has x, y, z and color index values
                        int x, y, z;
                        for (int i = 0; i < numVoxels; i++) {
                            voxelData[x = stream.read() + offX][y = stream.read() + offY][z = stream.read()] = stream.readByte();
                            VoxIOExtended.minX = Math.min(VoxIOExtended.minX, x);
                            VoxIOExtended.minY = Math.min(VoxIOExtended.minY, y);
                            VoxIOExtended.minZ = Math.min(VoxIOExtended.minZ, z);
                            VoxIOExtended.maxX = Math.max(VoxIOExtended.maxX, x);
                            VoxIOExtended.maxY = Math.max(VoxIOExtended.maxY, y);
                            VoxIOExtended.maxZ = Math.max(VoxIOExtended.maxZ, z);
                        }
                        System.out.printf("Subranges: x=%d to x=%d, y=%d to y=%d, z=%d to z=%d\n", VoxIOExtended.minX, VoxIOExtended.maxX, VoxIOExtended.minY, VoxIOExtended.maxY, VoxIOExtended.minZ, VoxIOExtended.maxZ);
                    } else if(chunkName.equals("RGBA"))
                    {
                        for (int i = 1; i < 256; i++) {
                            VoxIOExtended.lastPalette[i] = Integer.reverseBytes(stream.readInt());
                        }
                        stream.readInt();
                    } else if(chunkName.equals("MATL")){ // remove this block if you don't handle materials
                        int materialID = stream.readInt();
                        int dictSize = stream.readInt();
                        for (int i = 0; i < dictSize; i++) {
                            int keyLen = stream.readInt();
                            stream.read(key, 0, keyLen);
                            int valLen = stream.readInt();
                            stream.read(val, 0, valLen);
                            VoxMaterial vm;
                            if ((vm = VoxIOExtended.lastMaterials.getOrDefault(materialID, null)) == null) {
                                VoxIOExtended.lastMaterials.put(materialID, vm = new VoxMaterial());
                            }
                            String ks = new String(key, 0, keyLen, StandardCharsets.ISO_8859_1);
                            String vs = new String(val, 0, valLen, StandardCharsets.ISO_8859_1);
                            vm.putTrait(ks, vs);
                        }
                    }
                    else{
                        stream.skipBytes(chunkSize);   // read any excess bytes
                    }
                }

            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return voxelData;
    }

    private static void writeInt(DataOutputStream bin, int value) throws IOException
    {
        bin.writeInt(Integer.reverseBytes(value));
    }

    public static void writeVOX(String filename, byte[][][] voxelData, int[] palette) {
        writeVOX(filename, voxelData, palette, null);
    }

    public static void writeVOX(String filename, byte[][][] voxelData, int[] palette, IntObjectMap<VoxMaterial> materials) {
        // check out https://github.com/ephtracy/voxel-model/blob/master/MagicaVoxel-file-format-vox.txt for the file format used below
        try {
            int xSize = voxelData.length, ySize = voxelData[0].length, zSize = voxelData[0][0].length;

            FileOutputStream fos = new FileOutputStream(filename);
            DataOutputStream bin = new DataOutputStream(fos);
            ByteArrayOutputStream voxelsRaw = new ByteArrayOutputStream(0);
            int cc;
            for (int x = 0; x < xSize; x++) {
                for (int y = 0; y < ySize; y++) {
                    for (int z = 0; z < zSize; z++) {
                        cc = voxelData[x][y][z];
                        if(cc == 0) continue;
                        voxelsRaw.write(x);
                        voxelsRaw.write(y);
                        voxelsRaw.write(z);
                        voxelsRaw.write(cc);
                    }
                }
            }

            // a MagicaVoxel .vox file starts with a 'magic' 4 character 'VOX ' identifier
            bin.writeBytes("VOX ");
            // current version
            writeInt(bin, 150);

            bin.writeBytes("MAIN");
            writeInt(bin, 0);
            writeInt(bin, 12 + 12 + 12 + 4 + voxelsRaw.size() + 12 + 1024);

            bin.writeBytes("SIZE");
            writeInt(bin, 12);
            writeInt(bin, 0);
            writeInt(bin, xSize);
            writeInt(bin, ySize);
            writeInt(bin, zSize);

            bin.writeBytes("XYZI");
            writeInt(bin, 4 + voxelsRaw.size());
            writeInt(bin, 0);
            writeInt(bin, voxelsRaw.size() >> 2);
            bin.write(voxelsRaw.toByteArray());

            bin.writeBytes("RGBA");
            writeInt(bin, 1024);
            writeInt(bin, 0);
            int i = 1;
            for (; i < 256 && i < palette.length; i++) {
                bin.writeInt(palette[i]);
            }
            // if the palette is smaller than 256 colors, this fills the rest with lastPalette's colors
            for (; i < 256; i++) {
                bin.writeInt(VoxIOExtended.lastPalette[i]);
            }
            writeInt(bin,  0);
            if(materials != null && materials.notEmpty()) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream(128);
                DataOutputStream dos = new DataOutputStream(bytes);
                for(IntObjectMap.Entry<VoxMaterial> ent : materials) {
                    bin.writeBytes("MATL");
                    dos.flush();
                    bytes.reset();
                    // here we write to dos, which writes to bytes, so we know the length of the chunk.
                    writeInt(dos, ent.key);
                    writeInt(dos, ent.value.traits.size() + 1);
                    writeInt(dos, 5);
                    dos.writeBytes("_type");
                    String term = ent.value.type.name();
                    writeInt(dos, term.length());
                    dos.writeBytes(term);
                    for(IntFloatMap.Entry et : ent.value.traits) {
                        VoxMaterial.MaterialTrait mt = VoxMaterial.ALL_TRAITS[et.key];
                        term = mt.name();
                        writeInt(dos, term.length());
                        dos.writeBytes(term);
                        term = Float.toString(et.value);
                        if(term.length() > 8) term = term.substring(0, 8);
                        writeInt(dos, term.length());
                        dos.writeBytes(term);
                    }
                    writeInt(bin, bytes.size());
                    writeInt(bin, 0);
                    bytes.writeTo(bin);
                }
            }
            bin.flush();
            bin.close();
            fos.flush();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
