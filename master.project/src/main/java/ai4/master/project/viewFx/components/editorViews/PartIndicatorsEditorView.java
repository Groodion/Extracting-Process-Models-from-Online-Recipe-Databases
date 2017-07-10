package ai4.master.project.viewFx.components.editorViews;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.viewFx.components.editorViews.entries.PartIndicatorEntry;
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

public class PartIndicatorsEditorView extends EditorView {
	
	private ObservableList<String> partIndicators; 
	private ObjectProperty<KeyWordDatabase> kwdb;
	TableView<PartIndicatorEntry> partIndicatorsTable;
	
	
	@SuppressWarnings("unchecked")
	public PartIndicatorsEditorView(ObservableList<String> partIndicators, ObjectProperty<KeyWordDatabase> kwdb) {
		this.partIndicators = partIndicators;
		this.kwdb = kwdb;
		
		setSpacing(10);
		
		/*
		 * Add new PartIndicator
		 */
		
		HBox addPartIndicatorPane = new HBox();
		addPartIndicatorPane.setSpacing(10);

		TextField addPartIndicatorNameTF = new TextField();
		addPartIndicatorNameTF.setPromptText("Event Indicator");
		Button addPartIndicatorBtn = new Button("Add");
		addPartIndicatorBtn.disableProperty().bind(addPartIndicatorNameTF.textProperty().isEmpty());
		
		addPartIndicatorPane.getChildren().addAll(
				new Label("Event Indicator: "),
				addPartIndicatorNameTF,
				addPartIndicatorBtn
		);
		
		/*
		 * Edit PartIndicators
		 */
		
		partIndicatorsTable = new TableView<PartIndicatorEntry>();
		partIndicatorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		partIndicatorsTable.setEditable(true);
		VBox.setVgrow(partIndicatorsTable, Priority.ALWAYS);

		partIndicatorsTable.getColumns().addAll(
				nameColumn()
		);
		
		/*
		 * Add-Btn Logic
		 */
		
		addPartIndicatorBtn.setOnMouseClicked(e -> {
			String newIndicator = addPartIndicatorNameTF.getText();

			for (PartIndicatorEntry entry : partIndicatorsTable.getItems()) {
				if (newIndicator.equals(entry.getPartIndicator())) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText("The given name " + newIndicator + " already exists!");

					alert.showAndWait();
					return;
				}
			}

			partIndicators.add(newIndicator);
			
			addPartIndicatorNameTF.setText("");
		});
		
		/*
		 * PartIndicators changed
		 */
		
		ListChangeListener<String> partIndicatorsChanged = changed -> {
			partIndicatorsTable.getItems().clear();
			for(String partIndicator : partIndicators) {
				partIndicatorsTable.getItems().add(new PartIndicatorEntry(partIndicator, partIndicators));
			}
		};
		partIndicators.addListener(partIndicatorsChanged);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(partIndicatorsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			partIndicators.remove(partIndicatorsTable.getSelectionModel().getSelectedIndex());
		});

		cm.getItems().add(remove);

		
		partIndicatorsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(partIndicatorsTable, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		/*
		 * Add components to view
		 */
		
		getChildren().addAll(
				addPartIndicatorPane, 
				partIndicatorsTable
		);
	}

	private TableColumn<PartIndicatorEntry, String> nameColumn() {
		TableColumn<PartIndicatorEntry, String> nameColumn = new TableColumn<PartIndicatorEntry, String>("Event Indicator");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<PartIndicatorEntry, String>("partIndicator"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		return nameColumn;
	}

	public boolean contains(String word) {
		return kwdb.get().isPartIndicator(word);
	}
	public void scrollTo(String word) {
		partIndicatorsTable.scrollTo(partIndicators.indexOf(word));
	}
}