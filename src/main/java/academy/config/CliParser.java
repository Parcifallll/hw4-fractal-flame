package academy.config;

import academy.model.AffineTransformation;
import academy.model.TransformationType;
import academy.model.WeightedFunction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CliParser {
    // parse affine params in format: a,b,c,d,e,f/a,b,c,d,e,f/
    public static List<AffineTransformation> parseAffineParams(String affineParams) {
        if (affineParams == null || affineParams.isBlank()) {
            return generateDefaultAffine();
        }

        List<AffineTransformation> result = new ArrayList<>();
        String[] transformations = affineParams.split("/");

        for (String transformation : transformations) {
            String[] coeffs = transformation.split(",");
            if (coeffs.length != 6) {
                throw new IllegalArgumentException(
                        "Affine transformation must have 6 coefficients, got: " + coeffs.length);
            }

            try {
                double a = Double.parseDouble(coeffs[0].trim());
                double b = Double.parseDouble(coeffs[1].trim());
                double c = Double.parseDouble(coeffs[2].trim());
                double d = Double.parseDouble(coeffs[3].trim());
                double e = Double.parseDouble(coeffs[4].trim());
                double f = Double.parseDouble(coeffs[5].trim());

                // put random color to each affine transformation
                // Intentionally creating new Random instance for each transformation to get different colors
                @SuppressFBWarnings(
                        value = "DMI_RANDOM_USED_ONLY_ONCE",
                        justification = "Need different random colors for each affine transformation")
                Random random = new Random();
                int red = random.nextInt(256);
                int green = random.nextInt(256);
                int blue = random.nextInt(256);

                result.add(new AffineTransformation(a, b, c, d, e, f, red, green, blue));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid affine coefficient: " + ex.getMessage());
            }
        }

        return result;
    }

    // parse functions in format: name:weight,name:weight, ...
    public static List<WeightedFunction> parseFunctions(String functions) {
        if (functions == null || functions.isBlank()) {
            return List.of(
                    new WeightedFunction(TransformationType.SINUSOIDAL, 1.0),
                    new WeightedFunction(TransformationType.SPHERICAL, 1.0));
        }

        List<WeightedFunction> result = new ArrayList<>();
        String[] parts = functions.split(",");

        for (String part : parts) {
            String[] nameWeight = part.split(":");
            if (nameWeight.length != 2) {
                throw new IllegalArgumentException("Function must be in format 'name:weight', got: " + part);
            }

            try {
                String name = nameWeight[0].trim();
                double weight = Double.parseDouble(nameWeight[1].trim());
                TransformationType type = TransformationType.fromString(name);
                result.add(new WeightedFunction(type, weight));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid weight: " + nameWeight[1]);
            }
        }

        return result;
    }

    private static List<AffineTransformation> generateDefaultAffine() {
        Random random = new Random();
        List<AffineTransformation> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            double a = random.nextDouble() * 2 - 1;
            double b = random.nextDouble() * 2 - 1;
            double c = random.nextDouble() * 2 - 1;
            double d = random.nextDouble() * 2 - 1;
            double e = random.nextDouble() * 2 - 1;
            double f = random.nextDouble() * 2 - 1;
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);

            result.add(new AffineTransformation(a, b, c, d, e, f, red, green, blue));
        }

        return result;
    }
}
