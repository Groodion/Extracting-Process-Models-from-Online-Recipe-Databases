package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.ImplicitNamedObject;
import ai4.master.project.recipe.object.NamedObject;


public class ItemGroup<T extends BaseNamedObject<N, T>, N extends ImplicitNamedObject<T>> {
	
	private List<T> items;
	
	
	public ItemGroup() {
		items = new ArrayList<T>();
	}
	@SuppressWarnings("unchecked")
	private ItemGroup(ItemGroup<T, N> parent, KeyWordDatabase kwdb) {
		this();
		
		for(T item : parent.items) {
			items.add((T) kwdb.find(item.getFirstName()));
		}
	}

	public List<T> getItems() {
		return items;
	}
	
	public T getItem() {
		if(items.isEmpty()) {
			return null;
		} else {
			return items.get(0);
		}
	}
	public N getImpliedItem() {
		if(items.isEmpty()) {
			return null;
		} else {
			N item = items.get(0).toObject();
			item.setImplicit(true);
			return item;
		}
	}

	public boolean checkList(List<N> list) {
		for(NamedObject<T> object : list) {
			if(items.contains(object.getBaseObject())) {
				return true;
			}
			if(object.getBaseObject() instanceof BaseIngredient) {
				for(BaseIngredientGroup ingredientGroup : ((BaseIngredient) object.getBaseObject()).getIngredientGroups()) {
					if(items.contains(ingredientGroup)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		
		for(int i = 0; i < items.size(); i++) {
			if(i != 0) {
				sB.append(", ");
			}
			sB.append(items.get(i).getFirstName());
		}
		
		return sB.toString();
	}
	
	public String toXML() {
		if(items.size() == 1) {
			return getItem().toRefXML();
		} else {
			StringBuilder sB = new StringBuilder();
			
			sB.append("<ItemGroup>");
			for(T item : items) {
				sB.append(item.toRefXML());
			}
			sB.append("</ItemGroup>");
			
			return sB.toString();
		}
	}

	public ItemGroup<T, N> clone(KeyWordDatabase kwdb) {
		return new ItemGroup<T, N>(this, kwdb);
	}
}