package ai4.master.project.view;

import javafx.beans.property.SimpleStringProperty;

public class IngredientEntry {
	private final SimpleStringProperty ingredientName;
	private final SimpleStringProperty ingredientSynonyms;
	private final SimpleStringProperty ingredientGroups;
	
	public IngredientEntry(String ingredientName, String ingredientSynonyms, String ingredientGroups) {
		this.ingredientName = new SimpleStringProperty(ingredientName);
		this.ingredientSynonyms = new SimpleStringProperty(ingredientSynonyms);
		this.ingredientGroups = new SimpleStringProperty(ingredientGroups);

	}
	
	public String getIngredientName() {
		return ingredientName.get();
	}
	
	public String getIngredientSynonyms() {
		return ingredientSynonyms.get();
	}
	
	public String getIngredientGroups() {
		return ingredientGroups.get();
	}
	
	public void setIngredientName(String ingredientName) {
		this.ingredientName.set(ingredientName);
	}
	
	public void setIngredientSynonyms(String ingredientSynonyms) {
		this.ingredientSynonyms.set(ingredientSynonyms);
	}
	
	public void setIngredientGroups(String ingredientGroups) {
		this.ingredientGroups.set(ingredientGroups);
	}
	
}
