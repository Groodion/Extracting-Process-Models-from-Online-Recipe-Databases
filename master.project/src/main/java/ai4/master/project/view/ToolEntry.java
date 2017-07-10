package ai4.master.project.view;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseTool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ToolEntry {
	private StringProperty toolName;
	private ObservableList<String> synonyms;

	public ToolEntry(BaseTool tool, ObservableList<ToolEntry> parent, final KeyWordDatabase kwdb) {
		this.toolName = new SimpleStringProperty();
		this.synonyms = FXCollections.observableArrayList();

		for (String name : tool.getNames()) {
			synonyms.add(name);
		}

		if (synonyms.size() != 0) {
			this.toolName.set(synonyms.get(0));
			synonyms.remove(0);
		}

		this.toolName.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				if(!synonyms.isEmpty()) {
					toolName.set(synonyms.get(0));
					synonyms.remove(0);
					updateTool(tool, this);
				}
				else {
					parent.remove(this);
					kwdb.getTools().remove(tool);
				}
			} else {
				updateTool(tool, this);
			}
		});

		ListChangeListener<String> lcListener = change -> {
			while (change.next()) {
				updateTool(tool, this);
			}
		};

		synonyms.addListener(lcListener);
		parent.add(this);
	}

	public String getToolName() {
		return toolName.get();
	}

	public void setToolName(String toolName) {
		this.toolName.set(toolName);
	}

	public StringProperty getNameProperty() {
		return toolName;
	}

	public ObservableList<String> getSynonyms() {
		return synonyms;
	}

	private static void updateTool(BaseTool tool, ToolEntry entry) {
		tool.getNames().clear();

		while (entry.synonyms.remove(""))
			;

		tool.getNames().add(entry.getToolName());
		for (String name : entry.getSynonyms()) {
			tool.getNames().add(name);
		}
	}
}