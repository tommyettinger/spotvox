package com.github.tommyettinger.io;

import com.badlogic.gdx.math.Vector3;
import com.github.tommyettinger.LittleEndianDataInputStream;
import com.github.tommyettinger.Tools3D;
import com.github.tommyettinger.VoxMaterial;
import com.github.tommyettinger.ds.IntObjectMap;
import com.github.yellowstonegames.core.StringTools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.PrimitiveIterator;


/**
 * Handles reading MagicaVoxel .vox files from file to VoxModel objects.
 * Major credit for figuring out the largely-undocumented MagicaVoxel .vox extension format goes to Zarbuz, since I had
 * to check <a href="https://github.com/Zarbuz/FileToVox">FileToVox</a> many times for help.
 * <br>
 * Created by Tommy Ettinger on 6/28/2021.
 */
public class VoxIOExtended {
    /**
     * If this is a general-purpose .vox loader, this should be true. For CG-specialized models, false.
     */
    public static final boolean GENERAL = true;
    public static final int[] defaultPalette = {
            0x00000000, 0xffffffff, 0xffffccff, 0xffff99ff, 0xffff66ff, 0xffff33ff, 0xffff00ff, 0xffccffff,
            0xffccccff, 0xffcc99ff, 0xffcc66ff, 0xffcc33ff, 0xffcc00ff, 0xff99ffff, 0xff99ccff, 0xff9999ff,
            0xff9966ff, 0xff9933ff, 0xff9900ff, 0xff66ffff, 0xff66ccff, 0xff6699ff, 0xff6666ff, 0xff6633ff,
            0xff6600ff, 0xff33ffff, 0xff33ccff, 0xff3399ff, 0xff3366ff, 0xff3333ff, 0xff3300ff, 0xff00ffff,
            0xff00ccff, 0xff0099ff, 0xff0066ff, 0xff0033ff, 0xff0000ff, 0xccffffff, 0xccffccff, 0xccff99ff,
            0xccff66ff, 0xccff33ff, 0xccff00ff, 0xccccffff, 0xccccccff, 0xcccc99ff, 0xcccc66ff, 0xcccc33ff,
            0xcccc00ff, 0xcc99ffff, 0xcc99ccff, 0xcc9999ff, 0xcc9966ff, 0xcc9933ff, 0xcc9900ff, 0xcc66ffff,
            0xcc66ccff, 0xcc6699ff, 0xcc6666ff, 0xcc6633ff, 0xcc6600ff, 0xcc33ffff, 0xcc33ccff, 0xcc3399ff,
            0xcc3366ff, 0xcc3333ff, 0xcc3300ff, 0xcc00ffff, 0xcc00ccff, 0xcc0099ff, 0xcc0066ff, 0xcc0033ff,
            0xcc0000ff, 0x99ffffff, 0x99ffccff, 0x99ff99ff, 0x99ff66ff, 0x99ff33ff, 0x99ff00ff, 0x99ccffff,
            0x99ccccff, 0x99cc99ff, 0x99cc66ff, 0x99cc33ff, 0x99cc00ff, 0x9999ffff, 0x9999ccff, 0x999999ff,
            0x999966ff, 0x999933ff, 0x999900ff, 0x9966ffff, 0x9966ccff, 0x996699ff, 0x996666ff, 0x996633ff,
            0x996600ff, 0x9933ffff, 0x9933ccff, 0x993399ff, 0x993366ff, 0x993333ff, 0x993300ff, 0x9900ffff,
            0x9900ccff, 0x990099ff, 0x990066ff, 0x990033ff, 0x990000ff, 0x66ffffff, 0x66ffccff, 0x66ff99ff,
            0x66ff66ff, 0x66ff33ff, 0x66ff00ff, 0x66ccffff, 0x66ccccff, 0x66cc99ff, 0x66cc66ff, 0x66cc33ff,
            0x66cc00ff, 0x6699ffff, 0x6699ccff, 0x669999ff, 0x669966ff, 0x669933ff, 0x669900ff, 0x6666ffff,
            0x6666ccff, 0x666699ff, 0x666666ff, 0x666633ff, 0x666600ff, 0x6633ffff, 0x6633ccff, 0x663399ff,
            0x663366ff, 0x663333ff, 0x663300ff, 0x6600ffff, 0x6600ccff, 0x660099ff, 0x660066ff, 0x660033ff,
            0x660000ff, 0x33ffffff, 0x33ffccff, 0x33ff99ff, 0x33ff66ff, 0x33ff33ff, 0x33ff00ff, 0x33ccffff,
            0x33ccccff, 0x33cc99ff, 0x33cc66ff, 0x33cc33ff, 0x33cc00ff, 0x3399ffff, 0x3399ccff, 0x339999ff,
            0x339966ff, 0x339933ff, 0x339900ff, 0x3366ffff, 0x3366ccff, 0x336699ff, 0x336666ff, 0x336633ff,
            0x336600ff, 0x3333ffff, 0x3333ccff, 0x333399ff, 0x333366ff, 0x333333ff, 0x333300ff, 0x3300ffff,
            0x3300ccff, 0x330099ff, 0x330066ff, 0x330033ff, 0x330000ff, 0x00ffffff, 0x00ffccff, 0x00ff99ff,
            0x00ff66ff, 0x00ff33ff, 0x00ff00ff, 0x00ccffff, 0x00ccccff, 0x00cc99ff, 0x00cc66ff, 0x00cc33ff,
            0x00cc00ff, 0x0099ffff, 0x0099ccff, 0x009999ff, 0x009966ff, 0x009933ff, 0x009900ff, 0x0066ffff,
            0x0066ccff, 0x006699ff, 0x006666ff, 0x006633ff, 0x006600ff, 0x0033ffff, 0x0033ccff, 0x003399ff,
            0x003366ff, 0x003333ff, 0x003300ff, 0x0000ffff, 0x0000ccff, 0x000099ff, 0x000066ff, 0x000033ff,
            0xee0000ff, 0xdd0000ff, 0xbb0000ff, 0xaa0000ff, 0x880000ff, 0x770000ff, 0x550000ff, 0x440000ff,
            0x220000ff, 0x110000ff, 0x00ee00ff, 0x00dd00ff, 0x00bb00ff, 0x00aa00ff, 0x008800ff, 0x007700ff,
            0x005500ff, 0x004400ff, 0x002200ff, 0x001100ff, 0x0000eeff, 0x0000ddff, 0x0000bbff, 0x0000aaff,
            0x000088ff, 0x000077ff, 0x000055ff, 0x000044ff, 0x000022ff, 0x000011ff, 0xeeeeeeff, 0xddddddff,
            0xbbbbbbff, 0xaaaaaaff, 0x888888ff, 0x777777ff, 0x555555ff, 0x444444ff, 0x222222ff, 0x111111ff
    };
    public static final IntObjectMap<VoxMaterial> lastMaterials = new IntObjectMap<>(256);
    public static int[] lastPalette = Arrays.copyOf(defaultPalette, 256);
    public static int minX = Integer.MAX_VALUE;
    public static int maxX;
    public static int minY = Integer.MAX_VALUE;
    public static int maxY;
    public static int minZ = Integer.MAX_VALUE;
    public static int maxZ;

