package StanfordTest;

import java.io.IOException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Test  {

  /** A logger for this class */
  //private static Redwood.RedwoodChannels log = Redwood.channels(Test.class);

  private Test() {}

  public static void main(String[] args) throws IOException,
  ClassNotFoundException {

	  String a = "Die Stangen in die Suppe rühren!";
	  MaxentTagger tagger =  new MaxentTagger("lib/models/german-ud.tagger");
	  String tagged = tagger.tagString(a);
	  System.out.println(tagged);
}
}
