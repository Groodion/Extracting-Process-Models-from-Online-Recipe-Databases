package ai4.master.project.stanfordParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.Ingredient;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.Tool;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Parser {

	public static final double ERROR = .40;

	private MaxentTagger tagger;
	private KeyWordDatabase kwdb;

	public Parser(String taggerUrl) {
		tagger = new MaxentTagger(taggerUrl);
		kwdb = new KeyWordDatabase();
	}
	
	private List<Sentence> analyzeText(String text) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		List<String> sentencesS = getSentencesFromString(text);
		List<List<TaggedWord>> taggedList = tagger.process(getSplittedSentencesFromString(text));
		
		for(int i = 0; i < sentencesS.size(); i++) {
			String sText = sentencesS.get(i);
			List<Sentence> parts = new ArrayList<Sentence>();
			Sentence sentence = new Sentence(sText, kwdb);
			parts.add(sentence);
			
			String adjective = null;
			String adverb = null;
			for(TaggedWord word : taggedList.get(i)) {
				
				String tag = word.tag();
				if(tag.equals(STTSTags.NN.name()) || tag.equals(STTSTags.NE.name())) {
					sentence.getNouns().add((adjective != null ? adjective + " ": "") + word.word());
					if(kwdb.isUnknown(word.word(), ERROR)) {
						System.err.println("Unknown noun found: " + word.word());
					}
					adjective = null;
				}
				else if(tag.equals(STTSTags.VVFIN.name())
						|| tag.equals(STTSTags.VAFIN.name())
						|| tag.equals(STTSTags.VMFIN.name())
						|| tag.equals(STTSTags.VVINF.name())
						|| tag.equals(STTSTags.VAINF.name())
						|| tag.equals(STTSTags.VMINF.name())
						|| tag.equals(STTSTags.VVIMP.name())
						|| tag.equals(STTSTags.VAIMP.name())
						|| tag.equals(STTSTags.VVPP.name())
						|| tag.equals(STTSTags.VAPP.name())
						|| tag.equals(STTSTags.VMPP.name())
						|| tag.equals(STTSTags.VVIZU.name())) {
					if(tag.equals(STTSTags.VVINF.name())) {
						sentence.setMainVerb(word.word());
						if(kwdb.isUnknown(word.word(), ERROR)) {
							System.err.println("Unknown action found: " + word.word());
						}
					}
					sentence.getVerbs().add((adverb == null ? "" : adverb + " ") + word.word());
					adverb = null;
				} else if(tag.equals(STTSTags.ADJD.name())) {
					adverb = word.word();
				} else if(tag.equals(STTSTags.ADJA.name())) {
					adjective = word.word();
				} else if(tag.equals(STTSTags.KON.name())) {
					sentence.setText(sText.substring(0, sText.toLowerCase().indexOf(word.word().toLowerCase())));
					sText = sText.substring(sText.toLowerCase().indexOf(word.word().toLowerCase()));
					sentence = new Sentence(sText, kwdb);
					parts.add(sentence);
				}
			}
			
			for(int j = 0; j < parts.size(); j++) {
				if(parts.get(j).getVerbs().isEmpty()) {
					parts.get(j).mergeWith(parts.get(j + 1));
					parts.remove(j + 1);
					j--;
				}
			}
			
			sentences.addAll(parts);
		}
		
		return sentences;
	}
	
	private List<List<HasWord>> getSplittedSentencesFromString(String text) {
		StringReader reader = new StringReader(text);

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		Iterator<List<HasWord>> sentences = dp.iterator();
		List<List<HasWord>> sentencesList = new ArrayList<List<HasWord>>();
		
		while(sentences.hasNext()) {
			sentencesList.add(sentences.next());
		}

/*		for(int i = 0; i < sentencesList.size(); i++) {
			System.out.println(sentencesList.get(i));
		}*/
		
		reader.close();
		
		return sentencesList;
	}
	private List<String> getSentencesFromString(String text) {
		StringReader reader = new StringReader(text);

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		List<String> sentencesList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
			String sentenceString = SentenceUtils.listToString(sentence);
			sentencesList.add(sentenceString);
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

	public Recipe parseText(String text) {		
		Recipe recipe = new Recipe();
		
		recipe.setPreparation(text);

		List<Sentence> sentences =  analyzeText(text);
		
		for(Sentence sentence : sentences) {
			for(int i = 0; i < sentence.getNouns().size(); i++) {
				String noun = sentence.getNouns().get(i);
				String lcNoun = noun.toLowerCase();
				for(String part : kwdb.getPartIndicators()) {
					if(lcNoun.contains(part.toLowerCase())) {
						String n = noun.substring(0, lcNoun.lastIndexOf(part.toLowerCase()));
						sentence.getNouns().add(n);
					}
				}
			}
			
			Step step = new Step();
			Ingredient reference = null;
			
			if(sentence.containsLastSentenceReference() && !recipe.getSteps().isEmpty()) {
				reference = recipe.getSteps().get(recipe.getSteps().size() - 1).getProduct();
			}
			if(reference != null) {
				step.getIngredients().add(reference);
			}
			
			step.setText(sentence.getText());
			step.setCookingAction(sentence.getCookingAction());
			step.getTools().addAll(sentence.getTools());
			step.getIngredients().addAll(sentence.getIngredients());
				
			if(step.getCookingAction() != null) {
				Ingredient mainIngredient = reference;
				if(mainIngredient == null) {
					mainIngredient = sentence.getMainIngredient();
				}
				step.setProduct(step.getCookingAction().transform(mainIngredient, step.getIngredients()));
			}
			
			recipe.getSteps().add(step);
		}
		
		return recipe;
	}
	
}