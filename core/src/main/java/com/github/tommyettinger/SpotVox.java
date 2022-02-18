package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.IOException;

public class SpotVox extends ApplicationAdapter {
    public static final boolean DEBUG = false;
    public Renderer renderer;
    public String name;
    public byte[][][] voxels;
    private PixmapIO.PNG png;
    public int multiple;
    public int outline;
    public int size;

    public SpotVox() {
        renderer = new Renderer();
    }
    public SpotVox(String name, int size, byte[][][] voxels, int multiple, String edge) {
        this.name = name;
        this.voxels = voxels;
        this.size = size;
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
        renderer.palette(VoxIO.lastPalette);
        renderer.init();
        renderer.outline = outline;
        png = new PixmapIO.PNG();
        Pixmap pixmap;
        boolean smoothing = multiple > 0;
        multiple = Math.abs(multiple);
        for (int m = 0, exp = 1; m < multiple; m++, exp += exp) {
            for (int i = 0; i < 8; i++) {
                pixmap = renderer.drawSplats(voxels, i * 0.125f, VoxIO.lastMaterials);
                try {
                    png.write(Gdx.files.local((DEBUG ? "out/" + name : name) + "/size" + exp + (smoothing ? "smooth/" : "blocky/") + name + "_angle" + i + ".png"), pixmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(m + 1 < multiple)
            {
                voxels = smoothing ? Tools3D.simpleScale(voxels) : Tools3D.blockyScale(voxels);
                VoxIO.minX <<= 1;
                VoxIO.minY <<= 1;
                VoxIO.minZ <<= 1;
                VoxIO.maxX <<= 1;
                VoxIO.maxY <<= 1;
                VoxIO.maxZ <<= 1;
                renderer = new Renderer(size <<= 1);
                renderer.palette(VoxIO.lastPalette);
                renderer.init();
                renderer.outline = outline;
            }
        }
        System.out.println("Rendered to files in " + (DEBUG ? "out/" + name : name));
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        Gdx.app.exit();
        System.exit(0);
    }
}