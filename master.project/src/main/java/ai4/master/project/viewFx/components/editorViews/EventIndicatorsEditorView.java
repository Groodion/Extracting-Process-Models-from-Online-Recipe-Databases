package ai4.master.project.viewFx.components.editorViews;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.viewFx.components.editorViews.entries.EventIndicatorEntry;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EventIndicatorsEditorView extends VBox {

	private ObservableList<String> eventIndicators; 
	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<EventIndicatorEntry> eventIndicatorsTable;

	
	@SuppressWarnings("unchecked")
	public EventIndicatorsEditorView(ObservableList<String> eventIndicators, ObjectProperty<KeyWordDatabase> kwdb) {
		this.eventIndicators = eventIndicators;
		this.kwdb = kwdb;
		
		setSpacing(10);
		
		/*
		 * Add new EventIndicator
		 */
		
		HBox addEventIndicatorPane = new HBox();
		addEventIndicatorPane.setSpacing(10);

		TextField addEventIndicatorNameTF = new TextField();
		addEventIndicatorNameTF.setPromptText("Event Indicator");
		Button addEventIndicatorBtn = new Button("Add");
		addEventIndicatorBtn.disableProperty().bind(addEventIndicatorNameTF.textProperty().isEmpty());
		
		addEventIndicatorPane.getChildren().addAll(
				new Label("Event Indicator: "),
				addEventIndicatorNameTF,
				addEventIndicatorBtn
		);
		
		/*
		 * Edit EventIndicators
		 */
		
		eventIndicatorsTable = new TableView<EventIndicatorEntry>();
		eventIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		eventIndicatorsTable.setEditable(true);
		VBox.setVgrow(eventIndicatorsTable, Priority.ALWAYS);

		eventIndicatorsTable.getColumns().addAll(
				nameColumn()
		);
		
		/*
		 * Add-Btn Logic
		 */
		
		addEventIndicatorBtn.setOnMouseClicked(e -> {
			String newIndicator = addEventIndicatorNameTF.getText();

			for (EventIndicatorEntry entry : eventIndicatorsTable.getItems()) {
				if (newIndicator.equals(entry.getEventIndicator())) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText("The given name " + newIndicator + " already exists!");

					alert.showAndWait();
					return;
				}
			}

			eventIndicators.add(newIndicator);
			
			addEventIndicatorNameTF.setText("");
		});
		
		/*
		 * EventIndicators changed
		 */
		
		ListChangeListener<String> eventIndicatorsChanged = changed -> {
			eventIndicatorsTable.getItems().clear();
			for(String eventIndicator : eventIndicators) {
				eventIndicatorsTable.getItems().add(new EventIndicatorEntry(eventIndicator, eventIndicators));
			}
		};
		eventIndicators.addListener(eventIndicatorsChanged);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(eventIndicatorsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			eventIndicators.remove(eventIndicatorsTable.getSelectionModel().getSelectedIndex());
		});

		cm.getItems().add(remove);

		
		eventIndicatorsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(eventIndicatorsTable, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		/*
		 * Add components to view
		 */
		
		getChildren().addAll(
				addEventIndicatorPane, 
				eventIndicatorsTable
		);
	}
	
	private TableColumn<EventIndicatorEntry, String> nameColumn() {
		TableColumn<EventIndicatorEntry, String> nameColumn = new TableColumn<EventIndicatorEntry, String>("Event Indicator");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<EventIndicatorEntry, String>("eventIndicator"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		return nameColumn;
	}

	public boolean contains(String word) {
		return kwdb.get().isPartIndicator(word);
	}
	public void scrollTo(String word) {
		eventIndicatorsTable.scrollTo(eventIndicators.indexOf(word));
	}
}