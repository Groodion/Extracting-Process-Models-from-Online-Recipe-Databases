package ai4.master.project.view;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class IngredientEntry {
	private StringProperty ingredientName;
	private ObservableList<String> ingredientSynonyms;
	private ObservableList<BaseIngredientGroup> ingredientGroups;
	
	public IngredientEntry(BaseIngredient ingredient, ObservableList<IngredientEntry> parent, final KeyWordDatabase kwdb) {
		this.ingredientName = new SimpleStringProperty();
		this.ingredientSynonyms = FXCollections.observableArrayList();
		this.ingredientGroups = FXCollections.observableArrayList(ingredient.getIngredientGroups());
		
		for(String name : ingredient.getNames()) {
			ingredientSynonyms.add(name);
		}
		
		if(ingredientSynonyms.size() != 0) {
			ingredientName.set(ingredientSynonyms.get(0));
			ingredientSynonyms.remove(0);
		}
		
		this.ingredientName.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				if(!ingredientSynonyms.isEmpty()) {
					ingredientName.set(ingredientSynonyms.get(0));
					ingredientSynonyms.remove(0);
					updateIngredient(ingredient, this);
				}
				else {
					parent.remove(this);
					kwdb.getIngredients().remove(ingredient);
				}
			} else {
				updateIngredient(ingredient, this);
			}
		});

		ListChangeListener<String> lcListener = change -> {
			while (change.next()) {
				updateIngredient(ingredient, this);
			}
		};

		ingredientSynonyms.addListener(lcListener);
		
		System.out.println(ingredientGroups);
		parent.add(this);
	}

	public String getIngredientName() {
		return ingredientName.get();
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName.set(ingredientName);
	}

	public StringProperty getIngredientNameProperty() {
		return ingredientName;
	}

	public ObservableList<String> getIngredientSynonyms() {
		return ingredientSynonyms;
	}
	
	public ObservableList<BaseIngredientGroup> getIngredientGroups() {
		return ingredientGroups;
	}

	private static void updateIngredient(BaseIngredient ingredient, IngredientEntry entry) {
		ingredient.getNames().clear();

		while (entry.ingredientSynonyms.remove(""))
			;

		ingredient.getNames().add(entry.getIngredientName());
		for (String name : entry.getIngredientSynonyms()) {
			ingredient.getNames().add(name);
		}
	}	
}