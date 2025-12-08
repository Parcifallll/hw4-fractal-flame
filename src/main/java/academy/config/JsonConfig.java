package academy.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class JsonConfig {
    @JsonProperty("size")
    public SizeConfig size;

    @JsonProperty("iteration_count")
    public Integer iterationCount;

    @JsonProperty("output_path")
    public String outputPath;

    @JsonProperty("threads")
    public Integer threads;

    @JsonProperty("seed")
    public Long seed;

    @JsonProperty("functions")
    public List<FunctionConfig> functions;

    @JsonProperty("affine_params")
    public List<AffineConfig> affineParams;

    @JsonProperty("gamma_correction")
    public Boolean gammaCorrection;

    @JsonProperty("gamma")
    public Double gamma;

    public static class SizeConfig {
        @JsonProperty("width")
        public Integer width;

        @JsonProperty("height")
        public Integer height;
    }

    public static class FunctionConfig {
        @JsonProperty("name")
        public String name;

        @JsonProperty("weight")
        public Double weight;
    }

    public static class AffineConfig {
        @JsonProperty("a")
        public Double a;

        @JsonProperty("b")
        public Double b;

        @JsonProperty("c")
        public Double c;

        @JsonProperty("d")
        public Double d;

        @JsonProperty("e")
        public Double e;

        @JsonProperty("f")
        public Double f;
    }
}
