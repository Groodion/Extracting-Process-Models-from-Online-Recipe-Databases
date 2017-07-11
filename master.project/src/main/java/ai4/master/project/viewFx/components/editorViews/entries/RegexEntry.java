package ai4.master.project.viewFx.components.editorViews.entries;

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
	private BooleanProperty chargeTools;
	private ObjectProperty<Regex.Result> result;
	
	public RegexEntry(Regex regex, ObservableList<Regex> regexList, ObservableList<String> regexIdList) {
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
			
			regex.setId(n);
		});
		
		expression.addListener((b, o, n) -> {
			regex.setExpression(n);
		});
		
		ingredientsNeeded = new SimpleBooleanProperty(regex.isIngredientsNeeded());
		referencePreviousProducts = new SimpleBooleanProperty(regex.isReferencePreviousProducts());
		chargeTools = new SimpleBooleanProperty(regex.isChargingTools());
		
		result = new SimpleObjectProperty<Regex.Result>(regex.getResult());
		
		ingredientsNeeded.addListener((b, o, n) -> regex.setIngredientsNeeded(n));
		referencePreviousProducts.addListener((b, o, n) -> regex.setReferencePreviousProducts(n));
		chargeTools.addListener((b, o, n) -> regex.setChargingTools(n));
		result.addListener((b, o, n) -> regex.setResult(n));
		
		if(regex.getId() != null && regex.getId().trim().length() != 0) {
			regexIdList.add(regex.getId());
		}
		if(regex.getId() != null && regex.getId().length() != 0) {
			regexIdList.add(regex.getId());
		}
	}

	public String getExpression() {
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
	public BooleanProperty referencePreviousProductsProperty() {
		return referencePreviousProducts;
	}
	
	public boolean isChargeTools() {
		return chargeTools.get();
	}
	public void setChargeTools(boolean chargeTools) {
		this.chargeTools.set(chargeTools);
	}
	public BooleanProperty chargeToolsProperty() {
		return chargeTools;
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