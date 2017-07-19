package ai4.master.project.recipe.baseObject;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import ai4.master.project.KeyWordDatabase;

public class Regex {
	public enum Result {
		FIRST, LAST, ALL, PREV, NO_RESULT;
	}
	
	private String expression;
	private Result result;
	private boolean ingredientsNeeded;
	private boolean referencePreviousProducts;
	private String id = null;
	private boolean chargingTools;
	
	private Set<String> usedTags;
	
	public Regex(String expression, Result result, boolean ingredientsNeeded, boolean referencePreviousProducts) {
		this.usedTags = new HashSet<String>();
		setExpression(expression);
		this.result = result;
		this.ingredientsNeeded = ingredientsNeeded;
		this.referencePreviousProducts = referencePreviousProducts;
	}
	public Regex(String expression, Result result) {
		this(expression, Result.FIRST, true, false);
	}
	private Regex(Regex parent) {
		this(parent.expression, parent.result, parent.ingredientsNeeded, parent.referencePreviousProducts);
		
		id = parent.id;
		chargingTools = parent.chargingTools;
	}

	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
		
		usedTags.clear();
		Scanner scanner = new Scanner(expression);
		scanner.useDelimiter(" ");
		String pattern = "[_~][A-Z]+";
		while(scanner.hasNext(pattern)) {
			usedTags.add(scanner.next(pattern));
		}
		scanner.close();
	}
	public Set<String> getUsedTags() {
		return usedTags;
	}
	
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public boolean isIngredientsNeeded() {
		return ingredientsNeeded;
	}
	public void setIngredientsNeeded(boolean ingredientsNeeded) {
		this.ingredientsNeeded = ingredientsNeeded;
	}
	public boolean isReferencePreviousProducts() {
		return referencePreviousProducts;
	}
	public void setReferencePreviousProducts(boolean referencePreviousProducts) {
		this.referencePreviousProducts = referencePreviousProducts;
	}
	public boolean isChargingTools() {
		return chargingTools;
	}
	public void setChargingTools(boolean chargingTools) {
		this.chargingTools = chargingTools;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean canExecute(Transformation transformation) {
		return transformation.getRegexIds().isEmpty() || transformation.getRegexIds().contains(id);
	}
	
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Regex result=\"");
		sB.append(result.toString().toLowerCase());
		sB.append("\" ingredientsNeeded=\"");
		sB.append(ingredientsNeeded);
		sB.append("\" referencePreviousProducts=\"");
		sB.append(referencePreviousProducts);
		sB.append("\" chargeTools=\"");
		sB.append(chargingTools);
		sB.append("\">");
		sB.append(expression);
		sB.append("</Regex>");
		
		return sB.toString();
	}
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		
		if(id != null && id.length() != 0) {
			sB.append(id);
			sB.append(" - ");
		}
		
		sB.append("/");
		sB.append(expression);
		sB.append("/");
		
		return sB.toString();
	}
	
	public Regex clone(KeyWordDatabase kwdb) {
		return new Regex(this);
	}
}