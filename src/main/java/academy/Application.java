package academy;

import academy.config.ConfigLoader;
import academy.model.FractalConfig;
import academy.model.FractalImage;
import academy.processor.ImageProcessor;
import academy.renderer.Renderer;
import academy.renderer.SingleThreadRenderer;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "fractal-flame", version = "1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Option(names = {"-w", "--width"}, description = "Image width")
    private Integer width;

    @Option(names = {"-h", "--height"}, description = "Image height")
    private Integer height;

    @Option(names = {"--seed"}, description = "Random generator seed")
    private Long seed;

    @Option(names = {"-i", "--iteration-count"}, description = "Number of iterations")
    private Integer iterationCount;

    @Option(names = {"-o", "--output-path"}, description = "Output PNG file path")
    private String outputPath;

    @Option(names = {"-t", "--threads"}, description = "Number of threads")
    private Integer threads;

    @Option(names = {"-ap", "--affine-params"}, description = "Affine transformation parameters")
    private String affineParams;

    @Option(names = {"-f", "--functions"}, description = "Transformation functions with weights")
    private String functions;

    @Option(names = {"--config"}, description = "Path to JSON config file")
    private String configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            FractalConfig config = ConfigLoader.load(
                    configPath, width, height, seed, iterationCount, outputPath, threads, affineParams, functions);

            LOGGER.atInfo()
                    .addKeyValue("width", config.width())
                    .addKeyValue("height", config.height())
                    .addKeyValue("seed", config.seed())
                    .addKeyValue("iterations", config.iterationCount())
                    .addKeyValue("threads", config.threads())
                    .addKeyValue("output", config.outputPath())
                    .log("Starting fractal flame generation");

            // Render fractal
            Renderer renderer = new SingleThreadRenderer();
            FractalImage image = renderer.render(config);

            // Save to file
            ImageProcessor processor = new ImageProcessor();
            processor.save(image, Path.of(config.outputPath()));

            LOGGER.atInfo().log("Fractal generation completed successfully");
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Failed to generate fractal: {}", e.getMessage());
            System.exit(1);
        }
    }
}

