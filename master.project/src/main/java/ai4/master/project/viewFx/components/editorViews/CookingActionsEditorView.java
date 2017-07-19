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
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;
import ai4.master.project.viewFx.components.editorViews.entries.CookingActionEntry;
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

public class CookingActionsEditorView extends EditorView {

	private ObjectProperty<KeyWordDatabase> kwdb;
	private TableView<CookingActionEntry> tableView;

	@SuppressWarnings("unchecked")
	public CookingActionsEditorView(ObservableList<BaseCookingAction> cookingActions,
			ObservableList<BaseIngredient> ingredients, ObservableList<BaseTool> tools,
			ObjectProperty<KeyWordDatabase> kwdb) {
		Map<Object, ObservableList<String>> regexIdMap = new HashMap<Object, ObservableList<String>>();

		this.kwdb = kwdb;

		setSpacing(10);

		/*
		 * Edit CookingActions
		 */

		tableView = new TableView<CookingActionEntry>();
		tableView.setEditable(true);
		tableView.getColumns().addAll(nameColumn(), synoymsColumn(), regexColumn(regexIdMap),
				transformationsColumn(regexIdMap, ingredients), toolsColumn(tools));

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
		TableColumn<CookingActionEntry, ObservableList<String>> synoymsColumn = new TableColumn<CookingActionEntry, ObservableList<String>>(
				"Synonyme");
		synoymsColumn
				.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<String>>("synonyms"));
		synoymsColumn.setCellFactory(column -> new SynonymsCell());

		return synoymsColumn;
	}

	private TableColumn<CookingActionEntry, ObservableList<Regex>> regexColumn(
			Map<Object, ObservableList<String>> regexIdMap) {
		TableColumn<CookingActionEntry, ObservableList<Regex>> regexColumn = new TableColumn<CookingActionEntry, ObservableList<Regex>>(
				"Regex");
		regexColumn.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<Regex>>("regex"));
		regexColumn.setCellFactory(column -> new RegexCell(regexIdMap));

		return regexColumn;
	}

	private TableColumn<CookingActionEntry, ObservableList<Transformation>> transformationsColumn(
			Map<Object, ObservableList<String>> regexIdMap, ObservableList<BaseIngredient> ingredients) {
		TableColumn<CookingActionEntry, ObservableList<Transformation>> transformationsColumn = new TableColumn<CookingActionEntry, ObservableList<Transformation>>(
				"Transformations");
		transformationsColumn.setCellValueFactory(
				new PropertyValueFactory<CookingActionEntry, ObservableList<Transformation>>("transformations"));
		transformationsColumn.setCellFactory(column -> new TransformationCell(regexIdMap));
		return transformationsColumn;
	}

	private TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn(ObservableList<BaseTool> tools) {
		TableColumn<CookingActionEntry, ObservableList<BaseTool>> toolsColumn = new TableColumn<CookingActionEntry, ObservableList<BaseTool>>(
				"Implied Tools");
		toolsColumn
				.setCellValueFactory(new PropertyValueFactory<CookingActionEntry, ObservableList<BaseTool>>("tools"));
		toolsColumn.setCellFactory(column -> new ImpliedToolsCell(tools));
		return toolsColumn;
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

			addSynonym.setOnAction(e -> synonyms.add("synonym" + synonyms.size()));
			MenuItem removeSynonym = new MenuItem("Remove Synonym");
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
			editRegex.disableProperty().bind(regexListView.getSelectionModel().selectedItemProperty().isNull());
			editRegex.setOnAction(e -> {
				showRegexEditorDialog(regexListView.getSelectionModel().getSelectedItem(), regexIdMap);
				regexListView.refresh();
			});
			MenuItem moveRegexUp = new MenuItem("MoveUp");
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
			addRegex.setOnAction(e -> {
				Regex regex = new Regex(".*", Result.ALL);
				showRegexEditorDialog(regex, regexIdMap);
				regexList.add(regex);
			});
			MenuItem removeRegex = new MenuItem("Remove Regex");
			removeRegex.disableProperty().bind(regexListView.getSelectionModel().selectedItemProperty().isNull());
			removeRegex.setOnAction(e -> regexList.remove(regexListView.getSelectionModel().getSelectedIndex()));
			regexListView.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					regexTableCM.show(regexListView, e.getScreenX(), e.getScreenY());
				} else {
					regexTableCM.hide();

					if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
						showRegexEditorDialog(regexListView.getSelectionModel().getSelectedItem(), regexIdMap);
						regexListView.refresh();
					}
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
			MenuItem addTransformation = new MenuItem("Add new Transformation");
			addTransformation.setOnAction(e -> {
				Transformation transformation = new Transformation();
				transformations.add(transformation);
			});
			MenuItem removeTransformation = new MenuItem("Remove Transformation");
			removeTransformation.disableProperty().bind(transformationsListView.getSelectionModel().selectedItemProperty().isNull());
			removeTransformation.setOnAction(e -> transformations.remove(transformationsListView.getSelectionModel().getSelectedIndex()));
			transformationsListView.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					transformationsTableCM.show(transformationsListView, e.getScreenX(), e.getScreenY());
				} else {
					transformationsTableCM.hide();
					if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
						showTransfomrationEditorDialog(transformationsListView.getSelectionModel().getSelectedItem(), regexIdMap);
					}
				}
			});
			transformationsTableCM.getItems().addAll(
					addTransformation,
					removeTransformation
			);
			
			setGraphic(transformationsListView);
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

	private static ObjectProperty<Regex> regex;
	private static ObjectProperty<Transformation> transformation;
	private static ListProperty<String> regexIdsList;
	private static ListProperty<BaseIngredient> ingreidensList;
	private static Dialog<ButtonType> regexEditorDlg;
	private static Dialog<ButtonType> transformationEditorDlg;

	static {
		regex = new SimpleObjectProperty<Regex>();
		transformation = new SimpleObjectProperty<Transformation>();
		regexIdsList = new SimpleListProperty<String>();
		regexEditorDlg = new Dialog<ButtonType>();
		transformationEditorDlg = new Dialog<ButtonType>();
		
		initRegexEditorPane();
		initTransformationEditorPane();
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
		ListView<BaseIngredient> mandatoryIngredientsListView = new ListView<BaseIngredient>();
		mandatoryIngredientsListView.setMinHeight(0);
		mandatoryIngredientsListView.setPrefHeight(100);
		ComboBox<BaseIngredient> productCB = new ComboBox<BaseIngredient>();
		productCB.setItems(ingreidensList);
		TextField ingredientTagTF = new TextField();
		TextField quantifierTagTF = new TextField();
		
		ListChangeListener<String> refIdsChanged = change -> {
			
		};
		refIdsListView.getItems().addListener(refIdsChanged);
		ListChangeListener<BaseIngredient> mandatoryIngredientsChanged = change -> {
			
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
			} else {
				transformation.get().setTag(null);	
			}
		});
		quantifierTagTF.textProperty().addListener((b, o, n) -> {
			if(n != null && n.length() != 0) {
				productCB.getSelectionModel().select(null);
				ingredientTagTF.setText(null);
				transformation.get().setTag(new QuantifierTag(n));
			} else {
				transformation.get().setTag(null);	
			}
		});

		transformation.addListener((b, o, n) -> {
			if (n != null) {
				refIdsListView.getItems().setAll(n.getRegexIds());
				mandatoryIngredientsListView.getItems().clear();
				n.getMandatoryIngredients().forEach(i -> mandatoryIngredientsListView.getItems().add(i.getBaseObject()));
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
}