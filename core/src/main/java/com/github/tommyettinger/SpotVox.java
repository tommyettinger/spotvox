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

    public Renderer renderer;
    public String name;
    public byte[][][] voxels;
    private PixmapIO.PNG png;
    int multiple;
    int outline;

    public SpotVox() {
        renderer = new Renderer();
    }
    public SpotVox(String name, Renderer r, byte[][][] voxels, int multiple, String edge) {
        renderer = r;
        this.name = name;
        this.voxels = voxels;
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
        renderer.init();
        png = new PixmapIO.PNG();
        png.setCompression(2); // we are likely to compress these with something better, like oxipng.
        Pixmap pixmap;
        boolean smoothing = multiple > 0;
        multiple = Math.abs(multiple);
        for (int m = 0; m < multiple; m++) {
            for (int i = 0; i < 8; i++) {
                pixmap = renderer.drawSplats(voxels, i * 0.125f, VoxIO.lastMaterials);
                try {
                    png.write(Gdx.files.local("out/" + name + "/size" + m + (smoothing ? "smooth/" : "blocky/") + name + "_angle" + i + ".png"), pixmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(m + 1 < multiple)
                voxels = smoothing ? Tools3D.smoothScale(voxels) : Tools3D.scaleAndSoak(voxels);
        }
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        Gdx.app.exit();
        System.exit(0);
    }
}