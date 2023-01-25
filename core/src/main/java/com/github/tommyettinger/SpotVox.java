package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.AnimatedGif;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PaletteReducer;
import com.github.tommyettinger.io.VoxIOExtended;

import java.io.IOException;

public class SpotVox extends ApplicationAdapter {
    public static final boolean DEBUG = true;
    public Renderer renderer;
    public String name;
    public byte[][][] voxels;
    private PixmapIO.PNG png;
    private AnimatedGif gif;
    public int multiple;
    public int outline;
    public int size;
    public int fps;
    public float saturation;

    public SpotVox() {
    }
    public SpotVox(String name, int size, byte[][][] voxels, int multiple, String edge, float saturation, int fps) {
        this.name = name;
        this.voxels = voxels;
        this.size = size;
        this.saturation = saturation;
        this.multiple = multiple == 0 ? 1 : multiple;
        this.fps = fps;
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
        gif = new AnimatedGif();
        gif.palette = new PaletteReducer();
        gif.setDitherAlgorithm(Dithered.DitherAlgorithm.ROBERTS);
        gif.setDitherStrength(0.25f);
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
            if(fps != 0){
                Array<Pixmap> pm = new Array<>(128);
                for (int i = 0; i < 128; i++) {
                    pixmap = renderer.drawSplats(voxels, i * 0x1p-7f + 0.125f, 0, 0, 0, 0, 0, VoxIOExtended.lastMaterials);
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