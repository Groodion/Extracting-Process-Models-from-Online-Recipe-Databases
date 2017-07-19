package ai4.master.project;

import ai4.master.project.recipe.baseObject.*;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;


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
	public static final String ELEMENT_EVENT_INDICATORS = "eventIndicators";
	public static final String ELEMENT_EVENT_INDICATOR = "EventIndicator";
	public static final String ELEMENT_NAME = "Name";
	public static final String ELEMENT_REGS = "regs";
	public static final String ELEMENT_REGEX = "Regex";
	public static final String ELEMENT_GROUPS = "groups";
	public static final String ELEMENT_GROUP = "Group";
	public static final String ELEMENT_ITEM_GROUP = "ItemGroup";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_RESULT_FINDER = "resultFinder";
	public static final String ATTRIBUTE_RESULT = "result";
	public static final String ATTRIBUTE_INGREDIENTS_NEEDED = "ingredientsNeeded";
	public static final String ATTRIBUTE_REFERENCE_PREVIOUS_PRODUCTS = "referencePreviousProducts";
	public static final String ELEMENT_TRANSFORMATIONS = "transformations";
	public static final String ELEMENT_TRANSFORMATION = "Transformation";
	public static final String ELEMENT_ADD_INGREDIENT_TAG = "AddIngredientTag";
	public static final String ELEMENT_ADD_QUANTIFIER_TAG = "AddQuantifierTag";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_REGEX_REF_IDS = "regexRefIds";
	public static final String ATTRIBUTE_CHARGE_TOOLS = "chargeTools";

	
	public static KeyWordDatabase load(String path) {
		KeyWordDatabase kwdb = null;
		
		try {
			kwdb = new XMLLoader().load(new URL("file", "", path));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return kwdb;
	}

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
			} else if(child.getName().equals(ELEMENT_GROUPS)) {
				readGroups(child, kwdb);
			} else if(child.getName().equals(ELEMENT_INGREDIENTS)) {
				readIngredients(child, kwdb);
			} else if(child.getName().equals(ELEMENT_COOKING_ACTIONS)) {
				readCookingActions(child, kwdb);
			} else if(child.getName().equals(ELEMENT_PART_INDICATORS)) {
				readPartIndicators(child, kwdb);
			} else if(child.getName().equals(ELEMENT_LAST_SENTENCE_REFERENCES)) {
				readLastSentenceReferences(child, kwdb);
			} else if(child.getName().equals(ELEMENT_EVENT_INDICATORS)) {
				readEventIndicators(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + root.getName());
			}
		}
		
		Collections.sort(kwdb.getTools());
		Collections.sort(kwdb.getIngredients());
		Collections.sort(kwdb.getIngredientGroups());
		Collections.sort(kwdb.getCookingActions());
		Collections.sort(kwdb.getEventIndicators());
		Collections.sort(kwdb.getPartIndicators());
		Collections.sort(kwdb.getLastSentenceReferences());
		
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

	private void readGroups(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_GROUP)) {
				readGroup(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readGroup(Element element, KeyWordDatabase kwdb) {
		BaseIngredientGroup group = new BaseIngredientGroup();
		
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals(ATTRIBUTE_NAME)) {
				group.addName(att.getValue());
			} else {
				System.err.println("Unknown Attribute");
			}
		}		

		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_NAME)) {
				group.addName(child.getText());
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}

		kwdb.getIngredientGroups().add(group);
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
			} else if(child.getName().equals(ELEMENT_GROUPS)) {
				readGroups(child, kwdb, ingredient);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		kwdb.getIngredients().add(ingredient);
	}
	private void readGroups(Element element, KeyWordDatabase kwdb, BaseIngredient ingredient) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_GROUP)) {
				readGroup(child, kwdb, ingredient);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readGroup(Element element, KeyWordDatabase kwdb, BaseIngredient ingredient) {
		for(Attribute att : element.getAttributes()) {
			if(att.getName().equals(ATTRIBUTE_NAME)) {
				BaseIngredientGroup g = kwdb.findIngredientGroup(att.getValue());
				ingredient.getIngredientGroups().add(g);
			} else {
				System.err.println("Unknown Attribute");
			}
		}
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
			} else if(child.getName().equals(ELEMENT_TOOLS)) {
				readTools(child, kwdb, cookingAction);
			} else if(child.getName().equals(ELEMENT_INGREDIENTS)) {
				readIngredients(child, kwdb, cookingAction);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
		
		kwdb.getCookingActions().add(cookingAction);
	}
	private void readTools(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_TOOL)) {
				readSimpleToolGroup(child, kwdb, cookingAction);
			} else if(child.getName().equals(ELEMENT_ITEM_GROUP)) {
				readToolGroup(child, kwdb, cookingAction);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readSimpleToolGroup(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		ItemGroup<BaseTool, Tool> toolGroup = new ItemGroup<BaseTool, Tool>();
		
		toolGroup.getItems().add(kwdb.findTool(element.getAttributeValue("name")));
		
		cookingAction.getImplicitTools().add(toolGroup);
	}
	private void readToolGroup(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		ItemGroup<BaseTool, Tool> toolGroup = new ItemGroup<BaseTool, Tool>();
		
		for(Element child : element.getChildren(ELEMENT_TOOL)) {
			toolGroup.getItems().add(kwdb.findTool(child.getAttributeValue("name")));
		}
		
		cookingAction.getImplicitTools().add(toolGroup);
	}
	private void readIngredients(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_INGREDIENT)) {
				readSimpleIngredientGroup(child, kwdb, cookingAction);
			} else if(child.getName().equals(ELEMENT_ITEM_GROUP)) {
				readIngredientGroup(child, kwdb, cookingAction);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}
	}
	private void readSimpleIngredientGroup(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		ItemGroup<BaseIngredient, Ingredient> ingredientGroup = new ItemGroup<BaseIngredient, Ingredient>();
		
		ingredientGroup.getItems().add(kwdb.findIngredient(element.getAttributeValue("name")));
		
		cookingAction.getImplicitIngredients().add(ingredientGroup);
	}
	private void readIngredientGroup(Element element, KeyWordDatabase kwdb, BaseCookingAction cookingAction) {
		ItemGroup<BaseIngredient, Ingredient> ingredientGroup = new ItemGroup<BaseIngredient, Ingredient>();
		
		for(Element child : element.getChildren(ELEMENT_INGREDIENT)) {
			ingredientGroup.getItems().add(kwdb.findIngredient(child.getAttributeValue("name")));
		}
		
		cookingAction.getImplicitIngredients().add(ingredientGroup);
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
		
		String ids = element.getAttributeValue(ATTRIBUTE_REGEX_REF_IDS);
		if(ids != null) {
			ids.replace(',', ' ');
			ids = ids.trim();
			for(String id : ids.split(" ")) {
				transformation.getRegexIds().add(id);
			}
		}
		
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
		
		boolean iN = true;
		boolean rPP = false;
		
		try {
			iN = element.getAttribute(ATTRIBUTE_INGREDIENTS_NEEDED).getBooleanValue();
		} catch(Exception e) { }
		try {
			rPP = element.getAttribute(ATTRIBUTE_REFERENCE_PREVIOUS_PRODUCTS).getBooleanValue();
		} catch(Exception e) { }
		Regex regex = new Regex(expression, result, iN, rPP);
		
		regex.setId(element.getAttributeValue(ATTRIBUTE_ID));
		
		try {
			regex.setChargingTools(element.getAttribute(ATTRIBUTE_CHARGE_TOOLS).getBooleanValue());
		} catch(Exception e) {}			
		
		cA.getRegexList().add(regex);
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
	private void readEventIndicators(Element element, KeyWordDatabase kwdb) {
		for(Element child : element.getChildren()) {
			if(child.getName().equals(ELEMENT_EVENT_INDICATOR)) {
				readEventIndicator(child, kwdb);
			} else {
				System.err.println("Unknown Child " + child.getName() + " in " + element.getName());
			}
		}		
	}
	private void readEventIndicator(Element element, KeyWordDatabase kwdb) {
		kwdb.getEventIndicators().add(element.getValue());
	}
}