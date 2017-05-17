package ai4.master.project;

import java.net.URL;

import ai4.master.project.recipe.Step;
import ai4.master.project.stanfordParser.*;

public class ParserTest {
	public static void main(String[] args) throws Exception {
		XMLLoader loader = new XMLLoader();
		KeyWordDatabase kwdb = loader.load(new URL("file:///D:\\Dropbox\\workspace\\Extracting-Process-Models-from-Online-Recipe-Databases\\master.project\\resources\\Lib.xml"));
				
		//String text = "Den Blätterteig aufrollen und eine Teighälfte mit gut der Hälfte des Schmands bestreichen. Die Hälfte der Schinkenwürfel und des Käses darauf verteilen.\nDie Seite des Blätterteiges, die nicht belegt ist auf die andere Seite klappen.\nWiederum die Hälfte des Teiges mit dem restlichen Schmand bestreichen und die Schinkenwürfel und Käseraspel darauf geben. Die unbestrichene Teighälfte darüber klappen.\nDen Blätterteig in Streifen schneiden. Vorsichtig spiralförmig drehen und auf ein mit Backpapier belegtes Blech legen.\nBei 180° ca. 25 Minuten backen.";
		//String text = "Aus möglichst großen Kartoffeln mit einem Kugelausstecher kleine Kugeln ausstechen. Diese kurz in kochendem Salzwasser garen, bis sie halb gar sind. Dann in einer Pfanne mit heißer Butter goldbraun anbraten. Zum Schluss die Butter in der Pfanne mit der Fleischbrühe verrühren und unter Rühren kurz einkochen lassen bis eine Sauce entsteht. Mit Salz, Pfeffer und Rosmarin abschmecken. ";
		String text = "Aus Mehl, Hefe, Salz, Zucker, Milch oder Wasser einen Hefeteig herstellen. Den fertigen Teig 1 Stunde ruhen lassen, bis er das doppelte Volumen erreicht hat. In der Zwischenzeit die Pflaumen waschen und entsteinen. Den Teig kurz durchkneten, in eine Tarteform von 24-26cm Durchmesser legen, Rand hochziehen. Die Pflaumen auf dem Teigboden verteilen. Für den Guss: den Zwieback fein reiben oder zerbröseln. Die Eier, mit dem Zucker und dem Zimt schaumig schlagen, die Zwiebackbrösel untermischen. Das ganze über die Pflaumen geben und im Backofen bei 180°C ca. 40 Minuten backen. ";
		Parser parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(kwdb);
				
		for(Step step : parser.parseText(text).getSteps())
			System.out.println(step);
	}
}
