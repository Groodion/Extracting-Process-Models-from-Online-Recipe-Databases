package ai4.master.project.stanfordParser;

/**
 * 
 * @author Martin Käppel
 * Describes the different tags of the Penn Treebank Project and maps them
 * to description
 *
 */
public enum PennTreebankTags {
	CC("Coordinating conjunction"),
	CD("Kardinalzahl"),
	DT("Artikel"),
	EX("Existential there"),
	FW("Foreign word"),
	IN("Preposition or subordunating conjunction"),
	JJ("Adjective"),
	JJR("Adjective, comparative"),
	JJS("Adjection, superlative"),
	LS("List item marker"),
	MD("Modal"),
	NN("Noun, singular or mass"),
	NNS("Noun plural"),
	NNP("Proper noun, singular"),
	NNPS("Proper nounm plural"),
	PDT("Predeterminer"),
	POS("Possessive ending"),
	PRP("Personal pronoun"),
	PRP$("Possessive pronoun"),
	RB("Adverb"),
	RBR("Adverb, comparative"),
	RBS("Adverb, superlative"),
	RP("Particle"),
	SYM("Symbol"),
	TO("to"),
	UH("Interjection"),
	VB("Verb, base form"),
	VBD("Verb past tense"),
	VBG("Verb, gerund or present participle"),
	VBN("Verb, past participle"),
	VBP("Verb, non-3rd person singular present"),
	VBZ("Verb, 3rd person singular present"),
	WDT("Wh determiiner"),
	WP("Wh pronoun"),
	WP$("Possessive wh pronoun"),
	WRB("Wh adverb")
	;

	private String description;

	private PennTreebankTags(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
