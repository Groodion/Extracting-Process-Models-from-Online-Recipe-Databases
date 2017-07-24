package ai4.master.project.viewFx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.XMLLoader;
import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.apirequests.RecipeGetterKochbar;
import ai4.master.project.output.XMLWriter;
import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
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
import ai4.master.project.stanfordParser.exceptions.SentenceContainsNoVerbException;
import ai4.master.project.viewFx.components.BridgeObjID;
import ai4.master.project.viewFx.components.BridgeSize;
import ai4.master.project.viewFx.components.LibEditor;
import ai4.master.project.viewFx.components.OnlineDatabaseButton;
import ai4.master.project.viewFx.components.OnlineDatabaseButton.SearchType;
import ai4.master.project.viewFx.components.ProcessTracker;
import ai4.master.project.viewFx.components.SettingsDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import netscape.javascript.JSObject;

public class Controller implements Initializable {

	private static final ObservableList<String> MESSAGES = FXCollections.observableArrayList();
	private static final DoubleProperty progress = new SimpleDoubleProperty(0);
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
	@FXML
	private ProgressBar progressBar;
	@FXML
	private TitledPane databases;
	@FXML
	private TitledPane importFromFile;
	@FXML
	private BorderPane diagrammView;

	private LibEditor libEditor;
	private SettingsDialog settingsDialog;

	private ObjectProperty<Recipe> recipe;
	private ObjectProperty<KeyWordDatabase> kwdb;
	private ObjectProperty<BaseNamedObject<?, ?>> selectedObject;
	private BooleanProperty recipeParsed;
	private ObservableList<BaseTool> identifiedTools;
	private ObservableList<BaseIngredient> identifiedIngredients;
	private ObservableList<BaseCookingAction> identifiedActions;

	private Parser parser;
	private boolean kwdbHasChanged = false;
	private StringProperty bpmnCode;

	private WebView webView;
	private WebEngine engine;
		
