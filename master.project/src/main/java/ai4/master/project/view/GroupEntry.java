package ai4.master.project.view;

import javafx.beans.property.SimpleStringProperty;

public class GroupEntry {
	private final SimpleStringProperty groupName;
	private final SimpleStringProperty groupSynonyms;
	
	public GroupEntry(String groupName, String groupSynonyms) {
		this.groupName = new SimpleStringProperty(groupName);
		this.groupSynonyms = new SimpleStringProperty(groupSynonyms);
	}
	
	public String getGroupName() {
		return groupName.get();
	}
	
	public String getGroupSynonyms() {
		return groupSynonyms.get();
	}
	
	public void setGroupName(String groupName) {
		this.groupName.set(groupName);
	}
	
	public void setGroupSynonyms(String groupSynonyms) {
		this.groupSynonyms.set(groupSynonyms);
	}
	
}
