package ai4.master.project;

import java.net.URL;

import ai4.master.project.recipe.Step;
import ai4.master.project.stanfordParser.*;

public class ParserTest {
	public static void main(String[] args) throws Exception {
		XMLLoader loader = new XMLLoader();
		KeyWordDatabase kwdb = loader.load(new URL("file:///D:\\Dropbox\\workspace\\Extracting-Process-Models-from-Online-Recipe-Databases\\master.project\\resources\\Lib.xml"));
				
		//String text = "Den Bl�tterteig aufrollen und eine Teigh�lfte mit gut der H�lfte des Schmands bestreichen. Die H�lfte der Schinkenw�rfel und des K�ses darauf verteilen.\nDie Seite des Bl�tterteiges, die nicht belegt ist auf die andere Seite klappen.\nWiederum die H�lfte des Teiges mit dem restlichen Schmand bestreichen und die Schinkenw�rfel und K�seraspel darauf geben. Die unbestrichene Teigh�lfte dar�ber klappen.\nDen Bl�tterteig in Streifen schneiden. Vorsichtig spiralf�rmig drehen und auf ein mit Backpapier belegtes Blech legen.\nBei 180� ca. 25 Minuten backen.";
		//String text = "Aus m�glichst gro�en Kartoffeln mit einem Kugelausstecher kleine Kugeln ausstechen. Diese kurz in kochendem Salzwasser garen, bis sie halb gar sind. Dann in einer Pfanne mit hei�er Butter goldbraun anbraten. Zum Schluss die Butter in der Pfanne mit der Fleischbr�he verr�hren und unter R�hren kurz einkochen lassen bis eine Sauce entsteht. Mit Salz, Pfeffer und Rosmarin abschmecken. ";
		String text = "Aus Mehl, Hefe, Salz, Zucker, Milch oder Wasser einen Hefeteig herstellen. Den fertigen Teig 1 Stunde ruhen lassen, bis er das doppelte Volumen erreicht hat. In der Zwischenzeit die Pflaumen waschen und entsteinen. Den Teig kurz durchkneten, in eine Tarteform von 24-26cm Durchmesser legen, Rand hochziehen. Die Pflaumen auf dem Teigboden verteilen. F�r den Guss: den Zwieback fein reiben oder zerbr�seln. Die Eier, mit dem Zucker und dem Zimt schaumig schlagen, die Zwiebackbr�sel untermischen. Das ganze �ber die Pflaumen geben und im Backofen bei 180�C ca. 40 Minuten backen. ";
		Parser parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(kwdb);
				
		for(Step step : parser.parseText(text).getSteps())
			System.out.println(step);
	}
}
