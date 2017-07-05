package ai4.master.project.view;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Regex.Result;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LibEditor extends Dialog<Object> implements Observer {

	private ButtonType okayButtonType;
	private ButtonType cancelButtonType;
	private Button okayButton;
	private Button cancelButton;
	private KeyWordDatabase kwdb;

	private ComboBox<String> type;
	private VBox box;

	private VBox toolsView;
	private VBox ingredientsView;
	private VBox groupsView;
	private VBox cookingActionsView;
	private VBox partIndicatorsView;
	private VBox lastSentenceReferencesView;
	private VBox eventIndicatorsView;

	private StackPane stackPane;

	private TableView<ToolEntry> toolTable;
	private ObservableList<BaseTool> realToolsList = FXCollections.observableArrayList();

	private ObservableList<String> realEventIndicatorsList = FXCollections.observableArrayList();
	private TableView<EventIndicatorEntry> eventIndicatorsTable;

	private TableView<LastSentenceReferenceEntry> lastSentenceReferencesTable;
	private ObservableList<String> realLastSentenceReferencesList = FXCollections.observableArrayList();

	private TableView<PartIndicatorEntry> partIndicatorsTable;
	private ObservableList<String> realPartIndicatorsList = FXCollections.observableArrayList();

	private TableView<GroupEntry> groupsTable;
	private ObservableList<BaseIngredientGroup> realGroupsList = FXCollections.observableArrayList();

	private TableView<IngredientEntry> ingredientsTable;
	private ObservableList<BaseIngredient> realIngredientsList = FXCollections.observableArrayList();

	public LibEditor() {
		cancelButtonType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		okayButtonType = new ButtonType("Okay", ButtonData.OK_DONE);

		initializeDialog();
		try {
			initializeComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initializeGroupsPane() {
		groupsView = new VBox();
		groupsView.setSpacing(10);

		HBox addGroupsPane = new HBox();
		addGroupsPane.setSpacing(10);
		addGroupsPane.getChildren().add(new Label("Group name: "));

		TextField tFName = new TextField();
		tFName.setPromptText("Group name");

		addGroupsPane.getChildren().add(tFName);
		addGroupsPane.getChildren().add(new Label("Synonymes: "));
		TextField tFSynonyms = new TextField();
		tFSynonyms.setPromptText("Synonymes");
		tFSynonyms.disableProperty().bind(tFName.textProperty().isEmpty());
		addGroupsPane.getChildren().add(tFSynonyms);
		Button addGroup = new Button("Add");
		addGroup.disableProperty().bind(tFName.textProperty().isEmpty());
		addGroupsPane.getChildren().add(addGroup);

		groupsTable = new TableView<GroupEntry>();
		groupsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		groupsTable.setEditable(true);

		TableColumn<GroupEntry, String> nameColumn = new TableColumn<GroupEntry, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<GroupEntry, String>("groupName"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<GroupEntry, ObservableList<String>> synonymsColumn = new TableColumn<GroupEntry, ObservableList<String>>(
				"Synonyms");
		synonymsColumn
				.setCellValueFactory(new PropertyValueFactory<GroupEntry, ObservableList<String>>("groupSynonyms"));
		synonymsColumn.setCellFactory(column -> {
			TableCell<GroupEntry, ObservableList<String>> cell = new TableCell<GroupEntry, ObservableList<String>>() {
				@Override
				public void updateItem(ObservableList<String> synonyms, boolean empty) {
					super.updateItem(synonyms, empty);
					VBox layout = new VBox();

					ListView<String> synonymsView = new ListView<String>();
					synonymsView.setCellFactory(TextFieldListCell.forListView());
					synonymsView.setMinHeight(0);
					synonymsView.setPrefHeight(50);
					synonymsView.setEditable(true);
					
					
					ContextMenu synonymCm = new ContextMenu();
					MenuItem removeSynonymItem = new MenuItem("Remove synonym");
					MenuItem addSynonymItem = new MenuItem("Add new synonym");
					synonymCm.getItems().addAll(removeSynonymItem, addSynonymItem);
					
					removeSynonymItem.disableProperty().bind(synonymsView.getSelectionModel().selectedItemProperty().isNull());
					removeSynonymItem.setOnAction(e -> {
						int index = synonymsView.getSelectionModel().getSelectedIndex();
						synonymsView.getItems().remove(index);
					});
					
					addSynonymItem.setOnAction(e -> {
						synonyms.add("new Synonym " + synonyms.size());
						synonymsView.requestFocus();
					});
					
					if (synonyms != null) {
						synonymsView.setItems(synonyms);
					}

					synonymsView.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							synonymCm.show(synonymsView, e.getScreenX(), e.getScreenY());
						}
						else {
							synonymCm.hide();
						}
					});
					
					layout.getChildren().add(synonymsView);

					setGraphic(layout);
				}
			};
			return cell;
		});

		groupsTable.getColumns().add(nameColumn);
		groupsTable.getColumns().add(synonymsColumn);

		addGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				BaseIngredientGroup group = new BaseIngredientGroup();
				group.addName(tFName.getText());
				String s = tFSynonyms.getText();

				for (String t : s.split(";")) {
					group.getNames().add(t);
				}

				for (String name : group.getNames()) {
					if (kwdb.findIngredient(name) != null) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The given name " + name + " already exists!");

						alert.showAndWait();
						return;
					}
				}

				new GroupEntry(group, groupsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
				realGroupsList.add(group);
				tFName.setText("");
				tFSynonyms.setText("");

			}
		});

		for (

		BaseIngredientGroup group : KeyWordDatabase.GERMAN_KWDB.getIngredientGroups()) {
			new GroupEntry(group, groupsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		ContextMenu cm = new ContextMenu();
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(groupsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			int index = groupsTable.getSelectionModel().getSelectedIndex();
			realGroupsList.remove(index);
			groupsTable.getItems().remove(index);
		});
		cm.getItems().add(remove);

		groupsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(groupsTable, e.getScreenX(), e.getScreenY());
			}
			else {
				cm.hide();
			}
		});

		VBox.setVgrow(groupsTable, Priority.ALWAYS);
		groupsView.getChildren().addAll(addGroupsPane, groupsTable);
	}

	public void initializeCookingActionsPane() {
		ObservableList<Regex.Result> results = FXCollections.observableArrayList();
		for (Regex.Result result : Regex.Result.values()) {
			results.add(result);
		}

		cookingActionsView = new VBox();

		TableView<CookingActionEntry> tableView = new TableView<CookingActionEntry>();

		tableView.setEditable(true);

		TableColumn<CookingActionEntry, String> nameColumn = new TableColumn<CookingActionEntry, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, String>("name"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<CookingActionEntry, ObservableList<String>> synoymsColumn = new TableColumn<CookingActionEntry, ObservableList<String>>(
				"Synonyme");
		synoymsColumn.setCellValueFactory(
				data -> new SimpleObjectProperty<ObservableList<String>>(data.getValue().getSynonyms()));
		synoymsColumn.setCellFactory(column -> {
			TableCell<CookingActionEntry, ObservableList<String>> cell = new TableCell<CookingActionEntry, ObservableList<String>>() {
				@Override
				public void updateItem(ObservableList<String> synonyms, boolean empty) {
					super.updateItem(synonyms, empty);
					VBox layout = new VBox();

					Button addSynonymBtn = new Button("Add Synonym");
					ListView<String> synonymsView = new ListView<String>();
					synonymsView.setCellFactory(TextFieldListCell.forListView());
					synonymsView.setMinHeight(0);
					synonymsView.setPrefHeight(50);
					synonymsView.setEditable(true);

					addSynonymBtn.setOnAction(e -> {
						synonyms.add("new Synonym " + synonyms.size());
						synonymsView.requestFocus();
					});

					if (synonyms != null) {
						synonymsView.setItems(synonyms);
					}

					addSynonymBtn.visibleProperty()
							.bind(synonymsView.focusedProperty().or(addSynonymBtn.focusedProperty()));

					layout.getChildren().add(synonymsView);
					layout.getChildren().add(addSynonymBtn);

					setGraphic(layout);
				}
			};
			return cell;
		});

		TableColumn<CookingActionEntry, ObservableList<Regex>> regexColumn = new TableColumn<CookingActionEntry, ObservableList<Regex>>(
				"Regex");
		regexColumn.setCellValueFactory(
				data -> new SimpleObjectProperty<ObservableList<Regex>>(data.getValue().getRegex()));
		regexColumn.setCellFactory(column -> {
			TableCell<CookingActionEntry, ObservableList<Regex>> cell = new TableCell<CookingActionEntry, ObservableList<Regex>>() {

				@Override
				public void updateItem(ObservableList<Regex> regexList, boolean empty) {
					super.updateItem(regexList, empty);

					VBox layout = new VBox();

					Button addRegexBtn = new Button("Add Regex");
					TableView<RegexEntry> regexTable = new TableView<RegexEntry>();
					regexTable.setMinHeight(0);
					regexTable.setPrefHeight(75);
					regexTable.setEditable(true);

					TableColumn<RegexEntry, String> idColumn = new TableColumn<RegexEntry, String>("Id");
					idColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, String>("id"));
					idColumn.setCellFactory(TextFieldTableCell.forTableColumn());

					TableColumn<RegexEntry, String> expressionColumn = new TableColumn<RegexEntry, String>(
							"Expression");
					expressionColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, String>("expression"));
					expressionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

					TableColumn<RegexEntry, Regex.Result> resultColumn = new TableColumn<RegexEntry, Regex.Result>(
							"Result");
					resultColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Regex.Result>("result"));
					resultColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(results));

					TableColumn<RegexEntry, Boolean> ingredientsNeededColumn = new TableColumn<RegexEntry, Boolean>(
							"IngredientsNeeded");
					ingredientsNeededColumn
							.setCellValueFactory(new PropertyValueFactory<RegexEntry, Boolean>("ingredientsNeeded"));
					ingredientsNeededColumn.setCellFactory(CheckBoxTableCell.forTableColumn(ingredientsNeededColumn));

					TableColumn<RegexEntry, Boolean> referencePreviousProductsColumn = new TableColumn<RegexEntry, Boolean>(
							"ReferencePreviousProducts");
					referencePreviousProductsColumn.setCellValueFactory(
							new PropertyValueFactory<RegexEntry, Boolean>("referencePreviousProducts"));
					referencePreviousProductsColumn
							.setCellFactory(CheckBoxTableCell.forTableColumn(ingredientsNeededColumn));

					regexTable.getColumns().add(idColumn);
					regexTable.getColumns().add(expressionColumn);
					regexTable.getColumns().add(resultColumn);
					regexTable.getColumns().add(ingredientsNeededColumn);
					regexTable.getColumns().add(referencePreviousProductsColumn);

					addRegexBtn.setOnAction(e -> {
						Regex regex = new Regex(".*", Result.ALL);
						regexList.add(regex);
						new RegexEntry(regex, regexTable.getItems(), regexList);
						regexTable.requestFocus();
					});

					if (regexList != null) {
						for (Regex regex : regexList) {
							new RegexEntry(regex, regexTable.getItems(), regexList);
						}
					}

					addRegexBtn.visibleProperty().bind(regexTable.focusedProperty().or(addRegexBtn.focusedProperty()));

					layout.getChildren().add(regexTable);
					layout.getChildren().add(addRegexBtn);

					setGraphic(layout);
				}
			};
			return cell;
		});

		TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn = new TableColumn<CookingActionEntry, ObservableList<BaseTool>>(
				"Implied Tools");
		toolsColumn
				.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<BaseTool>>("tools"));
		toolsColumn.setCellFactory(column -> {
			TableCell<CookingActionEntry, ObservableList<BaseTool>> cell = new TableCell<CookingActionEntry, ObservableList<BaseTool>>() {
				@Override
				public void updateItem(ObservableList<BaseTool> tools, boolean empty) {
					super.updateItem(tools, empty);
					VBox layout = new VBox();
					HBox btns = new HBox();
					Button addToolBtn = new Button("Add Tool");
					Button removeToolBtn = new Button("Remove Tool");
					ListView<BaseTool> toolsView = new ListView<BaseTool>();
					toolsView.setCellFactory(ComboBoxListCell.forListView(realToolsList));
					toolsView.setMinHeight(0);
					toolsView.setPrefHeight(50);
					toolsView.setEditable(true);

					addToolBtn.setOnAction(e -> {
						tools.add(null);
						toolsView.requestFocus();
						toolsView.edit(tools.size() - 1);
					});
					removeToolBtn.setOnAction(e -> {
						tools.remove(toolsView.getSelectionModel().getSelectedItem());
						toolsView.requestFocus();
					});

					removeToolBtn.disableProperty().bind(toolsView.getSelectionModel().selectedItemProperty().isNull());

					if (tools != null) {
						toolsView.setItems(tools);
					}

					addToolBtn.visibleProperty().bind(toolsView.focusedProperty().or(addToolBtn.focusedProperty()));
					removeToolBtn.visibleProperty()
							.bind(toolsView.focusedProperty().or(removeToolBtn.focusedProperty()));

					layout.getChildren().add(toolsView);
					btns.getChildren().add(addToolBtn);
					btns.getChildren().add(removeToolBtn);
					layout.getChildren().add(btns);

					setGraphic(layout);
				}
			};
			return cell;
		});

		tableView.getColumns().add(nameColumn);
		tableView.getColumns().add(synoymsColumn);
		tableView.getColumns().add(regexColumn);
		tableView.getColumns().add(toolsColumn);

		for (BaseCookingAction cookingAction : KeyWordDatabase.GERMAN_KWDB.getCookingActions()) {
			new CookingActionEntry(cookingAction, tableView.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		cookingActionsView.getChildren().add(tableView);
	}

	public void initializePartIndicatorsPane() {
		partIndicatorsView = new VBox();
		partIndicatorsView.setSpacing(10);

		HBox addPartIndicatorPane = new HBox();
		addPartIndicatorPane.setSpacing(10);
		
		addPartIndicatorPane.getChildren().add(new Label("Part Indicator: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Part Indicator");
		addPartIndicatorPane.getChildren().add(tFName);
		
		Button addPartIndicator = new Button("Add");
		addPartIndicator.disableProperty().bind(tFName.textProperty().isEmpty());
		addPartIndicatorPane.getChildren().add(addPartIndicator);

		partIndicatorsTable = new TableView<PartIndicatorEntry>();
		partIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		partIndicatorsTable.setEditable(true);

		TableColumn<PartIndicatorEntry, String> nameColumn = new TableColumn<PartIndicatorEntry, String>("Part Indicator");
		nameColumn.setCellValueFactory(new PropertyValueFactory<PartIndicatorEntry, String>("partIndicator"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		partIndicatorsTable.getColumns().add(nameColumn);

		addPartIndicator.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				String newPartIndicator = tFName.getText();
				
				for(PartIndicatorEntry entry : partIndicatorsTable.getItems()) {
					if(newPartIndicator.equals(entry.getPartIndicator())) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The given name " + newPartIndicator + " already exists!");

						alert.showAndWait();
						return;
					}
				}				

				new PartIndicatorEntry(newPartIndicator, partIndicatorsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
				realPartIndicatorsList.add(newPartIndicator);
				tFName.setText("");
			}
		});

		for (

		String partIndicator : KeyWordDatabase.GERMAN_KWDB.getPartIndicators()) {
			new PartIndicatorEntry(partIndicator, partIndicatorsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		ContextMenu cm = new ContextMenu();
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(partIndicatorsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			int index = partIndicatorsTable.getSelectionModel().getSelectedIndex();
			realPartIndicatorsList.remove(index);
			partIndicatorsTable.getItems().remove(index);
		});
		cm.getItems().add(remove);

		partIndicatorsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(partIndicatorsTable, e.getScreenX(), e.getScreenY());
			}
			else {
				cm.hide();
			}
		});

		VBox.setVgrow(partIndicatorsTable, Priority.ALWAYS);
		partIndicatorsView.getChildren().addAll(addPartIndicatorPane, partIndicatorsTable);
	}

	public void initializeLastSentenceReferencesPane() {
		lastSentenceReferencesView = new VBox();
		lastSentenceReferencesView.setSpacing(10);

		HBox addLastSentenceReferencePane = new HBox();
		addLastSentenceReferencePane.setSpacing(10);
		
		addLastSentenceReferencePane.getChildren().add(new Label("Last Sentence Reference: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Last Sentence Reference");
		addLastSentenceReferencePane.getChildren().add(tFName);
		
		Button addLastSentenceReference = new Button("Add");
		addLastSentenceReference.disableProperty().bind(tFName.textProperty().isEmpty());
		addLastSentenceReferencePane.getChildren().add(addLastSentenceReference);

		lastSentenceReferencesTable = new TableView<LastSentenceReferenceEntry>();
		lastSentenceReferencesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		lastSentenceReferencesTable.setEditable(true);

		TableColumn<LastSentenceReferenceEntry, String> nameColumn = new TableColumn<LastSentenceReferenceEntry, String>("Last Sentence Reference");
		nameColumn.setCellValueFactory(new PropertyValueFactory<LastSentenceReferenceEntry, String>("lastSentenceReference"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		lastSentenceReferencesTable.getColumns().add(nameColumn);

		addLastSentenceReference.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				String newLastSentenceReference = tFName.getText();
				
				for(LastSentenceReferenceEntry entry : lastSentenceReferencesTable.getItems()) {
					if(newLastSentenceReference.equals(entry.getLastSentenceReference())) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The given name " + newLastSentenceReference + " already exists!");

						alert.showAndWait();
						return;
					}
				}				

				new LastSentenceReferenceEntry(newLastSentenceReference, lastSentenceReferencesTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
				realLastSentenceReferencesList.add(newLastSentenceReference);
				tFName.setText("");
			}
		});

		for (

		String lastSentenceReference : KeyWordDatabase.GERMAN_KWDB.getLastSentenceReferences()) {
			new LastSentenceReferenceEntry(lastSentenceReference, lastSentenceReferencesTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		ContextMenu cm = new ContextMenu();
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(lastSentenceReferencesTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			int index = lastSentenceReferencesTable.getSelectionModel().getSelectedIndex();
			realLastSentenceReferencesList.remove(index);
			lastSentenceReferencesTable.getItems().remove(index);
		});
		cm.getItems().add(remove);

		lastSentenceReferencesTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(lastSentenceReferencesTable, e.getScreenX(), e.getScreenY());
			}
			else {
				cm.hide();
			}
		});

		VBox.setVgrow(lastSentenceReferencesTable, Priority.ALWAYS);
		lastSentenceReferencesView.getChildren().addAll(addLastSentenceReferencePane, lastSentenceReferencesTable);
	}

	public void initializeEventIndicatorsPane() {
		eventIndicatorsView = new VBox();
		eventIndicatorsView.setSpacing(10);

		HBox addEventIndicatorPane = new HBox();
		addEventIndicatorPane.setSpacing(10);
		
		addEventIndicatorPane.getChildren().add(new Label("Event Indicator: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Event Indicator");
		addEventIndicatorPane.getChildren().add(tFName);
		
		Button addEventIndicator = new Button("Add");
		addEventIndicator.disableProperty().bind(tFName.textProperty().isEmpty());
		addEventIndicatorPane.getChildren().add(addEventIndicator);

		eventIndicatorsTable = new TableView<EventIndicatorEntry>();
		eventIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		eventIndicatorsTable.setEditable(true);

		TableColumn<EventIndicatorEntry, String> nameColumn = new TableColumn<EventIndicatorEntry, String>("Event Indicator");
		nameColumn.setCellValueFactory(new PropertyValueFactory<EventIndicatorEntry, String>("eventIndicator"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		eventIndicatorsTable.getColumns().add(nameColumn);

		addEventIndicator.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				String newIndicator = tFName.getText();
				
				for(EventIndicatorEntry entry : eventIndicatorsTable.getItems()) {
					if(newIndicator.equals(entry.getEventIndicator())) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The given name " + newIndicator + " already exists!");

						alert.showAndWait();
						return;
					}
				}				

				new EventIndicatorEntry(newIndicator, eventIndicatorsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
				realEventIndicatorsList.add(newIndicator);
				tFName.setText("");
			}
		});

		for (

		String eventIndicator : KeyWordDatabase.GERMAN_KWDB.getEventIndicators()) {
			new EventIndicatorEntry(eventIndicator, eventIndicatorsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		ContextMenu cm = new ContextMenu();
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(eventIndicatorsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			System.out.println(realEventIndicatorsList);
			System.out.println(eventIndicatorsTable.getItems());
			int index = eventIndicatorsTable.getSelectionModel().getSelectedIndex();
			realEventIndicatorsList.remove(index);
			eventIndicatorsTable.getItems().remove(index);
		});
		cm.getItems().add(remove);

		eventIndicatorsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(eventIndicatorsTable, e.getScreenX(), e.getScreenY());
			}
			else {
				cm.hide();
			}
		});

		VBox.setVgrow(eventIndicatorsTable, Priority.ALWAYS);
		eventIndicatorsView.getChildren().addAll(addEventIndicatorPane, eventIndicatorsTable);
	}

	public void initializeDialog() {
		setTitle("Library Editor");
		setHeaderText("Library Editor");
		setResizable(true);
		getDialogPane().setPrefSize(1024, 720);
		setGraphic(new ImageView(this.getClass().getResource("/img/editorIcon.png").toString()));
		setOnCloseRequest(new EventHandler<DialogEvent>() {

			@Override
			public void handle(DialogEvent e) {
				hide();
			}
		});

		getDialogPane().getButtonTypes().addAll(okayButtonType, cancelButtonType);

		okayButton = (Button) this.getDialogPane().lookupButton(okayButtonType);
		okayButton.setId("OKAY");
		okayButton.setFocusTraversable(false);

		cancelButton = (Button) this.getDialogPane().lookupButton(cancelButtonType);
		cancelButton.setId("CANCEL");
		cancelButton.setFocusTraversable(false);
	}

	public void addOkayListener(EventHandler<ActionEvent> handler) {
		okayButton.setOnAction(handler);
	}

	public void addCancelListener(EventHandler<ActionEvent> handler) {
		cancelButton.setOnAction(handler);
	}

	public void initializeToolsPane() throws Exception {
		toolsView = new VBox();
		toolsView.setSpacing(10);

		HBox addTools = new HBox();
		addTools.setSpacing(10);

		addTools.getChildren().add(new Label("Name: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Name");
		addTools.getChildren().add(tFName);

		addTools.getChildren().add(new Label("Synonyme: "));
		TextField tFSynonyms = new TextField();
		tFSynonyms.setPromptText("Synonymes");
		tFSynonyms.disableProperty().bind(tFName.textProperty().isEmpty());
		addTools.getChildren().add(tFSynonyms);

		Button add = new Button("Add");
		add.disableProperty().bind(tFName.textProperty().isEmpty());
		addTools.getChildren().add(add);

		toolTable = new TableView<ToolEntry>();
		toolTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		toolTable.setEditable(true);

		TableColumn<ToolEntry, String> nameColumn = new TableColumn<ToolEntry, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<ToolEntry, String>("toolName"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<ToolEntry, ObservableList<String>> synonymsColumn = new TableColumn<ToolEntry, ObservableList<String>>(
				"Synonyms");
		synonymsColumn.setCellValueFactory(new PropertyValueFactory<ToolEntry, ObservableList<String>>("synonyms"));
		synonymsColumn.setCellFactory(column -> {
			TableCell<ToolEntry, ObservableList<String>> cell = new TableCell<ToolEntry, ObservableList<String>>() {
				@Override
				public void updateItem(ObservableList<String> synonyms, boolean empty) {
					super.updateItem(synonyms, empty);
					VBox layout = new VBox();

					ListView<String> synonymsView = new ListView<String>();
					synonymsView.setCellFactory(TextFieldListCell.forListView());
					synonymsView.setMinHeight(0);
					synonymsView.setPrefHeight(50);
					synonymsView.setEditable(true);

					layout.getChildren().add(synonymsView);

					ContextMenu synonymCm = new ContextMenu();
					MenuItem removeSynonymItem = new MenuItem("Remove synonym");
					MenuItem addSynonymItem = new MenuItem("Add new synonym");
					synonymCm.getItems().addAll(removeSynonymItem, addSynonymItem);
					
					removeSynonymItem.disableProperty().bind(synonymsView.getSelectionModel().selectedItemProperty().isNull());
					removeSynonymItem.setOnAction(e -> {
						int index = synonymsView.getSelectionModel().getSelectedIndex();
						synonymsView.getItems().remove(index);
					});
					
					addSynonymItem.setOnAction(e -> {
						synonyms.add("new Synonym " + synonyms.size());
						synonymsView.requestFocus();
					});
					
					if (synonyms != null) {
						synonymsView.setItems(synonyms);
					}

					synonymsView.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							synonymCm.show(synonymsView, e.getScreenX(), e.getScreenY());
						}
						else {
							synonymCm.hide();
						}
					});
					
					setGraphic(layout);
				}
			};
			return cell;
		});

		toolTable.getColumns().add(nameColumn);
		toolTable.getColumns().add(synonymsColumn);

		add.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				BaseTool tool = new BaseTool();
				tool.addName(tFName.getText());
				String s = tFSynonyms.getText();

				for (String t : s.split(";")) {
					tool.getNames().add(t);
				}

				for (String name : tool.getNames()) {
					if (kwdb.findTool(name) != null) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The given name " + name + " already exists!");

						alert.showAndWait();
						return;
					}
				}

				new ToolEntry(tool, toolTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
				realToolsList.add(tool);
				tFName.setText("");
				tFSynonyms.setText("");
			}
		});

		for (

		BaseTool tool : KeyWordDatabase.GERMAN_KWDB.getTools()) {
			new ToolEntry(tool, toolTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		ContextMenu cm = new ContextMenu();
		MenuItem remove = new MenuItem("Remove Tool");
		remove.disableProperty().bind(toolTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			int index = toolTable.getSelectionModel().getSelectedIndex();
			realToolsList.remove(index);
			toolTable.getItems().remove(index);
		});
		cm.getItems().add(remove);

		toolTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(toolTable, e.getScreenX(), e.getScreenY());
			}
			else {
				cm.hide();
			}
		});

		VBox.setVgrow(toolTable, Priority.ALWAYS);
		toolsView.getChildren().addAll(addTools, toolTable);
	}

	public void initializeIngredientsPane() {
		ingredientsView = new VBox();
		ingredientsView.setSpacing(10);

		HBox addIngredientsPane = new HBox();
		addIngredientsPane.setSpacing(10);
		
		addIngredientsPane.getChildren().add(new Label("Ingredient name: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Ingredient name");
		addIngredientsPane.getChildren().add(tFName);
		
		addIngredientsPane.getChildren().add(new Label("Synonymes: "));
		TextField tFSynonyms = new TextField();
		tFSynonyms.setPromptText("Synonymes");
		tFSynonyms.disableProperty().bind(tFName.textProperty().isEmpty());
		addIngredientsPane.getChildren().add(tFSynonyms);
		
		addIngredientsPane.getChildren().add(new Label("Groups: "));
		TextField tFGroups = new TextField();
		tFGroups.setPromptText("Groups");
		tFGroups.disableProperty().bind(tFName.textProperty().isEmpty());
		addIngredientsPane.getChildren().add(tFGroups);
		
		Button addIngredient = new Button("Add");
		addIngredient.disableProperty().bind(tFName.textProperty().isEmpty());
		addIngredientsPane.getChildren().add(addIngredient);

		ingredientsTable = new TableView<IngredientEntry>();
		ingredientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ingredientsTable.setEditable(true);

		TableColumn<IngredientEntry, String> nameColumn = new TableColumn<IngredientEntry, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<IngredientEntry, String>("ingredientName"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<IngredientEntry, ObservableList<String>> synonymsColumn = new TableColumn<IngredientEntry, ObservableList<String>>(
				"Synonyms");
		synonymsColumn
				.setCellValueFactory(new PropertyValueFactory<IngredientEntry, ObservableList<String>>("ingredientSynonyms"));
		synonymsColumn.setCellFactory(column -> {
			TableCell<IngredientEntry, ObservableList<String>> cell = new TableCell<IngredientEntry, ObservableList<String>>() {
				@Override
				public void updateItem(ObservableList<String> synonyms, boolean empty) {
					super.updateItem(synonyms, empty);
					VBox layout = new VBox();

					ListView<String> synonymsView = new ListView<String>();
					synonymsView.setCellFactory(TextFieldListCell.forListView());
					synonymsView.setMinHeight(0);
					synonymsView.setPrefHeight(50);
					synonymsView.setEditable(true);
					
					
					ContextMenu synonymCm = new ContextMenu();
					MenuItem removeSynonymItem = new MenuItem("Remove synonym");
					MenuItem addSynonymItem = new MenuItem("Add new synonym");
					synonymCm.getItems().addAll(removeSynonymItem, addSynonymItem);
					
					removeSynonymItem.disableProperty().bind(synonymsView.getSelectionModel().selectedItemProperty().isNull());
					removeSynonymItem.setOnAction(e -> {
						int index = synonymsView.getSelectionModel().getSelectedIndex();
						synonymsView.getItems().remove(index);
					});
					
					addSynonymItem.setOnAction(e -> {
						synonyms.add("new Synonym " + synonyms.size());
						synonymsView.requestFocus();
					});
					
					if (synonyms != null) {
						synonymsView.setItems(synonyms);
					}

					synonymsView.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							synonymCm.show(synonymsView, e.getScreenX(), e.getScreenY());
						}
						else {
							synonymCm.hide();
						}
					});
					
					layout.getChildren().add(synonymsView);

					setGraphic(layout);
				}
			};
			return cell;
		});
		
		TableColumn<IngredientEntry, ObservableList<BaseIngredientGroup>> groupsColumn = new TableColumn<IngredientEntry, ObservableList<BaseIngredientGroup>>(
				"Groups");
		groupsColumn
				.setCellValueFactory(new PropertyValueFactory<IngredientEntry, ObservableList<BaseIngredientGroup>>("ingredientGroups"));
		groupsColumn.setCellFactory(column -> {
			TableCell<IngredientEntry, ObservableList<BaseIngredientGroup>> cell = new TableCell<IngredientEntry, ObservableList<BaseIngredientGroup>>() {
				@Override
				public void updateItem(ObservableList<BaseIngredientGroup> groups, boolean empty) {
					super.updateItem(groups, empty);
					VBox layout = new VBox();

					ListView<BaseIngredientGroup> groupsView = new ListView<BaseIngredientGroup>();
					groupsView.setCellFactory(ComboBoxListCell.forListView(realGroupsList));
					groupsView.setMinHeight(0);
					groupsView.setPrefHeight(50);
					groupsView.setEditable(true);
					
					
					ContextMenu groupsCm = new ContextMenu();
					MenuItem removeGroupItem = new MenuItem("Remove group");
					MenuItem addGroupItem = new MenuItem("Add new group");
					groupsCm.getItems().addAll(removeGroupItem, addGroupItem);
					
					removeGroupItem.disableProperty().bind(groupsView.getSelectionModel().selectedItemProperty().isNull());
					removeGroupItem.setOnAction(e -> {
						int index = groupsView.getSelectionModel().getSelectedIndex();
						groupsView.getItems().remove(index);
					});
					
					addGroupItem.setOnAction(e -> {
						groups.add(null);
						groupsView.requestFocus();
						groupsView.getSelectionModel().select(groups.size()-1);
					});
					
					if (groups != null) {
						groupsView.setItems(groups);
					}

					groupsView.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							groupsCm.show(groupsView, e.getScreenX(), e.getScreenY());
						}
						else {
							groupsCm.hide();
						}
					});
					
					layout.getChildren().add(groupsView);

					setGraphic(layout);
				}
			};
			return cell;
		});

		ingredientsTable.getColumns().add(nameColumn);
		ingredientsTable.getColumns().add(synonymsColumn);
		ingredientsTable.getColumns().add(groupsColumn);

		addIngredient.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				BaseIngredient ingredient = new BaseIngredient();
				ingredient.addName(tFName.getText());
				String s = tFSynonyms.getText();

				for (String t : s.split(";")) {
					ingredient.getNames().add(t);
				}
				
				for (String name : ingredient.getNames()) {
					if (kwdb.findIngredient(name) != null) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The given name for Ingredient " + name + " already exists!");

						alert.showAndWait();
						return;
					}
				}
				
				String g = tFGroups.getText();
			
				for(String p : g.split(";")) {
					if(kwdb.findIngredientGroup(p.toLowerCase()) != null || g.length() == 0) {
						ingredient.getIngredientGroups().add(kwdb.findIngredientGroup(p.toLowerCase()));
					}
					else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning Dialog");
						alert.setContentText("The Group " + p + " doesn't exists exists!");

						alert.showAndWait();
						return;
					}
				}

				new IngredientEntry(ingredient, ingredientsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
				realIngredientsList.add(ingredient);
				tFName.setText("");
				tFSynonyms.setText("");
				tFGroups.setText("");

			}
		});

		for (

		BaseIngredient ingredient : KeyWordDatabase.GERMAN_KWDB.getIngredients()) {
			new IngredientEntry(ingredient, ingredientsTable.getItems(), KeyWordDatabase.GERMAN_KWDB);
		}

		ContextMenu cm = new ContextMenu();
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(ingredientsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			int index = ingredientsTable.getSelectionModel().getSelectedIndex();
			realIngredientsList.remove(index);
			ingredientsTable.getItems().remove(index);
		});
		cm.getItems().add(remove);

		ingredientsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(ingredientsTable, e.getScreenX(), e.getScreenY());
			}
			else {
				cm.hide();
			}
		});

		VBox.setVgrow(ingredientsTable, Priority.ALWAYS);
		ingredientsView.getChildren().addAll(addIngredientsPane, ingredientsTable);
	}

	public void initializeComponents() throws Exception {
		kwdb = KeyWordDatabase.GERMAN_KWDB;

		realToolsList.addAll(kwdb.getTools());
		realGroupsList.addAll(kwdb.getIngredientGroups());
		realIngredientsList.addAll(kwdb.getIngredients());
		realEventIndicatorsList.addAll(kwdb.getEventIndicators());
		realPartIndicatorsList.addAll(kwdb.getPartIndicators());
		realLastSentenceReferencesList.addAll(kwdb.getLastSentenceReferences());

		initializeToolsPane();
		initializeGroupsPane();
		initializeIngredientsPane();
		initializeCookingActionsPane();
		initializePartIndicatorsPane();
		initializeLastSentenceReferencesPane();
		initializeEventIndicatorsPane();

		stackPane = new StackPane();
		VBox.setVgrow(stackPane, Priority.ALWAYS);
		stackPane.getChildren().addAll(toolsView, groupsView, ingredientsView, cookingActionsView, partIndicatorsView,
				lastSentenceReferencesView, eventIndicatorsView);
		stackPane.getChildren().setAll(toolsView);

		type = new ComboBox<String>(FXCollections.observableArrayList("Tools", "Groups", "Ingredients",
				"Cooking Actions", "Part Indicators", "Last Sentence References", "Event Indicators"));
		type.setTooltip(new Tooltip("Select a type you want to edit"));
		type.getSelectionModel().selectFirst();
		type.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg, Number oldNumber, Number newNumber) {
				String selectedItem = type.getItems().get((int) newNumber).toString();
				if (selectedItem.equals("Tools")) {
					stackPane.getChildren().setAll(toolsView);
				} else if (selectedItem.equals("Groups")) {
					stackPane.getChildren().setAll(groupsView);
				} else if (selectedItem.equals("Ingredients")) {
					stackPane.getChildren().setAll(ingredientsView);
				} else if (selectedItem.equals("Cooking Actions")) {
					stackPane.getChildren().setAll(cookingActionsView);
				} else if (selectedItem.equals("Part Indicators")) {
					stackPane.getChildren().setAll(partIndicatorsView);
				} else if (selectedItem.equals("Last Sentence References")) {
					stackPane.getChildren().setAll(lastSentenceReferencesView);
				} else if (selectedItem.equals("Event Indicators")) {
					stackPane.getChildren().setAll(eventIndicatorsView);
				}
			}
		});

		box = new VBox();
		box.setSpacing(10);
		box.getChildren().addAll(type, stackPane);

		getDialogPane().setContent(box);
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}
