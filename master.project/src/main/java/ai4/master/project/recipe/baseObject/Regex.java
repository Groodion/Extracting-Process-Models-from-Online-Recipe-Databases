package ai4.master.project.recipe.baseObject;

public class Regex {
	public enum Result {
		FIRST, LAST, ALL, PREV, NO_RESULT;
	}
	
	private String expression;
	private Result result;
	private boolean ingredientsNeeded;
	private boolean referencePreviousProducts;
	
	
	public Regex(String expression, Result result, boolean ingredientsNeeded, boolean referencePreviousProducts) {
		this.expression = expression;
		this.result = result;
		this.ingredientsNeeded = ingredientsNeeded;
		this.referencePreviousProducts = referencePreviousProducts;
	}
	public Regex(String expression, Result result) {
		this(expression, Result.FIRST, true, false);
	}

	public String getExpression() {
		return expression;
	}
	public Result getResult() {
		return result;
	}
	public boolean isIngredientsNeeded() {
		return ingredientsNeeded;
	}
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Regex result=\"");
		sB.append(result.toString().toLowerCase());
		sB.append("\" ingredientsNeeded=\"");
		sB.append(ingredientsNeeded);
		sB.append("\" referencePreviousProducts=\"");
		sB.append(referencePreviousProducts);
		sB.append("\">");
		sB.append(expression);
		sB.append("</Regex>");
		
		return sB.toString();
	}
	public boolean isReferencePreviousProducts() {
		return referencePreviousProducts;
	}
}