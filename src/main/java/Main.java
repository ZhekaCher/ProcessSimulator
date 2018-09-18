import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Scene mainScene = new Scene(new FXMLLoader().load(getClass().getResource("/scenes/main_scene.fxml")));
        mainStage.setScene(mainScene);
        mainStage.show();
    }
}
