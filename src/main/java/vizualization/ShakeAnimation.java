package vizualization;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class ShakeAnimation {
    public ShakeAnimation() {
    }

    public final void shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(this.duration()), node);
        shake.setFromX(0.0D);
        shake.setByX(8.0D);
        shake.setAutoReverse(true);
        shake.setCycleCount(4);
        shake.playFromStart();
        shake.setOnFinished((actionEvent) -> {
            this.activityOnFinished();
        });
    }

    public void activityOnFinished() {
    }

    public double duration() {
        return 50;
    }
}
