package com.github.tommyettinger.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.*;
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

	@CommandLine.Option(names = {"-e", "--edge"}, description = "How to shade the edges of voxels next to gaps or the background; one of: heavy, light, partial, none.", defaultValue = "light")
	public String edge = "light";

	@CommandLine.Option(names = {"-m", "--multiple"}, description = "How many multiples the model should be scaled up to; if negative, this doesn't use smoothing.", defaultValue = "3")
	public int multiple = 3;

	@CommandLine.Parameters(description = "The absolute or relative path to a MagicaVoxel .vox file.", defaultValue = "../vox/Truck.vox")
	public String input = "vox/Tree.vox";

	public static void main(String[] args) {
		int exitCode = new picocli.CommandLine(new HeadlessLauncher()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		configuration.updatesPerSecond = -1;
		try {
			//// loads a file by its full path, which we get via a command-line arg
			byte[][][] voxels = VoxIO.readVox(new LittleEndianDataInputStream(new FileInputStream(input)));
			if(voxels == null) {
				System.out.println("Unable to read input file.");
				return -1;
			}
			if(size < 0) size = voxels.length;
//			voxels = Tools3D.scaleAndSoak(voxels);
            voxels = Tools3D.soak(voxels);

			int nameStart = Math.max(input.lastIndexOf('/'), input.lastIndexOf('\\')) + 1;
			this.input = input.substring(nameStart, input.indexOf('.', nameStart));
			new HeadlessApplication(new SpotVox(input, size, voxels, multiple, edge), configuration){
				{
					try {
						mainLoopThread.join(60000L);
					} catch (InterruptedException e) {
						System.out.println("Interrupted!");
					}
				}
			};

		} catch (FileNotFoundException e) {
			System.out.println("Specified file not found.");
			return -1;
		}
		return 0;
	}
}