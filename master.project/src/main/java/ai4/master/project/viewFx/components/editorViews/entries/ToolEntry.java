package ai4.master.project.viewFx.components.editorViews.entries;

import ai4.master.project.recipe.baseObject.BaseTool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ToolEntry {
	private StringProperty toolName;
	private ObservableList<String> synonyms;

	public ToolEntry(BaseTool tool, ObservableList<BaseTool> parent) {		
		toolName = new SimpleStringProperty(tool.getFirstName());
		synonyms = FXCollections.observableArrayList(tool.getNames());
		
		synonyms.remove(tool.getFirstName());
		
		toolName.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				if(!synonyms.isEmpty()) {
					toolName.set(synonyms.get(0));
					synonyms.remove(0);
				}
				else {
					parent.remove(tool);
				}
			} else {
				updateTool(tool, this);
			}
		});

		ListChangeListener<String> lcListener = change -> updateTool(tool, this);

		synonyms.addListener(lcListener);
	}

	public String getName() {
		return toolName.get();
	}
	public void setName(String toolName) {
		this.toolName.set(toolName);
	}
	public StringProperty nameProperty() {
		return toolName;
	}

	public ObservableList<String> getSynonyms() {
		return synonyms;
	}

	private static void updateTool(BaseTool tool, ToolEntry entry) {		
		tool.clearNames();

		entry.synonyms.removeIf(synonym -> synonym.replace(" ", "").length() == 0);

		tool.addName(entry.getName());
		for (String name : entry.getSynonyms()) {
			tool.addName(name);
		}		
	}
}