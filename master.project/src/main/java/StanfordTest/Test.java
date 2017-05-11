package StanfordTest;


public class Test {
	
	
	public static void main(String args[]) {
		Parser p = new Parser("lib/models/german-fast.tagger");
		
		System.out.println(p.getSentencesFromFile("test.txt"));
		System.out.println(p.getSentencesFromString("Das ist ein Satz. Dies ist ein weiterer Satz. Und noch einer hintendran!?"));
		System.out.println("---------------------------");
		
		System.out.println(p.getSplittedSentencesFromUrl("test.txt"));
		System.out.println("---------------------------");
		
		p.analyzeText("test.txt");
		System.out.println("---------------------------");
		System.out.println(p.getNouns());
		System.out.println(p.getVerbs());
		System.out.println(p.getAdjectives());
		
	}


	
}
