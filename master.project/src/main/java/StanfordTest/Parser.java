package StanfordTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Parser {

	private MaxentTagger tagger;
	private List<String> nouns = new ArrayList<String>();
	private List<String> verbs = new ArrayList<String>();
	private List<String> adjectives = new ArrayList<String>();

	public Parser(String taggerUrl) {
		tagger = new MaxentTagger(taggerUrl);
	}
	
	public List<String> getNouns() {
		return nouns;
	}
	
	public List<String> getVerbs() {
		return verbs;
	}
	
	public List<String> getAdjectives() {
		return adjectives;
	}
	
	public void analyzeText(String url) {
		List<List<TaggedWord>> taggedList = tagger.process(getSplittedSentencesFromUrl(url));
		
		System.out.println(taggedList.toString());
		
		for(int i = 0; i < taggedList.size(); i++) {
			for(int j = 0; j < taggedList.get(i).size(); j++) {
				String actualTag = taggedList.get(i).get(j).tag();
				
				if(actualTag.equals(STTSTags.NN.name()) 
						|| actualTag.equals(STTSTags.NE.name())
						) {
					nouns.add(taggedList.get(i).get(j).word());
				}
				else if(actualTag.equals(STTSTags.VVFIN.name())
						|| actualTag.equals(STTSTags.VAFIN.name())
						|| actualTag.equals(STTSTags.VMFIN.name())
						|| actualTag.equals(STTSTags.VVINF.name())
						|| actualTag.equals(STTSTags.VAINF.name())
						|| actualTag.equals(STTSTags.VMINF.name())
						|| actualTag.equals(STTSTags.VVIMP.name())
						|| actualTag.equals(STTSTags.VAIMP.name())
						|| actualTag.equals(STTSTags.VVPP.name())
						|| actualTag.equals(STTSTags.VAPP.name())
						|| actualTag.equals(STTSTags.VMPP.name())
						|| actualTag.equals(STTSTags.VVIZU.name())
						) {
					verbs.add(taggedList.get(i).get(j).word());
				}
				else if(actualTag.equals(STTSTags.ADJD.name()) 
						|| actualTag.equals(STTSTags.ADJA.name())
						) {
					adjectives.add(taggedList.get(i).get(j).word());
				}
			}
		}
	}
	
	public List<List<HasWord>> getSplittedSentencesFromString(String text) {
		StringReader reader = new StringReader(text);

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		Iterator<List<HasWord>> sentences = dp.iterator();
		List<List<HasWord>> sentencesList = new ArrayList<List<HasWord>>();
		
		while(sentences.hasNext()) {
			sentencesList.add(sentences.next());
		}

		for(int i = 0; i < sentencesList.size(); i++) {
			System.out.println(sentencesList.get(i));
		}
		
		reader.close();
		
		return sentencesList;
	}
	
	public List<List<HasWord>> getSplittedSentencesFromUrl(String url) {
		BufferedReader reader = null;
		File file = new File(url);

		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		
		Iterator<List<HasWord>> sentences = dp.iterator();
		List<List<HasWord>> sentencesList = new ArrayList<List<HasWord>>();
		
		while(sentences.hasNext()) {
			sentencesList.add(sentences.next());
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sentencesList;
	}

	public List<String> getSentencesFromFile(String url) {
		BufferedReader reader = null;
		File file = new File(url);

		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		List<String> sentencesList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
			String sentenceString = SentenceUtils.listToString(sentence);
			sentencesList.add(sentenceString);
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sentencesList;
	}

	public List<String> getSentencesFromString(String text) {
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
}