	private File temp;
	
	
	public Controller() {
		recipe = new SimpleObjectProperty<Recipe>(new Recipe(LANG_FLAG.DE));
		kwdb = new SimpleObjectProperty<KeyWordDatabase>();
		selectedObject = new SimpleObjectProperty<BaseNamedObject<?, ?>>();
		recipeParsed = new SimpleBooleanProperty(false);
		bpmnCode = new SimpleStringProperty();
		
		kwdb.addListener((b, o, n) -> {
			if (parser != null) {
				parser.setKwdb(n);
			}
		});

		ButtonType resetButton = new ButtonType("Reset", ButtonData.OTHER);

		Configurations.PARSER_CONFIGURATION.addListener((b, o, n) -> {
			try {
				View.blockLoading();
				View.setLoadingText("Loading configurations... Parser");
				parser = new Parser(n.getAbsolutePath());
				parser.setKwdb(kwdb.get());
				View.unblockLoading();
			} catch (Exception e) {
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, null, ButtonType.OK, resetButton, ButtonType.CANCEL);
					alert.setTitle("ERROR");
					alert.setHeaderText("Error while loading Parser-Tagger!");
					alert.setContentText("Please select a valid Tagger-File.");

					alert.showAndWait().ifPresent(buttonType -> {
						if (buttonType == ButtonType.OK) {
							FileChooser fileChooser = new FileChooser();
							fileChooser.getExtensionFilters()
									.add(new ExtensionFilter("Tagger-File (*.tagger)", "*.tagger"));
							File file = fileChooser.showOpenDialog(null);

							if (file != null) {
								Configurations.PARSER_CONFIGURATION.set(file);
							}
						} else if (buttonType == resetButton) {
							Configurations.PARSER_CONFIGURATION
									.set(new File(Configurations.DEFAULT_PARSER_CONFIGURATION));
						} else if (parser == null) {
							System.exit(0);
						}
						View.unblockLoading();
					});
				});
				e.printStackTrace();
			}
		});
		Configurations.LIB_LOCATION.addListener((b, o, n) -> {
			if (kwdbHasChanged()) {
				Alert alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
				alert.setTitle("Save Changes");
				alert.setHeaderText("The database has been changed!");
				alert.setContentText("Do you want to save the changes?");
				((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
				Optional<ButtonType> result = alert.showAndWait();
				result.ifPresent(button -> {
					if (button == ButtonType.YES) {
						File dbFile = o;
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
							try (BufferedWriter writer = new BufferedWriter(
									new OutputStreamWriter(new FileOutputStream(dbFile), "UTF-8"))) {
								writer.write(getKeyWordDatabase().toXML());
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
						kwdbHasChanged = false;
						Configurations.LIB_LOCATION.set(o);
						kwdbHasChanged = true;
						return;
					}
				});
			}

			try {
				View.blockLoading();
				View.setLoadingText("Loading configurations... Library");
				kwdb.set(XMLLoader.load(n.getAbsolutePath()));
				View.unblockLoading();
			} catch (Exception e) {
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, null, ButtonType.OK, resetButton, ButtonType.CANCEL);
					alert.setTitle("ERROR");
					alert.setHeaderText("Error while loading KeyWordDatabase!");
					alert.setContentText("Please select a valid KeyWordDatabase-File.");

					alert.showAndWait().ifPresent(buttonType -> {
						if (buttonType == ButtonType.OK) {
							FileChooser fileChooser = new FileChooser();
							fileChooser.getExtensionFilters()
									.add(new ExtensionFilter("KeyWordDatabase (*.xml)", "*.xml"));
							File file = fileChooser.showOpenDialog(null);

							if (file != null) {
								Configurations.LIB_LOCATION.set(file);
							}
						} else if (buttonType == resetButton) {
							Configurations.LIB_LOCATION.set(new File(Configurations.DEFAULT_LIB_LOCATION));
						} else if (kwdb.get() == null) {
							System.exit(0);
						}

						View.unblockLoading();
					});
				});
				e.printStackTrace();
			}
		});

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
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				showLibEditor();
				libEditor.searchAndScroll(toolsListView.getSelectionModel().getSelectedItem().getFirstName());
			}
		});
		ingredientsListView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				showLibEditor();
				libEditor.searchAndScroll(ingredientsListView.getSelectionModel().getSelectedItem().getFirstName());
			}
		});
		actionsListView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
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

		progressBar.setProgress(0);
		progressBar.progressProperty().bind(progress);

		initializeDiagrammViewer();
		/*
		 * Layout
		 */

		recipeDatabasesPane.getChildren()
				.addAll(new OnlineDatabaseButton("Chefkoch", "www.chefkoch.de", "German", "/img/chefkoch.png",
						new RecipeGetterChefkoch(), recipe, SearchType.ID, SearchType.LINK, SearchType.CATEGORY),
						new OnlineDatabaseButton("Kochbar", "www.kochbar.de", "German", "/img/kochbar.jpg",
								new RecipeGetterKochbar(), recipe, SearchType.LINK),
						new OnlineDatabaseButton("Food2Fork", "www.food2fork.com", "English", "/img/food2fork.jpg",
								null, recipe, SearchType.ID, SearchType.LINK));
		databases.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
		importFromFile.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
	}

	public void initializeDiagrammViewer() {
		System.out.println("1");

		Platform.runLater(() -> {
			try {

				webView = new WebView();

				webView.setContextMenuEnabled(false);
				ScrollPane scrollPane = new ScrollPane(webView);

				engine = webView.getEngine();

				// creating bridgeObjID for javascript injection
				BridgeObjID bridgeObjID = new BridgeObjID();
				bridgeObjID.getProperty().addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						System.out.println("selected ObjID: " + newValue);
					}
				});

				// creating bridgeSize for javascript injection
				BridgeSize bridgeSize = new BridgeSize();

				String file = Controller.class.getClassLoader().getResource("indexFX.html").toExternalForm();
				engine.load(file);

				engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

					@Override
					public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldState,
							Worker.State newState) {
						System.out.println("changed: old " + oldState + " new " + newState);
						if (newState == Worker.State.SUCCEEDED) {
							JSObject win = (JSObject) engine.executeScript("window");
							// injection of bridgeObjID into javascript
							win.setMember("bridgeObjID", bridgeObjID);
							// injection of bridgeSize into javascript
							win.setMember("bridgeSize", bridgeSize);
							win.setMember("java", this);

							Timeline t = new Timeline();
							t.getKeyFrames().add(new KeyFrame(Duration.ONE, e -> {
								String value = engine.executeScript("getViewSize()").toString();
								String[] parts = value.split(",");

								double width = Double.parseDouble(parts[0]);
								double height = Double.parseDouble(parts[1]);

								webView.setPrefSize(width + 100, height + 100);
							}));
							t.setCycleCount(-1);
							t.play();
						}
					}

				});

				ContextMenu cmDiagrammSave = new ContextMenu();

				MenuItem saveAsSVG = new MenuItem("Save as svg");
				saveAsSVG.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
				MenuItem saveAsBPMN = new MenuItem("Save as bpmn");
				saveAsBPMN.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));

				saveAsSVG.setOnAction(e -> {
					String temp = engine
							.executeScript("var svgCode; bpmnViewer.saveSVG(function(a, svg) {svgCode = svg}); svgCode")
							.toString();
					FileChooser fc = new FileChooser();
					fc.getExtensionFilters().add(new ExtensionFilter("SVG File (*.svg)", ".svg"));
					File svgFile = fc.showSaveDialog(null);
					if (svgFile != null) {
						XMLWriter writer = new XMLWriter(svgFile.getName());
						writer.writeTo(svgFile, temp);
					}

				});

				saveAsBPMN.setOnAction(e -> {
					FileChooser fc = new FileChooser();
					fc.getExtensionFilters().add(new ExtensionFilter("BPMN File (*.bpmn)", ".bpmn"));
					File bpmnFile = fc.showSaveDialog(null);
					if (bpmnFile != null) {
						XMLWriter writer = new XMLWriter(bpmnFile.getName());
						writer.writeTo(bpmnFile, bpmnCode.get());
					}

				});

				cmDiagrammSave.getItems().add(saveAsSVG);
				cmDiagrammSave.getItems().add(saveAsBPMN);

				webView.setOnMouseClicked(e -> {
					if (e.getButton() == MouseButton.SECONDARY) {
						cmDiagrammSave.show(webView, e.getScreenX(), e.getScreenY());
					} else {
						cmDiagrammSave.hide();
					}
				});

				diagrammView.setCenter(scrollPane);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
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
			e.printStackTrace();
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
		while ((line = br.readLine()) != null) {
			text = text + line;
		}
		preparationTA.setText(text);
		br.close();

	}

	public void updateRecipeSteps() {
		identifiedTools.clear();
		identifiedIngredients.clear();
		identifiedActions.clear();

		ingredientsPane.getChildren().clear();

		for (String ingrdient : recipe.get().getIngredients()) {
			createWordLabel(ingrdient, ingredientsPane);
			ingredientsPane.getChildren().add(new Label(" "));
		}

		stepsPane.getChildren().clear();

		if (recipe.get().getSteps().isEmpty()) {
			createTextFlowPane(recipe.get().getPreparation(), stepsPane);
		} else {
			for (Step step : recipe.get().getSteps()) {
				HBox stepPane = new HBox();
				stepPane.setMaxWidth(Double.MAX_VALUE);
				stepPane.setSpacing(10);
				Label showStepInfoBtn = new Label("O");
				showStepInfoBtn.setMaxHeight(Double.MAX_VALUE);
				showStepInfoBtn.setOnMouseClicked(e -> {
					Alert stepInfoDlg = new Alert(AlertType.INFORMATION);
					stepInfoDlg.setContentText(step.toEasyToReadString());
					stepInfoDlg.showAndWait();
				});
				stepPane.getChildren().add(showStepInfoBtn);
				createTextFlowPane(step.getText(), stepPane);

				stepsPane.getChildren().add(stepPane);
			}
		}

		Collections.sort(identifiedTools);
		Collections.sort(identifiedIngredients);
		Collections.sort(identifiedActions);
	}

	public void createTextFlowPane(String text, Pane parent) {
		FlowPane textFlowPane = new FlowPane();
		textFlowPane.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(textFlowPane, Priority.ALWAYS);
		text = text.replaceAll("[!\"§$%&/()=?*+'#,;.:_<>\n]", " $0 ");
		text = text.trim();

		String[] words = text.split(" ");

		for (String word : words) {
			createWordLabel(word, textFlowPane);
			textFlowPane.getChildren().add(new Label(" "));
		}
		parent.getChildren().add(textFlowPane);
	}

	public void createWordLabel(String word, FlowPane flowPane) {
		Label label = new Label(word);
		if (!word.toLowerCase().equals(word.toUpperCase())) {

			BaseNamedObject<?, ?> obj = kwdb.get().find(word);

			label.backgroundProperty().bind(Bindings.when(label.hoverProperty())
					.then(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)))
					.otherwise(Bindings.when(selectedObject.isNotNull().and(selectedObject.isEqualTo(obj)))
							.then(new Background(
									new BackgroundFill(Color.color(0.9, 0.9, 0.9), CornerRadii.EMPTY, Insets.EMPTY)))
							.otherwise(new Background(
									new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)))));

			if (kwdb.get().findCookingAction(word) != null) {
				label.textFillProperty().bind(Configurations.COOKING_ACTION_COLOR);
				BaseCookingAction action = kwdb.get().findCookingAction(word);
				if (!identifiedActions.contains(action)) {
					identifiedActions.add(action);
				}
			} else if (kwdb.get().findIngredient(word) != null) {
				BaseIngredient ingredient = kwdb.get().findIngredient(word);
				if (!identifiedIngredients.contains(ingredient)) {
					identifiedIngredients.add(ingredient);
				}
				if (kwdb.get().findIngredientGroup(word) != null) {
					label.textFillProperty().bind(Configurations.GROUPS_COLOR);
				} else {
					label.textFillProperty().bind(Configurations.INGREDIENT_COLOR);
				}
			} else if (kwdb.get().findTool(word) != null) {
				BaseTool tool = kwdb.get().findTool(word);
				if (!identifiedTools.contains(tool)) {
					identifiedTools.add(tool);
				}
				label.textFillProperty().bind(Configurations.TOOL_COLOR);
			} else {
				ContextMenu cm = new ContextMenu();

				MenuItem addToTools = new MenuItem("add to Tools");
				addToTools.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addAsSynonymToTools = new MenuItem("add as synonym to Tools");
				addAsSynonymToTools.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addToIngredients = new MenuItem("add to Ingredients");
				addToIngredients.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addAsSynonymToIngredients = new MenuItem("add as synonym to Ingredients");
				addAsSynonymToIngredients.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addToGroups = new MenuItem("add to Groups");
				addToGroups.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addAsSynonymToGroups = new MenuItem("add as synonym to Groups");
				addAsSynonymToGroups.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addToCookingActions = new MenuItem("add to CookingActions");
				addToCookingActions.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
				MenuItem addAsSynonymToCookingActions = new MenuItem("add as synonym to CookingActions");
				addAsSynonymToCookingActions.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));

				Alert objectAdded = new Alert(AlertType.INFORMATION);
				objectAdded.setHeaderText("Object added");

				addToTools.setOnAction(e -> {
					BaseTool tool = new BaseTool();
					tool.addName(word);
					kwdb.get().getTools().add(tool);
					objectAdded.showAndWait();
					updateRecipeSteps();
					kwdbHasChanged = true;
				});
				addAsSynonymToTools.setOnAction(e -> {
					Dialog<BaseTool> selectToolDialog = new Dialog<BaseTool>();
					selectToolDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
					((Button) selectToolDialog.getDialogPane().lookupButton(ButtonType.OK)).setDefaultButton(false);
					selectToolDialog.setHeaderText("Add '" + word + "' as synonym to ");
					ComboBox<BaseTool> selectToolCB = new ComboBox<BaseTool>();
					selectToolCB.setItems(FXCollections.observableArrayList(kwdb.get().getTools()));
					selectToolCB.getSelectionModel().selectFirst();
					selectToolDialog.setResultConverter(buttonType -> {
						if (buttonType == ButtonType.OK) {
							return selectToolCB.getValue();
						} else {
							return null;
						}
					});
					selectToolDialog.getDialogPane().setContent(new StackPane(selectToolCB));
					selectToolDialog.showAndWait().ifPresent(tool -> {
						kwdbHasChanged = true;
						tool.addName(word);
						updateRecipeSteps();
					});
				});
				addToIngredients.setOnAction(e -> {
					BaseIngredient ingredient = new BaseIngredient();
					ingredient.addName(word);
					kwdb.get().getIngredients().add(ingredient);
					objectAdded.showAndWait();
					updateRecipeSteps();
					kwdbHasChanged = true;
				});
				addAsSynonymToIngredients.setOnAction(e -> {
					Dialog<BaseIngredient> selectIngredientDialog = new Dialog<BaseIngredient>();
					selectIngredientDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
					((Button) selectIngredientDialog.getDialogPane().lookupButton(ButtonType.OK))
							.setDefaultButton(false);
					selectIngredientDialog.setHeaderText("Add '" + word + "' as synonym to ");
					ComboBox<BaseIngredient> selectIngredientCB = new ComboBox<BaseIngredient>();
					selectIngredientCB.setItems(FXCollections.observableArrayList(kwdb.get().getIngredients()));
					selectIngredientCB.getSelectionModel().selectFirst();
					selectIngredientDialog.setResultConverter(buttonType -> {
						if (buttonType == ButtonType.OK) {
							System.out.println("ok");
							return selectIngredientCB.getValue();
						} else {
							return null;
						}
					});
					selectIngredientDialog.getDialogPane().setContent(new StackPane(selectIngredientCB));
					selectIngredientDialog.showAndWait().ifPresent(ingredient -> {
						kwdbHasChanged = true;
						ingredient.addName(word);
						updateRecipeSteps();
					});
				});
				addToGroups.setOnAction(e -> {
					BaseIngredientGroup group = new BaseIngredientGroup();
					group.addName(word);
					kwdb.get().getIngredientGroups().add(group);
					objectAdded.showAndWait();
					updateRecipeSteps();
					kwdbHasChanged = true;
				});
				addAsSynonymToGroups.setOnAction(e -> {
					Dialog<BaseIngredientGroup> selectIngredientGroupGroupDialog = new Dialog<BaseIngredientGroup>();
					selectIngredientGroupGroupDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,
							ButtonType.OK);
					((Button) selectIngredientGroupGroupDialog.getDialogPane().lookupButton(ButtonType.OK))
							.setDefaultButton(false);
					selectIngredientGroupGroupDialog.setHeaderText("Add '" + word + "' as synonym to ");
					ComboBox<BaseIngredientGroup> selectIngredientGroupCB = new ComboBox<BaseIngredientGroup>();
					selectIngredientGroupCB
							.setItems(FXCollections.observableArrayList(kwdb.get().getIngredientGroups()));
					selectIngredientGroupCB.getSelectionModel().selectFirst();
					selectIngredientGroupGroupDialog.setResultConverter(buttonType -> {
						if (buttonType == ButtonType.OK) {
							return selectIngredientGroupCB.getValue();
						} else {
							return null;
						}
					});
					selectIngredientGroupGroupDialog.getDialogPane().setContent(new StackPane(selectIngredientGroupCB));
					selectIngredientGroupGroupDialog.showAndWait().ifPresent(ingredientGroupGroup -> {
						kwdbHasChanged = true;
						ingredientGroupGroup.addName(word);
						updateRecipeSteps();
					});
				});
				addToCookingActions.setOnAction(e -> {
					BaseCookingAction action = new BaseCookingAction();
					action.getRegexList().add(new Regex(".*", Result.NO_RESULT));
					action.addName(word);
					kwdb.get().getCookingActions().add(action);
					objectAdded.showAndWait();
					updateRecipeSteps();
					kwdbHasChanged = true;
				});
				addAsSynonymToCookingActions.setOnAction(e -> {
					Dialog<BaseCookingAction> selectCookingActionDialog = new Dialog<BaseCookingAction>();
					selectCookingActionDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
					((Button) selectCookingActionDialog.getDialogPane().lookupButton(ButtonType.OK))
							.setDefaultButton(false);
					selectCookingActionDialog.setHeaderText("Add '" + word + "' as synonym to ");
					ComboBox<BaseCookingAction> selectCookingActionCB = new ComboBox<BaseCookingAction>();
					selectCookingActionCB.setItems(FXCollections.observableArrayList(kwdb.get().getCookingActions()));
					selectCookingActionCB.getSelectionModel().selectFirst();
					selectCookingActionDialog.setResultConverter(buttonType -> {
						if (buttonType == ButtonType.OK) {
							return selectCookingActionCB.getValue();
						} else {
							return null;
						}
					});
					selectCookingActionDialog.getDialogPane().setContent(new StackPane(selectCookingActionCB));
					selectCookingActionDialog.showAndWait().ifPresent(cookingAction -> {
						kwdbHasChanged = true;
						cookingAction.addName(word);
						updateRecipeSteps();
					});
				});

				cm.getItems().addAll(addToTools, addAsSynonymToTools, addToIngredients, addAsSynonymToIngredients,
						addToGroups, addAsSynonymToGroups, addToCookingActions, addAsSynonymToCookingActions);

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
		
		Task<?> parseRecipe = new Task<Object>() {
			@Override
			protected Recipe call() throws Exception {
				parser.parseRecipe(recipe.get());

				return recipe.get();
			}
		};
		parseRecipe.setOnSucceeded(e -> {
			updateRecipeSteps();
			recipeParsed.set(true);
			blockingPane.setVisible(false);
		});
		parseRecipe.exceptionProperty().addListener((b, o, n) -> {
			if(n != null) {
				if(n instanceof SentenceContainsNoVerbException) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("The Sentence '" + ((SentenceContainsNoVerbException) n).getSentence().getText()
							+ "' contains no verb and can't be parsed");

				}
				
				n.printStackTrace();
			}
		});

		new Thread(parseRecipe).start();
	}

	public void showLibEditor() {
		blockingPane.setVisible(true);
		if (libEditor == null) {
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

	public void showProperties() {
		blockingPane.setVisible(true);
		if (settingsDialog == null) {
			settingsDialog = new SettingsDialog();
		}

		settingsDialog.showAndWait();

	}

	public void prevStep() {
		processTracker.previous();

		for (int i = 0; i < contentStack.getChildren().size(); i++) {
			contentStack.getChildren().get(i).setVisible(i == processTracker.getActiveStep());
		}

		progressBar.progressProperty().unbind();
		progressBar.setProgress(0);

		switch ((int) processTracker.getActiveStep()) {
		case 0: {
			break;
		}
		case 1: {
			break;
		}
		}
	}

	public void nextStep() {
		MESSAGES.clear();
		processTracker.next();

		for (int i = 0; i < contentStack.getChildren().size(); i++) {
			contentStack.getChildren().get(i).setVisible(i == processTracker.getActiveStep());
		}

		setProgress(0);

		switch ((int) processTracker.getActiveStep()) {
		case 1: {
			recipeParsed.set(false);
			Recipe recipe = this.recipe.get();
			recipe.getIngredients().clear();
			String ingredientsList = ingredientsTA.getText().replaceAll(",", " ").trim();
			String[] ingredients = ingredientsList.split(" ");
			for (String ingredient : ingredients) {
				ingredient = ingredient.trim();
				if (ingredient.length() != 0) {
					recipe.getIngredients().add(ingredient);
				}
			}
			recipe.setPreparation(preparationTA.getText());

			recipe.getSteps().clear();

			updateRecipeSteps();
			break;
		}
		case 2: {
			try {
				temp = File.createTempFile("diagram", ".bpmn");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Task<String> createModel = new Task<String>() {
				@Override
				protected String call() throws Exception {
					ProcessModeler processModeler = new ProcessModelerImpl();
					processModeler.setFile(temp);
					processModeler.getProgress().addListener((b, o, n) -> {
						setProgress((double) n);
					});
					processModeler.createBpmn(recipe.get());
					return processModeler.getXml();
				}
			};
			createModel.setOnSucceeded(e -> {
				try {
					bpmnCode.set(createModel.get());
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
				
				callDiagram(new SimpleStringProperty(temp.toURI().toString()));
			});
			createModel.exceptionProperty().addListener((b, o, n) -> {
				if(n != null) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Error during model creation!");
					alert.showAndWait();
					n.printStackTrace();
				}
			});
			
			new Thread(createModel).start();
			}
		}
	}

	public void finish() {
		Alert alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.setTitle("Information");
		alert.setHeaderText("Not saved files would not be stored!");
		alert.setContentText("Do you want to save your files?");
		((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);

		Optional<ButtonType> result = alert.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.CANCEL) {
				return;
			} else if (buttonType == ButtonType.YES) {
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(new ExtensionFilter("BPMN File (*.bpmn)", ".bpmn"));
				File bpmnFile = fc.showSaveDialog(null);
				if (bpmnFile != null) {
					XMLWriter writer = new XMLWriter(bpmnFile.getName());
					writer.writeTo(bpmnFile, bpmnCode.get());
				}
			}
			recipeImportFilePathTF.setText("");
			ingredientsTA.setText("");
			preparationTA.setText("");

			messagesListView.getItems().clear();

			prevStep();
			prevStep();

			removeDiagram();
			identifiedTools.clear();
			identifiedIngredients.clear();
			identifiedActions.clear();
		});
	}

	public void resetParsing() {
		recipe.get().getSteps().clear();
		recipeParsed.set(false);
		updateRecipeSteps();
	}

	public KeyWordDatabase getKeyWordDatabase() {
		return kwdb.get();
	}

	public boolean kwdbHasChanged() {
		return kwdbHasChanged;
	}

	private void callDiagram(StringProperty bpmnUrl) {
		if (bpmnUrl.getValue() != null) {
			removeDiagram();

			String js2 = "showBpmnDiagram('" + bpmnUrl.getValue() + "');";
			engine.executeScript(js2);
		}
	}

	private void removeDiagram() {
		String js1 = "removeBpmnDiagram()";
		engine.executeScript(js1);
	}

	public static void blockView() {
		bPane.setVisible(true);
	}

	public static void unblockView() {
		bPane.setVisible(false);
	}

	public static void setProgress(double progress) {
		Platform.runLater(() -> Controller.progress.set(progress));
	}

	public static void addMessage(String string) {
		Platform.runLater(() -> MESSAGES.add(string));
	}
}