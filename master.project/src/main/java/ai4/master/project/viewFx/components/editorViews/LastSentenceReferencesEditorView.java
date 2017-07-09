package ai4.master.project.viewFx.components.editorViews;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.viewFx.components.editorViews.entries.LastSentenceReferenceEntry;
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

public class LastSentenceReferencesEditorView extends VBox {
	@SuppressWarnings("unchecked")
	public LastSentenceReferencesEditorView(ObservableList<String> lastSentenceReferences, ObjectProperty<KeyWordDatabase> kwdb) {
		setSpacing(10);
		
		/*
		 * Add new LastSentenceReference
		 */
		
		HBox addLastSentenceReferencePane = new HBox();
		addLastSentenceReferencePane.setSpacing(10);

		TextField addLastSentenceReferenceNameTF = new TextField();
		addLastSentenceReferenceNameTF.setPromptText("Event Indicator");
		Button addLastSentenceReferenceBtn = new Button("Add");
		addLastSentenceReferenceBtn.disableProperty().bind(addLastSentenceReferenceNameTF.textProperty().isEmpty());
		
		addLastSentenceReferencePane.getChildren().addAll(
				new Label("Event Indicator: "),
				addLastSentenceReferenceNameTF,
				addLastSentenceReferenceBtn
		);
		
		/*
		 * Edit LastSentenceReferences
		 */
		
		TableView<LastSentenceReferenceEntry> lastSentenceReferencesTable = new TableView<LastSentenceReferenceEntry>();
		lastSentenceReferencesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		lastSentenceReferencesTable.setEditable(true);
		VBox.setVgrow(lastSentenceReferencesTable, Priority.ALWAYS);

		lastSentenceReferencesTable.getColumns().addAll(
				nameColumn()
		);
		
		/*
		 * Add-Btn Logic
		 */
		
		addLastSentenceReferenceBtn.setOnMouseClicked(e -> {
			String newIndicator = addLastSentenceReferenceNameTF.getText();

			for (LastSentenceReferenceEntry entry : lastSentenceReferencesTable.getItems()) {
				if (newIndicator.equals(entry.getLastSentenceReference())) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText("The given name " + newIndicator + " already exists!");

					alert.showAndWait();
					return;
				}
			}

			lastSentenceReferences.add(newIndicator);
			
			addLastSentenceReferenceNameTF.setText("");
		});
		
		/*
		 * LastSentenceReferences changed
		 */
		
		ListChangeListener<String> lastSentenceReferencesChanged = changed -> {
			lastSentenceReferencesTable.getItems().clear();
			for(String lastSentenceReference : lastSentenceReferences) {
				lastSentenceReferencesTable.getItems().add(new LastSentenceReferenceEntry(lastSentenceReference, lastSentenceReferences));
			}
		};
		lastSentenceReferences.addListener(lastSentenceReferencesChanged);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem remove = new MenuItem("Remove");
		remove.disableProperty().bind(lastSentenceReferencesTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> {
			lastSentenceReferences.remove(lastSentenceReferencesTable.getSelectionModel().getSelectedIndex());
		});

		cm.getItems().add(remove);

		
		lastSentenceReferencesTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(lastSentenceReferencesTable, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		/*
		 * Add components to view
		 */
		
		getChildren().addAll(
				addLastSentenceReferencePane, 
				lastSentenceReferencesTable
		);
	}
	
	private TableColumn<LastSentenceReferenceEntry, String> nameColumn() {
		TableColumn<LastSentenceReferenceEntry, String> nameColumn = new TableColumn<LastSentenceReferenceEntry, String>("Event Indicator");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<LastSentenceReferenceEntry, String>("lastSentenceReference"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		return nameColumn;
	}
}