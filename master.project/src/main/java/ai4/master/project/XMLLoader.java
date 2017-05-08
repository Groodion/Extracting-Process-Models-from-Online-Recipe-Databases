package ai4.master.project;

import java.net.URL;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import ai4.master.project.model.CookingAction;
import ai4.master.project.model.Ingredient;
import ai4.master.project.model.Tool;
import lombok.Getter;

@Getter
public class XMLLoader {
	
	private List<Tool> tools;
	private List<Ingredient> ingredients;
	private List<CookingAction> cookingActions;
	
	public void load(URL url) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(url);
		Element root = document.getRootElement();
		
		for(Element child : root.getChildren()) {
			if(child.getName().equals("Werkzeuge")) {
				readTools(child);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + root.getName());
			}
		}
	}
	
	private void readTools(Element element) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals("Werkzeug")) {
				readTool(child);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readTool(Element element) {
		Tool tool = new Tool();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals("name")) {
				tool.getNames().add(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals("Name")) {
				tool.getNames().add(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		tools.add(tool);
	}
}
