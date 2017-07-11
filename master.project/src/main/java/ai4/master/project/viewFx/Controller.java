package ai4.master.project.viewFx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.recipe.LANG_FLAG;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseNamedObject;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Regex.Result;
import ai4.master.project.stanfordParser.Parser;
import ai4.master.project.viewFx.components.LibEditor;
import ai4.master.project.viewFx.components.OnlineDatabaseButton;
import ai4.master.project.viewFx.components.ProcessTracker;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class Controller implements Initializable {

	public static final ObservableList<String> MESSAGES = FXCollections.observableArrayList();
	private static Pane bPane;
	
	@FXML
	private VBox recipeDatabasesPane;

	@FXML
	private TextField recipeImportFilePathTF;
	@FXML
	private TextArea ingredientsTA;
	@FXML
	private TextArea preparationTA;
	@FXML
	private ProcessTracker processTracker;
	@FXML 
	private StackPane contentStack;
	@FXML
	private FlowPane ingredientsPane;
	@FXML
	private VBox stepsPane;
	@FXML
	private ListView<BaseTool> toolsListView;
	@FXML
	private ListView<BaseIngredient> ingredientsListView;
	@FXML
	private ListView<BaseCookingAction> actionsListView;
	@FXML
	private ListView<String> messagesListView;
	@FXML
	private Pane blockingPane;
	
	private LibEditor libEditor;
	
	private ObjectProperty<Recipe> recipe;
	private ObjectProperty<KeyWordDatabase> kwdb;
	private ObjectProperty<BaseNamedObject<?, ?>> selectedObject;
	private BooleanProperty recipeParsed;
	private ObservableList<BaseTool> identifiedTools;
	private ObservableList<BaseIngredient> identifiedIngredients;
	private ObservableList<BaseCookingAction> identifiedActions;
	
	private Parser parser;
	private boolean kwdbHasChanged = false;
	
	
	public Controller() {
		parser = new Parser("lib/models/german-fast.tagger");
		recipe = new SimpleObjectProperty<Recipe>(new Recipe(LANG_FLAG.DE));
		kwdb = new SimpleObjectProperty<KeyWordDatabase>(KeyWordDatabase.GERMAN_KWDB);
		selectedObject = new SimpleObjectProperty<BaseNamedObject<?, ?>>();
		recipeParsed = new SimpleBooleanProperty(false);
		parser.setKwdb(kwdb.get());

		identifiedTools = FXCollections.observableArrayList();
		identifiedIngredients = FXCollections.observableArrayList();
		identifiedActions = FXCollections.observableArrayList();
	}
	@Override
	public void initialize(URL url, ResourceBundle rB) {
		bPane = blockingPane;
		
		/*
		 * Logik
		 */
		kwdb.addListener((b, o, n) -> {
			parser.setKwdb(n);
		});
		
		toolsListView.setItems(identifiedTools);
		ingredientsListView.setItems(identifiedIngredients);
		actionsListView.setItems(identifiedActions);
		messagesListView.setItems(MESSAGES);
		
		ChangeListener<BaseNamedObject<?, ?>> cListener = (b, o, n) -> {
			selectedObject.set(n);
		};
		toolsListView.getSelectionModel().selectedItemProperty().addListener(cListener);
		ingredientsListView.getSelectionModel().selectedItemProperty().addListener(cListener);
		actionsListView.getSelectionModel().selectedItemProperty().addListener(cListener);

		toolsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				showLibEditor();
				libEditor.searchAndScroll(toolsListView.getSelectionModel().getSelectedItem().getFirstName());
			}
		});
		ingredientsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				showLibEditor();
				libEditor.searchAndScroll(ingredientsListView.getSelectionModel().getSelectedItem().getFirstName());
			}
		});
		actionsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				showLibEditor();
				libEditor.searchAndScroll(actionsListView.getSelectionModel().getSelectedItem().getFirstName());
			}
		});
		
		recipe.addListener((b, o, n) -> {
			if (n == null) {
				ingredientsTA.setText("");
				preparationTA.setText("");
			} else {
				StringBuilder ingredientsText = new StringBuilder();

				for (int i = 0; i < n.getIngredients().size(); i++) {
					if (i != 0) {
						ingredientsText.append(", ");
					}

					ingredientsText.append(n.getIngredients().get(i));
				}

				ingredientsTA.setText(ingredientsText.toString());

				preparationTA.setText(n.getPreparation());
			}
		});
		kwdb.addListener((b, o, n) -> {

		});

		/*
		 * Layout
		 */

		recipeDatabasesPane.getChildren().addAll(
				new OnlineDatabaseButton("Chefkoch", "www.chefkoch.de", "German", "/img/chefkoch.png",
						new RecipeGetterChefkoch(), recipe),
				new OnlineDatabaseButton("Kochbar", "www.kochbar.de", "German", "/img/kochbar.jpg", null, recipe),
				new OnlineDatabaseButton("Food2Fork", "www.food2fork.com", "English", "/img/food2fork.jpg", null,
						recipe));
	}

	public boolean isRecipeParsed() {
		return recipeParsed.get();
	}
	public void setRecipeParsed(boolean recipeParsed) {
		this.recipeParsed.set(recipeParsed);
	}
	public BooleanProperty recipeParsedProperty() {
		return recipeParsed;
	}
	
	public void selectFileForRecipeImport() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");

		File initialDirectory = null;

		try {
			initialDirectory = new File(recipeImportFilePathTF.getText()).getParentFile();
		} catch (Exception e) {

		}

		if (initialDirectory != null) {
			fileChooser.setInitialDirectory(initialDirectory);
		}

		File file = fileChooser.showOpenDialog(null);

		if (file != null) {
			recipeImportFilePathTF.setText(file.getAbsolutePath());
		}
	}
	public void loadFileForRecipeImport() throws IOException {
		 FileReader fr = new FileReader(recipeImportFilePathTF.getText());
		 BufferedReader br = new BufferedReader(fr);

		    String line = "";
		    String text = "";
		    while( (line = br.readLine()) != null )
		    {
		      text = text+line;
		    }
		    preparationTA.setText(text);
		    br.close();
		
	}

	public void updateRecipeSteps() {
		identifiedTools.clear();
		identifiedIngredients.clear();
		identifiedActions.clear();
		
		ingredientsPane.getChildren().clear();
		
		for(String ingrdient : recipe.get().getIngredients()) {
			createWordLabel(ingrdient, ingredientsPane);
			ingredientsPane.getChildren().add(new Label(" "));
		}
		
		stepsPane.getChildren().clear();
		
		if(recipe.get().getSteps().isEmpty()) {
			createTextFlowPane(recipe.get().getPreparation());
		} else {
			for(Step step : recipe.get().getSteps()) {
				createTextFlowPane(step.getText());
			}
		}
	}
	public void createTextFlowPane(String text) {
		FlowPane textFlowPane = new FlowPane();
		textFlowPane.setMaxWidth(Double.MAX_VALUE);
		text = text.replaceAll("[!\"§$%&/()=?*+'#,;.:_<>\n]", " $0 ");
		text = text.trim();
		
		String[] words = text.split(" ");
		
		for (String word : words) {
			createWordLabel(word, textFlowPane);
			textFlowPane.getChildren().add(new Label(" "));
		}
		stepsPane.getChildren().add(textFlowPane);
	}
	public void createWordLabel(String word, FlowPane flowPane) {
		Label label = new Label(word);
		if (!word.toLowerCase().equals(word.toUpperCase())) {
			
			BaseNamedObject<?, ?> obj = kwdb.get().find(word);
			
			
			label.backgroundProperty().bind(Bindings.when(label.hoverProperty())
					.then(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)))
					.otherwise(Bindings.when(selectedObject.isNotNull().and(selectedObject.isEqualTo(obj)))
							.then(new Background(new BackgroundFill(Color.color(0.9, 0.9, 0.9), CornerRadii.EMPTY, Insets.EMPTY)))
							.otherwise(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)))));

			if (kwdb.get().findCookingAction(word) != null) {
				label.setTextFill(Color.RED);
				BaseCookingAction action = kwdb.get().findCookingAction(word);
				if(!identifiedActions.contains(action)) {
					identifiedActions.add(action);
				}
			} else if (kwdb.get().findIngredient(word) != null) {
				BaseIngredient ingredient = kwdb.get().findIngredient(word);
				if(!identifiedIngredients.contains(ingredient)) {
					identifiedIngredients.add(ingredient);
				}
				if (kwdb.get().findIngredientGroup(word) != null) {
					label.setTextFill(Color.GREENYELLOW);
				} else {
					label.setTextFill(Color.GREEN);
				}
			} else if (kwdb.get().findTool(word) != null) {
				BaseTool tool = kwdb.get().findTool(word);
				if(!identifiedTools.contains(tool)) {
					identifiedTools.add(tool);
				}
				label.setTextFill(Color.BLUE);
			} else {
				ContextMenu cm = new ContextMenu();

				MenuItem addToTools = new MenuItem("add to Tools");
				MenuItem addToIngredients = new MenuItem("add to Ingredients");
				MenuItem addToGroups = new MenuItem("add to Groups");
				MenuItem addToCookingActions = new MenuItem("add to CookingActions");

				Alert objectAdded = new Alert(AlertType.INFORMATION);
				objectAdded.setHeaderText("Object added");
							
				addToTools.setOnAction(e -> {
					BaseTool tool = new BaseTool();
					tool.addName(word);
					kwdb.get().getTools().add(tool);
					objectAdded.showAndWait();
					updateRecipeSteps();
				});
				addToIngredients.setOnAction(e -> {
					BaseIngredient ingredient = new BaseIngredient();
					ingredient.addName(word);
					kwdb.get().getIngredients().add(ingredient);
					objectAdded.showAndWait();
					updateRecipeSteps();
				});
				addToGroups.setOnAction(e -> {
					BaseIngredientGroup group = new BaseIngredientGroup();
					group.addName(word);
					kwdb.get().getIngredientGroups().add(group);
					objectAdded.showAndWait();
					updateRecipeSteps();
				});
				addToCookingActions.setOnAction(e -> {
					BaseCookingAction action = new BaseCookingAction();
					action.getRegexList().add(new Regex(".*", Result.NO_RESULT));
					action.addName(word);
					kwdb.get().getCookingActions().add(action);
					objectAdded.showAndWait();
					updateRecipeSteps();
				});
				
				cm.getItems().add(addToTools);
				cm.getItems().add(addToIngredients);
				cm.getItems().add(addToGroups);
				cm.getItems().add(addToCookingActions);

				label.setOnMouseClicked(e -> {
					if (e.getButton() == MouseButton.SECONDARY) {
						cm.show(label, e.getScreenX(), e.getScreenY());
					} else {
						cm.hide();
					}
				});
			}
		}
		flowPane.getChildren().add(label);
	}
	
	public void parseRecipe() {
		blockingPane.setVisible(true);
		MESSAGES.clear();
		parser.parseRecipe(recipe.get());
		updateRecipeSteps();
		recipeParsed.set(true);
		blockingPane.setVisible(false);
	}
	
	public void showLibEditor() {
		blockingPane.setVisible(true);
		if(libEditor == null) {
			libEditor = new LibEditor(kwdb);
		}
		
		Platform.runLater(() -> {
			Optional<KeyWordDatabase> kwdb = libEditor.showAndWait();
			kwdb.ifPresent(db -> {
				this.kwdb.set(db);
				kwdbHasChanged = true;
			});
			blockingPane.setVisible(false);
		});
	}
	
	public void prevStep() {
		processTracker.previous();
		
		for(int i = 0; i < contentStack.getChildren().size(); i++) {
			contentStack.getChildren().get(i).setVisible(i == processTracker.getActiveStep());
		}
	}
	public void nextStep() {
		MESSAGES.clear();
		processTracker.next();

		for(int i = 0; i < contentStack.getChildren().size(); i++) {
			contentStack.getChildren().get(i).setVisible(i == processTracker.getActiveStep());
		}
		
		switch ((int) processTracker.getActiveStep()) {
			case 1: {
				recipeParsed.set(false);
				Recipe recipe = this.recipe.get();
				recipe.getIngredients().clear();
				String ingredientsList = ingredientsTA.getText().replaceAll(",", " ").trim();
				String[] ingredients = ingredientsList.split(" ");
				for(String ingredient : ingredients) {
					ingredient = ingredient.trim();
					if(ingredient.length() != 0) {
						recipe.getIngredients().add(ingredient);
					}
				}
				recipe.setPreparation(preparationTA.getText());
				
				recipe.getSteps().clear();
				
				updateRecipeSteps();
				break;
			}
			case 2: {
	
				break;
			}
			case 3: {
	
				break;
			}
		}
	}
	public KeyWordDatabase getKeyWordDatabase() {
		return kwdb.get();
	}
	public boolean kwdbHasChanged() {
		return kwdbHasChanged;
	}

	public static void blockView() {
		bPane.setVisible(true);
	}
	public static void unblockView() {
		bPane.setVisible(false);
	}
}