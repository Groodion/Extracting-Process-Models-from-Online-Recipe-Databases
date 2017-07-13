package ai4.master.project.viewFx.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BridgeObjID {

	private final StringProperty objID = new SimpleStringProperty();

	public void setObjectID(String id){
		this.objID.set(id);
	}

	public String getObjectID(){
		return this.objID.get();
	}

	public StringProperty getProperty(){
		return this.objID;
	}

}
