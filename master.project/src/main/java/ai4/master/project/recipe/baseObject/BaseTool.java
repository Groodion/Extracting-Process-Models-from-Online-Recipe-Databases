package ai4.master.project.recipe.baseObject;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.Tool;


public class BaseTool extends BaseNamedObject<Tool, BaseTool> {

	public BaseTool() {
		super();
	}
	private BaseTool(BaseTool parent, KeyWordDatabase kwdb) {
		super(parent, kwdb);
	}
	
	@Override
	public Tool toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		return new Tool(name, this);
	}

	@Override
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Tool name=\"");
		sB.append(getFirstName());
		sB.append("\">");
		for(String name : getNames()) {
			if(name != getFirstName()) {
				sB.append("<Name>");
				sB.append(name);
				sB.append("</Name>");
			}
		}
		sB.append("</Tool>");
		
		return sB.toString();
	}
	@Override
	public String toRefXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Tool name=\"");
		sB.append(getFirstName());
		sB.append("\" />");
		
		return sB.toString();
	}

	@Override
	public BaseTool clone(KeyWordDatabase kwdb) {
		return new BaseTool(this, kwdb);
	}
}