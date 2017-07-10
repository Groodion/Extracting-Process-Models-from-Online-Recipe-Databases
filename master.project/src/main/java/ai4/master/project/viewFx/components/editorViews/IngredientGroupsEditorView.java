package ai4.master.project.viewFx.components.editorViews;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.viewFx.components.editorViews.entries.IngredientGroupEntry;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class IngredientGroupsEditorView extends EditorView {
	
	private ObservableList<BaseIngredientGroup> ingredientGroups;
	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<IngredientGroupEntry> ingredientGroupsTable;
	
	
	@SuppressWarnings("unchecked")
	public IngredientGroupsEditorView(ObservableList<BaseIngredientGroup> ingredientGroups,
			ObjectProperty<KeyWordDatabase> kwdb) {
		this.ingredientGroups = ingredientGroups;
		this.kwdb = kwdb;
		
		setSpacing(10);
		
		/*
		 * Add new IngredientGroup
		 */
		
		HBox addIngredientGroupPane = new HBox();
		addIngredientGroupPane.setSpacing(10);

		TextField addIngredientGroupNameTF = new TextField();
		addIngredientGroupNameTF.setPromptText("Name");
		TextField addIngredientGroupSynonymsTF = new TextField();
		addIngredientGroupSynonymsTF.setPromptText("Synonymes");
		addIngredientGroupSynonymsTF.disableProperty().bind(addIngredientGroupNameTF.textProperty().isEmpty());
		Button addIngredientGroupBtn = new Button("Add");
		addIngredientGroupBtn.disableProperty().bind(addIngredientGroupNameTF.textProperty().isEmpty());

		addIngredientGroupPane.getChildren().addAll(
				new Label("Name: "),
				addIngredientGroupNameTF,
				new Label("Synonyme: "),
				addIngredientGroupSynonymsTF,
				addIngredientGroupBtn
		);
		
		/*
		 * Edit IngredientGroups
		 */
		
		ingredientGroupsTable = new TableView<IngredientGroupEntry>();
		ingredientGroupsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ingredientGroupsTable.setEditable(true);
		VBox.setVgrow(ingredientGroupsTable, Priority.ALWAYS);

		ingredientGroupsTable.getColumns().addAll(
				nameColumn(),
				synonymsColumn()
		);
		
		/*
		 * Add-Btn Logic
		 */
		
		addIngredientGroupBtn.setOnAction(e -> {
			BaseIngredientGroup ingredientGroup = new BaseIngredientGroup();
			ingredientGroup.addName(addIngredientGroupNameTF.getText());
			String s = addIngredientGroupSynonymsTF.getText();

			for (String t : s.split(";")) {
				ingredientGroup.getNames().add(t);
			}

			for (String name : ingredientGroup.getNames()) {
				if (kwdb.get().findIngredientGroup(name) != null) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText("The given name " + name + " already exists!");

					alert.showAndWait();
					return;
				}
			}
			
			ingredientGroups.add(ingredientGroup);
			
			addIngredientGroupNameTF.setText("");
			addIngredientGroupSynonymsTF.setText("");
		});
		
		/*
		 * ingredientGroups-changed
		 */
		
		ListChangeListener<BaseIngredientGroup> ingredientGroupsChanged = changed -> {
			ingredientGroupsTable.getItems().clear();
			for(BaseIngredientGroup ingredientGroup : ingredientGroups) {
				ingredientGroupsTable.getItems().add(new IngredientGroupEntry(ingredientGroup, ingredientGroups));
			}
		};
		ingredientGroups.addListener(ingredientGroupsChanged);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem remove = new MenuItem("Remove IngredientGroup");
		remove.disableProperty().bind(ingredientGroupsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> ingredientGroups.remove(ingredientGroupsTable.getSelectionModel().getSelectedIndex()));
		
		cm.getItems().add(remove);

		
		ingredientGroupsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(ingredientGroupsTable, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		/*
		 * Add components to view
		 */
		
		getChildren().addAll(
				addIngredientGroupPane,
				ingredientGroupsTable
		);
	}
	
	private TableColumn<IngredientGroupEntry, String> nameColumn() {
		TableColumn<IngredientGroupEntry, String> nameColumn = new TableColumn<IngredientGroupEntry, String>("Name");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<IngredientGroupEntry, String>("name"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		return nameColumn;
	}
	private TableColumn<IngredientGroupEntry, ObservableList<String>> synonymsColumn() {
		TableColumn<IngredientGroupEntry, ObservableList<String>> synonymsColumn = new TableColumn<IngredientGroupEntry, ObservableList<String>>("Synonyms");
		
		synonymsColumn.setCellValueFactory(new PropertyValueFactory<IngredientGroupEntry, ObservableList<String>>("synonyms"));
		synonymsColumn.setCellFactory(column -> new SynonymsCell());
		
		return synonymsColumn;
	}

	public boolean contains(String word) {
		return kwdb.get().findIngredientGroup(word) != null;
	}
	public void scrollTo(String word) {
		scrollTo(kwdb.get().findIngredientGroup(word));
	}
	public void scrollTo(BaseIngredientGroup ingredientGroup) {
		ingredientGroupsTable.scrollTo(ingredientGroups.indexOf(ingredientGroup));
	}

	private class SynonymsCell extends TableCell<IngredientGroupEntry, ObservableList<String>> { 
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
	}
}