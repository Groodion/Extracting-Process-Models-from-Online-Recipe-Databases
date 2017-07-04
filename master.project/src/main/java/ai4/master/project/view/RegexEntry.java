package ai4.master.project.view;

import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.Regex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class RegexEntry {
	
	private StringProperty expression;
	private StringProperty id;
	private BooleanProperty ingredientsNeeded;
	private BooleanProperty referencePreviousProducts;
	private ObjectProperty<Regex.Result> result;
	
	public RegexEntry(Regex regex, ObservableList<RegexEntry> parent, final ObservableList<Regex> regexList, ObservableList<String> regexIdList) {
		expression = new SimpleStringProperty(regex.getExpression());
		id = new SimpleStringProperty(regex.getId());
		
		id.addListener((b, o, n) -> {
			if(regexIdList.contains(o)) {
				int i = regexIdList.indexOf(o);
				if(n == null || n.trim().length() == 0) {
					regexIdList.remove(i);
				} else {
					regexIdList.set(i, n);
				}
			} else if(n == null || n.trim().length() == 0) {
				
			} else {
				regexIdList.add(n);
			}
		});
		if(regex.getId() != null && regex.getId().trim().length() != 0) {
			regexIdList.add(regex.getId());
		}
		
		if(regex.getId() != null && regex.getId().length() != 0) {
			regexIdList.add(regex.getId());
		}
		
		ingredientsNeeded = new SimpleBooleanProperty(regex.isIngredientsNeeded());
		referencePreviousProducts = new SimpleBooleanProperty(regex.isReferencePreviousProducts());
		
		result = new SimpleObjectProperty<Regex.Result>(regex.getResult());
		
		parent.add(this);
	}

	public String getExpresstion() {
		return expression.get();
	}
	public void setExpression(String expression) {
		this.expression.set(expression);
	}
	public StringProperty expressionProperty() {
		return expression;
	}

	public String getId() {
		return id.get();
	}
	public void setId(String id) {
		this.id.set(id);
	}
	public StringProperty idProperty() {
		return id;
	}

	public boolean isIngredientsNeeded() {
		return ingredientsNeeded.get();
	}
	public void setIngredientsNeeded(boolean ingredientsNeeded) {
		this.ingredientsNeeded.set(ingredientsNeeded);
	}
	public BooleanProperty ingredientsNeededProperty() {
		return ingredientsNeeded;
	}

	public boolean isReferencePreviousProducts() {
		return referencePreviousProducts.get();
	}
	public void setReferencePreviousProducts(boolean referencePreviousProducts) {
		this.referencePreviousProducts.set(referencePreviousProducts);
	}
	public BooleanProperty eferencePreviousProductsProperty() {
		return referencePreviousProducts;
	}

	public Regex.Result getResult() {
		return result.get();
	}
	public void setResult(Regex.Result result) {
		this.result.set(result);
	}
	public ObjectProperty<Regex.Result> resultProperty() {
		return result;
	}
}