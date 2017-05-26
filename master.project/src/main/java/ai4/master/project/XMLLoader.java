package ai4.master.project;

import java.io.IOException;
import java.net.URL;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import ai4.master.project.recipe.*;


public class XMLLoader {	
	
	public static final String ELEMENT_TOOLS = "tools";
	public static final String ELEMENT_TOOL = "Tool";
	public static final String ELEMENT_INGREDIENTS = "ingredients";
	public static final String ELEMENT_INGREDIENT = "Ingredient";
	public static final String ELEMENT_COOKING_ACTIONS = "cookingActions";
	public static final String ELEMENT_COOKING_ACTION = "CookingAction";
	public static final String ELEMENT_PART_INDICATORS = "partIndicators";
	public static final String ELEMENT_PART_INDICATOR = "PartIndicator";
	public static final String ELEMENT_LAST_SENTENCE_REFERENCES = "lastSentenceReferences";
	public static final String ELEMENT_LAST_SENTENCE_REFERENCE = "LastSentenceReference";
	public static final String ELEMENT_NAME = "Name";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_RESULT_FINDER = "resultFinder";
	
	/**
	 * Loads XML-File from URL and puts the elements into lists
	 *
	 * @param url
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public KeyWordDatabase load(URL url) throws JDOMException, IOException {
		KeyWordDatabase kwdb = new KeyWordDatabase();
		
		SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
		Document document = builder.build(url);
		
		Element root = document.getRootElement();
		
		for(Element child : root.getChildren()) {
			if(child.getName().equals(ELEMENT_TOOLS)) {
				readTools(child, kwdb);
			} else if(child.getName().equals(ELEMENT_INGREDIENTS)) {
				readIngredients(child, kwdb);
			} else if(child.getName().equals(ELEMENT_COOKING_ACTIONS)) {
				readCookingActions(child, kwdb);
			} else if(child.getName().equals(ELEMENT_PART_INDICATORS)) {
				readPartIndicators(child, kwdb);
			} else if(child.getName().equals(ELEMENT_LAST_SENTENCE_REFERENCES)) {
				readLastSentenceReferences(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + root.getName());
			}
		}
		
		return kwdb;
	}
	
	private void readTools(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_TOOL)) {
				readTool(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readTool(Element element, KeyWordDatabase kwdb) {
		Tool tool = new Tool();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals(ATTRIBUTE_NAME)) {
				tool.addName(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_NAME)) {
				tool.addName(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		kwdb.getTools().add(tool);
	}
	private void readIngredients(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_INGREDIENT)) {
				readIngredient(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readIngredient(Element element, KeyWordDatabase kwdb) {
		Ingredient ingredient = new Ingredient();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals(ATTRIBUTE_NAME)) {
				ingredient.addName(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_NAME)) {
				ingredient.addName(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		kwdb.getIngredients().add(ingredient);
	}
	private void readCookingActions(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_COOKING_ACTION)) {
				readCookingAction(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readCookingAction(Element element, KeyWordDatabase kwdb) {
		CookingAction cookingAction = new CookingAction();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals(ATTRIBUTE_NAME)) {
				cookingAction.addName(att.getValue());
			} else if(att.getName().equals(ATTRIBUTE_RESULT_FINDER)) {
				if(att.getValue().equals("Subjekt")) {
					cookingAction.setResultFinder(ResultType.SUBJECT);
				} else {
					cookingAction.setResultFinder(ResultType.OBJECT);	
				}
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_NAME)) {
				cookingAction.addName(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		kwdb.getCookingActions().add(cookingAction);
	}
	private void readPartIndicators(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_PART_INDICATOR)) {
				readPartIndicator(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readPartIndicator(Element element, KeyWordDatabase kwdb) {
		kwdb.getPartIndicators().add(element.getValue());
	}
	private void readLastSentenceReferences(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_LAST_SENTENCE_REFERENCE)) {
				readLastSentenceReference(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readLastSentenceReference(Element element, KeyWordDatabase kwdb) {
		kwdb.getLastSentenceReferences().add(element.getValue());
	}
}