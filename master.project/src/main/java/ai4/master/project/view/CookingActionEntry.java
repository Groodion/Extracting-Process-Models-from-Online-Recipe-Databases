package ai4.master.project.view;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Transformation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class CookingActionEntry {
	
	private StringProperty name;
	private ObservableList<String> synonyms;
	private ObservableList<Regex> regex;
	private ObservableList<Transformation> transformations;	
	private ObservableList<BaseTool> tools;	
	
	public CookingActionEntry(BaseCookingAction cookingAction, ObservableList<CookingActionEntry> parent, final KeyWordDatabase kwdb) {
		name = new SimpleStringProperty();
				
		synonyms = FXCollections.observableArrayList();
		
		regex = FXCollections.observableArrayList();
		transformations = FXCollections.observableArrayList();
		tools = FXCollections.observableArrayList();
		
		int i = 0;
		for(String name : cookingAction.getNames()) {
			if(i++ == 0) {
				this.name.set(name);
			} else {
				synonyms.add(name);
			}
		}
		regex.addAll(cookingAction.getRegexList());
		transformations.addAll(cookingAction.getTransformations());
		tools.addAll(cookingAction.getImplicitTools());
		
		name.addListener((b, o, n) -> {
			if(n == null || n.length() == 0) {
				kwdb.getCookingActions().remove(cookingAction);
				parent.remove(this);
			} else {
				updateCookingAction(cookingAction, this);
			}
		});
		ListChangeListener<String> lcListener = change -> updateCookingAction(cookingAction, this);
		
		synonyms.addListener(lcListener);
		
		parent.add(this);
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
	public ObservableList<Regex> getRegex() {
		return regex;
	}
	public ObservableList<Transformation> getTransformations() {
		return transformations;
	}
	public ObservableList<BaseTool> getTools() {
		return tools;
	}
	
	private static void updateCookingAction(BaseCookingAction cookingAction, CookingActionEntry entry) {
		cookingAction.getNames().clear();
		
		cookingAction.getNames().add(entry.getName());
		for(String name : entry.getSynonyms()) {
			cookingAction.getNames().add(name);
		}
	}
}
