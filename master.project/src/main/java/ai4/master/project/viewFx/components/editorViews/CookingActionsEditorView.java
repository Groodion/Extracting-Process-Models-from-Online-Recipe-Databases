package ai4.master.project.viewFx.components.editorViews;

import java.util.HashMap;
import java.util.Map;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Regex.Result;
import ai4.master.project.recipe.baseObject.Transformation;
import ai4.master.project.viewFx.components.editorViews.entries.CookingActionEntry;
import ai4.master.project.viewFx.components.editorViews.entries.RegexEntry;
import ai4.master.project.viewFx.components.editorViews.entries.TransformationEntry;
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
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;

public class CookingActionsEditorView extends EditorView {
	
	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<CookingActionEntry> tableView;
	
	
	@SuppressWarnings("unchecked")
	public CookingActionsEditorView(ObservableList<BaseCookingAction> cookingActions,
			ObservableList<BaseIngredient> ingredients,
			ObservableList<BaseTool> tools,
			ObjectProperty<KeyWordDatabase> kwdb) {
		Map<Object, ObservableList<String>> regexIdMap = new HashMap<Object, ObservableList<String>>();
		
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
				regexColumn(regexIdMap),
				transformationsColumn(regexIdMap, ingredients),
				toolsColumn(tools)
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
	private TableColumn<CookingActionEntry, ObservableList<Transformation>> transformationsColumn(Map<Object, ObservableList<String>> regexIdMap, ObservableList<BaseIngredient> ingredients) {
		TableColumn<CookingActionEntry, ObservableList<Transformation>> transformationsColumn = new TableColumn<CookingActionEntry, ObservableList<Transformation>>("Transformations");
		transformationsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<Transformation>>("transformations"));
		transformationsColumn.setCellFactory(column -> new TransformationCell(regexIdMap, ingredients));
		return transformationsColumn;
	}
	private TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn(ObservableList<BaseTool> tools) {
		TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn = new TableColumn<CookingActionEntry, ObservableList<BaseTool>>("Implied Tools");
		toolsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<BaseTool>>("tools"));
		toolsColumn.setCellFactory(column -> new ImpliedToolsCell(tools));
		return toolsColumn;
	}
	
	public boolean contains(String word) {
		return kwdb.get().findCookingAction(word) != null;
	}
	public void scrollTo(String word) {
		for(CookingActionEntry entry : tableView.getItems()) {
			if(entry.getName().equals(word) || entry.getSynonyms().contains(word)) {
				tableView.scrollTo(entry);
				tableView.getSelectionModel().select(entry);
				break;
			}
		}
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
					referencePreviousProductsColumn(),
					chargeToolsColumn()
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
		private TableColumn<RegexEntry, Boolean> chargeToolsColumn() {
			TableColumn<RegexEntry, Boolean> chargeToolsColumn = new TableColumn<RegexEntry, Boolean>("ChargeTools");
			chargeToolsColumn.setCellValueFactory(new PropertyValueFactory<RegexEntry, Boolean>("chargeTools"));
			chargeToolsColumn.setCellFactory(CheckBoxTableCell.forTableColumn(chargeToolsColumn));
			return chargeToolsColumn;
		}
	}
	private class TransformationCell extends TableCell<CookingActionEntry, ObservableList<Transformation>> {
		
		private Map<Object, ObservableList<String>> regexIdMap;
		private ObservableList<BaseIngredient> ingredients;

		
		public TransformationCell(Map<Object, ObservableList<String>> regexIdMap, ObservableList<BaseIngredient> ingredients) {
			this.regexIdMap = regexIdMap;
			this.ingredients = ingredients;
		}
		
		@SuppressWarnings("unchecked")
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
				transformationsTable.requestFocus();
			});
			transformationsTableCM.getItems().add(addTransformation);
			MenuItem removeTransformation = new MenuItem("Remove Transformation");
			removeTransformation.disableProperty().bind(transformationsTable.getSelectionModel().selectedItemProperty().isNull());
			removeTransformation.setOnAction(e -> transformations.remove(transformationsTable.getSelectionModel().getSelectedIndex()));
			transformationsTableCM.getItems().add(removeTransformation);
			transformationsTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					transformationsTableCM.show(transformationsTable, e.getScreenX(), e.getScreenY());
				} else {
					transformationsTableCM.hide();
				}
			});

			transformationsTable.getColumns().addAll(
					regexIdsColumn(transformations != null && !transformations.isEmpty() ? regexIdMap.get(transformations.get(0)) : null),
					ingredientsColumn(),
					ingredientTagColumn(),
					quantifierTagColumn(),
					productColumn()
			);

			if(transformations != null)
			{
				transformations.forEach(transformation -> transformationsTable.getItems().add(new TransformationEntry(transformation, transformations)));
			}

			setGraphic(transformationsTable);
		}
		
		private TableColumn<TransformationEntry, ObservableList<String>> regexIdsColumn(ObservableList<String> regexIds) {
			TableColumn<TransformationEntry, ObservableList<String>> regexIdsColumn = new TableColumn<TransformationEntry, ObservableList<String>>("RefRegexIds");
			regexIdsColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry, ObservableList<String>>("regexIds"));
			regexIdsColumn.setCellFactory(column -> new RegexIdsCell(regexIds));
			return regexIdsColumn;
		}
		private TableColumn<TransformationEntry, ObservableList<BaseIngredient>> ingredientsColumn() {
			TableColumn<TransformationEntry, ObservableList<BaseIngredient>> ingredientsColumn = new TableColumn<TransformationEntry, ObservableList<BaseIngredient>>("Mandatory Ingredients");
			ingredientsColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry, ObservableList<BaseIngredient>>("mandatoryIngredients"));
			ingredientsColumn.setCellFactory(column -> new IngredientsCell(ingredients));
			return ingredientsColumn;
		}
		private TableColumn<TransformationEntry, String> ingredientTagColumn() {
			TableColumn<TransformationEntry, String> ingredientTagColumn = new TableColumn<TransformationEntry, String>("IngredientTag");
			ingredientTagColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry,String>("ingredientTag"));
			ingredientTagColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			return ingredientTagColumn;
		}
		private TableColumn<TransformationEntry, String> quantifierTagColumn() {
			TableColumn<TransformationEntry, String> quantifierTagColumn = new TableColumn<TransformationEntry, String>("QuantifierTag");
			quantifierTagColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry,String>("quantifierTag"));
			quantifierTagColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			return quantifierTagColumn;
		}
		private TableColumn<TransformationEntry, BaseIngredient> productColumn() {
			TableColumn<TransformationEntry, BaseIngredient> productColumn = new TableColumn<TransformationEntry, BaseIngredient>("Product");
			productColumn.setCellValueFactory(new PropertyValueFactory<TransformationEntry,BaseIngredient>("product"));
			productColumn.setCellFactory(ComboBoxTableCell.forTableColumn(ingredients));
			return productColumn;
		}
		
		private class RegexIdsCell extends TableCell<TransformationEntry, ObservableList<String>> {
			
			private ObservableList<String> regexIds;

			
			public RegexIdsCell(ObservableList<String> regexIds) {
				this.regexIds = regexIds;
			}
						
			@Override
			public void updateItem(ObservableList<String> refIds, boolean empty) {
				super.updateItem(refIds, empty);
				
				ListView<String> refIdsView = new ListView<String>();
				
				refIdsView.setCellFactory(ComboBoxListCell.forListView(regexIds));
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
				removeTransformation.disableProperty().bind(refIdsView.getSelectionModel().selectedItemProperty().isNull());
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
		}
		private class IngredientsCell extends TableCell<TransformationEntry, ObservableList<BaseIngredient>> {
			
			private ObservableList<BaseIngredient> ingredients;
			
			public IngredientsCell(ObservableList<BaseIngredient> ingredients) {
				this.ingredients = ingredients;
			}
			
			
			@Override
			public void updateItem(ObservableList<BaseIngredient> ingredients, boolean empty) {
				super.updateItem(ingredients, empty);

				ListView<BaseIngredient> ingredientsView = new ListView<BaseIngredient>();
				ingredientsView.setCellFactory(ComboBoxListCell.forListView(this.ingredients));
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
				removeTransformation.disableProperty().bind(ingredientsView.getSelectionModel().selectedItemProperty().isNull());
				removeTransformation.setOnAction(e -> ingredients.remove(ingredientsView.getSelectionModel().getSelectedItem()));
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
		}
	}
	private class ImpliedToolsCell extends TableCell<CookingActionEntry, ObservableList<BaseTool>> {

		ObservableList<BaseTool> tools;
		
		public ImpliedToolsCell(ObservableList<BaseTool> tools) {
			this.tools = tools;
		}
		
		@Override
		public void updateItem(ObservableList<BaseTool> tools, boolean empty) {
			super.updateItem(tools, empty);
			ListView<BaseTool> toolsView = new ListView<BaseTool>();
			toolsView.setCellFactory(ComboBoxListCell.forListView(this.tools));
			toolsView.setMinHeight(0);
			toolsView.setPrefHeight(50);
			toolsView.setEditable(true);

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

			setGraphic(toolsView);
		}
	}
}