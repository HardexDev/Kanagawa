package kanagawa.utils;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Utils {
    public static void closeWindow(MouseEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }
}
