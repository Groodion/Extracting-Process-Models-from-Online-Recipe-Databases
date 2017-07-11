package ai4.master.project.viewFx.components.editorViews.entries;

import java.util.Map;

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
	

	public CookingActionEntry(BaseCookingAction cookingAction, ObservableList<BaseCookingAction> cookingActions, Map<Object, ObservableList<String>> regexIdMap) {
		ObservableList<String> regexIds = FXCollections.observableArrayList();
		
		name = new SimpleStringProperty(cookingAction.getFirstName());
				
		synonyms = FXCollections.observableArrayList(cookingAction.getNames());
		
		regex = FXCollections.observableArrayList(cookingAction.getRegexList());
		transformations = FXCollections.observableArrayList(cookingAction.getTransformations());
		tools = FXCollections.observableArrayList(cookingAction.getImplicitTools());
				
		name.addListener((b, o, n) -> {
			if(n == null || n.length() == 0) {
				if(synonyms.isEmpty()) {
					cookingActions.remove(cookingAction);
				} else {
					name.set(synonyms.remove(0));
					update(cookingAction, this);
				}
			} else {
				update(cookingAction, this);
			}
		});
		ListChangeListener<Object> lcListener = change -> update(cookingAction, this);
		
		synonyms.addListener(lcListener);
		regex.addListener(lcListener);
		transformations.addListener(lcListener);
		tools.addListener(lcListener);
		
		for(Regex regex : regex) {
			regexIdMap.put(regex, regexIds);
		}
		for(Transformation transformation : transformations) {
			regexIdMap.put(transformation, regexIds);
		}
		
		ListChangeListener<Object> regexIdChange = change -> {
			while(change.next()) {
				for(Object o : change.getRemoved()) {
					regexIdMap.remove(o);
				}
				for(Object o : change.getAddedSubList()) {
					regexIdMap.put(o, regexIds);
				}
			}
		};
		regex.addListener(regexIdChange);
		transformations.addListener(regexIdChange);
		
		for(Regex regex : regex) {
			if(regex.getId() != null && regex.getId().length() != 0) {
				regexIds.add(regex.getId());
			}
		}
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
	
	private static void update(BaseCookingAction cookingAction, CookingActionEntry entry) {
		cookingAction.getNames().clear();
		cookingAction.getStemmedNames().clear();
		
		cookingAction.getRegexList().clear();
		cookingAction.getTransformations().clear();
		cookingAction.getImplicitTools().clear();
		
		entry.synonyms.removeIf(synonym -> synonym.replace(" ", "").length() == 0);
		
		cookingAction.addName(entry.getName());
		for(String name : entry.getSynonyms()) {
			cookingAction.addName(name);
		}
		
		cookingAction.getRegexList().addAll(entry.getRegex());
		cookingAction.getTransformations().addAll(entry.getTransformations());
		cookingAction.getImplicitTools().addAll(entry.getTools());
	}
}
