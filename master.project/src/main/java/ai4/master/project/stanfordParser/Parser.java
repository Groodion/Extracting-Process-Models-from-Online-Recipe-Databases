package ai4.master.project.stanfordParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.stanfordParser.sentence.PunctuationMark;
import ai4.master.project.stanfordParser.sentence.Sentence;
import ai4.master.project.stanfordParser.sentence.SentencePart;
import ai4.master.project.stanfordParser.sentence.Word;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Parser {

	public static final double ERROR = .20;

	private MaxentTagger tagger;
	private KeyWordDatabase kwdb;

	
	public Parser(String taggerUrl) {
		tagger = new MaxentTagger(taggerUrl);
		kwdb = new KeyWordDatabase();
	}
	
	private List<Sentence> analyzeText(String text) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		List<List<TaggedWord>> taggedList = tagger.process(getSplittedSentencesFromString(text));
		Sentence sentence = null;
		
		for(List<TaggedWord> taggedSentence : taggedList) {			
			sentence = new Sentence(sentence);
			SentencePart part = new SentencePart(sentence);
			
			Word word = null;
			
			for(TaggedWord taggedWord : taggedSentence) {
				if(taggedWord.tag().equals(STTSTag.KON.name())) {
					word = null;
					part = new SentencePart(part, sentence);
				}
				
				try {
					STTSTag tag = STTSTag.valueOf(taggedWord.tag());
					word = new Word(taggedWord.word(), tag, word, part);
				} catch(Exception e) {
					new PunctuationMark(taggedWord.word(), part);
				}
			}
			
			for(int j = 0; j < sentence.getParts().size() - 1; j++) {
				if(!sentence.getParts().get(j).containsVerb()) {
					sentence.getParts().get(j).mergeWith(sentence.getParts().get(j + 1));
					j--;
				}
			}
			
			
			sentences.add(sentence);
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
		String text = recipe.getPreparation();
		recipe.setPreparation(text);

		List<Sentence> sentences =  analyzeText(text);
		
		for(Sentence sentence : sentences) {
			sentence.init(kwdb);
		}
		
		for(Sentence s : sentences) {
			for(SentencePart sP : s.getParts()) {
				System.out.println(sP.getText());
				Step step = new Step();
				BaseCookingAction action = sP.getCookingAction();
				
				step.setText(sP.getText());
				step.setCookingAction(new CookingAction(sP.getMainVerb().getText(), action));
				
				step.getIngredients().addAll(sP.getIngredients());
				step.getTools().addAll(sP.getTools());
				
				recipe.getSteps().add(step);
			}
		}
	}
}