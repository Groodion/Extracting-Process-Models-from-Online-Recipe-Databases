package ai4.master.project.viewFx.components.editorViews.entries;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class IngredientEntry {
	private StringProperty name;
	private ObservableList<String> synonyms;
	private ObservableList<BaseIngredientGroup> ingredientGroups;

	public IngredientEntry(BaseIngredient ingredient, ObservableList<BaseIngredient> ingredients) {		
		name = new SimpleStringProperty(ingredient.getFirstName());
		synonyms = FXCollections.observableArrayList(ingredient.getNames());
		this.ingredientGroups = FXCollections.observableArrayList(ingredient.getIngredientGroups());

		synonyms.remove(ingredient.getFirstName());
		
		name.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				if(!synonyms.isEmpty()) {
					name.set(synonyms.get(0));
					synonyms.remove(0);
				}
				else {
					ingredients.remove(ingredient);
				}
			} else {
				update(ingredient, this);
			}
		});

		ListChangeListener<Object> lcListener = change -> update(ingredient, this);

		synonyms.addListener(lcListener);
		ingredientGroups.addListener(lcListener);
	}

	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public StringProperty nameProperty() {
		return name;
	}

	public ObservableList<String> getSynonyms() {
		return synonyms;
	}
	
	public ObservableList<BaseIngredientGroup> getIngredientGroups() {
		return ingredientGroups;
	}

	private static void update(BaseIngredient ingredient, IngredientEntry entry) {		
		ingredient.getNames().clear();
		ingredient.getStemmedNames().clear();

		ingredient.getIngredientGroups().clear();
		
		entry.synonyms.removeIf(synonym -> synonym.replace(" ", "").length() == 0);

		ingredient.addName(entry.getName());
		for (String name : entry.getSynonyms()) {
			ingredient.addName(name);
		}
		
		ingredient.getIngredientGroups().addAll(entry.getIngredientGroups());
	}
}