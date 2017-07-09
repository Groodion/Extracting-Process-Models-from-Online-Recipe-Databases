package ai4.master.project.view;

import java.util.Optional;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.stanfordParser.Parser;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class View extends Application {
	private BorderPane border;
	private GridPane mainView;
	private TextArea recipeText;
	private LibEditor editor;
	private ObservableList<String> problemsEntry = FXCollections.observableArrayList("Unknown Child Group in groups",
			"Unknown Child Group in groups", "Unknown Child Group in groups", "Unknown Child Group in groups");

	HBox chefkoch;

	private HBox header;
	private VBox parserTextLines;
	private RecipeGetterChefkoch recipeChefkochGetter;

	private Recipe recipe = null;
	private Parser parser;

	@Override
	public void start(Stage primaryStage) {
		primaryStage = new Stage();

		parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(KeyWordDatabase.GERMAN_KWDB);

		// Font test =
		 Font.loadFont(View.class.getResource("/fonts/HelveticaNeue.ttf").toExternalForm(), 20);
		editor = new LibEditor();
		recipeText = new TextArea();

		recipeText.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		recipeText.setWrapText(true);
		border = new BorderPane();
		border.getStyleClass().add("background");
		header = new HBox();
		header.getStyleClass().add("hbackground");
		header.setPadding(new Insets(5, 5, 5, 5));
		Scene scene = new Scene(border);
		scene.getStylesheets().add(View.class.getClass().getResource("/css/style.css").toExternalForm());

		recipeChefkochGetter = new RecipeGetterChefkoch();

		chefkoch = new HBox();
		chefkoch.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("Choose recipe source");
				dialog.setHeaderText("Chefkoch");
				dialog.setContentText("Please enter the recipe id:");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					String id = result.get();
					recipe = recipeChefkochGetter.getRecipe(id);
					recipeText.setText(recipe.getPreparation());
				}
			}

		});

		chefkoch.getStyleClass().add("databaseLabel");
		chefkoch.getStyleClass().add("chefkoch-label");
		chefkoch.setSpacing(5);

		// Logo initialisieren
		ImageView chefkochLogo = new ImageView(this.getClass().getResource("/img/chefkoch.png").toString());
		chefkochLogo.setFitHeight(69);
		chefkochLogo.setFitWidth(116);

		Separator chefkochSep = new Separator();
		chefkochSep.getStyleClass().add("separator");
		chefkochSep.setOrientation(Orientation.VERTICAL);
		chefkochSep.setValignment(VPos.CENTER);
		chefkochSep.setPrefHeight(70);

		// Adding Information
		GridPane chefkochGrid = new GridPane();
		chefkochGrid.setVgap(4);
		chefkochGrid.setHgap(4);
		chefkochGrid.setPadding(new Insets(5, 5, 5, 5));
		Label chefkochLabel = new Label("Chefkoch");
		chefkochLabel.getStyleClass().add("header-label");
		chefkochGrid.add(chefkochLabel, 0, 0);
		chefkochGrid.add(new Label(""), 1, 0);
		chefkochGrid.add(new Label("Language:"), 0, 1);
		Label chefkochLangLabel = new Label("German");
		chefkochLangLabel.getStyleClass().add("attribute-value");
		chefkochGrid.add(chefkochLangLabel, 1, 1);
		chefkochGrid.add(new Label("Link:"), 0, 2);
		chefkochGrid.add(new Label("www.chefkoch.de"), 1, 2);

		chefkoch.getChildren().add(chefkochSep);
		chefkoch.getChildren().add(chefkochLogo);
		chefkoch.getChildren().add(chefkochGrid);

		/*
		 * ----------------------------------------------------------
		 */
		HBox kochbar = new HBox();
		kochbar.getStyleClass().add("databaseLabel");
		kochbar.getStyleClass().add("kochbar-label");
		kochbar.setSpacing(5);

		// Logo initialisieren
		ImageView kochbarLogo = new ImageView(this.getClass().getResource("/img/kochbar.jpg").toString());
		kochbarLogo.setFitHeight(69);
		kochbarLogo.setFitWidth(116);

		Separator kochbarSep = new Separator();
		kochbarSep.getStyleClass().add("separator");
		kochbarSep.setOrientation(Orientation.VERTICAL);
		kochbarSep.setValignment(VPos.CENTER);
		kochbarSep.setPrefHeight(70);

		// Adding Information
		GridPane kochbarGrid = new GridPane();
		kochbarGrid.setVgap(4);
		kochbarGrid.setHgap(4);
		kochbarGrid.setPadding(new Insets(5, 5, 5, 5));
		Label kochbarLabel = new Label("Kochbar");
		kochbarLabel.getStyleClass().add("header-label");
		kochbarGrid.add(kochbarLabel, 0, 0);
		kochbarGrid.add(new Label(""), 1, 0);
		kochbarGrid.add(new Label("Language:"), 0, 1);
		Label kochbarLangLabel = new Label("German");
		kochbarLangLabel.getStyleClass().add("attribute-value");
		kochbarGrid.add(kochbarLangLabel, 1, 1);
		kochbarGrid.add(new Label("Link:"), 0, 2);
		kochbarGrid.add(new Label("www.kochbar.de"), 1, 2);

		kochbar.getChildren().add(kochbarSep);
		kochbar.getChildren().add(kochbarLogo);
		kochbar.getChildren().add(kochbarGrid);

		/*
		 * ----------------------------------------------------------
		 */

		HBox food2fork = new HBox();
		food2fork.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				editor.showAndWait();
			}

		});
		food2fork.getStyleClass().add("databaseLabel");
		food2fork.getStyleClass().add("food2fork-label");
		food2fork.setSpacing(5);

		// Logo initialisieren
		ImageView food2forkLogo = new ImageView(this.getClass().getResource("/img/food2fork.jpg").toString());
		food2forkLogo.setFitHeight(69);
		food2forkLogo.setFitWidth(116);

		Separator food2forkSep = new Separator();
		food2forkSep.getStyleClass().add("separator");
		food2forkSep.setOrientation(Orientation.VERTICAL);
		food2forkSep.setValignment(VPos.CENTER);
		food2forkSep.setPrefHeight(70);

		// Adding Information
		GridPane food2forkGrid = new GridPane();
		food2forkGrid.setVgap(4);
		food2forkGrid.setHgap(4);
		food2forkGrid.setPadding(new Insets(5, 5, 5, 5));
		Label food2forkLabel = new Label("Food2Fork");
		food2forkLabel.getStyleClass().add("header-label");
		food2forkGrid.add(food2forkLabel, 0, 0);
		food2forkGrid.add(new Label(""), 1, 0);
		food2forkGrid.add(new Label("Language:"), 0, 1);
		Label food2forkLangLabel = new Label("English");
		food2forkLangLabel.getStyleClass().add("attribute-value");
		food2forkGrid.add(food2forkLangLabel, 1, 1);
		food2forkGrid.add(new Label("Link:"), 0, 2);
		food2forkGrid.add(new Label("www.food2fork.com"), 1, 2);

		food2fork.getChildren().add(food2forkSep);
		food2fork.getChildren().add(food2forkLogo);
		food2fork.getChildren().add(food2forkGrid);

		VBox recipeDatabases = new VBox();
		recipeDatabases.setSpacing(10);
		recipeDatabases.getChildren().add(chefkoch);
		recipeDatabases.getChildren().add(kochbar);
		recipeDatabases.getChildren().add(food2fork);

		TitledPane gridTitlePane = new TitledPane();
		gridTitlePane.setText("Choose an recipe of an online recipe database");
		// gridTitlePane.setMaxHeight(Double.MAX_VALUE);
		gridTitlePane.setContent(recipeDatabases);

		TitledPane localFileSource = new TitledPane();
		localFileSource.setText("Choose an local file");
		HBox localFileChoose = new HBox();
		localFileChoose.setSpacing(10);
		TextField path = new TextField();
		path.setPromptText("Path");
		path.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.showOpenDialog(null);
			}

		});
		Button load = new Button("Load");
		localFileChoose.getChildren().addAll(path, load);
		localFileSource.setContent(localFileChoose);

		TitledPane preferences = new TitledPane();
		preferences.setText("Edit Library");
		HBox preferencesActionField = new HBox();
		preferencesActionField.setSpacing(10);
		
		VBox sources = new VBox();
		sources.setSpacing(10);
		sources.setMaxHeight(Double.MAX_VALUE);
		sources.getChildren().addAll(gridTitlePane, localFileSource, preferences);

		mainView = new GridPane();
		ColumnConstraints column2 = new ColumnConstraints();
		ColumnConstraints column1 = new ColumnConstraints();
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(95);

		column2.setPercentWidth(65);
		column1.setPercentWidth(35);
		mainView.getColumnConstraints().addAll(column1, column2);
		mainView.getRowConstraints().addAll(row1);
		mainView.setHgap(10);
		mainView.setVgap(10);
		mainView.setPadding(new Insets(10, 10, 10, 10));
		mainView.add(sources, 0, 0);
		mainView.add(recipeText, 1, 0);

		Button next = new Button("Next >");

		mainView.add(next, 1, 1);
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		// btn.setFont(test);

		btn.setPrefWidth(250);
		btn.setPrefHeight(75);
		btn.getStyleClass().add("button-type-1");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Hello World!");
			}
		});

		ImageView logo = new ImageView(this.getClass().getResource("/img/logo.png").toString());
		logo.setFitHeight(59);
		logo.setFitWidth(192);

		HBox processTracker = new HBox();
		processTracker.setPadding(new Insets(20, 10, 20, 10));
		processTracker.getStyleClass().add("process-tracker");
		processTracker.setSpacing(50);
		HBox step1 = new HBox();
		step1.setSpacing(15);
		ImageView step1Icon = new ImageView(this.getClass().getResource("/img/step1.png").toString());
		Label step1Label = new Label("Step 1");
		step1Label.getStyleClass().add("process-step-header-active");
		Label step1DescriptionLabel = new Label("Choose an Recipe Source");
		step1DescriptionLabel.getStyleClass().add("process-step-description-active");

		VBox descriptionStep1 = new VBox();
		descriptionStep1.getChildren().add(step1Label);
		descriptionStep1.getChildren().add(step1DescriptionLabel);

		step1.getChildren().add(step1Icon);
		step1.getChildren().add(descriptionStep1);

		HBox step2 = new HBox();
		step2.setSpacing(15);
		ImageView step2Icon = new ImageView(this.getClass().getResource("/img/step2.png").toString());
		Label step2Label = new Label("Step 2");
		step2Label.getStyleClass().add("process-step-header");
		Label step2DescriptionLabel = new Label("Anaylse and parse the recipse");
		step2DescriptionLabel.getStyleClass().add("process-step-description");

		VBox descriptionStep2 = new VBox();
		descriptionStep2.getChildren().add(step2Label);
		descriptionStep2.getChildren().add(step2DescriptionLabel);

		step2.getChildren().add(step2Icon);
		step2.getChildren().add(descriptionStep2);

		HBox step3 = new HBox();
		step3.setSpacing(15);
		ImageView step3Icon = new ImageView(this.getClass().getResource("/img/step3.png").toString());
		Label step3Label = new Label("Step 3");
		step3Label.getStyleClass().add("process-step-header");
		Label step3DescriptionLabel = new Label("Creating a BPMN-Model");
		step3DescriptionLabel.getStyleClass().add("process-step-description");

		VBox descriptionStep3 = new VBox();
		descriptionStep3.getChildren().add(step3Label);
		descriptionStep3.getChildren().add(step3DescriptionLabel);

		step3.getChildren().add(step3Icon);
		step3.getChildren().add(descriptionStep3);

		HBox step4 = new HBox();
		step4.setSpacing(15);
		ImageView step4Icon = new ImageView(this.getClass().getResource("/img/step4.png").toString());
		Label step4Label = new Label("Step 4");
		step4Label.getStyleClass().add("process-step-header");
		Label step4DescriptionLabel = new Label("Optimizing BPMN-Model");
		step4DescriptionLabel.getStyleClass().add("process-step-description");

		VBox descriptionStep4 = new VBox();
		descriptionStep4.getChildren().add(step4Label);
		descriptionStep4.getChildren().add(step4DescriptionLabel);

		step4.getChildren().add(step4Icon);
		step4.getChildren().add(descriptionStep4);

		processTracker.getChildren().add(step1);
		processTracker.getChildren().add(step2);
		processTracker.getChildren().add(step3);
		processTracker.getChildren().add(step4);

		ImageView languageIcon = new ImageView(this.getClass().getResource("/img/language.png").toString());
		ImageView informationIcon = new ImageView(this.getClass().getResource("/img/information.png").toString());

		languageIcon.setFitHeight(50);
		languageIcon.setFitWidth(50);

		informationIcon.setFitHeight(50);
		informationIcon.setFitWidth(50);

		Button language = new Button();
		language.getStyleClass().add("button-type-1");
		language.setGraphic(languageIcon);

		Button information = new Button("Info");
		information.getStyleClass().add("button-type-1");
		information.setGraphic(informationIcon);

		HBox tools = new HBox();
		tools.setSpacing(10);
		tools.getChildren().add(information);
		tools.getChildren().add(language);

		GridPane step2Pane = new GridPane();
		step2Pane.setPadding(new Insets(10, 10, 10, 10));
		step2Pane.setHgap(10);
		step2Pane.setVgap(10);
		RowConstraints problemRow1 = new RowConstraints();
		problemRow1.setPercentHeight(80);
		RowConstraints problemRow2 = new RowConstraints();
		problemRow2.setPercentHeight(20);
		ColumnConstraints problemColumn1 = new ColumnConstraints();
		problemColumn1.setPercentWidth(80);
		ColumnConstraints problemColumn2 = new ColumnConstraints();
		problemColumn2.setPercentWidth(20);
		step2Pane.getColumnConstraints().addAll(problemColumn1, problemColumn2);
		step2Pane.getRowConstraints().addAll(problemRow1, problemRow2);

		VBox parserTextPane = new VBox();
		parserTextLines = new VBox();

		TitledPane problemsPane = new TitledPane();
		problemsPane.setText("Problems and Warnings");

		TitledPane legendPane = new TitledPane();
		legendPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		legendPane.setText("Explanations");
		TitledPane parserPane = new TitledPane();
		parserPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		parserPane.setText("Parsing");
		
		Button parseBtn = new Button("Parse/Reparse");
		parseBtn.setOnAction(e -> {
			parser.parseRecipe(recipe);
			
			setParsedRecipeText();
		});
		
		parserTextPane.getChildren().add(parserTextLines);
		parserTextPane.getChildren().add(parseBtn);
		parserPane.setContent(parserTextPane);
		step2Pane.add(parserPane, 0, 0);
		step2Pane.add(legendPane, 1, 0);
		step2Pane.add(problemsPane, 0, 1);
		step2Pane.setGridLinesVisible(true);
		GridPane.setColumnSpan(problemsPane, 2);

		ListView<String> problems = new ListView<String>(problemsEntry);
		problemsPane.setContent(problems);

		next.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				step2Label.getStyleClass().add("process-step-header-active");
				step2DescriptionLabel.getStyleClass().add("process-step-description-active");
				recipe.setPreparation(recipeText.getText());

				setUnparsedRecipeText();

				border.setCenter(step2Pane);
			}

		});

		header.getChildren().add(logo);
		header.setSpacing(670);
		header.getChildren().add(tools);

		border.setTop(header);
		border.setCenter(mainView);
		border.setBottom(processTracker);

		primaryStage.setTitle("Extracting Models vom Recipe Databases");
		primaryStage.setWidth(1100);
		primaryStage.setHeight(768);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	public void addWordLabel(String word, FlowPane pane) {
		Label label = new Label(word);
		if (!word.toLowerCase().equals(word.toUpperCase())) {
			label.backgroundProperty().bind(Bindings.when(label.hoverProperty())
					.then(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)))
					.otherwise(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))));

			if (KeyWordDatabase.GERMAN_KWDB.findCookingAction(word) != null) {
				label.setTextFill(Color.RED);
			} else if (KeyWordDatabase.GERMAN_KWDB.findIngredient(word) != null) {
				if (KeyWordDatabase.GERMAN_KWDB.findIngredientGroup(word) != null) {
					label.setTextFill(Color.GREENYELLOW);
				} else {
					label.setTextFill(Color.GREEN);
				}
			} else if (KeyWordDatabase.GERMAN_KWDB.findTool(word) != null) {
				label.setTextFill(Color.BLUE);
			} else {
				ContextMenu cm = new ContextMenu();

				MenuItem addToTools = new MenuItem("add to Tools");
				MenuItem addToIngredients = new MenuItem("add to Ingredients");
				MenuItem addToGroups = new MenuItem("add to Groups");
				MenuItem addToCookingActions = new MenuItem("add to CookingActions");

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
		pane.getChildren().add(label);
	}

	public void setUnparsedRecipeText() {
		String text = recipe.getPreparation();
		text = text.replaceAll("[!\"§$%&/()=?*+'#,;.:_<>\n]", " $0 ");

		text = text.trim();

		parserTextLines.getChildren().clear();
		
		FlowPane parserText = new FlowPane();
		parserTextLines.getChildren().add(parserText);
		
		String[] words = text.split(" ");
		for (String word : words) {
			addWordLabel(word, parserText);
			parserText.getChildren().add(new Label(" "));
		}
	}

	public void setParsedRecipeText() {
		parserTextLines.getChildren().clear();
		
		for(Step step : recipe.getSteps()) {
			String text = step.getText();
			text = text.replaceAll("[!\"§$%&/()=?*+'#,;.:_<>\n]", " $0 ");

			text = text.trim();

			FlowPane parserText = new FlowPane();
			parserTextLines.getChildren().add(parserText);
			
			String[] words = text.split(" ");
			for (String word : words) {
				addWordLabel(word, parserText);
				parserText.getChildren().add(new Label(" "));
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
