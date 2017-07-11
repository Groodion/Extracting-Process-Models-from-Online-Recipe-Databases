package ai4.master.project.viewFx.components.editorViews;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.viewFx.components.editorViews.entries.IngredientEntry;
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
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class IngredientsEditorView extends EditorView {
	
	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<IngredientEntry> ingredientsTable;

	
	@SuppressWarnings("unchecked")
	public IngredientsEditorView(ObservableList<BaseIngredient> ingredients,
			ObservableList<BaseIngredientGroup> ingredientGroups,
			ObjectProperty<KeyWordDatabase> kwdb) {
		this.kwdb = kwdb;
		
		setSpacing(10);
		
		/*
		 * Add new Ingredient
		 */
		
		HBox addIngredientPane = new HBox();
		addIngredientPane.setSpacing(10);

		TextField addIngredientNameTF = new TextField();
		addIngredientNameTF.setPromptText("Name");
		TextField addIngredientSynonymsTF = new TextField();
		addIngredientSynonymsTF.setPromptText("Synonymes");
		addIngredientSynonymsTF.disableProperty().bind(addIngredientNameTF.textProperty().isEmpty());
		TextField addIngredientGroupsTF = new TextField();
		addIngredientGroupsTF.setPromptText("Groups");
		addIngredientGroupsTF.disableProperty().bind(addIngredientNameTF.textProperty().isEmpty());
		Button addIngredientBtn = new Button("Add");
		addIngredientBtn.disableProperty().bind(addIngredientNameTF.textProperty().isEmpty());
		
		addIngredientPane.getChildren().addAll(
				new Label("Name: "),
				addIngredientNameTF,
				new Label("Synonyme: "),
				addIngredientSynonymsTF,
				new Label("Groups: "),
				addIngredientGroupsTF,
				addIngredientBtn
		);
		
		/*
		 * Edit Ingredients
		 */
		
		ingredientsTable = new TableView<IngredientEntry>();
		ingredientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ingredientsTable.setEditable(true);
		VBox.setVgrow(ingredientsTable, Priority.ALWAYS);

		ingredientsTable.getColumns().addAll(
				nameColumn(),
				synonymsColumn(),
				groupsColumn(ingredientGroups)
		);
		
		/*
		 * Add-Btn Logic
		 */
		
		addIngredientBtn.setOnAction(e -> {
			BaseIngredient ingredient = new BaseIngredient();
			ingredient.addName(addIngredientNameTF.getText());
			String s = addIngredientSynonymsTF.getText();

			for (String t : s.split(";")) {
				ingredient.getNames().add(t);
			}

			for (String name : ingredient.getNames()) {
				if (kwdb.get().findIngredient(name) != null) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText("The given name " + name + " already exists!");

					alert.showAndWait();
					return;
				}
			}
			
			ingredients.add(ingredient);
			
			addIngredientNameTF.setText("");
			addIngredientSynonymsTF.setText("");
		});
		
		/*
		 * ingredients-changed
		 */
		
		ListChangeListener<BaseIngredient> ingredientsChanged = changed -> {
			ingredientsTable.getItems().clear();
			for(BaseIngredient ingredient : ingredients) {
				ingredientsTable.getItems().add(new IngredientEntry(ingredient, ingredients));
			}
		};
		ingredients.addListener(ingredientsChanged);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem remove = new MenuItem("Remove Ingredient");
		remove.disableProperty().bind(ingredientsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> ingredients.remove(ingredientsTable.getSelectionModel().getSelectedIndex()));
		
		cm.getItems().add(remove);

		
		ingredientsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(ingredientsTable, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		/*
		 * Add components to view
		 */
		
		getChildren().addAll(
				addIngredientPane,
				ingredientsTable
		);
	}
	
	private TableColumn<IngredientEntry, String> nameColumn() {
		TableColumn<IngredientEntry, String> nameColumn = new TableColumn<IngredientEntry, String>("Name");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<IngredientEntry, String>("name"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		return nameColumn;
	}
	private TableColumn<IngredientEntry, ObservableList<String>> synonymsColumn() {
		TableColumn<IngredientEntry, ObservableList<String>> synonymsColumn = new TableColumn<IngredientEntry, ObservableList<String>>("Synonyms");
		
		synonymsColumn.setCellValueFactory(new PropertyValueFactory<IngredientEntry, ObservableList<String>>("synonyms"));
		synonymsColumn.setCellFactory(column -> new SynonymsCell());
		
		return synonymsColumn;
	}
	private TableColumn<IngredientEntry, ObservableList<BaseIngredientGroup>> groupsColumn(ObservableList<BaseIngredientGroup> ingredientGroups) {
		TableColumn<IngredientEntry, ObservableList<BaseIngredientGroup>> groupsColumn = new TableColumn<IngredientEntry, ObservableList<BaseIngredientGroup>>(
				"Groups");
		groupsColumn
				.setCellValueFactory(new PropertyValueFactory<IngredientEntry, ObservableList<BaseIngredientGroup>>("ingredientGroups"));
		groupsColumn.setCellFactory(column -> new GroupsCell(ingredientGroups));
		
		return groupsColumn;
	}

	public boolean contains(String word) {
		return kwdb.get().findIngredient(word) != null;
	}
	public void scrollTo(String word) {
		for(IngredientEntry entry : ingredientsTable.getItems()) {
			if(entry.getName().equals(word) || entry.getSynonyms().contains(word)) {
				ingredientsTable.scrollTo(entry);
				ingredientsTable.getSelectionModel().select(entry);
				break;
			}
		}
	}

	private class SynonymsCell extends TableCell<IngredientEntry, ObservableList<String>> { 
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
			removeSynonymItem.setOnAction(e -> synonymsView.getItems().remove(synonymsView.getSelectionModel().getSelectedIndex()));
			
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
	private class GroupsCell extends TableCell<IngredientEntry, ObservableList<BaseIngredientGroup>> {
		private ObservableList<BaseIngredientGroup> ingredientGroups;
		
		public GroupsCell(ObservableList<BaseIngredientGroup> ingredientGroups) {
			this.ingredientGroups = ingredientGroups;
		}
		
		@Override
		public void updateItem(ObservableList<BaseIngredientGroup> groups, boolean empty) {
			super.updateItem(groups, empty);
			VBox layout = new VBox();

			ListView<BaseIngredientGroup> groupsView = new ListView<BaseIngredientGroup>();
			groupsView.setCellFactory(ComboBoxListCell.forListView(ingredientGroups));
			groupsView.setMinHeight(0);
			groupsView.setPrefHeight(50);
			groupsView.setEditable(true);
			
			
			ContextMenu groupsCm = new ContextMenu();
			MenuItem removeGroupItem = new MenuItem("Remove group");
			MenuItem addGroupItem = new MenuItem("Add new group");
			groupsCm.getItems().addAll(removeGroupItem, addGroupItem);
			
			removeGroupItem.disableProperty().bind(groupsView.getSelectionModel().selectedItemProperty().isNull());
			removeGroupItem.setOnAction(e -> groupsView.getItems().remove(groupsView.getSelectionModel().getSelectedIndex()));
			
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
	}
}