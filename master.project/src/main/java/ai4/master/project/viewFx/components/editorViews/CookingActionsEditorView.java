package ai4.master.project.viewFx.components.editorViews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.ItemGroup;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Regex.Result;
import ai4.master.project.recipe.baseObject.Transformation;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;
import ai4.master.project.viewFx.components.editorViews.entries.CookingActionEntry;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CookingActionsEditorView extends EditorView {

	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<CookingActionEntry> tableView;

	@SuppressWarnings("unchecked")
	public CookingActionsEditorView(ObservableList<BaseCookingAction> cookingActions,
			ObservableList<BaseIngredient> ingredients, ObservableList<BaseTool> tools,
			ObjectProperty<KeyWordDatabase> kwdb) {
		Map<Object, ObservableList<String>> regexIdMap = new HashMap<Object, ObservableList<String>>();

		this.kwdb = kwdb;
		ingredientsList.set(ingredients);
		toolsList.set(tools);

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
				toolsColumn(tools),
				ingredientsColumn(ingredients)
		);
		VBox.setVgrow(tableView, Priority.ALWAYS);

		/*
		 * Context Menu
		 */

		ContextMenu cookingActionTableCM = new ContextMenu();
		MenuItem removeCookingAction = new MenuItem("Remove CookingAction");
		removeCookingAction.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
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
			for (BaseCookingAction cookingAction : cookingActions) {
				tableView.getItems().add(new CookingActionEntry(cookingAction, cookingActions, regexIdMap));
			}
		};
		cookingActions.addListener(cookingActionsChanged);

		/*
		 * Add Content to View
		 */

		getChildren().addAll(tableView);
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
		transformationsColumn.setCellFactory(column -> new TransformationCell(regexIdMap));
		return transformationsColumn;
	}
	private TableColumn<CookingActionEntry, ObservableList<ItemGroup<BaseTool, Tool>>> toolsColumn(ObservableList<BaseTool> tools) {
		TableColumn<CookingActionEntry, ObservableList<ItemGroup<BaseTool, Tool>>> toolsColumn = new TableColumn<CookingActionEntry, ObservableList<ItemGroup<BaseTool, Tool>>>("Implied Tools");
		toolsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<ItemGroup<BaseTool, Tool>>>("tools"));
		toolsColumn.setCellFactory(column -> new ImpliedToolsCell());
		return toolsColumn;
	}
	private TableColumn<CookingActionEntry, ObservableList<ItemGroup<BaseIngredient, Ingredient>>> ingredientsColumn(ObservableList<BaseIngredient> ingredients) {
		TableColumn<CookingActionEntry, ObservableList<ItemGroup<BaseIngredient, Ingredient>>> ingredientsColumn = new TableColumn<CookingActionEntry, ObservableList<ItemGroup<BaseIngredient, Ingredient>>>("Implied Ingredients");
		ingredientsColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<ItemGroup<BaseIngredient, Ingredient>>>("ingredients"));
		ingredientsColumn.setCellFactory(column -> new ImpliedIngredientsCell());
		return ingredientsColumn;
	}

	public boolean contains(String word) {
		return kwdb.get().findCookingAction(word) != null;
	}
	public void scrollTo(String word) {
		for (CookingActionEntry entry : tableView.getItems()) {
			if (entry.getName().equals(word) || entry.getSynonyms().contains(word)) {
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
			addSynonym.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));


			addSynonym.setOnAction(e -> synonyms.add("synonym" + synonyms.size()));
			MenuItem removeSynonym = new MenuItem("Remove Synonym");
			removeSynonym.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
			removeSynonym.disableProperty().bind(synonymsView.getSelectionModel().selectedItemProperty().isNull());
			removeSynonym.setOnAction(
					e -> synonymsView.getItems().remove(synonymsView.getSelectionModel().getSelectedIndex()));
			synonymsViewCM.getItems().addAll(addSynonym, removeSynonym);
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

		@Override
		public void updateItem(ObservableList<Regex> regexList, boolean empty) {
			super.updateItem(regexList, empty);

			ListView<Regex> regexListView = new ListView<Regex>();
			regexListView.setItems(regexList);
			regexListView.setMinHeight(0);
			regexListView.setPrefHeight(50);

			ContextMenu regexTableCM = new ContextMenu();
			MenuItem editRegex = new MenuItem("Edit");
			editRegex.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
			editRegex.disableProperty().bind(regexListView.getSelectionModel().selectedItemProperty().isNull());
			editRegex.setOnAction(e -> {
				showRegexEditorDialog(regexListView.getSelectionModel().getSelectedItem(), regexIdMap);
				regexListView.refresh();
			});
			MenuItem moveRegexUp = new MenuItem("MoveUp");
			moveRegexUp.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
			moveRegexUp.disableProperty().bind(
					regexListView.getSelectionModel().selectedItemProperty().isNull()
				.or(regexListView.getSelectionModel().selectedIndexProperty().isEqualTo(0))
			);
			moveRegexUp.setOnAction(e -> {
				Regex regex = regexListView.getSelectionModel().getSelectedItem();
				int index = regexListView.getItems().indexOf(regex);
				
				regexListView.getItems().set(index, regexListView.getItems().get(index - 1));
				regexListView.getItems().set(index - 1, regex);
			});
			MenuItem moveRegexDown = new MenuItem("MoveDown");
			moveRegexDown.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
			moveRegexDown.disableProperty().bind(
					regexListView.getSelectionModel().selectedItemProperty().isNull()
				.or(regexListView.getSelectionModel().selectedIndexProperty().isEqualTo(
						new SimpleListProperty<Regex>(regexListView.getItems()).sizeProperty().subtract(1)))
			);
			moveRegexDown.setOnAction(e -> {
				Regex regex = regexListView.getSelectionModel().getSelectedItem();
				int index = regexListView.getItems().indexOf(regex);
				
				regexListView.getItems().set(index, regexListView.getItems().get(index + 1));
				regexListView.getItems().set(index + 1, regex);
			});
			MenuItem addRegex = new MenuItem("Add new Regex");
			addRegex.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
			addRegex.setOnAction(e -> {
				Regex regex = new Regex(".*", Result.ALL);
				showRegexEditorDialog(regex, regexIdMap);
				regexList.add(regex);
			});
			MenuItem removeRegex = new MenuItem("Remove Regex");
			removeRegex.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
			removeRegex.disableProperty().bind(regexListView.getSelectionModel().selectedItemProperty().isNull());
			removeRegex.setOnAction(e -> regexList.remove(regexListView.getSelectionModel().getSelectedIndex()));
			regexListView.setOnMouseClicked(e -> {
				regexTableCM.hide();
				if (e.getButton() == MouseButton.SECONDARY) {
					regexTableCM.show(regexListView, e.getScreenX(), e.getScreenY());
				} else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && regexListView.getSelectionModel().getSelectedItem() != null) {
					showRegexEditorDialog(regexListView.getSelectionModel().getSelectedItem(), regexIdMap);
					regexListView.refresh();
				}
			});
			regexTableCM.getItems().addAll(
					editRegex,
					moveRegexUp,
					moveRegexDown,
					addRegex, 
					removeRegex
			);


			setGraphic(regexListView);
		}
	}
	private class TransformationCell extends TableCell<CookingActionEntry, ObservableList<Transformation>> {

		private Map<Object, ObservableList<String>> regexIdMap;

		public TransformationCell(Map<Object, ObservableList<String>> regexIdMap) {
			this.regexIdMap = regexIdMap;
		}

		@Override
		public void updateItem(ObservableList<Transformation> transformations, boolean empty) {
			super.updateItem(transformations, empty);

			ListView<Transformation> transformationsListView = new ListView<Transformation>();
			transformationsListView.setItems(transformations);
			transformationsListView.setPrefHeight(100);
			transformationsListView.setMinHeight(0);

			ContextMenu transformationsTableCM = new ContextMenu();
			MenuItem editTransformation = new MenuItem("Edit Transformation");
			editTransformation.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
			editTransformation.disableProperty().bind(transformationsListView.getSelectionModel().selectedItemProperty().isNull());
			editTransformation.setOnAction(e -> {
				showTransfomrationEditorDialog(transformationsListView.getSelectionModel().getSelectedItem(), regexIdMap);
			});
			MenuItem moveTransformationUp = new MenuItem("MoveUp");
			moveTransformationUp.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
			moveTransformationUp.disableProperty().bind(
					transformationsListView.getSelectionModel().selectedItemProperty().isNull()
				.or(transformationsListView.getSelectionModel().selectedIndexProperty().isEqualTo(0))
			);
			moveTransformationUp.setOnAction(e -> {
				Transformation transformation = transformationsListView.getSelectionModel().getSelectedItem();
				int index = transformationsListView.getItems().indexOf(transformation);
				
				transformationsListView.getItems().set(index, transformationsListView.getItems().get(index - 1));
				transformationsListView.getItems().set(index - 1, transformation);
			});
			MenuItem moveTransformationDown = new MenuItem("MoveDown");
			moveTransformationDown.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
			moveTransformationDown.disableProperty().bind(
					transformationsListView.getSelectionModel().selectedItemProperty().isNull()
				.or(transformationsListView.getSelectionModel().selectedIndexProperty().isEqualTo(
						new SimpleListProperty<Transformation>(transformationsListView.getItems()).sizeProperty().subtract(1)))
			);
			moveTransformationDown.setOnAction(e -> {
				Transformation transformation = transformationsListView.getSelectionModel().getSelectedItem();
				int index = transformationsListView.getItems().indexOf(transformation);
				
				transformationsListView.getItems().set(index, transformationsListView.getItems().get(index + 1));
				transformationsListView.getItems().set(index + 1, transformation);
			});
			MenuItem addTransformation = new MenuItem("Add new Transformation");
			addTransformation.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
			addTransformation.setOnAction(e -> {
				Transformation transformation = new Transformation();
				transformations.add(transformation);
			});
			MenuItem removeTransformation = new MenuItem("Remove Transformation");
			removeTransformation.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));

			removeTransformation.disableProperty().bind(transformationsListView.getSelectionModel().selectedItemProperty().isNull());
			removeTransformation.setOnAction(e -> transformations.remove(transformationsListView.getSelectionModel().getSelectedIndex()));
			transformationsListView.setOnMouseClicked(e -> {
				transformationsTableCM.hide();
				if (e.getButton() == MouseButton.SECONDARY) {
					transformationsTableCM.show(transformationsListView, e.getScreenX(), e.getScreenY());
				} else {
					if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && transformationsListView.getSelectionModel().getSelectedItem() != null) {
						showTransfomrationEditorDialog(transformationsListView.getSelectionModel().getSelectedItem(), regexIdMap);
						transformationsListView.refresh();
					}
				}
			});
			transformationsTableCM.getItems().addAll(
					editTransformation,
					moveTransformationUp,
					moveTransformationDown,
					addTransformation,
					removeTransformation
			);
			
			setGraphic(transformationsListView);
		}
	}
	private class ImpliedToolsCell extends TableCell<CookingActionEntry, ObservableList<ItemGroup<BaseTool, Tool>>> {
		@Override
		public void updateItem(ObservableList<ItemGroup<BaseTool, Tool>> toolGroups, boolean empty) {
			super.updateItem(toolGroups, empty);
			ListView<ItemGroup<BaseTool, Tool>> toolsView = new ListView<ItemGroup<BaseTool, Tool>>();
			toolsView.setMinHeight(0);
			toolsView.setPrefHeight(50);

			ContextMenu toolsViewCM = new ContextMenu();
			MenuItem editTool = new MenuItem("Edit ToolGroup");
			editTool.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
			editTool.disableProperty().bind(toolsView.getSelectionModel().selectedItemProperty().isNull());
			editTool.setOnAction(e -> {
				showImpliedToolsEditorDialog(toolsView.getSelectionModel().getSelectedItem());
			});
			MenuItem addTool = new MenuItem("Add new ToolGroup");
			addTool.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
			addTool.setOnAction(e -> {
				toolGroups.add(new ItemGroup<BaseTool, Tool>());
				toolsView.requestFocus();
			});
			MenuItem removeTool = new MenuItem("Remove Tool");
			removeTool.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
			removeTool.disableProperty().bind(toolsView.getSelectionModel().selectedItemProperty().isNull());
			removeTool.setOnAction(e -> {
				toolGroups.remove(toolsView.getSelectionModel().getSelectedItem());
				toolsView.requestFocus();
			});

			toolsViewCM.getItems().addAll(
					editTool,
					addTool,
					removeTool
			);
			toolsView.setOnMouseClicked(e -> {
				toolsViewCM.hide();
				if (e.getButton() == MouseButton.SECONDARY) {
					toolsViewCM.show(toolsView, e.getScreenX(), e.getScreenY());
				} else if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && toolsView.getSelectionModel().getSelectedItem() != null) {
					showImpliedToolsEditorDialog(toolsView.getSelectionModel().getSelectedItem());
					toolsView.refresh();
				}
			});

			if (toolGroups != null) {
				toolsView.setItems(toolGroups);
			}

			setGraphic(toolsView);
		}
	}
	private class ImpliedIngredientsCell extends TableCell<CookingActionEntry, ObservableList<ItemGroup<BaseIngredient, Ingredient>>> {
		@Override
		public void updateItem(ObservableList<ItemGroup<BaseIngredient, Ingredient>> ingredientGroups, boolean empty) {
			super.updateItem(ingredientGroups, empty);
			ListView<ItemGroup<BaseIngredient, Ingredient>> ingredientsView = new ListView<ItemGroup<BaseIngredient, Ingredient>>();
			ingredientsView.setMinHeight(0);
			ingredientsView.setPrefHeight(50);

			ContextMenu ingredientsViewCM = new ContextMenu();
			MenuItem editIngredient = new MenuItem("Edit IngredientGroup");
			editIngredient.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
			editIngredient.disableProperty().bind(ingredientsView.getSelectionModel().selectedItemProperty().isNull());
			editIngredient.setOnAction(e -> {
				showImpliedIngredientsEditorDialog(ingredientsView.getSelectionModel().getSelectedItem());
			});
			MenuItem addIngredient = new MenuItem("Add new IngredientGroup");
			addIngredient.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
			addIngredient.setOnAction(e -> {
				ingredientGroups.add(new ItemGroup<BaseIngredient, Ingredient>());
				ingredientsView.requestFocus();
			});
			MenuItem removeIngredient = new MenuItem("Remove Ingredient");
			removeIngredient.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
			removeIngredient.disableProperty().bind(ingredientsView.getSelectionModel().selectedItemProperty().isNull());
			removeIngredient.setOnAction(e -> {
				ingredientGroups.remove(ingredientsView.getSelectionModel().getSelectedItem());
				ingredientsView.requestFocus();
			});

			ingredientsViewCM.getItems().addAll(
					editIngredient,
					addIngredient,
					removeIngredient
			);
			ingredientsView.setOnMouseClicked(e -> {
				ingredientsViewCM.hide();
				if (e.getButton() == MouseButton.SECONDARY) {
					ingredientsViewCM.show(ingredientsView, e.getScreenX(), e.getScreenY());
				} else if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && ingredientsView.getSelectionModel().getSelectedItem() != null) {
					showImpliedIngredientsEditorDialog(ingredientsView.getSelectionModel().getSelectedItem());
					ingredientsView.refresh();
				}
			});

			if (ingredientGroups != null) {
				ingredientsView.setItems(ingredientGroups);
			}

			setGraphic(ingredientsView);
		}
	}

	
	private static ObjectProperty<Regex> regex;
	private static ObjectProperty<Transformation> transformation;
	private static ObjectProperty<ItemGroup<BaseTool, Tool>> toolGroup;
	private static ObjectProperty<ItemGroup<BaseIngredient, Ingredient>> ingredientGroup;
	private static ListProperty<String> regexIdsList;
	private static ListProperty<BaseIngredient> ingredientsList;
	private static ListProperty<BaseTool> toolsList;
	private static Dialog<ButtonType> regexEditorDlg;
	private static Dialog<ButtonType> transformationEditorDlg;
	private static Dialog<ButtonType> impliedToolsEditorDlg;
	private static Dialog<ButtonType> impliedIngredientsEditorDlg;

	static {
		regex = new SimpleObjectProperty<Regex>();
		transformation = new SimpleObjectProperty<Transformation>();
		toolGroup = new SimpleObjectProperty<ItemGroup<BaseTool, Tool>>();
		ingredientGroup = new SimpleObjectProperty<ItemGroup<BaseIngredient, Ingredient>>();
		regexIdsList = new SimpleListProperty<String>();
		ingredientsList = new SimpleListProperty<BaseIngredient>();
		toolsList = new SimpleListProperty<BaseTool>();
		
		regexEditorDlg = new Dialog<ButtonType>();
		transformationEditorDlg = new Dialog<ButtonType>();
		impliedToolsEditorDlg = new Dialog<ButtonType>();
		impliedIngredientsEditorDlg = new Dialog<ButtonType>();
		
		initRegexEditorPane();
		initTransformationEditorPane();
		initImpliedToolsEditorPane();
		initImpliedIngredientsEditorPane();
	}
	private static void initRegexEditorPane() {
		regexEditorDlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		GridPane contentPane = new GridPane();
		contentPane.setHgap(10);
		contentPane.setVgap(5);

		TextField idTF = new TextField();
		TextField expressionTF = new TextField();
		expressionTF.setPrefWidth(400);
		ComboBox<Regex.Result> resultCB = new ComboBox<Regex.Result>();
		resultCB.setItems(FXCollections.observableArrayList(Regex.Result.values()));
		CheckBox ingredientsNeededCB = new CheckBox();
		CheckBox referencePreviousProductsCB = new CheckBox();
		CheckBox chargeToolsCB = new CheckBox();

		idTF.textProperty().addListener((b, o, n) -> {
			if(o != null) {
				regexIdsList.remove(o);
			}
			if(n != null && n.length() != 0) {
				regexIdsList.add(n);
			}
			
			regex.get().setId(n);
		});
		expressionTF.textProperty().addListener((b, o, n) -> regex.get().setExpression(n));
		resultCB.valueProperty().addListener((b, o, n) -> regex.get().setResult(n));
		ingredientsNeededCB.selectedProperty().addListener((b, o, n) -> regex.get().setIngredientsNeeded(n));
		referencePreviousProductsCB.selectedProperty().addListener((b, o, n) -> regex.get().setReferencePreviousProducts(n));
		chargeToolsCB.selectedProperty().addListener((b, o, n) -> regex.get().setChargingTools(n));

		regex.addListener((b, o, n) -> {
			if (n != null) {
				idTF.setText(n.getId());
				expressionTF.setText(n.getExpression());
				resultCB.setValue(n.getResult());
				ingredientsNeededCB.setSelected(n.isIngredientsNeeded());
				referencePreviousProductsCB.setSelected(n.isReferencePreviousProducts());
				chargeToolsCB.setSelected(n.isChargingTools());
			}
		});

		contentPane.add(new Label("Id:"), 0, 0);
		contentPane.add(idTF, 1, 0);
		contentPane.add(new Label("Expression:"), 0, 1);
		contentPane.add(expressionTF, 1, 1);
		contentPane.add(new Label("Result:"), 0, 2);
		contentPane.add(resultCB, 1, 2);
		contentPane.add(new Label("Ingredients needed:"), 0, 3);
		contentPane.add(ingredientsNeededCB, 1, 3);
		contentPane.add(new Label("Ref. previous products:"), 0, 4);
		contentPane.add(referencePreviousProductsCB, 1, 4);
		contentPane.add(new Label("Charge tools:"), 0, 5);
		contentPane.add(chargeToolsCB, 1, 5);

		regexEditorDlg.getDialogPane().setContent(contentPane);
	}
	private static void initTransformationEditorPane() {
		transformationEditorDlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		GridPane contentPane = new GridPane();
		contentPane.setHgap(10);
		contentPane.setVgap(5);

		ListView<String> refIdsListView = new ListView<String>();
		refIdsListView.setEditable(true);
		refIdsListView.setMinHeight(0);
		refIdsListView.setPrefHeight(100);
		refIdsListView.setCellFactory(ComboBoxListCell.forListView(regexIdsList));
		ContextMenu rcm = new ContextMenu();
		MenuItem addRef = new MenuItem("Add Regex-ID reference");
		addRef.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
		addRef.setOnAction(e -> refIdsListView.getItems().add(null));
		MenuItem removeRef = new MenuItem("Remove Regex-ID reference");
		removeRef.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
		removeRef.disableProperty().bind(refIdsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
		removeRef.setOnAction(e -> refIdsListView.getItems().remove(refIdsListView.getSelectionModel().getSelectedItem()));
		rcm.getItems().addAll(
				addRef,
				removeRef
		);
		refIdsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.SECONDARY) {
				rcm.show(refIdsListView, e.getScreenX(), e.getScreenY());
			} else {
				rcm.hide();
			}
		});
		ListView<BaseIngredient> mandatoryIngredientsListView = new ListView<BaseIngredient>();
		mandatoryIngredientsListView.setEditable(true);
		mandatoryIngredientsListView.setMinHeight(0);
		mandatoryIngredientsListView.setPrefHeight(100);
		mandatoryIngredientsListView.setCellFactory(ComboBoxListCell.forListView(ingredientsList));
		ContextMenu icm = new ContextMenu();
		MenuItem addIngredient = new MenuItem("Add mandatory Ingredient");
		addIngredient.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
		addIngredient.setOnAction(e -> mandatoryIngredientsListView.getItems().add(null));
		MenuItem removeIngredient = new MenuItem("Remove mandatory Ingredient");
		removeIngredient.disableProperty().bind(mandatoryIngredientsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
		removeIngredient.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
		removeIngredient.setOnAction(e -> mandatoryIngredientsListView.getItems().remove(mandatoryIngredientsListView.getSelectionModel().getSelectedItem()));
		icm.getItems().addAll(
				addIngredient,
				removeIngredient
		);
		mandatoryIngredientsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.SECONDARY) {
				icm.show(refIdsListView, e.getScreenX(), e.getScreenY());
			} else {
				icm.hide();
			}
		});
		ComboBox<BaseIngredient> productCB = new ComboBox<BaseIngredient>();
		productCB.setItems(ingredientsList);
		TextField ingredientTagTF = new TextField();
		TextField quantifierTagTF = new TextField();
		
		ListChangeListener<String> refIdsChanged = change -> {
			transformation.get().getRegexIds().clear();
			transformation.get().getRegexIds().addAll(refIdsListView.getItems());
		};
		refIdsListView.getItems().addListener(refIdsChanged);
		ListChangeListener<BaseIngredient> mandatoryIngredientsChanged = change -> {
			transformation.get().getMandatoryIngredients().clear();
			mandatoryIngredientsListView.getItems().forEach(bI -> {
				if(bI != null) {
					transformation.get().getMandatoryIngredients().add(bI.toObject());
				}
			});
		};
		refIdsListView.getItems().addListener(refIdsChanged);
		mandatoryIngredientsListView.getItems().addListener(mandatoryIngredientsChanged);
		productCB.valueProperty().addListener((b, o, n) -> {
			if(n != null) {
				ingredientTagTF.setText(null);
				quantifierTagTF.setText(null);
				transformation.get().setProduct(n.toObject());
			} else {
				transformation.get().setProduct(null);	
			}
		});
		ingredientTagTF.textProperty().addListener((b, o, n) -> {
			if(n != null && n.length() != 0) {
				productCB.getSelectionModel().select(null);
				quantifierTagTF.setText(null);
				transformation.get().setTag(new IngredientTag(n));
			}
		});
		quantifierTagTF.textProperty().addListener((b, o, n) -> {
			if(n != null && n.length() != 0) {
				productCB.getSelectionModel().select(null);
				ingredientTagTF.setText(null);
				transformation.get().setTag(new QuantifierTag(n));
			}
		});

		transformation.addListener((b, o, n) -> {
			if (n != null) {
				refIdsListView.getItems().setAll(n.getRegexIds());
				List<BaseIngredient> manIngredients = new ArrayList<BaseIngredient>();
				n.getMandatoryIngredients().forEach(i -> {
					manIngredients.add(i.getBaseObject());
				});
				mandatoryIngredientsListView.getItems().setAll(manIngredients);
				ingredientTagTF.setText(null);
				quantifierTagTF.setText(null);
				if(n.getTag() != null) {
					if(n.getTag() instanceof QuantifierTag) {
						quantifierTagTF.setText(n.getTag().getName());
					} else {
						ingredientTagTF.setText(n.getTag().getName());
					}
				}
				if(n.getProduct() != null) {
					productCB.setValue(n.getProduct().getBaseObject());
				} else {
					productCB.setValue(null);
				}
			}
		});

		contentPane.add(new Label("Ref. Ids:"), 0, 0);
		contentPane.add(refIdsListView, 1, 0);
		contentPane.add(new Label("Mandatory Ingredients:"), 0, 1);
		contentPane.add(mandatoryIngredientsListView, 1, 1);
		contentPane.add(new Label("Product:"), 0, 2);
		contentPane.add(productCB, 1, 2);
		contentPane.add(new Label("Ingredient Tag:"), 0, 3);
		contentPane.add(ingredientTagTF, 1, 3);
		contentPane.add(new Label("Quantifier Tag:"), 0, 4);
		contentPane.add(quantifierTagTF, 1, 4);

		transformationEditorDlg.getDialogPane().setContent(contentPane);
	}
	private static void initImpliedToolsEditorPane() {
		impliedToolsEditorDlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		HBox contentPane = new HBox();
		contentPane.setSpacing(10);

		ListView<BaseTool> toolsListView = new ListView<BaseTool>();
		toolsListView.setEditable(true);
		toolsListView.setMinHeight(0);
		toolsListView.setPrefHeight(100);
		toolsListView.setCellFactory(ComboBoxListCell.forListView(toolsList));
		
		ContextMenu cm = new ContextMenu();
		MenuItem moveToolItem = new MenuItem("Move to top");
		moveToolItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
		moveToolItem.disableProperty().bind(toolsListView.getSelectionModel().selectedIndexProperty().lessThan(1));
		moveToolItem.setOnAction(e -> toolsListView.getItems().set(
				toolsListView.getSelectionModel().getSelectedIndex(), 
				toolsListView.getItems().set(
						0, 
						toolsListView.getSelectionModel().getSelectedItem()
				)
		));
		MenuItem addToolItem = new MenuItem("Add Tool");
		addToolItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
		addToolItem.setOnAction(e -> toolsListView.getItems().add(null));
		MenuItem removeToolItem = new MenuItem("Remove Tool");
		removeToolItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
		removeToolItem.disableProperty().bind(toolsListView.getSelectionModel().selectedItemProperty().isNull());
		removeToolItem.setOnAction(e -> toolsListView.getItems().remove(toolsListView.getSelectionModel().getSelectedItem()));
		cm.getItems().addAll(
				moveToolItem,
				addToolItem,
				removeToolItem
		);
		toolsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.SECONDARY) {
				cm.show(toolsListView, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		ListChangeListener<BaseTool> toolsChanged = change -> {
			toolGroup.get().getItems().clear();
			toolGroup.get().getItems().addAll(toolsListView.getItems());			
		};
		toolsListView.getItems().addListener(toolsChanged);

		contentPane.getChildren().addAll(
				toolsListView
		);
		
		toolGroup.addListener((b, o, n) -> toolsListView.getItems().setAll(n.getItems()));
		
		impliedToolsEditorDlg.getDialogPane().setContent(contentPane);
	}
	private static void initImpliedIngredientsEditorPane() {
		impliedIngredientsEditorDlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		HBox contentPane = new HBox();
		contentPane.setSpacing(10);

		ListView<BaseIngredient> ingredientsListView = new ListView<BaseIngredient>();
		ingredientsListView.setEditable(true);
		ingredientsListView.setMinHeight(0);
		ingredientsListView.setPrefHeight(100);
		ingredientsListView.setCellFactory(ComboBoxListCell.forListView(ingredientsList));
		
		ContextMenu cm = new ContextMenu();
		MenuItem moveIngredientItem = new MenuItem("Move to top");
		moveIngredientItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
		moveIngredientItem.disableProperty().bind(ingredientsListView.getSelectionModel().selectedIndexProperty().lessThan(1));
		moveIngredientItem.setOnAction(e -> ingredientsListView.getItems().set(
				ingredientsListView.getSelectionModel().getSelectedIndex(), 
				ingredientsListView.getItems().set(
						0, 
						ingredientsListView.getSelectionModel().getSelectedItem()
				)
		));
		MenuItem addIngredientItem = new MenuItem("Add Ingredient");
		addIngredientItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
		addIngredientItem.setOnAction(e -> ingredientsListView.getItems().add(null));
		MenuItem removeIngredientItem = new MenuItem("Remove Ingredient");
		removeIngredientItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
		removeIngredientItem.disableProperty().bind(ingredientsListView.getSelectionModel().selectedItemProperty().isNull());
		removeIngredientItem.setOnAction(e -> ingredientsListView.getItems().remove(ingredientsListView.getSelectionModel().getSelectedItem()));
		cm.getItems().addAll(
				moveIngredientItem,
				addIngredientItem,
				removeIngredientItem
		);
		ingredientsListView.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.SECONDARY) {
				cm.show(ingredientsListView, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		ListChangeListener<BaseIngredient> ingredientsChanged = change -> {
			ingredientGroup.get().getItems().clear();
			ingredientGroup.get().getItems().addAll(ingredientsListView.getItems());			
		};
		ingredientsListView.getItems().addListener(ingredientsChanged);

		contentPane.getChildren().addAll(
				ingredientsListView
		);
		
		ingredientGroup.addListener((b, o, n) -> ingredientsListView.getItems().setAll(n.getItems()));
		
		impliedIngredientsEditorDlg.getDialogPane().setContent(contentPane);
	}
	
	private static void showRegexEditorDialog(Regex regex, Map<Object, ObservableList<String>>regexIdMap) {
		CookingActionsEditorView.regex.set(regex);
		CookingActionsEditorView.regexIdsList.set(regexIdMap.get(regex));
		regexEditorDlg.showAndWait();
	}
	private static void showTransfomrationEditorDialog(Transformation transformation, Map<Object, ObservableList<String>>regexIdMap) {
		CookingActionsEditorView.transformation.set(transformation);
		CookingActionsEditorView.regexIdsList.set(regexIdMap.get(transformation));
		transformationEditorDlg.showAndWait();
	}
	private static void showImpliedToolsEditorDialog(ItemGroup<BaseTool, Tool> toolGroup) {
		CookingActionsEditorView.toolGroup.set(toolGroup);
		impliedToolsEditorDlg.showAndWait();
	}
	private static void showImpliedIngredientsEditorDialog(ItemGroup<BaseIngredient, Ingredient> ingredientGroup) {
		CookingActionsEditorView.ingredientGroup.set(ingredientGroup);
		impliedIngredientsEditorDlg.showAndWait();
	}
}