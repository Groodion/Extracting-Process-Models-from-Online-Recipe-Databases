package ai4.master.project.viewFx.components;

import java.util.Optional;

import ai4.master.project.apirequests.RecipeGetter;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.viewFx.Controller;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class OnlineDatabaseButton extends HBox {

	public static final double LOGO_WIDTH = 116d;
	public static final double LOGO_HEIGHT = 69d;

	public enum SearchType {
		ID, LINK, CATEGORY
	};

	public OnlineDatabaseButton(String name, String link, String language, String logoPath, RecipeGetter recipeGetter,
			ObjectProperty<Recipe> recipe, boolean searchCategory) {
		setOnMouseClicked(e -> {
			if (recipeGetter != null) {
				Controller.blockView();
				Dialog<String> dialog = new Dialog<String>();
				
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
				HBox layout = new HBox();
				layout.setPadding(new Insets(10, 10, 10, 10));
				layout.setSpacing(10);

				dialog.setTitle("Choose recipe source");
				dialog.setHeaderText(name);

				ComboBox<SearchType> type = new ComboBox<SearchType>(
						FXCollections.observableArrayList(SearchType.values()));
				type.getSelectionModel().selectFirst();
				
				if(!searchCategory) {
					type.getItems().remove(SearchType.CATEGORY);
				}

				TextField input = new TextField();
				input.promptTextProperty().bind(type.getSelectionModel().selectedItemProperty().asString());
				layout.getChildren().addAll(type, input);

				dialog.getDialogPane().setContent(layout);

				dialog.setResultConverter(buttonType -> {
					if (buttonType == ButtonType.OK) {
						return input.getText();
					} else {
						return null;
					}
				});
				
				
				Optional<String> result = dialog.showAndWait();
				
				result.ifPresent(r -> {
					if(r != null) {
						switch(type.getValue()) {
							case ID:
								recipe.set(recipeGetter.getRecipeByID(r));
								break;
							case LINK:
								recipe.set(recipeGetter.getRecipeByLink(r));
								break;
							case CATEGORY:
								recipe.set(recipeGetter.getRecipeByCategory(r));
								break;
						}
					}
				});
				
				Controller.unblockView();
			}
		});

		if (recipeGetter == null) {
			this.setDisable(true);
		}

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

		getChildren().addAll(seperator, logo, layout);
	}
}