package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.LittleEndianDataInputStream;
import com.github.tommyettinger.SpotVox;
import com.github.tommyettinger.io.GroupChunk;
import com.github.tommyettinger.io.ShapeModel;
import com.github.tommyettinger.io.TransformChunk;
import com.github.tommyettinger.io.VoxIOExtended;
import com.github.tommyettinger.io.VoxModel;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "spotvox", version = "SpotVox 0.0.1",
		description = "Given a .vox file, write pixel art renders to a subfolder.",
		mixinStandardHelpOptions = true)
public class HeadlessLauncher implements Callable<Integer> {

	@CommandLine.Option(names = {"-s", "--size"}, description = "The width, height, and depth of the space to place the model into, in voxels.", defaultValue = "-1")
	public int size = -1;

	@CommandLine.Option(names = {"-S", "--saturation"}, description = "A modifier that affects how saturated colors will be; 0 is unchanged, 0.5 is super-bold, and -1 is grayscale.", defaultValue = "0")
	public float saturation = 0;

	@CommandLine.Option(names = {"-e", "--edge"}, description = "How to shade the edges of voxels next to gaps or the background; one of: heavy, light, partial, none.", defaultValue = "light")
	public String edge = "light";

	@CommandLine.Option(names = {"-m", "--multiple"}, description = "How many multiples the model should be scaled up to; if negative, this keeps the voxels as blocks, without smoothing.", defaultValue = "1")
	public int multiple = 1;

	@CommandLine.Parameters(description = "The absolute or relative path to a MagicaVoxel .vox file.", defaultValue = "FigureSplit.vox")
	public String input = "FigureSplit.vox";

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
			VoxModel voxels = VoxIOExtended.readVox(new LittleEndianDataInputStream(new FileInputStream(input)));
			if(voxels == null) {
				System.out.println("Unable to read input file.");
				return -1;
			}
			if(size < 0) {
				size = 1;
				for(GroupChunk gc : voxels.groupChunks.values()) {
					for (int ch : gc.childIds) {
						TransformChunk tc = voxels.transformChunks.get(ch);
						if (tc != null) {
							for (ShapeModel sm : voxels.shapeChunks.get(tc.childId).models) {
								byte[][][] g = voxels.grids.get(sm.id);
								size = Math.max(size, Math.round(tc.translation.x + g.length * 0.5f));
								size = Math.max(size, Math.round(tc.translation.y + g[0].length* 0.5f));
								size = Math.max(size, Math.round(tc.translation.z + g[0][0].length * 0.5f));
							}
						}
					}
				}
			}

			int nameStart = Math.max(input.lastIndexOf('/'), input.lastIndexOf('\\')) + 1;
			this.input = input.substring(nameStart, input.indexOf('.', nameStart));
			new HeadlessApplication(new SpotVox(input, size, voxels, multiple, edge, saturation), configuration){
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