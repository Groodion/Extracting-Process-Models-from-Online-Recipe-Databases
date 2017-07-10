package ai4.master.project.viewFx.components;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.viewFx.components.editorViews.CookingActionsEditorView;
import ai4.master.project.viewFx.components.editorViews.EditorView;
import ai4.master.project.viewFx.components.editorViews.EventIndicatorsEditorView;
import ai4.master.project.viewFx.components.editorViews.IngredientGroupsEditorView;
import ai4.master.project.viewFx.components.editorViews.IngredientsEditorView;
import ai4.master.project.viewFx.components.editorViews.LastSentenceReferencesEditorView;
import ai4.master.project.viewFx.components.editorViews.PartIndicatorsEditorView;
import ai4.master.project.viewFx.components.editorViews.ToolsEditorView;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LibEditor extends Dialog<KeyWordDatabase> {

	private KeyWordDatabase kwdb;
	
	private ObservableList<BaseTool> tools;
	private ObservableList<BaseIngredient> ingredients;
	private ObservableList<BaseIngredientGroup> ingredientGroups;
	private ObservableList<BaseCookingAction> cookingActions;
	private ObservableList<String> eventIndicators;
	private ObservableList<String> lastSentenceReferences;
	private ObservableList<String> partIndicators;
	private ObservableList<BaseIngredient> allIngredients;
	
	private boolean editorInitialized = false;

	private StackPane editorViewsStackPane;
	private ComboBox<String> selectEditorCB;
	
	public LibEditor(ObjectProperty<KeyWordDatabase> kwdb) {
		tools = FXCollections.observableArrayList();
		ingredients = FXCollections.observableArrayList();			
		ingredientGroups = FXCollections.observableArrayList();
		cookingActions = FXCollections.observableArrayList();
		eventIndicators = FXCollections.observableArrayList();
		lastSentenceReferences = FXCollections.observableArrayList();
		partIndicators = FXCollections.observableArrayList();
		allIngredients = FXCollections.observableArrayList();
		
		ListChangeListener<BaseTool> toolsChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getTools().removeAll(change.getRemoved());
					this.kwdb.getTools().addAll(change.getAddedSubList());
				}
			}
		};
		ListChangeListener<BaseIngredient> ingredientsChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getIngredients().removeAll(change.getRemoved());
					this.kwdb.getIngredients().addAll(change.getAddedSubList());
					allIngredients.removeAll(change.getRemoved());
					allIngredients.addAll(change.getAddedSubList());
				}
			}
		};
		ListChangeListener<BaseIngredientGroup> ingredientGroupsChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getIngredientGroups().removeAll(change.getRemoved());
					this.kwdb.getIngredientGroups().addAll(change.getAddedSubList());
					allIngredients.removeAll(change.getRemoved());
					allIngredients.addAll(change.getAddedSubList());
				}
			}
		};
		ListChangeListener<BaseCookingAction> cookingActionsChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getCookingActions().removeAll(change.getRemoved());
					this.kwdb.getCookingActions().addAll(change.getAddedSubList());
				}
			}
		};
		ListChangeListener<String> eventIndicatorsChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getEventIndicators().removeAll(change.getRemoved());
					this.kwdb.getEventIndicators().addAll(change.getAddedSubList());
				}
			}
		};
		ListChangeListener<String> lastSentenceReferencesChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getLastSentenceReferences().removeAll(change.getRemoved());
					this.kwdb.getLastSentenceReferences().addAll(change.getAddedSubList());
				}
			}
		};
		ListChangeListener<String> partIndicatorsChanged = change -> {
			if(editorInitialized && this.kwdb != null) {
				while(change.next()) {
					this.kwdb.getPartIndicators().removeAll(change.getRemoved());
					this.kwdb.getPartIndicators().addAll(change.getAddedSubList());
				}
			}
		};
		
		tools.addListener(toolsChanged);		
		ingredients.addListener(ingredientsChanged);
		ingredientGroups.addListener(ingredientGroupsChanged);
		cookingActions.addListener(cookingActionsChanged);
		eventIndicators.addListener(eventIndicatorsChanged);
		lastSentenceReferences.addListener(lastSentenceReferencesChanged);
		partIndicators.addListener(partIndicatorsChanged);
		
		setOnShowing(e -> {
			editorInitialized = false;
			
			this.kwdb = kwdb.get().clone();
			
			tools.clear();
			ingredients.clear();
			cookingActions.clear();
			eventIndicators.clear();
			lastSentenceReferences.clear();
			partIndicators.clear();
			
			tools.addAll(this.kwdb.getTools());
			ingredients.addAll(this.kwdb.getIngredients());
			ingredientGroups.addAll(this.kwdb.getIngredientGroups());
			cookingActions.addAll(this.kwdb.getCookingActions());
			eventIndicators.addAll(this.kwdb.getEventIndicators());
			lastSentenceReferences.addAll(this.kwdb.getLastSentenceReferences());
			eventIndicators.addAll(this.kwdb.getEventIndicators());
			partIndicators.addAll(this.kwdb.getPartIndicators());
						
			editorInitialized = true;
		});
		
		setResultConverter(r -> {
			if(r.getButtonData() == ButtonData.OK_DONE) {
				return this.kwdb;
			} else {
				return null;
			}
		});
		
		initDialog();
		initMainLayout(kwdb);
	}

	private void initDialog() {
		setTitle("Library Editor");
		setHeaderText("Library Editor");
		setResizable(true);
		getDialogPane().setPrefSize(1024, 720);
		setGraphic(new ImageView(this.getClass().getResource("/img/editorIcon.png").toString()));
		setOnCloseRequest(e -> close());

		ButtonType okayButtonType = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		
		getDialogPane().getButtonTypes().addAll(okayButtonType, cancelButtonType);

		Button okayButton = (Button) this.getDialogPane().lookupButton(okayButtonType);
		okayButton.setId("OKAY");
		okayButton.setFocusTraversable(false);

		Button cancelButton = (Button) this.getDialogPane().lookupButton(cancelButtonType);
		cancelButton.setId("CANCEL");
		cancelButton.setFocusTraversable(false);
	}
	private void initMainLayout(ObjectProperty<KeyWordDatabase> kwdb) {		
		VBox mainLayout = new VBox();
		mainLayout.setSpacing(10);
		
		editorViewsStackPane = new StackPane();
		VBox.setVgrow(editorViewsStackPane, Priority.ALWAYS);
		editorViewsStackPane.getChildren().addAll(
				new ToolsEditorView(tools, kwdb), 
				new IngredientGroupsEditorView(ingredientGroups, kwdb), 
				new IngredientsEditorView(ingredients, ingredientGroups, kwdb), 
				new CookingActionsEditorView(cookingActions, allIngredients, tools, kwdb), 
				new PartIndicatorsEditorView(partIndicators, kwdb),
				new LastSentenceReferencesEditorView(lastSentenceReferences, kwdb), 
				new EventIndicatorsEditorView(eventIndicators, kwdb)
		);

		selectEditorCB = new ComboBox<String>(
				FXCollections.observableArrayList(
						"Tools", 
						"Groups", 
						"Ingredients",
						"Cooking Actions",
						"Part Indicators",
						"Last Sentence References",
						"Event Indicators"
				)
		);
		selectEditorCB.setTooltip(new Tooltip("Select a type you want to edit"));
		selectEditorCB.getSelectionModel().selectedIndexProperty().addListener((b, o, n) -> {
			for(int i = 0; i < editorViewsStackPane.getChildren().size(); i++) {
				editorViewsStackPane.getChildren().get(i).setVisible(i == (int) n);
			}
		});
		selectEditorCB.getSelectionModel().selectFirst();
		
		mainLayout.getChildren().addAll(
				selectEditorCB,
				editorViewsStackPane
		);
		
		this.getDialogPane().setContent(mainLayout);
	}
	
	public void searchAndScroll(String word) {
		EditorView editorView = null;
		for(Node node : editorViewsStackPane.getChildren()) {
			EditorView eV = (EditorView) node;
			if(editorView.contains(word)) {
				editorView = eV;
			}
		}
		
		if(editorView != null) {
			selectEditorCB.getSelectionModel().select(editorViewsStackPane.getChildren().indexOf(editorView));
			editorView.scrollTo(word);
		}
	}
}
