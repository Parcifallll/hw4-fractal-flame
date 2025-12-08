package academy.renderer;

import academy.model.FractalConfig;
import academy.model.FractalImage;

public interface Renderer {
    FractalImage render(FractalConfig config);
}
