package ai4.master.project.viewFx.components;

import java.io.File;

import org.controlsfx.control.textfield.CustomTextField;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseTool;
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
import javafx.stage.FileChooser;

public class SettingsDialog extends Dialog<String> {
	private TextField savePath;
	private Button search;

	public SettingsDialog() {
		this.getDialogPane().getStylesheets().add(
				   getClass().getResource("/css/style.css").toExternalForm());
		
		
		
		
		setResultConverter(r -> {
			if(r.getButtonData() == ButtonData.OK_DONE) {
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
		getDialogPane().setPrefSize(1024, 720);
		setGraphic(new ImageView(this.getClass().getResource("/img/preferences.png").toString()));
		setOnCloseRequest(e -> close());

		ButtonType okayButtonType = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		
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
		row0.setPercentHeight(50);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(50);
		mainLayout.getRowConstraints().addAll(row0, row1);
		
		TitledPane parserSettings = new TitledPane();
		parserSettings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		parserSettings.setCollapsible(false);
		parserSettings.setText("Parser Settings");
		TitledPane saveSettings = new TitledPane();
		saveSettings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		saveSettings.setText("Save Settings");
		saveSettings.setCollapsible(false);
		TitledPane layoutSettings = new TitledPane();
		layoutSettings.setCollapsible(false);
		layoutSettings.setText("Layout Settings");
		layoutSettings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		GridPane parserLayout = new GridPane();
		parserLayout.setHgap(10);
		parserLayout.setVgap(10);
		parserSettings.setContent(parserLayout);
		
		
		mainLayout.add(parserSettings, 0, 0);
		mainLayout.add(saveSettings, 0, 1);
		mainLayout.add(layoutSettings, 1, 0);
		
		parserLayout.add(new Label("Parser"), 0, 0);
		
		ComboBox<String> parser = new ComboBox();
		parserLayout.add(parser, 1, 0);
		
		
		GridPane saveLayout = new GridPane();
		saveSettings.setContent(saveLayout);
		saveLayout.setHgap(10);
		saveLayout.setVgap(10);
		saveLayout.add(new Label("Location for BPMN Models: "), 0, 0);
		savePath = new TextField();
		
		saveLayout.add(savePath, 1, 0);
		savePath.setPromptText("Path");
		savePath.setOnMouseClicked(new EventHandler<MouseEvent>() {

			
			@Override
			public void handle(MouseEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select File");

				File initialDirectory = null;

				try {
					initialDirectory = new File(savePath.getText()).getParentFile();
				} catch (Exception ex) {

				}

				if (initialDirectory != null) {
					fileChooser.setInitialDirectory(initialDirectory);
				}

				File file = fileChooser.showOpenDialog(null);

				if (file != null) {
					savePath.setText(file.getAbsolutePath());
				}
			}
			
		});

		GridPane styleLayout = new GridPane();
		styleLayout.setHgap(10);
		styleLayout.setVgap(10);
		
		layoutSettings.setContent(styleLayout);
		Label font = new Label("Font: ");
		Label fontSize = new Label("Font size: ");
		Label errorColor = new Label("Error color: ");
		Label unknownIngredient = new Label("Unknown Ingredient: ");
		Label unknownTool = new Label("Unknown Tool: ");
		Label unknownCookingAction = new Label("Unknown CookingAction: ");
		
		styleLayout.add(font, 0, 0);
		styleLayout.add(fontSize, 0, 1);
		styleLayout.add(errorColor, 0, 2);
		styleLayout.add(unknownIngredient, 0, 3);
		styleLayout.add(unknownTool, 0, 4);
		styleLayout.add(unknownCookingAction, 0, 5);
		
		ColorPicker colorPicker1 = new ColorPicker();
		styleLayout.add(colorPicker1, 1, 2);
        
		ColorPicker colorPicker2 = new ColorPicker();
		styleLayout.add(colorPicker2, 1, 3);
		
		ColorPicker colorPicker3 = new ColorPicker();
		styleLayout.add(colorPicker3, 1, 4);
		
		ColorPicker colorPicker4 = new ColorPicker();
		styleLayout.add(colorPicker4, 1, 5);
 
        colorPicker1.setOnAction(new EventHandler() {
            public void handle(Event t) {
            }
        });
        
        colorPicker2.setOnAction(new EventHandler() {
            public void handle(Event t) {
            }
        });
        
        colorPicker3.setOnAction(new EventHandler() {
            public void handle(Event t) {
            }
        });
        
        colorPicker4.setOnAction(new EventHandler() {
            public void handle(Event t) {
            }
        });
		
		getDialogPane().setContent(mainLayout);
	}
	


}
