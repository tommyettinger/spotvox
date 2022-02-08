# spotvox
A drag-and-drop or command-line tool that renders MagicaVoxel .vox files to pixel art.

# Usage
Nothing yet.

# Thanks
This project uses the great [PicoCLI](https://picocli.info/) library for clean command-line handling.
The Win64 native-image version would not be possible if not for ByerN's work
getting Graal to play nice with libGDX; I have copied some configuration
and built libraries here from [his example repo](https://github.com/ByerN/libgdx-graalvm-example).
Of course, this uses [libGDX](https://libgdx.com/); I can't get by without it.

# Notes
To build the native EXE, I drop spotvox-0.0.1.jar into `graalvm-env/`, run the appropriate Visual
Studio variable setter (`vcvars64.bat`), then `build_native.bat`. This is all ByerN's work, big thanks!