package ai4.master.project.recipe.baseObject;

import ai4.master.project.recipe.object.Tool;


public class BaseTool extends BaseNamedObject<Tool, BaseTool> {

	@Override
	public Tool toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		return new Tool(name, this);
	}
}