package ai4.master.project.stanfordParser;

public enum STTSTags {
	ADJA("attributives Adjektiv"),
	ADJD("adverbiales oder prädikatives Adjektiv"),
	ADV("Adverb"),
	APPR("Präposition, Zirkumposition links"),
	APPRART("Präposition mit Artikel"),
	APPO("Postposition"),
	APZR("Zirkumposition rechts"),
	ART("bestimmter oder unbestimmter Artikel"),
	CARD("Kardinalzahl"),
	FM("Fremdsprachliches Material"),
	ITJ("Interjektion"),
	KOUI("unterordnende Konjunktion mit (zu-) Infinitiv"),
	KOUS("unterordnende Konjunktion"),
	KON("nebenordnende Konjunktion"),
	KOKOM("Vergleichkonjunktion"),
	NN("normales Nomen"),
	NE("Eigennamen"),
	PDAT("attribuierende Demonstrativpronomen"),
	PDS("substituierendes Demonstrativpronomen"),
	PIAT("attribuierendes Indefinitpronomen"),
	PIS("substituierendes Indefinitpronomen"),
	PPER("(nicht reflexives) Personalpronomen"),
	PPOSAT("attribuierendes Possesivpronomen"),
	PPOSS("substituierendes Possesivpronomen"),
	PRELS("substituierendes Relativpronomen"),
	PRELAT("attribuierendes Relativpronomen"),
	PRF("Reflexivpronomen"),
	PWS("substituierendes Interrogativpronomen"),
	PWAT("attribuierendes Interrogativpronomen"),
	PWAV("adverviales Interrogatiivpronomen"),
	PROAV("Pronomialadverb"),
	PTKZU("zu vor Infinitib"),
	PTKNEG("Negationspartikel nicht"),
	PTKVZ("abgetrennter Verbzusatz / Verbpartikel"),
	PTKANT("Antwortpartikel"),
	PTKA("Partikel am oder zu vor Adjektiv oder Adjverb"),
	TRUNC("abgetrenntes Kompositionserstglied"),
	VVFIN("finites Vollverb"),
	VAFIN("finites Voll- oder Kopulaverb"),
	VMFIN(""),
	VVINF("infinites Vollverb"),
	VAINF(""),
	VMINF("infinites Modalverb"),
	VVIMP("Vollverb im Imperativ"),
	VAIMP("Kopulaverb im Imperativ"),
	VVPP("partizipiales Vollverb (Partizip II"),
	VAPP("partizipiales Hilfs/-Kopulaverb (Partizip II"),
	VMPP("partizipiales Modalverb (Partizip II"),
	VVIZU("Vollverb / Partikelverb im zu Infinitiv"),
	XY("Nichtwort, Sonderzeichen, Kürzel")	
	;

	private String description;

	private STTSTags(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
