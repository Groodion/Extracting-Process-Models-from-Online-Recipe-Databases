package ai4.master.project.viewFx.components.editorViews;

import java.util.HashMap;
import java.util.Map;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Regex.Result;
import ai4.master.project.viewFx.components.editorViews.entries.CookingActionEntry;
import ai4.master.project.viewFx.components.editorViews.entries.RegexEntry;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

public class CookingActionsEditorView extends VBox {
	
	private ObservableList<BaseCookingAction> cookingActions;
	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<CookingActionEntry> tableView;
	
	
	@SuppressWarnings("unchecked")
	public CookingActionsEditorView(ObservableList<BaseCookingAction> cookingActions,
			ObservableList<BaseIngredient> ingredients,
			ObservableList<BaseTool> tools,
			ObjectProperty<KeyWordDatabase> kwdb) {
		Map<Object, ObservableList<String>> regexIdMap = new HashMap<Object, ObservableList<String>>();
		
		this.cookingActions = cookingActions;
		this.kwdb = kwdb;
		
		setSpacing(10);
		
		/*
		 * Edit CookingActions
		 */
		
		tableView = new TableView<CookingActionEntry>();
		tableView.setEditable(true);
		tableView.getColumns().addAll(
				nameColumn(),
				synoymsColumn(),
				regexColumn(regexIdMap)
		);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cookingActionTableCM = new ContextMenu();
		MenuItem removeCookingAction = new MenuItem("Remove CookingAction");
		removeCookingAction.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		removeCookingAction.setOnAction(e -> {
			cookingActions.remove(tableView.getSelectionModel().getSelectedIndex());
		});
		cookingActionTableCM.getItems().add(removeCookingAction);
		tableView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cookingActionTableCM.show(tableView, e.getScreenX(), e.getScreenY());
			} else {
				cookingActionTableCM.hide();
			}
		});

		/*
		 * coockingActions-changed
		 */
		
		ListChangeListener<BaseCookingAction> cookingActionsChanged = changed -> {
			tableView.getItems().clear();
			for(BaseCookingAction cookingAction : cookingActions) {
				tableView.getItems().add(new CookingActionEntry(cookingAction, cookingActions, regexIdMap));
			}
		};
		cookingActions.addListener(cookingActionsChanged);
		
		/*
		 * Add Content to View
		 */
		
		getChildren().addAll(
				tableView
		);
	}
	
	private TableColumn<CookingActionEntry, String> nameColumn() {
		TableColumn<CookingActionEntry, String> nameColumn = new TableColumn<CookingActionEntry, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, String>("name"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		return nameColumn;
	}
	private TableColumn<CookingActionEntry, ObservableList<String>> synoymsColumn() {
		TableColumn<CookingActionEntry, ObservableList<String>> synoymsColumn = new TableColumn<CookingActionEntry, ObservableList<String>>("Synonyme");
		synoymsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<String>>("synonyms"));
		synoymsColumn.setCellFactory(column -> new SynonymsCell());
		
		return synoymsColumn;
	}
	private TableColumn<CookingActionEntry, ObservableList<Regex>> regexColumn(Map<Object, ObservableList<String>> regexIdMap) {
		TableColumn<CookingActionEntry, ObservableList<Regex>> regexColumn = new TableColumn<CookingActionEntry, ObservableList<Regex>>("Regex");
		regexColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<Regex>>("regex"));
		regexColumn.setCellFactory(column -> new RegexCell(regexIdMap));
		
		return regexColumn;
	}
	
	public boolean contains(String word) {
		return kwdb.get().findCookingAction(word) != null;
	}
	public void scrollTo(String word) {
		scrollTo(kwdb.get().findCookingAction(word));
	}
	public void scrollTo(BaseCookingAction cookingAction) {
		tableView.scrollTo(cookingActions.indexOf(cookingAction));
	}

	
	private class SynonymsCell extends TableCell<CookingActionEntry, ObservableList<String>> {
		@Override
		public void updateItem(ObservableList<String> synonyms, boolean empty) {
			super.updateItem(synonyms, empty);

			ListView<String> synonymsView = new ListView<String>();
			synonymsView.setCellFactory(TextFieldListCell.forListView());
			synonymsView.setMinHeight(0);
			synonymsView.setPrefHeight(50);
			synonymsView.setEditable(true);


			ContextMenu synonymsViewCM = new ContextMenu();
			MenuItem addSynonym = new MenuItem("Add new Synonym");
			
			addSynonym.setOnAction(e -> synonyms.add("synonym" + synonyms.size()));
			MenuItem removeSynonym = new MenuItem("Remove Synonym");
			removeSynonym.disableProperty().bind(synonymsView.getSelectionModel().selectedItemProperty().isNull());
			removeSynonym.setOnAction(e -> synonymsView.getItems().remove(synonymsView.getSelectionModel().getSelectedIndex()));
			synonymsViewCM.getItems().addAll(
					addSynonym,
					removeSynonym
			);
			synonymsView.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					synonymsViewCM.show(synonymsView, e.getScreenX(), e.getScreenY());
				} else {
					synonymsViewCM.hide();
				}
			});
			
			if (synonyms != null) {
				synonymsView.setItems(synonyms);
			}

			setGraphic(synonymsView);
		}
	}
	private class RegexCell extends TableCell<CookingActionEntry, ObservableList<Regex>> {
		
		private ObservableList<Regex.Result> results;
		private Map<Object, ObservableList<String>> regexIdMap;
		
		public RegexCell(Map<Object, ObservableList<String>> regexIdMap) {
			this.regexIdMap = regexIdMap;
			
			results = FXCollections.observableArrayList();
			for (Regex.Result result : Regex.Result.values()) {
				results.add(result);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void updateItem(ObservableList<Regex> regexList, boolean empty) {
			super.updateItem(regexList, empty);
			
			TableView<RegexEntry> regexTable = new TableView<RegexEntry>();
			regexTable.setMinHeight(0);
			regexTable.setPrefHeight(75);
			regexTable.setEditable(true);
			regexTable.getColumns().addAll(
					idColumn(),
					expressionColumn(),
					resultColumn(),
					ingredientsNeededColumn(),
					referencePreviousProductsColumn()
			);
	
			ContextMenu regexTableCM = new ContextMenu();
			MenuItem addRegex = new MenuItem("Add new Regex");
			addRegex.setOnAction(e -> regexList.add(new Regex(".*", Result.ALL)));
			MenuItem removeRegex = new MenuItem("Remove Regex");
			removeRegex.disableProperty().bind(regexTable.getSelectionModel().selectedItemProperty().isNull());
			removeRegex.setOnAction(e -> regexList.remove(regexTable.getSelectionModel().getSelectedIndex()));
			regexTableCM.getItems().addAll(
					addRegex,
					removeRegex
			);
			regexTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					regexTableCM.show(regexTable, e.getScreenX(), e.getScreenY());
				} else {
					regexTableCM.hide();
				}
			});
				
			setGraphic(regexTable);
	
			if (regexList != null) {
				for (Regex regex : regexList) {
					regexTable.getItems().add(new RegexEntry(regex, regexList, regexIdMap.get(regex)));
				}
				ListChangeListener<Regex> listener = change -> {
					regexTable.getItems().clear();
					for (Regex regex : regexList) {
						regexTable.getItems().add(new RegexEntry(regex, regexList, regexIdMap.get(regex)));
					}
				};
				regexList.addListener(listener);
			}
	
			setGraphic(regexTable);
		}
		private TableColumn<RegexEntry, String> idColumn() {
			TableColumn<RegexEntry, String> idColumn = new TableColumn<RegexEntry, String>("Id");
			idColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, String>("id"));
			idColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			return idColumn;
		}
		private TableColumn<RegexEntry, String> expressionColumn() {
			TableColumn<RegexEntry, String> expressionColumn = new TableColumn<RegexEntry, String>("Expression");
			expressionColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, String>("expression"));
			expressionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			return expressionColumn;
		}
		private TableColumn<RegexEntry, Regex.Result> resultColumn() {
			TableColumn<RegexEntry, Regex.Result> resultColumn = new TableColumn<RegexEntry, Regex.Result>("Result");
			resultColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Regex.Result>("result"));
			resultColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(results));
			return resultColumn;
		}
		private TableColumn<RegexEntry, Boolean> ingredientsNeededColumn() {
			TableColumn<RegexEntry, Boolean> ingredientsNeededColumn = new TableColumn<RegexEntry, Boolean>("IngredientsNeeded");
			ingredientsNeededColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Boolean>("ingredientsNeeded"));
			ingredientsNeededColumn.setCellFactory(CheckBoxTableCell.forTableColumn(ingredientsNeededColumn));
			return ingredientsNeededColumn;
		}
		private TableColumn<RegexEntry, Boolean> referencePreviousProductsColumn() {
			TableColumn<RegexEntry, Boolean> referencePreviousProductsColumn = new TableColumn<RegexEntry, Boolean>("ReferencePreviousProducts");
			referencePreviousProductsColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Boolean>("referencePreviousProducts"));
			referencePreviousProductsColumn.setCellFactory(CheckBoxTableCell.forTableColumn(referencePreviousProductsColumn));
			return referencePreviousProductsColumn;
		}
	}
}

