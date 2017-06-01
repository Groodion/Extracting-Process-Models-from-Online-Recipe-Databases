package ai4.master.project.recipe.baseObject;

public class Regex {
	public enum Result {
		FIRST, LAST, ALL;
	}
	
	private String expression;
	private Result result;
	
	
	public Regex(String expression, Result result) {
		this.expression = expression;
		this.result = result;
	}
	public Regex(String expression) {
		this(expression, Result.FIRST);
	}
	
	public String getExpression() {
		return expression;
	}
	public Result getResult() {
		return result;
	}
}