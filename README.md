# spotvox
A drag-and-drop or command-line tool that renders MagicaVoxel .vox files to pixel art.

# It Looks Like...

First, a cartoon-style helicopter, at 2x size with smoothing and a light outline, viewed diagonally:

![A helicopter](https://i.imgur.com/nx0MSVP.png)

Then, a large render (8x size) of a tree, with smoothing and a heavy outline, viewed from a side:

![A large tree](https://i.imgur.com/w6b9yj7.png)

A Monument Valley level, at 2x size without smoothing but with a partial outline, viewed diagonally:

![Monument Valley 16](https://i.imgur.com/Dp2WQYx.png)

A Monument Valley level, at 4x size without smoothing and with no outline, viewed diagonally:

![Monument Valley 10](https://i.imgur.com/NuYktKL.png)

Materials are supported; here's a glowing creature at 2x size with smoothing and a light outline, viewed diagonally:

![A magical creature that glows](https://i.imgur.com/v3334jW.png)

# Usage
Download a .zip file from the latest in the [Releases section](https://github.com/tommyettinger/spotvox/releases);
use windows-x64 if you're on 64-bit Windows (most recent installations are 64-bit), or all-platforms if you aren't.
Always extract the .zip, of course. On Windows you can drag and drop a .vox file into the .exe or .bat file. When using
drag and drop, 3 sizes are created, with smoothing, and with light outlines; these go in a folder next to the .vox. On
other platforms, use `java -jar spotvox.jar --help` from the command line (in the same folder as spotvox.jar) to see
usage. Using the command line gives various options, like using more or fewer scales (by default, this produces
x1, x2, and x4 scales), changing the style of voxel edges/outlines, and positioning the .vox model in a different size
of bounding box, so you can synchronize the sizing of different models' renders. The path to the .vox file to render
always goes last. You can use the command line on any platform; if using windows-x64, use `spotvox.exe --help` instead.

If you get errors loading some models, try loading them in the current MagicaVoxel, editing them in some non-destructive
way (like rotating 360 degrees), then saving before you load them again. Older formats of .vox model aren't read
correctly at the moment.

# Thanks
This project uses the great [PicoCLI](https://picocli.info/) library for clean command-line handling.
The Win64 native-image version would not be possible if not for ByerN's work
getting Graal to play nice with libGDX; I have copied some configuration
and built libraries here from [his example repo](https://github.com/ByerN/libgdx-graalvm-example).
Of course, this uses [libGDX](https://libgdx.com/); I can't get by without it.

The other libraries this project uses are mostly things I've made over the last few years.
[SquidSquad](https://github.com/yellowstonegames/SquidSquad) is a broad-purpose set of libraries that I
use for a few things here. [jdkgdxds](https://github.com/tommyettinger/jdkgdxds) is a data structure
library that SquidSquad uses; it provides similar data structures to libGDX, but greatly expanded.
[colorful-gdx](https://github.com/tommyettinger/colorful-gdx) is a library devoted to color manipulation,
which this uses to handle things like the lightness change from an emissive material near other voxels.

This project wouldn't exist without ephtracy and the incredible [MagicaVoxel](https://ephtracy.github.io/) tool.
Spotvox is the successor to [IsoVoxel](https://github.com/tommyettinger/IsoVoxel), which I wrote a while ago and felt
recently that it could really use some better/more consistent color handling, as well as support for materials.

# Notes
To build the native EXE, I drop spotvox.jar into `graalvm-env/`, run the appropriate Visual
Studio variable setter (`vcvars64.bat`), then `build_native.bat`. This is all ByerN's work, big thanks!