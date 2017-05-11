package ai4.master.project;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import ai4.master.project.model.*;


public class XMLLoader {
	
	private List<Tool> tools = new ArrayList<Tool>();
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
	private List<CookingAction> cookingActions = new ArrayList<CookingAction>();
	
	
	/**
	 * Loads XML-File from URL and puts the elements into lists
	 *
	 * @param url
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void load(URL url) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
		Document document = builder.build(url);
		
		
		Element root = document.getRootElement();
		
		for(Element child : root.getChildren()) {
			if(child.getName().equals("Werkzeuge")) {
				readTools(child);
			} else if(child.getName().equals("Zutaten")) {
				readIngredients(child);
			} else if(child.getName().equals("Tätigkeiten")) {
				readCookingActions(child);
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
	private void readIngredients(Element element) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals("Zutat")) {
				readIngredient(child);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readIngredient(Element element) {
		Ingredient ingredient = new Ingredient();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals("name")) {
				ingredient.getNames().add(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals("Name")) {
				ingredient.getNames().add(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		ingredients.add(ingredient);
	}
	private void readCookingActions(Element element) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals("Tätigkeit")) {
				readCookingAction(child);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readCookingAction(Element element) {
		CookingAction cookingAction = new CookingAction();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals("name")) {
				cookingAction.getNames().add(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals("Name")) {
				cookingAction.getNames().add(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		cookingActions.add(cookingAction);
	}
	
	public List<Tool> getTools() {
		return tools;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	public List<CookingAction> getCookingActions() {
		return cookingActions;
	}
}