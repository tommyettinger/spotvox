package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.io.VoxIOExtended;
import com.github.tommyettinger.io.VoxModel;

import java.io.IOException;

public class SpotVox extends ApplicationAdapter {
    public static final boolean DEBUG = true;
    public Renderer renderer;
    public String name;
    public byte[][][] voxels;
    private PixmapIO.PNG png;
    public int multiple;
    public int outline;
    public int size;
    public float saturation;

    public SpotVox() {
    }
    public SpotVox(String name, int size, byte[][][] voxels, int multiple, String edge, float saturation) {
        this.name = name;
        this.voxels = voxels;
        this.size = size;
        this.saturation = saturation;
        this.multiple = multiple == 0 ? 1 : multiple;
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
            default:
                outline = 2;
        }
    }

    @Override
    public void create() {
        long startTime = TimeUtils.millis();
        renderer = new Renderer(size);
        renderer.palette(VoxIOExtended.lastPalette);
        renderer.init();
        renderer.outline = outline;
        renderer.saturation(saturation);
        png = new PixmapIO.PNG();
        Pixmap pixmap;
        boolean smoothing = multiple > 0;
        multiple = Math.abs(multiple);
        for (int m = 0, exp = 1; m < multiple; m++, exp += exp) {
            for (int i = 0; i < 8; i++) {
                pixmap = renderer.drawSplats(voxels, i * 0.125f, 0, 0, 0, 0, 0, VoxIOExtended.lastMaterials);
                try {
                    png.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/" : "blocky/") + name + "_angle" + i + ".png"), pixmap);
                } catch (IOException e) {
                    e.printStackTrace();
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