package ai4.master.project.viewFx.components.editorViews.entries;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.Transformation;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransformationEntry {

	private ObjectProperty<BaseIngredient> product;
	private StringProperty ingredientTag;
	private StringProperty quantifierTag;

	private ObservableList<BaseIngredient> mandatoryIngredients;
	private ObservableList<String> regexIds;

	
	public TransformationEntry(Transformation transformation, ObservableList<Transformation> transformations) {
		product = new SimpleObjectProperty<BaseIngredient>(transformation.getProduct() == null ? null : transformation.getProduct().getBaseObject());
		mandatoryIngredients = FXCollections.observableArrayList();
		regexIds = FXCollections.observableArrayList(transformation.getRegexIds());
		ingredientTag = new SimpleStringProperty();
		quantifierTag = new SimpleStringProperty();
		
		if(transformation.getTag() != null) {
			if(transformation.getTag() instanceof QuantifierTag) {
				quantifierTag.set(transformation.getTag().getName());
			} else {
				ingredientTag.set(transformation.getTag().getName());
			}
		}
		transformation.getMandatoryIngredients().forEach(i -> mandatoryIngredients.add(i.getBaseObject()));

		product.addListener((b, o, n) -> {
			if(n != null) {
				ingredientTag.set(null);
				quantifierTag.set(null);
				transformation.setProduct(n.toObject());
			} else {
				transformation.setProduct(null);
			}
		});
		ingredientTag.addListener((b, o, n) -> {
			if(n != null) {
				product.set(null);
				quantifierTag.set(null);
			}
			transformation.setTag(new IngredientTag(n));
		});
		quantifierTag.addListener((b, o, n) -> {
			if(n != null) {
				product.set(null);
				ingredientTag.set(null);
			}
			transformation.setTag(new QuantifierTag(n));
		});
		
		ListChangeListener<String> regexIdsChanged = change -> {
			transformation.getRegexIds().clear();
			transformation.getRegexIds().addAll(regexIds);
		};
		regexIds.addListener(regexIdsChanged); 
		
		ListChangeListener<BaseIngredient> mandatoryIngredientsChanged = change -> {
			transformation.getMandatoryIngredients().clear();
			mandatoryIngredients.forEach(i -> transformation.getMandatoryIngredients().add(i.toObject()));
		};
		mandatoryIngredients.addListener(mandatoryIngredientsChanged); 
		
	}

	public BaseIngredient getProduct() {
		return product.get();
	}
	public void setProduct(BaseIngredient product) {
		this.product.set(product);
	}
	public ObjectProperty<BaseIngredient> productProperty() {
		return product;
	}
	
	public String getIngredientTag() {
		return ingredientTag.get();
	}
	public void setIngredientTag(String ingredientTag) {
		this.ingredientTag.set(ingredientTag);
	}
	public StringProperty ingredientTagProperty() {
		return ingredientTag;
	}
	
	public String getQuantifierTag() {
		return ingredientTag.get();
	}
	public void setQuantifierTag(String quantifierTag) {
		this.quantifierTag.set(quantifierTag);
	}
	public StringProperty quantifierTagProperty() {
		return quantifierTag;
	}
	
	public ObservableList<BaseIngredient> getMandatoryIngredients() {
		return mandatoryIngredients;
	}
	public ObservableList<String> getRegexIds() {
		return regexIds;
	}
}