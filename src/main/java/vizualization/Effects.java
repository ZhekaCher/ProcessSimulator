package vizualization;

import javafx.scene.effect.*;
import javafx.scene.paint.Color;

public class Effects {
    public Effects() {
    }

    public static Effect successShadow() {
        InnerShadow successShadow = new InnerShadow();
        successShadow.setColor(Color.GREEN);
        return successShadow;
    }

    public static Effect errorShadow() {
        InnerShadow errorShadow = new InnerShadow();
        errorShadow.setColor(Color.RED);
        return errorShadow;
    }
}