package ai4.master.project.stanfordParser;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.*;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.baseObject.Regex;
import ai4.master.project.recipe.baseObject.Regex.Result;
import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.IngredientGroup;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.stanfordParser.sentence.PunctuationMark;
import ai4.master.project.stanfordParser.sentence.Sentence;
import ai4.master.project.stanfordParser.sentence.SentencePart;
import ai4.master.project.stanfordParser.sentence.Word;
import ai4.master.project.viewFx.Controller;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {

	private MaxentTagger tagger;
	private KeyWordDatabase kwdb;
	private DoubleProperty progress;
	
	public Parser(String taggerUrl) {
		tagger = new MaxentTagger(taggerUrl);
		kwdb = new KeyWordDatabase();
		progress = new SimpleDoubleProperty();
	}

	private List<Sentence> analyzeText(String text) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		// Text wird in saetze zerlegt
		List<List<TaggedWord>> taggedList = tagger.process(getSplittedSentencesFromString(text));
		Sentence sentence = null;

		for (int i = 0; i < taggedList.size(); i++) {
			List<TaggedWord> taggedSentence = taggedList.get(i);
			sentence = new Sentence(sentence);
			SentencePart part = new SentencePart(sentence);

			Word word = null;

			for (TaggedWord taggedWord : taggedSentence) {
				if (taggedWord.tag().equals(STTSTag.KON.name())) {
					// Wenn ein UND oder ODER gefunden wird, wird ein neuer
					// Satzteil erzeugt
					word = null;
					part = new SentencePart(part, sentence);
				}

				if (taggedWord.word().equals("-LRB-")) {
					taggedWord.setWord("(");
					taggedWord.setTag("$(");
				}
				if (taggedWord.word().equals("-RRB-")) {
					taggedWord.setWord(")");
					taggedWord.setTag("$(");
				}

				try {
					STTSTag tag = STTSTag.valueOf(taggedWord.tag());
					word = new Word(taggedWord.word(), tag, word, part);
				} catch (Exception e) {
					// Satzzeichen erzeugen neuen Satzteil
					new PunctuationMark(taggedWord.word(), part);
					word = null;
					part = new SentencePart(part, sentence);
				}
			}

			// Satzteile ohne eigenes Verb werden mit dem n�chsten Satzteil
			// verschmolzen
			for (int j = 0; j < sentence.getParts().size() - 1; j++) {
				if (!sentence.getParts().get(j).containsVerb()) {
					if (sentence.getParts().get(j).containsWord("ebenfalls")
							|| sentence.getParts().get(j).containsWord(")")) {
						sentence.getParts().get(j - 1).mergeWith(sentence.getParts().get(j));
					} else {
						sentence.getParts().get(j).mergeWith(sentence.getParts().get(j + 1));
					}
					j--;
				}
			}

			if (sentence.getParts().get(sentence.getParts().size() - 1).getWords().isEmpty()) {
				sentence.getParts().remove(sentence.getParts().size() - 1);
			}

			sentences.add(sentence);
			
			progress.set((i+1) * 0.25 / taggedList.size());
		}

		return sentences;
	}
	private List<List<HasWord>> getSplittedSentencesFromString(String text) {
		StringReader reader = new StringReader(text);

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		Iterator<List<HasWord>> sentences = dp.iterator();
		List<List<HasWord>> sentencesList = new ArrayList<List<HasWord>>();

		while (sentences.hasNext()) {
			sentencesList.add(sentences.next());
		}

		reader.close();

		return sentencesList;
	}

	public KeyWordDatabase getKwdb() {
		return kwdb;
	}
	public void setKwdb(KeyWordDatabase kwdb) {
		this.kwdb = kwdb;
	}

	public void parseRecipe(Recipe recipe) {
		progress.set(0);
		recipe.getSteps().clear();
		
		String text = recipe.getPreparation();
		recipe.setPreparation(text);

		
		List<Sentence> sentences = analyzeText(text);
		progress.set(0.25);
		

		for (int i = 0; i < sentences.size(); i++) {
			sentences.get(i).init(kwdb);
			progress.set(0.25 + (i+1) * 0.25 / sentences.size());
		}
		progress.set(0.5);

		IngredientList activeIngredients = new IngredientList();
		List<BaseTool> chargedTools = new ArrayList<BaseTool>();

		for (String s : recipe.getIngredients()) {
			String name = s.split("\\(")[0];
			BaseIngredient ingredient = kwdb.findIngredient(name);

			if (ingredient != null) {
				activeIngredients.add(ingredient.toObject());
			} else {
				Controller.MESSAGES.add("Unknown Ingredient: " + name);
			}
		}

		Step lastStep = null;

		for (int j = 0; j < sentences.size(); j++) {
			Sentence s = sentences.get(j);
			double maxProg = 0.5 / sentences.size();
			for (int k = 0; k < s.getParts().size(); k++) {
				SentencePart sP = s.getParts().get(k);
				if (sP.getCookingAction() == null) {
					Controller.MESSAGES.add("Can't convert to Step:");
					Controller.MESSAGES.add(sP.getText());
				} else {
					Step step = new Step();
					BaseCookingAction action = sP.getCookingAction();

					step.setText(sP.getText());
					step.setCookingAction(new CookingAction(sP.getMainVerb().getText(), action));

					step.getIngredients().addAll(sP.getIngredients());
					step.getTools().addAll(sP.getTools());

					boolean needsImplicitTool = !step.getCookingAction().getBaseObject().getImplicitTools().isEmpty();

					if (needsImplicitTool) {
						for (Tool tool : step.getTools()) {
							if (step.getCookingAction().getBaseObject().getImplicitTools()
									.contains(tool.getBaseObject())) {
								needsImplicitTool = false;
								break;
							}
						}
						if (needsImplicitTool) {
							Tool implicitTool = step.getCookingAction().getBaseObject().getImplicitTools().get(0)
									.toObject();
							implicitTool.setImplicit(true);
							step.getTools().add(implicitTool);
						}
					}

					if (sP.containsLastSentenceProductReference()) {
						if (lastStep == null) {
							Controller.MESSAGES.add("LastSentenceReference in first sentence!");
						} else {
							step.getIngredients().addAll(lastStep.getProducts());
						}
					}

					if (lastStep != null) {
						for (int i = 0; i < step.getIngredients().size(); i++) {
							for (Ingredient product : lastStep.getProducts()) {
								if (step.getIngredients().get(i).getBaseObject() == product.getBaseObject()
										&& step.getIngredients().get(i).getTags().isEmpty()) {
									step.getIngredients().set(i, product);
								}
							}
						}
					}

					boolean noResult = false;
					boolean ingredientsNeeded = true;
					Regex mRegex = new Regex(".*", Result.ALL);
					
					for (Ingredient ingredient : step.getIngredients()) {
						if (ingredient instanceof IngredientGroup) {
							List<Ingredient> aIngredients = activeIngredients.get(ingredient.getBaseObject());
							if(aIngredients.size() != 1 || aIngredients.get(0).getBaseObject() != ingredient.getBaseObject()) {
								((IngredientGroup) ingredient).getIngredients().addAll(aIngredients);
							}
						}
					}
					
					for(Tool tool : step.getTools()) {
						if(chargedTools.contains(tool.getBaseObject())) {
							tool.setCharged(true);
						}
					}
					
					for (Regex regex : action.getRegexList()) {
						if (sP.matches(regex.getExpression(), false)) {
							ingredientsNeeded = regex.isIngredientsNeeded();
							mRegex = regex;
							
							if (regex.isReferencePreviousProducts()) {
								step.getIngredients().addAll(lastStep.getProducts());
							}

							switch (regex.getResult()) {
							case ALL:
								for (Ingredient ingredient : step.getIngredients()) {
									step.getProducts().addAll(action.transform(ingredient, step.getIngredients(), regex));
								}
								break;
							case FIRST:
								if (!step.getIngredients().isEmpty())
									step.getProducts().addAll(
											action.transform(step.getIngredients().get(0), step.getIngredients(), regex));
								break;
							case LAST:
								if (!step.getIngredients().isEmpty())
									step.getProducts()
											.addAll(action.transform(
													step.getIngredients().get(step.getIngredients().size() - 1),
													step.getIngredients(), regex));
								break;
							case PREV:
								step.getIngredients().addAll(lastStep.getProducts());
								for (Ingredient ingredient : lastStep.getProducts()) {
									step.getProducts().addAll(action.transform(ingredient, step.getIngredients(), regex));
								}
								break;
							case NO_RESULT:
								noResult = true;
								break;
							default:
								break;
							}
							
							break;
						}
					}
					sP.clearMemory();
										
					if (lastStep != null && ingredientsNeeded) {
						if (step.getIngredients().isEmpty()) {
							step.getIngredients().addAll(lastStep.getProducts());

							if (!noResult) {
								for (Ingredient ingredient : lastStep.getProducts()) {
									if (ingredient != null) {
										step.getProducts().addAll(action.transform(ingredient, step.getIngredients(), mRegex));
									}
								}
							}
						}
					}
					
					while (step.getIngredients().remove(null));
					while (step.getProducts().remove(null));

					for (int i = 0; i < step.getIngredients().size(); i++) {
						Ingredient ingredient = step.getIngredients().get(i);
						if (activeIngredients.contains(ingredient.getBaseObject())) {
							step.getIngredients().remove(i);
							step.getIngredients().addAll(activeIngredients.get(ingredient.getBaseObject()));
							activeIngredients.remove(ingredient.getBaseObject());
						}
					}

					step.getProducts().forEach(product -> activeIngredients.add(product));

					step.getEvents().addAll(sP.getEvents());

					if (step.getEvents().isEmpty() && sP.containsEventIndicator()) {
						step.getEvents().add(new CookingEvent(sP.getText(), EventType.TIMER, Position.AFTER));
					}

					if(mRegex.isChargingTools() || step.getIngredients().isEmpty()) {
						for(Tool tool : step.getTools()) {
							if(!tool.isCharged()) {
								chargedTools.add(tool.getBaseObject());
							}
						}
					}
					
					recipe.getSteps().add(step);
					
					lastStep = step;
				}
				progress.set(0.5 + j * maxProg + (k+1) * maxProg / s.getParts().size());
			}
			progress.set(0.5 + (j+1) * maxProg);
		}
		progress.set(1);
	}

	public DoubleProperty progressProperty() {
		return progress;
	}
}