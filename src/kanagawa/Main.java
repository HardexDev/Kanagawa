package kanagawa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kanagawa.models.Game;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/start_screen.fxml")));
        primaryStage.setTitle("Kanagawa");
        primaryStage.setScene(new Scene(root, screenBounds.getWidth(), screenBounds.getHeight()));
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
