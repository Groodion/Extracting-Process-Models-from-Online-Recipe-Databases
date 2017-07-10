package ai4.master.project.viewFx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class View extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Font.loadFont(getClass().getResource("/fonts/HelveticaNeue.ttf").toExternalForm(), 20);
		
		Parent parent = FXMLLoader.load(getClass().getResource("/fxml/view.fxml"));

		Scene scene = new Scene(parent);
		stage.setScene(scene);

		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
