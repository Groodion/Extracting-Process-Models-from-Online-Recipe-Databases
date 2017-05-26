package ai4.master.project.stanfordParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class LexParser {

	public static LexParser LEX = new LexParser();

	private StanfordCoreNLP pipeline;
	
	
	private LexParser() {
		Properties germanProperties = StringUtils
				.argsToProperties(new String[] { "-props",
						"resources/StanfordCoreNLP-german.properties" });
		pipeline = new StanfordCoreNLP(germanProperties);
	}
	
	public List<Tree> parse(String text) {
		List<Tree> sentenceTrees = new ArrayList<Tree>();
		
		Annotation germanAnnotation = new Annotation(text);
		pipeline.annotate(germanAnnotation);
		
		for (CoreMap sentence : germanAnnotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree sentenceTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
			
			sentenceTrees.add(sentenceTree);
		}
		
		return sentenceTrees;
	}
}
