package ai4.master.project.viewFx.components.editorViews.entries;

import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class IngredientGroupEntry {
	private StringProperty name;
	private ObservableList<String> synonyms;

	public IngredientGroupEntry(BaseIngredientGroup ingredientGroup, ObservableList<BaseIngredientGroup> ingredientGroups) {		
		name = new SimpleStringProperty(ingredientGroup.getFirstName());
		synonyms = FXCollections.observableArrayList(ingredientGroup.getNames());
		
		synonyms.remove(ingredientGroup.getFirstName());
		
		name.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				if(!synonyms.isEmpty()) {
					name.set(synonyms.get(0));
					synonyms.remove(0);
				}
				else {
					ingredientGroups.remove(ingredientGroup);
				}
			} else {
				update(ingredientGroup, this);
			}
		});

		ListChangeListener<String> lcListener = change -> update(ingredientGroup, this);

		synonyms.addListener(lcListener);
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

	private static void update(BaseIngredientGroup ingredientGroup, IngredientGroupEntry entry) {		
		ingredientGroup.clearNames();

		entry.synonyms.removeIf(synonym -> synonym.replace(" ", "").length() == 0);

		ingredientGroup.addName(entry.getName());
		for (String name : entry.getSynonyms()) {
			ingredientGroup.addName(name);
		}		
	}
}