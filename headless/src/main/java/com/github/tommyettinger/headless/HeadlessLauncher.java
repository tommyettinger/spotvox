package com.github.tommyettinger.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.Renderer;
import com.github.tommyettinger.SpotVox;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "spotvox", version = "SpotVox 0.0.1",
		description = "Given a .vox file, write pixel art renders to a subfolder.",
		mixinStandardHelpOptions = true)
public class HeadlessLauncher implements Callable<Integer> {

	public Renderer renderer = new Renderer();
	
	@CommandLine.Option(names = {"-O", "--octaves"}, description = "The amount of octaves to use; more increases detail.", defaultValue = "3")
	public int octaves = 3;

	@CommandLine.Option(names = {"-W", "--width"}, description = "The width of the resulting image.", defaultValue = "512")
	public int width = 512;

	@CommandLine.Option(names = {"-H", "--height"}, description = "The height of the resulting image.", defaultValue = "512")
	public int height = 512;

	@CommandLine.Option(names = {"-t", "--type"}, description = "The type of noise to generate; one of: simplex, perlin, cubic, foam, honey, mutant, value, white, cellular.", defaultValue = "simplex")
	public String type = "simplex";

	@CommandLine.Option(names = {"-F", "--fractal"}, description = "The fractal mode to use for most noise types; one of: fbm, billow, ridged.", defaultValue = "fbm")
	public String fractal = "fbm";

	@CommandLine.Option(names = {"-c", "--cellular"}, description = "The cellular return type to use for the cellular type; one of: value, lookup, distance, distance2, distance2add, distance2mul, distance2div.", defaultValue = "value")
	public String cellular = "value";

	@CommandLine.Option(names = {"-S", "--sharpness"}, description = "The sharpness multiplier for foam and mutant noise; higher than one means more extreme.", defaultValue = "1")
	public float sharpness = 1f;

	@CommandLine.Option(names = {"-C", "--curvature"}, description = "How steep the transition should be from black to white; must be positive.", defaultValue = "1")
	public float curvature = 1f;

	@CommandLine.Option(names = {"-M", "--middle"}, description = "When curvature is not 1.0, this determines where the noise starts to turn its curve; must be between 0 and 1, inclusive.", defaultValue = "0.5")
	public float middle = 0.5f;

	@CommandLine.Option(names = {"-m", "--mutation"}, description = "The extra 'spatial' value used by mutant noise; can be any float.", defaultValue = "0")
	public float mutation = 0f;

	@CommandLine.Option(names = {"-o", "--output"}, description = "The name and/or path for the output file.", defaultValue = "noise.png")
	public String output = "noise.png";

	@CommandLine.Option(names = {"-d", "--debug"}, description = "If true, draws higher-than-1 noise as red, and lower-than-negative-1 as blue.", defaultValue = "false")
	public boolean debug = false;

	@CommandLine.Option(names = {"-e", "--equalize"}, description = "If true, makes each grayscale value approximately as frequent as all other values.", defaultValue = "false")
	public boolean equalize = false;

	public static void main(String[] args) {
		int exitCode = new picocli.CommandLine(new HeadlessLauncher()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		configuration.updatesPerSecond = -1;
		// set all fields on Renderer here.
		// that might be a lot.
		new HeadlessApplication(new SpotVox(renderer), configuration){
			{
				try {
					mainLoopThread.join(60000L);
				} catch (InterruptedException e) {
					System.out.println("Interrupted!");
				}
			}
		};
		return 0;
	}
}