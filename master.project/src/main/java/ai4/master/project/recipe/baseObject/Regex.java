package ai4.master.project.recipe.baseObject;

public class Regex {
	public enum Result {
		FIRST, LAST, ALL, PREV, NO_RESULT;
	}
	
	private String expression;
	private Result result;
	private boolean ingredientsNeeded;
	
	
	public Regex(String expression, Result result, boolean ingredientsNeeded) {
		this.expression = expression;
		this.result = result;
		this.ingredientsNeeded = ingredientsNeeded;
	}
	public Regex(String expression) {
		this(expression, Result.FIRST, true);
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
		sB.append("\">");
		sB.append(expression);
		sB.append("</Regex>");
		
		return sB.toString();
	}
}