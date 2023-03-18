package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.*;
import com.github.tommyettinger.io.VoxIOExtended;

import java.io.IOException;

public class SpotVox extends ApplicationAdapter {
    public static final boolean DEBUG = true;
    public Renderer renderer;
    public String name;
    public byte[][][] voxels;
    private FastPNG png;
    private FastGif gif;
    public int multiple;
    public int outline;
    public int size;
    public int fps;
    public int rotations;
    public float iRotations;
    public float saturation;
    public float distortHXY, distortVXY, distortVZ;
    public boolean normals;
    public double normalSigma;
    public float lightPower;
    public float baseLight;
    public float yaw, pitch, roll;

    public SpotVox() {
    }
    public SpotVox(String name, int size, byte[][][] voxels, int multiple, String edge, float saturation, int fps,
                   int rotations, float yaw, float pitch, float roll, float distortHXY, float distortVXY,
                   float distortVZ, double normals, float lightPower, float baseLight) {
        this.name = name;
        this.voxels = voxels;
        this.size = size;
        this.saturation = saturation;
        this.multiple = multiple == 0 ? 1 : multiple;
        this.fps = fps;
        this.rotations = Math.max(1, rotations);
        this.yaw = yaw / 360f;
        this.pitch = pitch / 360f;
        this.roll = roll / 360f;
        this.distortHXY = distortHXY;
        this.distortVXY = distortVXY;
        this.distortVZ = distortVZ;
        this.normals = normals >= 0.0;
        this.normalSigma = normals;
        this.lightPower = lightPower + 1f;
        this.baseLight = baseLight;
        iRotations = 1f / this.rotations;
        switch (edge) {
            case "none":
                outline = 0;
                break;
            case "partial":
                outline = 1;
                break;
            case "heavy":
                outline = 3;
                break;
            case "block":
                outline = 4;
                break;
            default:
                outline = 2;
        }
    }

    @Override
    public void create() {
        long startTime = TimeUtils.millis();
        renderer = new Renderer(size);
        renderer.palette(VoxIOExtended.lastPalette);
        renderer.distortHXY = distortHXY;
        renderer.distoryVXY = distortVXY;
        renderer.distortVZ = distortVZ;
        renderer.computeNormals = normals;
        renderer.blurSigma = normalSigma;
        renderer.lightPower = lightPower;
        renderer.baseLight = baseLight;
        renderer.init();
        renderer.outline = outline;
        renderer.saturation(saturation);
        png = new FastPNG();
        gif = new FastGif();
        gif.palette = new FastPalette();
        gif.setDitherAlgorithm(Dithered.DitherAlgorithm.ROBERTS);
        gif.setDitherStrength(0.25f);
        Pixmap pixmap;
        boolean smoothing = multiple > 0;
        multiple = Math.abs(multiple);
        for (int m = 0, exp = 1; m < multiple; m++, exp += exp) {
            for (int i = 0; i < rotations; i++) {
                pixmap = renderer.drawSplats(voxels, i * iRotations + yaw, pitch, roll, 0, 0, 0, VoxIOExtended.lastMaterials);
                png.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/" : "blocky/") + name + "_angle" + i + ".png"), pixmap);
                if(normals){
                    png.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/normal_" : "blocky/normal_") + name + "_angle" + i + ".png"), renderer.normalMap);
                }
            }
            if(fps != 0){
                Array<Pixmap> pm = new Array<>(128);
                for (int i = 0; i < 128; i++) {
                    pixmap = renderer.drawSplats(voxels, i * 0x1p-7f + 0.125f + yaw, pitch, roll, 0, 0, 0, VoxIOExtended.lastMaterials);
                    Pixmap p = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
                    p.drawPixmap(pixmap, 0, 0);
                    pm.add(p);
                }
                gif.palette.analyze(pm, 75.0, 256);
                gif.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/" : "blocky/") + name + "_Turntable.gif"), pm, fps);
                for (Pixmap pix : pm) {
                    if (!pix.isDisposed())
                        pix.dispose();
                }
            }
            if(m + 1 < multiple)
            {
                if (smoothing) {
                    voxels = Tools3D.simpleScale(voxels);
                } else {
                    voxels = Tools3D.blockyScale(voxels);
                }
                VoxIOExtended.minX <<= 1;
                VoxIOExtended.minY <<= 1;
                VoxIOExtended.minZ <<= 1;
                VoxIOExtended.maxX <<= 1;
                VoxIOExtended.maxY <<= 1;
                VoxIOExtended.maxZ <<= 1;
                renderer = new Renderer(size <<= 1);
                renderer.palette(VoxIOExtended.lastPalette);
                renderer.distortHXY = distortHXY;
                renderer.distoryVXY = distortVXY;
                renderer.distortVZ = distortVZ;
                renderer.computeNormals = normals;
                renderer.blurSigma = normalSigma;
                renderer.lightPower = lightPower;
                renderer.baseLight = baseLight;
                renderer.init();
                renderer.outline = outline;
                renderer.saturation(saturation);
            }
        }
        System.out.println("Rendered to files in " + (DEBUG ? "out/" + name : name));
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        Gdx.app.exit();
        System.exit(0);
    }
}