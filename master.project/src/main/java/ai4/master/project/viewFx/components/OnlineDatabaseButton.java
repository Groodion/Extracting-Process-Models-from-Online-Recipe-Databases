package ai4.master.project.viewFx.components;

import java.util.Optional;

import ai4.master.project.apirequests.RecipeGetter;
import ai4.master.project.recipe.Recipe;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class OnlineDatabaseButton extends HBox {

	public static final double LOGO_WIDTH = 116d;
	public static final double LOGO_HEIGHT = 69d;

	public OnlineDatabaseButton(String name, String link, String language, String logoPath, RecipeGetter recipeGetter, ObjectProperty<Recipe> recipe) {
		setOnMouseClicked(e -> {
			if (recipeGetter != null) {

				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("Choose recipe source");
				dialog.setHeaderText(name);
				dialog.setContentText("Please enter the recipe id:");

				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					String id = result.get();
					recipe.set(recipeGetter.getRecipe(id));
				}
			}
		});

		ImageView logo = new ImageView(this.getClass().getResource(logoPath).toString());
		logo.setFitHeight(LOGO_HEIGHT);
		logo.setFitWidth(LOGO_WIDTH);

		getStyleClass().add("database-label");
		setSpacing(5);

		Separator seperator = new Separator();
		seperator.getStyleClass().add("separator");
		seperator.setOrientation(Orientation.VERTICAL);
		seperator.setValignment(VPos.CENTER);
		seperator.setPrefHeight(70);

		GridPane layout = new GridPane();
		layout.setVgap(4);
		layout.setHgap(4);
		layout.setPadding(new Insets(5, 5, 5, 5));
		Label nameLabel = new Label(name);
		layout.getStyleClass().add("header-label");
		layout.add(nameLabel, 0, 0);
		layout.add(new Label(""), 1, 0);
		layout.add(new Label("Language:"), 0, 1);
		Label languageLabel = new Label(language);
		languageLabel.getStyleClass().add("attribute-value");
		layout.add(languageLabel, 1, 1);
		layout.add(new Label("Link:"), 0, 2);
		layout.add(new Label(link), 1, 2);

		getChildren().addAll(
				seperator,
				logo,
				layout
		);
	}
}