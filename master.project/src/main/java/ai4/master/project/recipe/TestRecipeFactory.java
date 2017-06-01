package ai4.master.project.recipe;

import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;

import java.util.ArrayList;
import java.util.List;

public class TestRecipeFactory {
	
	public Recipe createRecipe() {
		Recipe recipe = new Recipe(LANG_FLAG.DE);
		
		BaseIngredient bI_schmand = new BaseIngredient();
		bI_schmand.getNames().add("Schmand");
		Ingredient schmand = bI_schmand.toObject();
		schmand.getTags().add(new QuantifierTag("200g"));
		
		BaseIngredient bI_schinkenwuerfel = new BaseIngredient();
		bI_schinkenwuerfel.getNames().add("Schinkenwuerfel");
		Ingredient schinkenwuerfel = bI_schinkenwuerfel.toObject();
		schinkenwuerfel.getTags().add(new IngredientTag("geraeuchert"));
		schinkenwuerfel.getTags().add(new IngredientTag("gewuerfelt"));
		schinkenwuerfel.getTags().add(new QuantifierTag("80g"));
		
		BaseIngredient bI_kaese = new BaseIngredient();
		bI_kaese.getNames().add("Kaese");
		Ingredient kaese = bI_kaese.toObject();
		kaese.getTags().add(new IngredientTag("gerieben"));
		kaese.getTags().add(new QuantifierTag("100g"));
		
		BaseTool bT_backpapier = new BaseTool();
		bT_backpapier.getNames().add("Backpapier");
		Tool backpapier = bT_backpapier.toObject();
		backpapier.setImplicit(false);
		
		BaseTool bT_blech = new BaseTool();
		bT_backpapier.getNames().add("Backblech");
		Tool backblech = bT_blech.toObject();
		backpapier.setImplicit(false);
		
		BaseTool bT_backofen = new BaseTool();
		bT_backofen.getNames().add("Backofen");
		Tool backofen = bT_backofen.toObject();
		backofen.setImplicit(true);
		
		BaseTool bT_messer = new BaseTool();
		bT_messer.getNames().add("Messer");
		Tool messer = bT_messer.toObject();
		messer.setImplicit(true);
		
		BaseIngredient bI_blaetterTeig = new BaseIngredient();
		bI_blaetterTeig.getNames().add("Blaetterteig");
		Ingredient blaetterteig = bI_blaetterTeig.toObject();
		blaetterteig.getTags().add(new QuantifierTag("1 Packung"));
		blaetterteig.getTags().add(new IngredientTag("aus dem Kuehlregal"));
		blaetterteig.getTags().add(new IngredientTag("rechteckig"));
		
		BaseIngredient bI_teigHaelfte1 = new BaseIngredient();
		bI_teigHaelfte1.getNames().add("Teighaelfte1");
		Ingredient blaetterteigHaelfte1 = bI_teigHaelfte1.toObject();
		blaetterteigHaelfte1.getTags().add(new QuantifierTag("halbiert"));
		
		BaseIngredient bI_teigHaelfte2 = new BaseIngredient();
		bI_teigHaelfte2.getNames().add("Teighaelfte2");
		Ingredient blaetterteigHaelfte2 = bI_teigHaelfte2.toObject();
		blaetterteigHaelfte2.getTags().add(new QuantifierTag("halbiert"));


		BaseCookingAction bA_aufrollen = new BaseCookingAction();
		bA_aufrollen.getNames().add("aufrollen");
		bA_aufrollen.setResult("aufgerollter Blaetterteig");
		BaseIngredient bI_aufgerollterBlaetterteig = bA_aufrollen.transform(bI_blaetterTeig, null);
		Ingredient i_aufgerollterBlaetterteig = bI_aufgerollterBlaetterteig.toObject();
		i_aufgerollterBlaetterteig.getTags().add(new IngredientTag("aufgerollt"));
		CookingAction cA_aufrollen = bA_aufrollen.toObject();
		
		BaseCookingAction bA_bestreichen = new BaseCookingAction();
		bA_bestreichen.getNames().add("bestreichen");
		bA_bestreichen.setResult("mit Schmand bestrichene Teighaelfte");
		List<BaseIngredient> bestreichen = new ArrayList<BaseIngredient>();
		bestreichen.add(bI_teigHaelfte1);
		BaseIngredient bI_bestricheneHaelfte = bA_bestreichen.transform(bI_schmand, bestreichen);
		Ingredient i_bestricheneHaelfte = bI_bestricheneHaelfte.toObject();
		i_bestricheneHaelfte.getTags().add(new IngredientTag("bestrichen"));
		CookingAction cA_bestreichen = bA_bestreichen.toObject();
		
		List<BaseIngredient> belag = new ArrayList<BaseIngredient>();
		belag.add(bI_kaese);
		belag.add(bI_schinkenwuerfel);
		BaseCookingAction bA_verteilen = new BaseCookingAction();
		bA_verteilen.getNames().add("verteilen");
		bA_verteilen.setResult("belegte Blaetterteighaelfte");
		BaseIngredient bI_bestricheneBelegteHaelfte = bA_verteilen.transform(bI_bestricheneHaelfte, belag);
		Ingredient i_bestricheneBelegteHaelfte = bI_bestricheneBelegteHaelfte.toObject();
		i_bestricheneBelegteHaelfte.getTags().add(new IngredientTag("belegt"));
		CookingAction cA_verteilen = bA_verteilen.toObject();

		BaseCookingAction bA_klappen = new BaseCookingAction();
		bA_klappen.getNames().add("falten");
		bA_klappen.setResult("gefalteter Blaetterteig");
		List<BaseIngredient> faltenList = new ArrayList<BaseIngredient>();
		faltenList.add(bI_teigHaelfte2);
		BaseIngredient bI_ersteFaltung = bA_klappen.transform(bI_bestricheneBelegteHaelfte, faltenList);
		Ingredient i_ersteFaltung = bI_ersteFaltung.toObject();
		i_ersteFaltung.getTags().add(new IngredientTag("gefalten"));
		CookingAction cA_klappen = bA_klappen.toObject();
		
		
		BaseCookingAction bA_bestreichen2 = new BaseCookingAction();
		bA_bestreichen2.getNames().add("Haelfte von der Haelfte bestreichen");
		bA_bestreichen2.setResult("Mit Schmand bestrichtene Teighaelfte");
		List<BaseIngredient> bestreichen2List = new ArrayList<BaseIngredient>();
		bestreichen2List.add(bI_schmand);
		BaseIngredient bI_bestrichen2 = bA_bestreichen2.transform(bI_ersteFaltung, bestreichen2List);
		Ingredient i_bestrichen2 = bI_bestrichen2.toObject();
		i_bestrichen2.getTags().add(new IngredientTag("bestrichen"));
		CookingAction cA_bestreichen2 = bA_bestreichen2.toObject();
		
		BaseCookingAction bA_belegen2 = new BaseCookingAction();
		bA_belegen2.getNames().add("belegen");
		bA_belegen2.setResult("Belegter Blaetterteig");
		List<BaseIngredient> belegen2List = new ArrayList<BaseIngredient>();
		belegen2List.add(bI_kaese);
		belegen2List.add(bI_schinkenwuerfel);
		BaseIngredient bI_belegt2 = bA_belegen2.transform(bI_bestrichen2, belegen2List);
		Ingredient i_belegt2 = bI_belegt2.toObject();
		i_belegt2.getTags().add(new IngredientTag("belegt"));
		CookingAction cA_belegen2 = bA_belegen2.toObject();
		
		BaseCookingAction bA_klappen2 = new BaseCookingAction();
		bA_klappen2.getNames().add("klappen");
		bA_klappen2.setResult("Gefalteter Blaetterteig");
		BaseIngredient bI_zweiteFaltung = bA_klappen2.transform(bI_belegt2, null);
		Ingredient i_zweiteFaltung = bI_zweiteFaltung.toObject();
		i_zweiteFaltung.getTags().add(new IngredientTag("gefalten"));
		CookingAction cA_klappen2 = bA_klappen2.toObject();
		
		BaseCookingAction bA_schneiden = new BaseCookingAction();
		bA_schneiden.getNames().add("schneiden");
		bA_schneiden.setResult("Stangen");
		BaseIngredient bI_streifen = bA_schneiden.transform(bI_zweiteFaltung, null);
		Ingredient i_streifen = bI_streifen.toObject();
		i_streifen.getTags().add(new IngredientTag("geschnitten"));
		CookingAction cA_schneiden = bA_schneiden.toObject();
		
		BaseCookingAction bA_drehen = new BaseCookingAction();
		bA_drehen.setName("drehen");
		bA_drehen.setResult("gedrehte Stangen");
		BaseIngredient bI_gedrehteStangen = bA_drehen.transform(bI_streifen, null);
		Ingredient i_gedrehteStangen = bI_gedrehteStangen.toObject();
		i_gedrehteStangen.getTags().add(new IngredientTag("spiralf�rmig"));
		CookingAction cA_drehen = bA_drehen.toObject();

		BaseCookingAction bA_legen = new BaseCookingAction();
		bA_legen.getNames().add("legen");
		bA_legen.setResult("Mit Stangen belegtes Blech");
		BaseIngredient bI_gelegteStangen = bA_legen.transform(bI_gedrehteStangen, null);
		Ingredient i_gelegteStangen = bI_gelegteStangen.toObject();
		i_gelegteStangen.getTags().add(new IngredientTag("gelegt"));
		CookingAction cA_legen = bA_legen.toObject();
		
		BaseCookingAction bA_backen = new BaseCookingAction();
		bA_backen.getNames().add("backen");
		bA_backen.setResult("gebackene Stangen");
		BaseIngredient bI_gebackeneStangen = bA_backen.transform(bI_gelegteStangen, null);
	Ingredient i_gebackeneStangen = bI_gebackeneStangen.toObject();
		i_gebackeneStangen.getTags().add(new IngredientTag("gebacken"));
		CookingAction cA_backen = bA_backen.toObject();
		
		//Creating Steps
		Step step1 = new Step();
		step1.getIngredients().add(blaetterteig);
		step1.setCookingAction(cA_aufrollen);
		step1.setText("Den Blaetterteig aufrollen");
		
		
		Step step2 = new Step();
		step2.setText("und eine Teighaelfte mit gut der Haelfte des Schmands bestreichen");
		step2.setCookingAction(cA_bestreichen);
		step2.getTools().add(messer);
		
		//Evtl. Steps hierarchisch anordnen
		Step step3 = new Step();
		step3.setText("Die Haelfte der Schinkenwuerfel und des Kaeses darauf verteilen");
		step3.setCookingAction(cA_verteilen);
		
		Step step4 = new Step();
		step4.setText("Die Seite des Blaetterteiges, die nicht belegt ist auf die andere Seite klappen");
		step4.setCookingAction(cA_klappen);
		
		Step step5 = new Step();
		step5.setText("Wiederum die Haelfte des Teiges mit dem restlichen Schmand bestreichen");
		step5.setCookingAction(cA_bestreichen2);
		step5.getTools().add(messer);
		
		Step step6 = new Step();
		step6.setText("und die Schinkenwuerfel und Kaeseraspel darauf geben");
		step6.setCookingAction(cA_belegen2);
		
		Step step7 = new Step();
		step7.setText("Die unbestrichene Teighaelfte darueber klappen");
		step7.setCookingAction(cA_klappen2);
		
		Step step8 = new Step();
		step8.setText("Den Blaetterteig in Streifen schneiden");
		step8.setCookingAction(cA_schneiden);
		step8.getTools().add(messer);
		
		Step step9 = new Step();
		step9.setText("Vorsichtig spiralfoermig drehen");
		step9.setCookingAction(cA_drehen);
		
		Step step10 = new Step();
		step10.setText("und auf ein mit Backpapier belegtes Blech legen");
		step10.setCookingAction(cA_legen);
		step10.getTools().add(backblech);
		step10.getTools().add(backpapier);
		
		Step step11 = new Step();
		step11.setText("Bei 180Grad ca. 25 Minuten backen");
		step11.setCookingAction(cA_backen);
		step11.getTools().add(backofen);
		CookingEvent ev = new CookingEvent("backen", EventType.TIMER);
		step11.setEvent(ev);

		recipe.getSteps().add(step1);
		recipe.getSteps().add(step2);
		recipe.getSteps().add(step3);
		recipe.getSteps().add(step4);
		recipe.getSteps().add(step5);
		recipe.getSteps().add(step6);
		recipe.getSteps().add(step7);
		recipe.getSteps().add(step8);
		recipe.getSteps().add(step9);
		recipe.getSteps().add(step10);
		recipe.getSteps().add(step11);
		
		recipe.getIngredients().add(bI_blaetterTeig.toObject());
		recipe.getIngredients().add(bI_kaese.toObject());
		recipe.getIngredients().add(bI_schinkenwuerfel.toObject());
		recipe.getIngredients().add(bI_schmand.toObject());
		
		return recipe;
	}
}

