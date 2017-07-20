package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.recipe.LANG_FLAG;
import ai4.master.project.recipe.Recipe;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.tk.Toolkit;

/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class Main {

	public static void main(String[] args) {
		Toolkit.getToolkit().getFontLoader().computeStringWidth("Hallo", Font.getDefault());
	}
}
