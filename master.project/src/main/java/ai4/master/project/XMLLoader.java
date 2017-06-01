package ai4.master.project;

import java.io.IOException;
import java.net.URL;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Transformation;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;


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
	public static final String ELEMENT_REGS = "regs";
	public static final String ELEMENT_REGEX = "Regex";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_RESULT_FINDER = "resultFinder";
	public static final String ATTRIBUTE_RESULT = "result";
	public static final String ELEMENT_TRANSFORMATIONS = "transformations";
	public static final String ELEMENT_TRANSFORMATION = "Transformation";
	public static final String ELEMENT_ADD_INGREDIENT_TAG = "AddIngredientTag";
	public static final String ELEMENT_ADD_QUANTIFIER_TAG = "AddQuantifierTag";
	
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
		BaseTool tool = new BaseTool();
		
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
		BaseIngredient ingredient = new BaseIngredient();
		
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
		BaseCookingAction cookingAction = new BaseCookingAction();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals(ATTRIBUTE_NAME)) {
				cookingAction.addName(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_NAME)) {
				cookingAction.addName(child.getText());
			} else if(child.getName().equals(ELEMENT_REGS)) {
				readRegs(child, kwdb, cookingAction);
			} else if(child.getName().equals(ELEMENT_TRANSFORMATIONS)) {
				readTransformations(child, kwdb, cookingAction);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		kwdb.getCookingActions().add(cookingAction);
	}
	private void readTransformations(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_TRANSFORMATION)) {
				readTransformation(child, kwdb, cookingAction);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readTransformation(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		Transformation transformation = new Transformation();
		
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_INGREDIENTS)) {
				readTransformationIngredientes(child, kwdb, transformation);
			} else if(child.getName().equals(ELEMENT_INGREDIENT)) {
				readTransformationProduct(child, kwdb, transformation);
			} else if(child.getName().equals(ELEMENT_ADD_INGREDIENT_TAG)) {
				readAddIngredientTag(child, kwdb, transformation);
			} else if(child.getName().equals(ELEMENT_ADD_QUANTIFIER_TAG)) {
				readAddQuantifierTag(child, kwdb, transformation);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
		
		cookingAction.getTransformations().add(transformation);
	}

	private void readAddIngredientTag(Element element, KeyWordDatabase kwdb, Transformation transformation) {
		transformation.setTag(new IngredientTag(element.getText()));
	}
	
	private void readAddQuantifierTag(Element element, KeyWordDatabase kwdb, Transformation transformation) {
		transformation.setTag(new QuantifierTag(element.getText()));
	}

	private void readTransformationProduct(Element element, KeyWordDatabase kwdb, Transformation transformation) {
		String name = element.getAttributeValue(ATTRIBUTE_NAME);
		
		transformation.setProduct(kwdb.findIngredient(name).toObject());
	}

	private void readTransformationIngredientes(Element element, KeyWordDatabase kwdb, Transformation transformation) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_INGREDIENT)) {
				readTransformationIngredient(child, kwdb, transformation);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}

	private void readTransformationIngredient(Element element, KeyWordDatabase kwdb, Transformation transformation) {
		String name = element.getAttributeValue(ATTRIBUTE_NAME);
		
		transformation.getMandatoryIngredients().add(kwdb.findIngredient(name).toObject());
	}

	private void readRegs(Element element, KeyWordDatabase kwdb, BaseCookingAction cA) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_REGEX)) {
				readRegex(child, kwdb, cA);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readRegex(Element element, KeyWordDatabase kwdb, BaseCookingAction cA) {
		String expression = element.getText();
		Regex.Result result = Regex.Result.FIRST;
		
		if(element.getAttributeValue(ATTRIBUTE_RESULT) != null) {
			result = Regex.Result.valueOf(element.getAttributeValue(ATTRIBUTE_RESULT).toUpperCase());
		}
		
		cA.getRegexList().add(new Regex(expression, result));
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