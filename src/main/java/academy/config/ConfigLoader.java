package academy.config;

import academy.model.AffineTransformation;
import academy.model.FractalConfig;
import academy.model.TransformationType;
import academy.model.WeightedFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static FractalConfig load(
            String configPath,
            Integer width,
            Integer height,
            Long seed,
            Integer iterationCount,
            String outputPath,
            Integer threads,
            String affineParams,
            String functions,
            Boolean gammaCorrection,
            Double gamma) {

        JsonConfig jsonConfig = null;
        if (configPath != null && !configPath.isBlank()) {
            jsonConfig = loadJsonConfig(configPath);
        }

        int finalWidth = selectValue(width, jsonConfig != null && jsonConfig.size != null
                ? jsonConfig.size.width
                : null, 1920);
        int finalHeight = selectValue(height, jsonConfig != null && jsonConfig.size != null
                ? jsonConfig.size.height
                : null, 1080);
        long finalSeed = selectValue(seed, jsonConfig != null ? jsonConfig.seed : null, 5L);
        int finalIterationCount = selectValue(
                iterationCount, jsonConfig != null ? jsonConfig.iterationCount : null, 2500);
        String finalOutputPath =
                selectValue(outputPath, jsonConfig != null ? jsonConfig.outputPath : null, "result.png");
        int finalThreads = selectValue(threads, jsonConfig != null ? jsonConfig.threads : null, 1);
        boolean finalGammaCorrection = selectValue(
                gammaCorrection, jsonConfig != null ? jsonConfig.gammaCorrection : null, true);
        double finalGamma = selectValue(gamma, jsonConfig != null ? jsonConfig.gamma : null, 2.2);

        // Priority: CLI > JSON > defaults
        List<AffineTransformation> affineTransformations;
        if (affineParams != null && !affineParams.isBlank()) {
            affineTransformations = CliParser.parseAffineParams(affineParams);
        } else if (jsonConfig != null && jsonConfig.affineParams != null) {
            affineTransformations = convertAffineFromJson(jsonConfig.affineParams);
        } else {
            affineTransformations = CliParser.parseAffineParams(null);
        }

        // Priority: CLI > JSON > defaults
        List<WeightedFunction> weightedFunctions;
        if (functions != null && !functions.isBlank()) {
            weightedFunctions = CliParser.parseFunctions(functions);
        } else if (jsonConfig != null && jsonConfig.functions != null) {
            weightedFunctions = convertFunctionsFromJson(jsonConfig.functions);
        } else {
            weightedFunctions = CliParser.parseFunctions(null);
        }

        return new FractalConfig(
                finalWidth,
                finalHeight,
                finalSeed,
                finalIterationCount,
                finalOutputPath,
                finalThreads,
                affineTransformations,
                weightedFunctions,
                finalGammaCorrection,
                finalGamma);
    }

    private static JsonConfig loadJsonConfig(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("Config file not found: " + path);
            }
            return MAPPER.readValue(file, JsonConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + e.getMessage(), e);
        }
    }

    private static List<AffineTransformation> convertAffineFromJson(List<JsonConfig.AffineConfig> affineConfigs) {
        List<AffineTransformation> result = new ArrayList<>();
        Random random = new Random();

        for (JsonConfig.AffineConfig config : affineConfigs) {
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);

            result.add(new AffineTransformation(
                    config.a != null ? config.a : 1.0,
                    config.b != null ? config.b : 0.0,
                    config.c != null ? config.c : 0.0,
                    config.d != null ? config.d : 0.0,
                    config.e != null ? config.e : 1.0,
                    config.f != null ? config.f : 0.0,
                    red,
                    green,
                    blue));
        }

        return result;
    }

    private static List<WeightedFunction> convertFunctionsFromJson(List<JsonConfig.FunctionConfig> functionConfigs) {
        List<WeightedFunction> result = new ArrayList<>();

        for (JsonConfig.FunctionConfig config : functionConfigs) {
            TransformationType type = TransformationType.fromString(config.name);
            double weight = config.weight != null ? config.weight : 1.0;
            result.add(new WeightedFunction(type, weight));
        }

        return result;
    }

    // Select value based on priority: CLI > JSON > default
    private static <T> T selectValue(T cliValue, T jsonValue, T defaultValue) {
        if (cliValue != null) {
            return cliValue;
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        return defaultValue;
    }
}
