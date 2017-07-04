package ai4.master.project.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.XMLLoader;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
	private ObservableList<ToolEntry> toolsList = FXCollections.observableArrayList();
	
	private ObservableList<EventIndicatorEntry> eventIndicatorsList = FXCollections.observableArrayList();
	private TableView<EventIndicatorEntry> eventIndicatorsTable;

	private TableView<LastSentenceReferenceEntry> lastSentenceReferencesTable;
	private ObservableList<LastSentenceReferenceEntry> lastSentenceReferencesList = FXCollections.observableArrayList();

	private TableView<PartIndicatorEntry> partIndicatorsTable;
	private ObservableList<PartIndicatorEntry> partIndicatorsList = FXCollections.observableArrayList();
	
	private TableView<GroupEntry> groupsTable;
	private ObservableList<GroupEntry> groupsList = FXCollections.observableArrayList();
	
	private TableView<IngredientEntry> ingredientsTable;
	private ObservableList<IngredientEntry> ingredientsList = FXCollections.observableArrayList();

	private ObservableList<String> eventIndicatorsList = FXCollections.observableArrayList();

	private TableView<String> eventIndicatorsTable;

	
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
		
		for (BaseIngredientGroup g : kwdb.getIngredientGroups()) {
			String groupSynonymes = "";
			for (String s : g.getNames()) {
				if (!s.equals(g.toString())) {
					groupSynonymes = groupSynonymes + s + ";";
				}
			}
			GroupEntry groupEntry = new GroupEntry(g.toString(), groupSynonymes);
			groupsList.add(groupEntry);
		}

		HBox addGroups = new HBox();
		addGroups.setSpacing(10);
		addGroups.getChildren().add(new Label("Group: "));
		
		TextField tFName = new TextField();
		tFName.setPromptText("Group");
		
		addGroups.getChildren().add(tFName);
		addGroups.getChildren().add(new Label("Synonymes: "));
		TextField tFSynonyms = new TextField();
		tFSynonyms.setPromptText("Synonymes");
		addGroups.getChildren().add(tFSynonyms);
		Button addGroup = new Button("Add");
		addGroups.getChildren().add(addGroup);

		addGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Set<String> groupEntries = new HashSet<String>();

				for (BaseIngredientGroup g : kwdb.getIngredientGroups()) {
					for (String s : g.getNames()) {
						groupEntries.add(s);
					}
				}

				if (!tFName.getText().equals("")) {
					if (!groupEntries.contains(tFName.getText())) {
						groupsList.add(new GroupEntry(tFName.getText(), tFSynonyms.getText()));
					} else {
						System.err.println("Error: This Group already exists. Maybe as own Group or as Synonym");
					}
				} else {
					System.err.println("Error: Empty name field!");
				}
			}
		});

		groupsTable = new TableView<GroupEntry>();
		groupsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		groupsTable.setEditable(true);

		TableColumn<GroupEntry, String> name = new TableColumn<GroupEntry, String>("Group name");
		name.setCellValueFactory(new PropertyValueFactory<GroupEntry, String>("groupName"));

		TableColumn<GroupEntry, String> synonyms = new TableColumn<GroupEntry, String>("Group synonyms");
		synonyms.setCellValueFactory(new PropertyValueFactory<GroupEntry, String>("groupSynonyms"));

		groupsTable.getColumns().add(name);
		groupsTable.getColumns().add(synonyms);
		groupsTable.setItems(groupsList);

		Button removeGroup = new Button("Remove");
		removeGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				GroupEntry selectedItem = groupsTable.getSelectionModel().getSelectedItem();
				groupsTable.getItems().remove(selectedItem);
			}
		});

		groupsView.getChildren().addAll(addGroups, groupsTable, removeGroup);
	}

	public void initializeCookingActionsPane() {
		ObservableList<Regex.Result> results = FXCollections.observableArrayList();
		for(Regex.Result result : Regex.Result.values()) {
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
					
					addSynonymBtn.visibleProperty().bind(synonymsView.focusedProperty().or(addSynonymBtn.focusedProperty()));
					
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
					
					TableColumn<RegexEntry, String> expressionColumn = new TableColumn<RegexEntry, String>("Expression");
					expressionColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, String>("expression"));
					expressionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					
					TableColumn<RegexEntry, Regex.Result> resultColumn = new TableColumn<RegexEntry, Regex.Result>("Result");
					resultColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Regex.Result>("result"));
					resultColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(results));
					
					TableColumn<RegexEntry, Boolean> ingredientsNeededColumn = new TableColumn<RegexEntry, Boolean>("IngredientsNeeded");
					ingredientsNeededColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Boolean>("ingredientsNeeded"));
					ingredientsNeededColumn.setCellFactory(CheckBoxTableCell.forTableColumn(ingredientsNeededColumn));

					TableColumn<RegexEntry, Boolean> referencePreviousProductsColumn = new TableColumn<RegexEntry, Boolean>("ReferencePreviousProducts");
					referencePreviousProductsColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Boolean>("referencePreviousProducts"));
					referencePreviousProductsColumn.setCellFactory(CheckBoxTableCell.forTableColumn(ingredientsNeededColumn));

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
						for(Regex regex : regexList) {
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

		TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn = new TableColumn<CookingActionEntry, ObservableList<BaseTool>>("Implied Tools");
		toolsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<BaseTool>>("tools"));
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
					removeToolBtn.visibleProperty().bind(toolsView.focusedProperty().or(removeToolBtn.focusedProperty()));
					
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
		
		HBox addPartIndicatorsPane = new HBox();
		addPartIndicatorsPane.setSpacing(10);
		
		addPartIndicatorsPane.getChildren().add(new Label("Part Indicator: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Part Indicator");
		addPartIndicatorsPane.getChildren().add(tFName);
		Button addPartIndicator = new Button("Add");
		addPartIndicatorsPane.getChildren().add(addPartIndicator);
		
		addPartIndicator.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				partIndicatorsList.add(new PartIndicatorEntry(tFName.getText()));
				if(!kwdb.getPartIndicators().contains(tFName.getText())) {
					kwdb.getPartIndicators().add(tFName.getText());
				}
				System.out.println(kwdb.toXML());
			}
			});
		
		
		partIndicatorsTable = new TableView<PartIndicatorEntry>();
		partIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		partIndicatorsTable.setEditable(true);

		TableColumn<PartIndicatorEntry, String> partIndicators = new TableColumn<PartIndicatorEntry, String>("Part Indicators");
		partIndicators.setCellValueFactory(new PropertyValueFactory<PartIndicatorEntry, String>("partIndicator"));
		
		partIndicatorsTable.getColumns().add(partIndicators);
		
		for(String s : kwdb.getPartIndicators()) {
			PartIndicatorEntry e = new PartIndicatorEntry(s);
			partIndicatorsList.add(e);
		}
	
		partIndicatorsTable.setItems(partIndicatorsList);
		
		Button removePartIndicators = new Button("Remove");
		removePartIndicators.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				PartIndicatorEntry selectedItem = partIndicatorsTable.getSelectionModel().getSelectedItem();
				partIndicatorsTable.getItems().remove(selectedItem);
				kwdb.getPartIndicators().remove(selectedItem.getPartIndicator());
				kwdb.toXML();
			}
		});

		partIndicatorsView.getChildren().addAll(addPartIndicatorsPane, partIndicatorsTable, removePartIndicators);
	}

	public void initializeLastSentenceReferencesPane() {
		lastSentenceReferencesView = new VBox();
		lastSentenceReferencesView.setSpacing(10);
		
		HBox addLastSentenceReferencesPane = new HBox();
		addLastSentenceReferencesPane.setSpacing(10);
		
		addLastSentenceReferencesPane.getChildren().add(new Label("Indicator: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Last Sentence Reference");
		addLastSentenceReferencesPane.getChildren().add(tFName);
		Button addLastSentenceRef = new Button("Add");
		addLastSentenceReferencesPane.getChildren().add(addLastSentenceRef);
		
		addLastSentenceRef.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				lastSentenceReferencesList.add(new LastSentenceReferenceEntry(tFName.getText()));
				if(!kwdb.getLastSentenceReferences().contains(tFName.getText())) {
					kwdb.getLastSentenceReferences().add(tFName.getText());
				}
				System.out.println(kwdb.toXML());
			}
			});
		
		
		lastSentenceReferencesTable = new TableView<LastSentenceReferenceEntry>();
		lastSentenceReferencesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		lastSentenceReferencesTable.setEditable(true);

		TableColumn<LastSentenceReferenceEntry, String> lastSentenceReferences = new TableColumn<LastSentenceReferenceEntry, String>("Last Sentences References");
		lastSentenceReferences.setCellValueFactory(new PropertyValueFactory<LastSentenceReferenceEntry, String>("lastSentenceReference"));
		
		lastSentenceReferencesTable.getColumns().add(lastSentenceReferences);
		
		for(String s : kwdb.getLastSentenceReferences()) {
			LastSentenceReferenceEntry e = new LastSentenceReferenceEntry(s);
			lastSentenceReferencesList.add(e);
		}
	
		lastSentenceReferencesTable.setItems(lastSentenceReferencesList);
		
		Button removeLastSentenceRef = new Button("Remove");
		removeLastSentenceRef.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				LastSentenceReferenceEntry selectedItem = lastSentenceReferencesTable.getSelectionModel().getSelectedItem();
				lastSentenceReferencesTable.getItems().remove(selectedItem);
				kwdb.getLastSentenceReferences().remove(selectedItem.getLastSentenceReference());
				kwdb.toXML();
			}
		});

		lastSentenceReferencesView.getChildren().addAll(addLastSentenceReferencesPane, lastSentenceReferencesTable, removeLastSentenceRef);
	}

	public void initializeEventIndicatorsPane() {
		eventIndicatorsView = new VBox();
		eventIndicatorsView.setSpacing(10);
		
		HBox addEventIndicatorPane = new HBox();
		addEventIndicatorPane.setSpacing(10);
		
		addEventIndicatorPane.getChildren().add(new Label("Indicator: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Indicator");
		addEventIndicatorPane.getChildren().add(tFName);
		Button addEvent = new Button("Add");
		addEventIndicatorPane.getChildren().add(addEvent);
		
		addEvent.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				eventIndicatorsList.add(new EventIndicatorEntry(tFName.getText()));
				if(!kwdb.getEventIndicators().contains(tFName.getText())) {
					kwdb.getEventIndicators().add(tFName.getText());
				}
				System.out.println(kwdb.toXML());
			}
			});
		
		
		eventIndicatorsTable = new TableView<EventIndicatorEntry>();
		eventIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		eventIndicatorsTable.setEditable(true);

		TableColumn<EventIndicatorEntry, String> eventIndicators = new TableColumn<EventIndicatorEntry, String>("Event Indicators");
		eventIndicators.setCellValueFactory(new PropertyValueFactory<EventIndicatorEntry, String>("eventIndicator"));
		
		eventIndicatorsTable.getColumns().add(eventIndicators);
		
		for(String s : kwdb.getEventIndicators()) {
			EventIndicatorEntry e = new EventIndicatorEntry(s);
			eventIndicatorsList.add(e);

		}

		eventIndicatorsTable.setItems(eventIndicatorsList);
		
		Button removeEvent = new Button("Remove");
		removeEvent.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				EventIndicatorEntry selectedItem = eventIndicatorsTable.getSelectionModel().getSelectedItem();
				eventIndicatorsTable.getItems().remove(selectedItem);
				kwdb.getEventIndicators().remove(selectedItem.getEventIndicator());
				kwdb.toXML();
			}
		});


		eventIndicatorsView.getChildren().addAll(addEventIndicatorPane, eventIndicatorsTable, removeEvent);
	}

	public void initializeDialog() {
		setTitle("Library Editor");
		setHeaderText("Library Editor");
		setResizable(true);
		getDialogPane().setPrefSize(700, 600);
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

		for (BaseTool t : kwdb.getTools()) {
			String synonymes = "";
			for (String s : t.getNames()) {
				if (!s.equals(t.toString())) {
					synonymes = synonymes + s + ";";
				}
			}
			ToolEntry entry = new ToolEntry(t.toString(), synonymes);
			toolsList.add(entry);
		}

		HBox addTools = new HBox();
		addTools.setSpacing(10);
		addTools.getChildren().add(new Label("Name: "));

		TextField tFName = new TextField();
		tFName.setPromptText("Name");

		addTools.getChildren().add(tFName);
		addTools.getChildren().add(new Label("Synonyme: "));
		TextField tFSynonyms = new TextField();
		tFSynonyms.setPromptText("Synonymes");
		addTools.getChildren().add(tFSynonyms);
		Button add = new Button("Add");
		addTools.getChildren().add(add);

		add.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Set<String> entries = new HashSet<String>();

				for (BaseTool t : kwdb.getTools()) {
					for (String s : t.getNames()) {
						entries.add(s);
					}
				}

				if (!tFName.getText().equals("")) {
					if (!entries.contains(tFName.getText())) {
						toolsList.add(new ToolEntry(tFName.getText(), tFSynonyms.getText()));
					} else {
						System.err.println("Error: This Tool already exists. Maybe as own Tool or as Synonym");
					}
				} else {
					System.err.println("Error: Empty name field!");
				}
			}
		});

		toolTable = new TableView<ToolEntry>();
		toolTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		toolTable.setEditable(true);

		TableColumn<ToolEntry, String> name = new TableColumn<ToolEntry, String>("Name");
		name.setCellValueFactory(new PropertyValueFactory<ToolEntry, String>("name"));

		TableColumn<ToolEntry, String> synonyms = new TableColumn<ToolEntry, String>("Synonyms");
		synonyms.setCellValueFactory(new PropertyValueFactory<ToolEntry, String>("synonyms"));

		toolTable.getColumns().add(name);
		toolTable.getColumns().add(synonyms);
		toolTable.setItems(toolsList);

		Button remove = new Button("Remove");
		remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				ToolEntry selectedItem = toolTable.getSelectionModel().getSelectedItem();
				toolTable.getItems().remove(selectedItem);
			}
		});

		toolsView.getChildren().addAll(addTools, toolTable, remove);
	}

	public void initializeIngredientsPane() {
		ingredientsView = new VBox();
		ingredientsView.setSpacing(10);
		
		for (BaseIngredient i : kwdb.getIngredients()) {
			String ingredientSynonymes = "";
			String ingredientGroups = "";
			for (String s : i.getNames()) {
				if (!s.equals(i.toString())) {
					ingredientSynonymes = ingredientSynonymes + s + ";";
				}
			}
			
			//for (BaseIngredientGroup ig : i.getIngredientGroups()) {
			//	if (!ig.toString().equals(i.toString())) {
			//		ingredientGroups = ingredientGroups + ig.toString() + ";";
			//	}
			//}
			IngredientEntry ingredientEntry = new IngredientEntry(i.toString(), ingredientSynonymes, ingredientGroups);
			ingredientsList.add(ingredientEntry);
		}

		HBox addIngredientPane = new HBox();
		addIngredientPane.setSpacing(10);
		
		addIngredientPane.getChildren().add(new Label("Ingredient: "));
		TextField tFName = new TextField();
		tFName.setPromptText("Ingredient");
		addIngredientPane.getChildren().add(tFName);
		
		addIngredientPane.getChildren().add(new Label("Synonymes: "));
		TextField tFSynonyms = new TextField();
		tFSynonyms.setPromptText("Synonymes");
		addIngredientPane.getChildren().add(tFSynonyms);
		
		addIngredientPane.getChildren().add(new Label("Group: "));
		TextField tFGroups = new TextField();
		tFGroups.setPromptText("Groups");
		addIngredientPane.getChildren().add(tFGroups);
		
		Button addIngredient = new Button("Add");
		addIngredientPane.getChildren().add(addIngredient);

		addIngredient.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Set<String> ingredientEntries = new HashSet<String>();

				for (BaseIngredient i : kwdb.getIngredients()) {
					for (String s : i.getNames()) {
						ingredientEntries.add(s);
					}
				}

				if (!tFName.getText().equals("")) {
					if (!ingredientEntries.contains(tFName.getText())) {
						ingredientsList.add(new IngredientEntry(tFName.getText(), tFSynonyms.getText(), tFGroups.getText()));
					} else {
						System.err.println("Error: This Ingredient already exists. Maybe as own Ingredient or as Synonym");
					}
				} else {
					System.err.println("Error: Empty name field!");
				}
			}
		});

		ingredientsTable = new TableView<IngredientEntry>();
		ingredientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ingredientsTable.setEditable(true);

		TableColumn<IngredientEntry, String> name = new TableColumn<IngredientEntry, String>("Ingredient name");
		name.setCellValueFactory(new PropertyValueFactory<IngredientEntry, String>("ingredientName"));

		TableColumn<IngredientEntry, String> synonyms = new TableColumn<IngredientEntry, String>("Ingredient synonyms");
		synonyms.setCellValueFactory(new PropertyValueFactory<IngredientEntry, String>("ingredientSynonyms"));
		
		TableColumn<IngredientEntry, String> groups = new TableColumn<IngredientEntry, String>("Ingredient groups");
		synonyms.setCellValueFactory(new PropertyValueFactory<IngredientEntry, String>("ingredientGroups"));

		ingredientsTable.getColumns().add(name);
		ingredientsTable.getColumns().add(synonyms);
		ingredientsTable.getColumns().add(groups);
		ingredientsTable.setItems(ingredientsList);

		Button removeIngredient = new Button("Remove");
		removeIngredient.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				IngredientEntry selectedItem = ingredientsTable.getSelectionModel().getSelectedItem();
				ingredientsTable.getItems().remove(selectedItem);
			}
		});

		ingredientsView.getChildren().addAll(addIngredientPane, ingredientsTable, removeIngredient);
	}

	public void initializeComponents() throws Exception {
		kwdb = KeyWordDatabase.GERMAN_KWDB;

		realToolsList.addAll(kwdb.getTools());
		
		initializeToolsPane();
		initializeGroupsPane();
		initializeIngredientsPane();
		initializeCookingActionsPane();
		initializePartIndicatorsPane();
		initializeLastSentenceReferencesPane();
		initializeEventIndicatorsPane();

		stackPane = new StackPane();
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
