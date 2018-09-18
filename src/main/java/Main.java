import data.SystemInformation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SystemInformation.setMainStage(primaryStage);
        Scene mainScene = new Scene(new FXMLLoader().load(getClass().getResource("/scenes/main_scene.fxml")));
        SystemInformation.getMainStage().setScene(mainScene);
        SystemInformation.getMainStage().show();
        SystemInformation.getMainStage().setTitle("Process simulator");
        SystemInformation.getMainStage().getIcons().add(new Image("img/process1.png"));
        SystemInformation.getMainStage().setOnCloseRequest(event -> System.exit(0));
    }
}