    static {
        lastMaterials.setDefaultValue(VoxMaterial.DEFAULT_MATERIAL);
    }

    protected static String readString(LittleEndianDataInputStream stream) throws IOException {
        int len = stream.readInt();
        byte[] buf = new byte[len];
        stream.read(buf, 0, len);
        return new String(buf, StandardCharsets.ISO_8859_1);
    }
    protected static String[][] readStringPairs(LittleEndianDataInputStream stream) throws IOException {
        int len = stream.readInt();
        String[][] pairs = new String[len][2];
        for (int i = 0; i < len; i++) {
            pairs[i][0] = readString(stream);
            pairs[i][1] = readString(stream);
        }
        return pairs;
    }

    public static void getRotation(TransformChunk chunk, String[][] pairs){
        for(String[] pair : pairs){
            if("_roll".equals(pair[0])){
                try{
                    chunk.roll = Float.parseFloat(pair[1]);
                } catch (Exception ignored){
                    return;
                }
            }
            else if("_pitch".equals(pair[0])){
                try{
                    chunk.pitch = Float.parseFloat(pair[1]);
                } catch (Exception ignored){
                    return;
                }
            }
            else if("_yaw".equals(pair[0])){
                try{
                    chunk.yaw = Float.parseFloat(pair[1]);
                } catch (Exception ignored){
                    return;
                }
            }
        }
    }

