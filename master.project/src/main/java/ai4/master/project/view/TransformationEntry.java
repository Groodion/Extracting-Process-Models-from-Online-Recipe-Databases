package ai4.master.project.view;

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
	
	private ObservableList<String> refRegexIds;
	private ObservableList<BaseIngredient> ingredients;
	private StringProperty ingredientTag;
	private StringProperty quantifierTag;
	private ObjectProperty<BaseIngredient> product;

	
	public TransformationEntry(Transformation transformation, ObservableList<TransformationEntry> parent, final ObservableList<Transformation> transformationList, ObservableList<String> refIds) {
		refRegexIds = FXCollections.observableArrayList(transformation.getRegexIds());
		ingredients = FXCollections.observableArrayList();
		
		ingredientTag = new SimpleStringProperty();
		quantifierTag = new SimpleStringProperty();
		product = new SimpleObjectProperty<BaseIngredient>();
		
		ingredientTag.addListener((b, o, n) -> {
			if(n != null && n.length() != 0) {
				quantifierTag.set(null);
				product.set(null);
				transformation.setTag(new IngredientTag(n));
				transformation.setProduct(null);
			}
		});
		quantifierTag.addListener((b, o, n) -> {
			if(n != null && n.length() != 0) {
				ingredientTag.set(null);
				product.set(null);
				transformation.setTag(new QuantifierTag(n));
				transformation.setProduct(null);
			}
		});
		product.addListener((b, o, n) -> {
			if(n != null) {
				ingredientTag.set(null);
				quantifierTag.set(null);
				transformation.setTag(null);
				transformation.setProduct(n.toObject());
			}
		});
		
		if(transformation.getTag() != null) {
			if(transformation.getTag() instanceof QuantifierTag) {
				quantifierTag.set(transformation.getTag().getName());
			} else {
				ingredientTag.set(transformation.getTag().getName());
			}
		} else if(transformation.getProduct() != null) {
			product.set(transformation.getProduct().getBaseObject());
		}
		
		for(int i = 0; i < transformation.getMandatoryIngredients().size(); i++) {
			ingredients.add(transformation.getMandatoryIngredients().get(i).getBaseObject());
		}
		
		ListChangeListener<String> refIdsChanged = change -> {
			while(change.next()) {
				for(int j = 0; j < refRegexIds.size(); j++) {
					String refId = refRegexIds.get(j);
					if(change.getRemoved().contains(refId)) {
						int i = change.getRemoved().indexOf(refId);
						
						refRegexIds.set(j, change.getAddedSubList().get(i));
					}
				}
			}
		};
		refIds.addListener(refIdsChanged);
		
		parent.add(this);
	}

	public void setIngredientTag(String ingredientTag) {
		this.ingredientTag.set(ingredientTag);
	}
	public String getIngredientTag() {
		return ingredientTag.get();
	}
	public void setQuantifierTag(String quantifierTag) {
		this.quantifierTag.set(quantifierTag);
	}
	public String getQuantifierTag() {
		return quantifierTag.get();
	}
	public BaseIngredient getProduct() {
		return product.get();
	}
	public void setProduct(BaseIngredient product) {
		this.product.set(product);
	}
	
	public ObservableList<String> getRefRegexIds() {
		return refRegexIds;
	}
	public ObservableList<BaseIngredient> getIngredients() {
		return ingredients;
	}
}