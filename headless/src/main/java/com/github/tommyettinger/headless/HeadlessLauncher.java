package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.LittleEndianDataInputStream;
import com.github.tommyettinger.SpotVox;
import com.github.tommyettinger.Tools3D;
import com.github.tommyettinger.io.*;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "spotvox", version = "SpotVox 0.0.7",
		description = "Given a .vox file, write pixel art renders to a subfolder.",
		mixinStandardHelpOptions = true)
public class HeadlessLauncher implements Callable<Integer> {

	@CommandLine.Option(names = {"-s", "--size"}, description = "The width, height, and depth of the space to place the model into, in voxels. Use -1 to have this calculate.", defaultValue = "-1")
	public int size = -1;

	@CommandLine.Option(names = {"-S", "--saturation"}, description = "A modifier that affects how saturated colors will be; 0 is unchanged, 0.5 is super-bold, and -1 is grayscale.", defaultValue = "0")
	public float saturation = 0;

	@CommandLine.Option(names = {"-L", "--lightness"}, description = "A multiplier that affects how much shading changes pixel colors; 0 is unchanged, 2.0 is high-contrast, and -1 is no-shading.", defaultValue = "0.0")
	public float lightPower = 0;

	@CommandLine.Option(names = {"-b", "--base-light"}, description = "Will be added to the lightness for all voxels; can be negative or positive, and is typically between -0.5 and 0.5.", defaultValue = "0.0")
	public float baseLight = 0;

	@CommandLine.Option(names = {"-e", "--edge"}, description = "How to shade the edges of voxels next to gaps or the background; one of: none, partial, light, heavy, block.", defaultValue = "light")
	public String edge = "light";

	@CommandLine.Option(names = {"-m", "--multiple"}, description = "How many multiples the model should be scaled up to; if negative, this keeps the voxels as blocks, without smoothing.", defaultValue = "3")
	public int multiple = 3;

	@CommandLine.Option(names = {"-t", "--turn-fps"}, description = "If non-zero, this will output a turntable GIF with the given frames per second.", defaultValue = "0")
	public int turn = 0;

	@CommandLine.Option(names = {"-n", "--normals"}, description = "If 0.0 or greater, this will output a normal-map image for each non-animated output image; the number is how much the normal-map should be blurred.", defaultValue = "-1.0")
	public double normals = -1.0;

	@CommandLine.Option(names = {"-r", "--rotations"}, description = "How many different rotations to render at each size; can be 1 or higher.", defaultValue = "8")
	public int rotations = 8;

	@CommandLine.Option(names = {"--horizontal-xy"}, description = "Modifies the projection; isometric uses 2.", defaultValue = "2")
	public float distortHXY = 2;

	@CommandLine.Option(names = {"--vertical-xy"}, description = "Modifies the projection; isometric uses 1.", defaultValue = "1")
	public float distortVXY = 1;

	@CommandLine.Option(names = {"--vertical-z"}, description = "Modifies the projection; isometric uses 3.", defaultValue = "3")
	public float distortVZ = 3;

	@CommandLine.Option(names = {"-Y", "--yaw"}, description = "Added to the yaw rotation, in degrees. May be a decimal.", defaultValue = "0")
	public float yaw = 0;

	@CommandLine.Option(names = {"-P", "--pitch"}, description = "Added to the pitch rotation, in degrees. May be a decimal.", defaultValue = "0")
	public float pitch = 0;

	@CommandLine.Option(names = {"-R", "--roll"}, description = "Added to the roll rotation, in degrees. May be a decimal.", defaultValue = "0")
	public float roll = 0;

	@CommandLine.Option(names = {"-x", "--expand"}, description = "How far soft lighting should expand into unlit areas, in voxels. Must be a non-negative integer.", defaultValue = "0")
	public int expand = 0;

	@CommandLine.Parameters(description = "The absolute or relative path to a MagicaVoxel .vox file.", defaultValue = "Eye-Tyrant.vox")
	public String input = "Eye-Tyrant.vox";

	public static void main(String[] args) {
		int exitCode = new picocli.CommandLine(new HeadlessLauncher()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		configuration.updatesPerSecond = -1;
		if(SpotVox.DEBUG)
			input = "../vox/" + input;
		try {
			//// loads a file by its full path, which we get via a command-line arg
			VoxModel model = VoxIOExtended.readVox(new LittleEndianDataInputStream(new FileInputStream(input)));
			if(model == null) {
				System.out.println("Unable to read input file.");
				return -1;
			}
			int xChange = 0, yChange = 0, zChange = -VoxIOExtended.minZ;
			if(VoxIOExtended.minX < 0) {
				xChange = -VoxIOExtended.minX;
				VoxIOExtended.maxX += xChange;
				VoxIOExtended.minX = 0;
			}
			if(VoxIOExtended.minY < 0) {
				yChange = -VoxIOExtended.minY;
				VoxIOExtended.maxY += yChange;
				VoxIOExtended.minY = 0;
			}

			if(size < 0) {
				size = 1;
				for(GroupChunk gc : model.groupChunks.values()) {
					for (int ch : gc.childIds) {
						TransformChunk tc = model.transformChunks.get(ch);
						if (tc != null) {
							for (ShapeModel sm : model.shapeChunks.get(tc.childId).models) {
								byte[][][] g = model.grids.get(sm.id);
								size = Math.max(size, Math.round(tc.translation.x + g.length + xChange));
								size = Math.max(size, Math.round(tc.translation.y + g[0].length + yChange));
								size = Math.max(size, Math.round(tc.translation.z + g[0][0].length + zChange));
							}
						}
					}
				}
			}

			byte[][][] voxels = new byte[size][size][size];
			for(GroupChunk gc : model.groupChunks.values()) {
				for (int ch : gc.childIds) {
					TransformChunk tc = model.transformChunks.get(ch);
					if (tc != null) {
						for (ShapeModel sm : model.shapeChunks.get(tc.childId).models) {
							byte[][][] g = model.grids.get(sm.id);
							Tools3D.translateCopyInto(g, voxels, Math.round(tc.translation.x + xChange), Math.round(tc.translation.y + yChange), Math.round(tc.translation.z + zChange));
						}
					}
				}
			}
			VoxIOExtended.maxZ += zChange;
			VoxIOExtended.minZ = 0;

//			VoxIOExtended.writeVOX("debugOutput.vox", voxels, VoxIOExtended.lastPalette, VoxIOExtended.lastMaterials);


			int nameStart = Math.max(input.lastIndexOf('/'), input.lastIndexOf('\\')) + 1;
			this.input = input.substring(nameStart, input.indexOf('.', nameStart));
			new HeadlessApplication(new SpotVox(input, size, voxels, multiple, edge, saturation, turn, rotations,
					yaw, pitch, roll, distortHXY, distortVXY, distortVZ, normals, lightPower, baseLight, expand),
					configuration){
				{
					try {
						mainLoopThread.join();
					} catch (InterruptedException e) {
						System.out.println("Interrupted!");
					}
				}
			};

		} catch (FileNotFoundException e) {
			System.out.println("Parameters are not valid. Run with -h to show help.");
			return -1;
		}
		return 0;
	}
}