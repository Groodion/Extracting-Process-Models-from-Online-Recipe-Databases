package ai4.master.project.view;

import java.net.URL;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import ai4.master.project.KeyWordDatabase;
import ai4.master.project.XMLLoader;
import ai4.master.project.recipe.baseObject.BaseTool;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private XMLLoader loader;

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
	private ObservableList<ToolEntry> toolsList = FXCollections.observableArrayList();
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
	}

	public void initializeCookingActionsPane() {
		cookingActionsView = new VBox();
	}

	public void initializePartIndicatorsPane() {
		partIndicatorsView = new VBox();
	}

	public void initializeLastSentenceReferencesPane() {
		lastSentenceReferencesView = new VBox();
	}

	public void initializeEventIndicatorsPane() {
		eventIndicatorsView = new VBox();
		
		eventIndicatorsTable = new TableView<String>();
		eventIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		eventIndicatorsTable.setEditable(true);

		TableColumn<String, String> eventIndicators = new TableColumn<String, String>("Event Indicators");
		eventIndicators.setCellValueFactory(new PropertyValueFactory<String, String>("Event Indicators"));
		
		eventIndicatorsTable.getColumns().add(eventIndicators);
		
		for(String s : kwdb.getEventIndicators()) {
			System.out.println(s);
			eventIndicatorsList.add(s);
		}
	
		eventIndicatorsTable.setItems(eventIndicatorsList);
		

		eventIndicatorsView.getChildren().addAll(eventIndicatorsTable);
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
	}

	public void initializeComponents() throws Exception {
		loader = new XMLLoader();
		kwdb = KeyWordDatabase.KWDB_GERMAN;

		initializeToolsPane();
		initializeGroupsPane();
		initializeIngredientsPane();
		initializeCookingActionsPane();
		initializePartIndicatorsPane();
		initializeLastSentenceReferencesPane();
		initializeEventIndicatorsPane();

		stackPane = new StackPane();
		stackPane.getChildren().addAll(toolsView, groupsView, ingredientsView, cookingActionsView, partIndicatorsView, lastSentenceReferencesView, eventIndicatorsView);
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
