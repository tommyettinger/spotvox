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

    public SpotVox() {
        renderer = new Renderer();
    }
    public SpotVox(String name, Renderer r, byte[][][] voxels) {
        renderer = r;
        this.name = name;
        this.voxels = voxels;
    }

    @Override
    public void create() {
        long startTime = TimeUtils.millis();
        renderer.init();
        png = new PixmapIO.PNG();
        png.setCompression(2); // we are likely to compress these with something better, like oxipng.
        Pixmap pixmap;
        for (int i = 0; i < 8; i++) {
            for (int f = 0; f < 4; f++) {
                pixmap = renderer.drawSplats(voxels, i * 0.125f, f, VoxIO.lastMaterials);
                try {
                    png.write(Gdx.files.local("out/" + name + '/' + name + "_angle" + i + "_" + f + ".png"), pixmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Finished in " + TimeUtils.timeSinceMillis(startTime) * 0.001 + " seconds.");
        Gdx.app.exit();
        System.exit(0);
    }
}