/*

TableColumn<CookingActionEntry, ObservableList<Transformation>> transformationsColumn = new TableColumn<CookingActionEntry, ObservableList<Transformation>>(
	"Transformations");transformationsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry,ObservableList<Transformation>>("transformations"));transformationsColumn.setCellFactory(column->
{
	TableCell<CookingActionEntry, ObservableList<Transformation>> cell = new TableCell<CookingActionEntry, ObservableList<Transformation>>() {
		@Override
		public void updateItem(ObservableList<Transformation> transformations, boolean empty) {
			super.updateItem(transformations, empty);

			TableView<TransformationEntry> transformationsTable = new TableView<TransformationEntry>();
			transformationsTable.setMinHeight(0);
			transformationsTable.setPrefHeight(125);
			transformationsTable.setEditable(true);

			ContextMenu transformationsTableCM = new ContextMenu();
			MenuItem addTransformation = new MenuItem("Add new Transformation");
			addTransformation.setOnAction(e -> {
				Transformation transformation = new Transformation();
				transformations.add(transformation);
				new TransformationEntry(transformation, transformationsTable.getItems(), transformations,
						regexIdMap.get(transformation));
				transformationsTable.requestFocus();
			});
			transformationsTableCM.getItems().add(addTransformation);
			MenuItem removeTransformation = new MenuItem("Remove Transformation");
			removeTransformation.disableProperty()
					.bind(transformationsTable.getSelectionModel().selectedItemProperty().isNull());
			removeTransformation.setOnAction(e -> {
				int index = transformationsTable.getSelectionModel().getSelectedIndex();
				transformations.remove(index);
				System.out.println(index + " " + transformations);
			});
			transformationsTableCM.getItems().add(removeTransformation);
			transformationsTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					transformationsTableCM.show(transformationsTable, e.getScreenX(), e.getScreenY());
				} else {
					transformationsTableCM.hide();
				}
			});

			TableColumn<TransformationEntry, ObservableList<String>> refIdColumn = new TableColumn<TransformationEntry, ObservableList<String>>(
					"RefRegexIds");
			refIdColumn.setCellValueFactory(
					new PropertyValueFactory<TransformationEntry, ObservableList<String>>("refRegexIds"));
			refIdColumn.setCellFactory(column -> {
				TableCell<TransformationEntry, ObservableList<String>> cell = new TableCell<TransformationEntry, ObservableList<String>>() {
					@Override
					public void updateItem(ObservableList<String> refIds, boolean empty) {
						super.updateItem(refIds, empty);
						
						ListView<String> refIdsView = new ListView<String>();
						if (transformations.size() != 0)
							refIdsView.setCellFactory(
									ComboBoxListCell.forListView(regexIdMap.get(transformations.get(0))));
						refIdsView.setMinHeight(0);
						refIdsView.setPrefHeight(50);
						refIdsView.setEditable(true);

						ContextMenu transformationsTableCM = new ContextMenu();
						MenuItem addTransformation = new MenuItem("Add RegexId");
						addTransformation.setOnAction(e -> {
							refIds.add(null);
							refIdsView.requestFocus();
						});
						transformationsTableCM.getItems().add(addTransformation);
						MenuItem removeTransformation = new MenuItem("Remove RegexId");
						removeTransformation.disableProperty()
								.bind(refIdsView.getSelectionModel().selectedItemProperty().isNull());
						removeTransformation.setOnAction(e -> {
							refIds.remove(refIdsView.getSelectionModel().getSelectedItem());
							refIdsView.requestFocus();
						});
						transformationsTableCM.getItems().add(removeTransformation);
						refIdsView.setOnMouseClicked(e -> {
							if (e.getButton() == MouseButton.SECONDARY) {
								transformationsTableCM.show(refIdsView, e.getScreenX(), e.getScreenY());
							} else {
								transformationsTableCM.hide();
							}
						});

						if (refIds != null) {
							refIdsView.setItems(refIds);
						}

						setGraphic(refIdsView);
					}
				};
				return cell;
			});
			TableColumn<TransformationEntry, ObservableList<BaseIngredient>> ingredientsColumn = new TableColumn<TransformationEntry, ObservableList<BaseIngredient>>(
					"ingredients");
			ingredientsColumn.setCellValueFactory(
					new PropertyValueFactory<TransformationEntry, ObservableList<BaseIngredient>>(
							"ingredients"));
			ingredientsColumn.setCellFactory(column -> {
				TableCell<TransformationEntry, ObservableList<BaseIngredient>> cell = new TableCell<TransformationEntry, ObservableList<BaseIngredient>>() {
					@Override
					public void updateItem(ObservableList<BaseIngredient> ingredients, boolean empty) {
						super.updateItem(ingredients, empty);

						ListView<BaseIngredient> ingredientsView = new ListView<BaseIngredient>();
						ingredientsView.setCellFactory(ComboBoxListCell.forListView(realIngredientsList));
						ingredientsView.setMinHeight(0);
						ingredientsView.setPrefHeight(50);
						ingredientsView.setEditable(true);

						ContextMenu transformationsTableCM = new ContextMenu();
						MenuItem addTransformation = new MenuItem("Add RegexId");
						addTransformation.setOnAction(e -> {
							ingredients.add(null);
							ingredientsView.requestFocus();
						});
						transformationsTableCM.getItems().add(addTransformation);
						MenuItem removeTransformation = new MenuItem("Remove RegexId");
						removeTransformation.disableProperty()
								.bind(ingredientsView.getSelectionModel().selectedItemProperty().isNull());
						removeTransformation.setOnAction(e -> {
							ingredients.remove(ingredientsView.getSelectionModel().getSelectedItem());
						});
						transformationsTableCM.getItems().add(removeTransformation);
						ingredientsView.setOnMouseClicked(e -> {
							if (e.getButton() == MouseButton.SECONDARY) {
								transformationsTableCM.show(ingredientsView, e.getScreenX(), e.getScreenY());
							} else {
								transformationsTableCM.hide();
							}
						});
						
						if (ingredients != null) {
							ingredientsView.setItems(ingredients);
						}

						setGraphic(ingredientsView);
					}
				};
				return cell;
			});

TableColumn<TransformationEntry, String> ingredientTagColumn = new TableColumn<TransformationEntry, String>(
	"IngredientTag");ingredientTagColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry,String>("ingredientTag"));ingredientTagColumn.setCellFactory(TextFieldTableCell.forTableColumn());

TableColumn<TransformationEntry, String> quantifierTagColumn = new TableColumn<TransformationEntry, String>(
	"QuantifierTag");quantifierTagColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry,String>("quantifierTag"));quantifierTagColumn.setCellFactory(TextFieldTableCell.forTableColumn());

TableColumn<TransformationEntry, BaseIngredient> productColumn = new TableColumn<TransformationEntry, BaseIngredient>(
	"Product");productColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry,BaseIngredient>("product"));productColumn.setCellFactory(ComboBoxTableCell.forTableColumn(realIngredientsList));

transformationsTable.getColumns().add(refIdColumn);transformationsTable.getColumns().add(ingredientsColumn);transformationsTable.getColumns().add(ingredientTagColumn);transformationsTable.getColumns().add(quantifierTagColumn);transformationsTable.getColumns().add(productColumn);

if(transformations!=null)
{
for (Transformation transformation : transformations) {
	new TransformationEntry(transformation, transformationsTable.getItems(), transformations,
			regexIdMap.get(transformation));
}
}

setGraphic(transformationsTable);
		}};return cell;});

TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn = new TableColumn<CookingActionEntry, ObservableList<BaseTool>>(
	"Implied Tools");toolsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry,ObservableList<BaseTool>>("tools"));toolsColumn.setCellFactory(column->
{
	TableCell<CookingActionEntry, ObservableList<BaseTool>> cell = new TableCell<CookingActionEntry, ObservableList<BaseTool>>() {
		@Override
		public void updateItem(ObservableList<BaseTool> tools, boolean empty) {
			super.updateItem(tools, empty);
			ListView<BaseTool> toolsView = new ListView<BaseTool>();
			toolsView.setCellFactory(ComboBoxListCell.forListView(realToolsList));
			toolsView.setMinHeight(0);
			toolsView.setPrefHeight(50);
			toolsView.setEditable(true);


			addToolBtn.setOnAction(e -> {

			ContextMenu toolsViewCM = new ContextMenu();
			MenuItem addTool = new MenuItem("Add new Tool");
			addTool.setOnAction(e -> {

				tools.add(null);
				toolsView.requestFocus();
				toolsView.edit(tools.size() - 1);
			});
			toolsViewCM.getItems().add(addTool);
			MenuItem removeTool = new MenuItem("Remove Tool");
			removeTool.disableProperty().bind(toolsView.getSelectionModel().selectedItemProperty().isNull());
			removeTool.setOnAction(e -> {
				tools.remove(toolsView.getSelectionModel().getSelectedItem());
				toolsView.requestFocus();
			});


			removeToolBtn.disableProperty().bind(toolsView.getSelectionModel().selectedItemProperty().isNull());
			toolsViewCM.getItems().add(removeTool);
			toolsView.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					toolsViewCM.show(toolsView, e.getScreenX(), e.getScreenY());
				} else {
					toolsViewCM.hide();
				}
			});


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
tableView.getColumns().add(transformationsColumn);
tableView.getColumns().add(toolsColumn);

for (BaseCookingAction cookingAction : KeyWordDatabase.GERMAN_KWDB.getCookingActions()) {
	new CookingActionEntry(cookingAction, tableView.getItems(), KeyWordDatabase.GERMAN_KWDB, regexIdMap);
}

cookingActionsView.getChildren().add(tableView);

*/