    public static void getTranslation(Vector3 result, String[][] pairs){
        for(String[] pair : pairs) {
            if("_t".equals(pair[0])) {
                String[] parts = StringTools.split(pair[1], " ");
                if (parts.length > 0) {
                    try {
                        result.x = Float.parseFloat(parts[0]);
                    } catch (Exception ignored){}
                }
                if (parts.length > 1) {
                    try {
                        result.y = Float.parseFloat(parts[1]);
                    } catch (Exception ignored){}
                }
                if (parts.length > 2) {
                    try {
                        result.z = Float.parseFloat(parts[2]);
                    } catch (Exception ignored){}
                }
                return;
            }
        }
    }

    public static VoxModel readVox(InputStream stream) {
        return readVox(new LittleEndianDataInputStream(stream));
    }

    public static VoxModel readVox(LittleEndianDataInputStream stream) {
        // check out https://github.com/ephtracy/voxel-model/blob/master/MagicaVoxel-file-format-vox.txt for the file format used below
        lastMaterials.clear();
        VoxModel model = new VoxModel();
        byte[][][] voxelData = null;
        IntObjectMap<ShapeModel> shapes = new IntObjectMap<>(8);
        TransformChunk latest = null;

        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        minZ = Integer.MAX_VALUE;
        maxX = 0;
        maxY = 0;
        maxZ = 0;

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
                    if (chunkName.equals("SIZE")) {
                        sizeX = stream.readInt();
                        sizeY = stream.readInt();
                        sizeZ = stream.readInt();
                        size = Math.max(sizeZ, Math.max(sizeX, sizeY));
                        offX = size - sizeX >> 1;
                        offY = size - sizeY >> 1;
                        voxelData = new byte[size][size][size];
                        stream.skipBytes(chunkSize - 4 * 3);
                    } else if (chunkName.equals("XYZI") && voxelData != null) {
                        // XYZI contains n voxels
                        int numVoxels = stream.readInt();

                        ShapeModel shp = shapes.get(model.grids.size());
                        if(shp == null) {
                            shp = new ShapeModel(model.grids.size(), new String[0][0]);
                            shapes.put(model.grids.size(), shp);
                        }
                        shp.minX = Integer.MAX_VALUE;
                        shp.minY = Integer.MAX_VALUE;
                        shp.minZ = Integer.MAX_VALUE;
                        shp.maxX = 0;
                        shp.maxY = 0;
                        shp.maxZ = 0;

                        // each voxel has x, y, z and color index values
                        for (int i = 0; i < numVoxels; i++) {
                            int x = stream.read() + offX;
                            int y = stream.read() + offY;
                            int z = stream.read();
                            byte color = stream.readByte();
                            voxelData[x][y][z] = color;
                        }
                        Tools3D.soakInPlace(voxelData);
                        model.grids.add(voxelData);
                        shp.minX = Math.min(shp.minX, 0);
                        shp.minY = Math.min(shp.minY, 0);
                        shp.minZ = Math.min(shp.minZ, 0);
                        shp.maxX = Math.max(shp.maxX, voxelData.length - 1);
                        shp.maxY = Math.max(shp.maxY, voxelData[0].length - 1);
                        shp.maxZ = Math.max(shp.maxZ, voxelData[0][0].length - 1);
                    } else if (chunkName.equals("RGBA")) {
                        for (int i = 1; i < 256; i++) {
                            lastPalette[i] = Integer.reverseBytes(stream.readInt());
                        }
                        System.arraycopy(lastPalette, 0, model.palette, 0, 256);
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
                            if ((vm = lastMaterials.getOrDefault(materialID, null)) == null) {
                                lastMaterials.put(materialID, vm = new VoxMaterial());
                            }
                            String ks = new String(key, 0, keyLen, StandardCharsets.ISO_8859_1);
                            String vs = new String(val, 0, valLen, StandardCharsets.ISO_8859_1);
                            vm.putTrait(ks, vs);
                        }
                    } else if (chunkName.equals("nTRN")) {
                        int chunkID = stream.readInt();
                        String[][] attributes = readStringPairs(stream);
                        int childID = stream.readInt();
                        int reservedID = stream.readInt();
                        int layerID = stream.readInt();
                        int frameCount = stream.readInt();
                        String[][][] frames = new String[frameCount][][];
                        for (int i = 0; i < frameCount; i++) {
                            frames[i] = readStringPairs(stream);
                        }
                        latest = new TransformChunk(chunkID, attributes, childID, reservedID, layerID, frames);
                        //latest.translation.z -= sizeZ * 0.5f;

                        model.transformChunks.put(chunkID, latest);
                    } else if (chunkName.equals("nGRP")) {
                        int chunkID = stream.readInt();
                        String[][] attributes = readStringPairs(stream);
                        int childCount = stream.readInt();
                        int[] childIds = new int[childCount];
                        for (int i = 0; i < childCount; i++) {
                            try {
                                childIds[i] = stream.readInt();
                            } catch (Exception ignored) {}
                        }
                        model.groupChunks.put(chunkID, new GroupChunk(chunkID, attributes, childIds));
                    } else if (chunkName.equals("nSHP")) {
                        int chunkID = stream.readInt();
                        String[][] attributes = readStringPairs(stream);
                        int modelCount = stream.readInt();
                        ShapeModel[] models = new ShapeModel[modelCount];
                        for (int i = 0; i < modelCount; i++) {
                            int shapeID = stream.readInt();
                            String[][] ps = readStringPairs(stream);
                            if(shapes.containsKey(shapeID))
                                models[i] = shapes.get(shapeID);
                            else
                                models[i] = new ShapeModel(shapeID, ps);
                            models[i].offsetX = Math.round(latest.translation.x);
                            models[i].offsetY = Math.round(latest.translation.y);
                            models[i].offsetZ = Math.round(latest.translation.z);
                            minX = Math.min(minX, models[i].minX + models[i].offsetX);
                            minY = Math.min(minY, models[i].minY + models[i].offsetY);
                            minZ = Math.min(minZ, models[i].minZ + models[i].offsetZ);
                            maxX = Math.max(maxX, models[i].maxX + models[i].offsetX);
                            maxY = Math.max(maxY, models[i].maxY + models[i].offsetY);
                            maxZ = Math.max(maxZ, models[i].maxZ + models[i].offsetZ);
                        }
                        model.shapeChunks.put(chunkID, new ShapeChunk(chunkID, attributes, models));
                    } else
                        stream.skipBytes(chunkSize);   // read any excess bytes
                }

            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.materials.putAll(lastMaterials);
        return model;
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
            new File(filename).getAbsoluteFile().getParentFile().mkdirs();
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
                bin.writeInt(lastPalette[i]);
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
                    PrimitiveIterator.OfInt it = ent.value.traits.keySet().iterator();
                    for(int k; it.hasNext();) {
                        k = it.nextInt();
                        if(k > 9) continue;
                        VoxMaterial.MaterialTrait mt = VoxMaterial.ALL_TRAITS[k];
                        float v = ent.value.traits.get(k);
                        term = mt.name();
                        writeInt(dos, term.length());
                        dos.writeBytes(term);
                        term = Float.toString(v);
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
