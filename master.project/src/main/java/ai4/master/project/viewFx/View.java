package ai4.master.project.viewFx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.Optional;

public class View extends Application {

	private Text welcome;
	private Text subtitle;
	private Text copyright;
	private static Text loadingComment;
	private Label close;
	private ImageView loading;


	private Controller controller;
	
	private static final IntegerProperty loadingCounter = new SimpleIntegerProperty(0);
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage splashScreen = new Stage();
		welcome = new Text("Welcome");
		subtitle = new Text("Parsing recipes from online databases");
		copyright = new Text("AI4 Universität Bayreuth");
		loadingComment = new Text("Bla bala");
		
		welcome.setStyle("-fx-fill: #ffffff");
		subtitle.setStyle("-fx-fill: #ffffff");
		copyright.setStyle("-fx-fill: #ffffff");
		loadingComment.setStyle("-fx-fill: #ffffff");
		
		welcome.setFont(new Font("Segoe UI", 48));
		subtitle.setFont(new Font("Segoe UI", 18));
		copyright.setFont(new Font("Segoe UI", 12));
		loadingComment.setFont(new Font("Segoe UI", 12));
		
		welcome.setLayoutX(45);
		welcome.setLayoutY(96);

		subtitle.setLayoutX(130);
		subtitle.setLayoutY(127);

		copyright.setLayoutX(14);
		copyright.setLayoutY(288);
		
		loadingComment.setLayoutX(14);
		loadingComment.setLayoutY(310);

		close = new Label("X");
		close.setLayoutX(474);
		close.setLayoutY(5);
		close.setFont(new Font("Arial Black", 16));
		close.setStyle("-fx-text-fill: #ffffff");
		close.setOnMouseClicked((MouseEvent event) -> {
			Platform.exit();
			System.exit(0);
		});

		loading = new ImageView();
		loading.setPickOnBounds(true);
		loading.setFitWidth(100);
		loading.setLayoutX(200);
		loading.setLayoutY(164);
		loading.preserveRatioProperty().set(true);
		loading.setImage(new Image(View.class.getResourceAsStream("/img/294.GIF")));

		AnchorPane pane = new AnchorPane();
		pane.setStyle("-fx-background-color: #008B61");
		pane.getChildren().addAll(loading, welcome, subtitle, copyright, close, loadingComment);
		Scene scene = new Scene(pane);

		splashScreen.initStyle(StageStyle.UNDECORATED);
		splashScreen.setWidth(500);
		splashScreen.setHeight(320);
		splashScreen.setScene(scene);
		splashScreen.show();

		Task<Parent> service = new Task<Parent>() {
			@Override
			protected Parent call() throws Exception {
				setLoadingText("Loading fonts...");
				Font.loadFont(getClass().getResource("/fonts/HelveticaNeue.ttf").toExternalForm(), 20);
				setLoadingText("Loading gui...");
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view.fxml"));
				Parent parent = null;
				try {
					parent = loader.load();
				} catch(Exception e) {
					e.printStackTrace();
					
					Alert loaderException = new Alert(AlertType.ERROR);
					loaderException.setTitle("Error");
					loaderException.setHeaderText("Fatal Error: Could not load the main GUI.");
					loaderException.showAndWait();
					System.exit(0);
				}
				controller = loader.getController();
				
				setLoadingText("Loading configurations...");
				Configurations.load();
				
				return parent;
			}
		};

		service.setOnSucceeded(e -> {
			primaryStage.setScene(new Scene(service.getValue()));
			primaryStage.setWidth(Configurations.VIEW_WIDTH.get());
			primaryStage.setHeight(Configurations.VIEW_HEIGHT.get());
			primaryStage.setTitle("Extracting BPMN Models from recipes");
			Configurations.VIEW_WIDTH.bind(primaryStage.widthProperty());
			Configurations.VIEW_HEIGHT.bind(primaryStage.heightProperty());
			
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.setOnCloseRequest(r -> {
				if (controller.kwdbHasChanged()) {
					Alert alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
					alert.setTitle("Save Changes");
					alert.setHeaderText("The database has been changed!");
					alert.setContentText("Do you want to save the changes?");
					((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
					Optional<ButtonType> result = alert.showAndWait();
					result.ifPresent(button -> {
						if (button == ButtonType.YES) {
							File dbFile = Configurations.LIB_LOCATION.get();
							if (!dbFile.exists()) {
								try {
									dbFile.createNewFile();
								} catch (IOException ex) {
									Alert fileNotFoundOrCorruptedAlert = new Alert(AlertType.ERROR);
									fileNotFoundOrCorruptedAlert.setHeaderText("Error");
									fileNotFoundOrCorruptedAlert.setHeaderText("Can't create file at specified location!");
									fileNotFoundOrCorruptedAlert.showAndWait();
									
									ex.printStackTrace();
								}
							}

							if (dbFile.exists()) {
								try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dbFile), "UTF-8"))) {
									writer.write(controller.getKeyWordDatabase().toXML());
									writer.flush();
								} catch (IOException ex) {
									Alert fileNotFoundOrCorruptedAlert = new Alert(AlertType.ERROR);
									fileNotFoundOrCorruptedAlert.setHeaderText("Error");
									fileNotFoundOrCorruptedAlert.setHeaderText("Can't access file!");
									fileNotFoundOrCorruptedAlert.showAndWait();
									
									ex.printStackTrace();
								}
							}
						} else if (button == ButtonType.CANCEL) {
							r.consume();
						}
					});
				}
				Configurations.save();
			});
			
			primaryStage.show();
			splashScreen.hide();
		});

		Thread thread = new Thread(service);
		thread.start();
	}

	public static void main(String args[]) {
		launch(args);
		System.exit(1);
	}
	
	public static void blockLoading() {
		loadingCounter.set(loadingCounter.get() + 1);
	}
	public static void unblockLoading() {
		loadingCounter.set(loadingCounter.get() - 1);		
	}
	public static void setLoadingText(String text) {
		loadingComment.setText(text);
	}
}
