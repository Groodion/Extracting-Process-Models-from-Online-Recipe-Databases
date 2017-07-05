package ai4.master.project.view;

import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.KeyWordDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class GroupEntry {
	private StringProperty groupName;
	private ObservableList<String> groupSynonyms;
	
	public GroupEntry(BaseIngredientGroup group, ObservableList<GroupEntry> parent, final KeyWordDatabase kwdb) {
		this.groupName = new SimpleStringProperty();
		this.groupSynonyms = FXCollections.observableArrayList();
		
		for(String name : group.getNames()) {
			groupSynonyms.add(name);
		}
		
		if(groupSynonyms.size() != 0) {
			groupName.set(groupSynonyms.get(0));
			groupSynonyms.remove(0);
		}
		

		this.groupName.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				if(!groupSynonyms.isEmpty()) {
					groupName.set(groupSynonyms.get(0));
					groupSynonyms.remove(0);
					updateGroup(group, this);
				}
				else {
					parent.remove(this);
					kwdb.getTools().remove(group);
				}
			} else {
				updateGroup(group, this);
			}
		});

		ListChangeListener<String> lcListener = change -> {
			while (change.next()) {
				updateGroup(group, this);
			}
		};

		groupSynonyms.addListener(lcListener);
		parent.add(this);
	}
	
	public String getGroupName() {
		return groupName.get();
	}
	
	public void setGroupName(String groupName) {
		this.groupName.set(groupName);
	}
	
	
	public StringProperty getNameProperty() {
		return groupName;
	}
	
	public ObservableList<String> getGroupSynonyms() {
		return groupSynonyms;
	}

	private static void updateGroup(BaseIngredientGroup group, GroupEntry entry) {
		group.getNames().clear();

		while (entry.groupSynonyms.remove(""))
			;

		group.getNames().add(entry.getGroupName());
		for (String name : entry.getGroupSynonyms()) {
			group.getNames().add(name);
		}
	}	
}