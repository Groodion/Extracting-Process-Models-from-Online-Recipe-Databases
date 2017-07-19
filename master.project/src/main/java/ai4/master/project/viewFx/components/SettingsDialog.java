package ai4.master.project.viewFx.components;

import java.io.File;

import org.controlsfx.control.textfield.CustomTextField;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.viewFx.Configurations;
import ai4.master.project.viewFx.Controller;
import ai4.master.project.viewFx.components.editorViews.CookingActionsEditorView;
import ai4.master.project.viewFx.components.editorViews.EditorView;
import ai4.master.project.viewFx.components.editorViews.EventIndicatorsEditorView;
import ai4.master.project.viewFx.components.editorViews.IngredientGroupsEditorView;
import ai4.master.project.viewFx.components.editorViews.IngredientsEditorView;
import ai4.master.project.viewFx.components.editorViews.LastSentenceReferencesEditorView;
import ai4.master.project.viewFx.components.editorViews.PartIndicatorsEditorView;
import ai4.master.project.viewFx.components.editorViews.ToolsEditorView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class SettingsDialog extends Dialog<String> {
	private TextField savePath;
	private Button search;

	public SettingsDialog() {
		this.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

		setResultConverter(r -> {
			if (r.getButtonData() == ButtonData.OK_DONE) {
				return null;
			} else {
				return null;
			}
		});

		initDialog();
		initMainLayout();
	}

	private void initDialog() {
		setTitle("Settings");
		setHeaderText("Settings");
		setResizable(true);
		getDialogPane().setPrefSize(700, 500);
		getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
				getDialogPane().getStyleClass().add("myDialog");
		setGraphic(new ImageView(this.getClass().getResource("/img/preferences.png").toString()));
		setOnCloseRequest(e -> close());

		ButtonType okayButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		setResultConverter(button -> {
			if (button == okayButtonType) {
				Configurations.save();
			}

			Controller.unblockView();
			return "";
		});

		getDialogPane().getButtonTypes().addAll(okayButtonType, cancelButtonType);

		Button okayButton = (Button) this.getDialogPane().lookupButton(okayButtonType);
		okayButton.setId("OKAY");
		okayButton.setFocusTraversable(false);
		okayButton.setDefaultButton(false);

		Button cancelButton = (Button) this.getDialogPane().lookupButton(cancelButtonType);
		cancelButton.setId("CANCEL");
		cancelButton.setFocusTraversable(false);
		cancelButton.setDefaultButton(false);
	}

	private void initMainLayout() {		
		GridPane mainLayout = new GridPane();
		mainLayout.setHgap(10);
		mainLayout.setVgap(10);

		ColumnConstraints column0 = new ColumnConstraints();
		column0.setPercentWidth(50);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		mainLayout.getColumnConstraints().addAll(column0, column1);
		
		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(90);
		mainLayout.getRowConstraints().addAll(row0);
		
		TitledPane parserSettings = new TitledPane();
		parserSettings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		parserSettings.setCollapsible(false);
		parserSettings.setText("File Settings");
		
		TitledPane layoutSettings = new TitledPane();
		layoutSettings.setCollapsible(false);
		layoutSettings.setText("Layout Settings");
		layoutSettings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		GridPane fileLayout = new GridPane();
		fileLayout.setHgap(10);
		fileLayout.setVgap(10);
		fileLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		parserSettings.setContent(fileLayout);
		
		mainLayout.add(parserSettings, 0, 0);
		mainLayout.add(layoutSettings, 1, 0);
		
		fileLayout.add(new Label("Parser:"), 0, 0);
		
		
		///
		TextField parserPath = new TextField();
		parserPath.setMaxWidth(Double.MAX_VALUE);
		
		parserPath.textProperty().bind(Configurations.PARSER_CONFIGURATION.asString());
		fileLayout.add(parserPath, 1, 0);
		parserPath.setPromptText("Parser");
		parserPath.setOnMouseClicked(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(Configurations.PARSER_CONFIGURATION.get().getParentFile());
			fileChooser.setTitle("Select File");
	
			fileChooser.setInitialDirectory(Configurations.PARSER_CONFIGURATION.get());
	
			File file = fileChooser.showOpenDialog(null);
	
			if (file != null) {
				Configurations.PARSER_CONFIGURATION.set(file);
			}
		});
		

		fileLayout.add(new Label("Location for BPMN Models: "), 0, 1);
		savePath = new TextField();
		
		fileLayout.add(savePath, 1, 1);
		
		savePath.setPromptText("Path");
		savePath.setMaxWidth(Double.MAX_VALUE);
		savePath.textProperty().bind(Configurations.BPMN_LOCATION.asString());

		savePath.setOnMouseClicked(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select Directory");

			directoryChooser.setInitialDirectory(Configurations.BPMN_LOCATION.get());

			File directory = directoryChooser.showDialog(null);

			if (directory != null) {
				Configurations.BPMN_LOCATION.set(directory);
			}
		});

		fileLayout.add(new Label("Lib Location: "),0,2);
		
		TextField libPath = new TextField();
		libPath.setMaxWidth(Double.MAX_VALUE);
		libPath.textProperty().bind(Configurations.LIB_LOCATION.asString());
		fileLayout.add(libPath, 1, 2);
		libPath.setPromptText("Lib Path");
		libPath.setOnMouseClicked(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(Configurations.LIB_LOCATION.get().getParentFile());
			fileChooser.setTitle("Select File");
	
			fileChooser.setInitialDirectory(Configurations.LIB_LOCATION.get());
	
			File file = fileChooser.showOpenDialog(null);
	
			if (file != null) {
				Configurations.LIB_LOCATION.set(file);
			}
		});
	
		GridPane styleLayout = new GridPane();styleLayout.setHgap(10);styleLayout.setVgap(10);
	
		layoutSettings.setContent(styleLayout);
		Label unknownIngredient = new Label("Ingredient color: ");
		Label unknownGroup = new Label("Group color: ");
		Label unknownTool = new Label("Tool color: ");
		Label unknownCookingAction = new Label("CookingAction color: ");
	
		styleLayout.add(unknownIngredient,0,0);styleLayout.add(unknownGroup,0,1);styleLayout.add(unknownTool,0,2);styleLayout.add(unknownCookingAction,0,3);
	
		ColorPicker colorPicker1 = new ColorPicker();colorPicker1.valueProperty().bindBidirectional(Configurations.INGREDIENT_COLOR);styleLayout.add(colorPicker1,1,0);
	
		ColorPicker colorPicker2 = new ColorPicker();colorPicker2.valueProperty().bindBidirectional(Configurations.GROUPS_COLOR);styleLayout.add(colorPicker2,1,1);
	
		ColorPicker colorPicker3 = new ColorPicker();colorPicker3.valueProperty().bindBidirectional(Configurations.TOOL_COLOR);styleLayout.add(colorPicker3,1,2);
	
		ColorPicker colorPicker4 = new ColorPicker();colorPicker4.valueProperty().bindBidirectional(Configurations.COOKING_ACTION_COLOR);styleLayout.add(colorPicker4,1,3);
	
		HBox container = new HBox();
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		Button restoreDefaults = new Button("Restore defaults");
		restoreDefaults.setOnAction(e -> {
			Configurations.restoreDefaultValues();
		});
		container.getChildren().addAll(pane, restoreDefaults);
		mainLayout.add(container, 1, 1);
		getDialogPane().setContent(mainLayout);
	}
}