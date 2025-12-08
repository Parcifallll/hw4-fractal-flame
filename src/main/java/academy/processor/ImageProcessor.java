package academy.processor;

import academy.model.FractalImage;
import academy.model.Pixel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessor.class);

    public void save(FractalImage image, Path outputPath, boolean gammaCorrection, double gamma) throws IOException {
        LOGGER.info("Processing image for output");

        // Find max hit count for normalization
        int maxHits = 0;
        for (Pixel pixel : image.getData()) {
            if (pixel.getHitCount() > maxHits) {
                maxHits = pixel.getHitCount();
            }
        }

        if (maxHits == 0) {
            LOGGER.warn("Image has no hits, output will be black");
            maxHits = 1;
        }

        BufferedImage bufferedImage = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_RGB);

        // Convert histogram to RGB image with gamma correction
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                Pixel pixel = image.pixel(x, y);
                if (pixel != null && pixel.isHit()) {
                    // Normalize by hit count
                    double normalR = pixel.getR() / pixel.getHitCount();
                    double normalG = pixel.getG() / pixel.getHitCount();
                    double normalB = pixel.getB() / pixel.getHitCount();

                    double correctedBrightness = 1.0;
                    if (gammaCorrection) {
                        // Apply logarithmic gamma correction
                        double brightness = Math.log10(pixel.getHitCount()) / Math.log10(maxHits);
                        correctedBrightness = Math.pow(brightness, 1.0 / gamma);
                    }

                    // Scale to [0, 255]
                    int r = clamp((int) (normalR * correctedBrightness));
                    int g = clamp((int) (normalG * correctedBrightness));
                    int b = clamp((int) (normalB * correctedBrightness));

                    // Combine RGB into single int
                    int rgb = (r << 16) | (g << 8) | b;
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }

        LOGGER.info("Saving image to: {}", outputPath);
        ImageIO.write(bufferedImage, "PNG", outputPath.toFile());
        LOGGER.info("Image saved successfully");
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
