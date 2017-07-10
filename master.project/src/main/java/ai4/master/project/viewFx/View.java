package ai4.master.project.viewFx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class View extends Application {

	public static String pathToKeyWordDatabase;

	@Override
	public void start(Stage stage) throws Exception {
		pathToKeyWordDatabase = "resources/Lib.xml";

		Font.loadFont(getClass().getResource("/fonts/HelveticaNeue.ttf").toExternalForm(), 20);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view.fxml"));
		Parent parent = loader.load();

		Scene scene = new Scene(parent);
		stage.setScene(scene);

		stage.show();

		stage.setOnCloseRequest(e -> {
			Controller controller = (Controller) loader.getController();
			if (controller.kwdbHasChanged()) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Save Changes");
				alert.setHeaderText("The database has been changed!");
				alert.setContentText("Do you want to save the changes?");

				Optional<ButtonType> result = alert.showAndWait();
				result.ifPresent(button -> {
					if (button == ButtonType.OK) {
						File dbFile = new File(pathToKeyWordDatabase);
						if (!dbFile.exists()) {
							try {
								dbFile.createNewFile();
							} catch (IOException ex) {
								Alert fileNotFoundOrCorruptedAlert = new Alert(AlertType.ERROR);
								fileNotFoundOrCorruptedAlert.setHeaderText("Error");
								fileNotFoundOrCorruptedAlert.setHeaderText("Can't create file at specified location!");
								fileNotFoundOrCorruptedAlert.showAndWait();
							}
						}
						if (dbFile.exists()) {
							try (BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(dbFile) , "UTF-8") )) {
								writer.write(controller.getKeyWordDatabase().toXML());
								writer.flush();
							} catch (IOException ex) {
								Alert fileNotFoundOrCorruptedAlert = new Alert(AlertType.ERROR);
								fileNotFoundOrCorruptedAlert.setHeaderText("Error");
								fileNotFoundOrCorruptedAlert.setHeaderText("Can't access file!");
								fileNotFoundOrCorruptedAlert.showAndWait();
							}
						}
					}
				});
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
