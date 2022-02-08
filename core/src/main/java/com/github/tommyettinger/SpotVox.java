package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.NumberUtils;

public class SpotVox extends ApplicationAdapter {

    public Renderer renderer;
    public int width = 512;
    public int height = 512;
    public String output = "render.png";

    public SpotVox() {
        renderer = new Renderer();
    }
    public SpotVox(Renderer n) {
        renderer = n;
    }
    public SpotVox(Renderer n, int w, int h) {
        renderer = n;
        width = w;
        height = h;
    }
    public SpotVox(Renderer n, int w, int h, String out) {
        renderer = n;
        width = w;
        height = h;
        output = out;
    }

    @Override
    public void create() {
        Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGB888);
        PixmapIO.writePNG(Gdx.files.local(output), pm);
        System.exit(0);
    }
}