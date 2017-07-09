package ai4.master.project.viewFx.components;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProcessTracker extends HBox {
	
	private LongProperty activeStep;
	
	public ProcessTracker() {
		setPadding(new Insets(20, 10, 20, 10));
		getStyleClass().add("process-tracker");
		setSpacing(50);

		getChildren().addAll(
			new Step(1, "/img/step1.png", "Choose a Recipe Source"),
			new Step(2, "/img/step2.png", "Analyse and parse the recipe"),
			new Step(3, "/img/step3.png", "Creating a BPMN-Model"),
			new Step(4, "/img/step4.png", "Optimizing BPMN-Model")
		);
		
		activeStep = new SimpleLongProperty(-1);
		
		activeStep.addListener((b, o, n) -> {
			for(int i = 0; i < getChildren().size(); i++) {
				((Step) getChildren().get(i)).setActive(i == (long) n);
			}
		});
		
		activeStep.set(0);
	}
	
	public void next() {
		activeStep.set(activeStep.get() + 1);
	}
	public void previous() {
		activeStep.set(activeStep.get() - 1);
	}

	
	public long getActiveStep() {
		return activeStep.get();
	}
	public void setActiveStep(long activeStep) {
		this.activeStep.set(activeStep);
	}
	public LongProperty activeStepProperty() {
		return activeStep;
	}
}
class Step extends HBox {
	
	private Label descriptionLabel;
	private Label nameLabel;
	
	public Step(int step, String iconPath, String description) {
		setSpacing(15);
		
		ImageView icon = new ImageView(this.getClass().getResource(iconPath).toString());
		
		VBox descriptionPane = new VBox();
		nameLabel = new Label("Step " + step);
		descriptionLabel = new Label(description);
		descriptionPane.getChildren().addAll(nameLabel, descriptionLabel);

		getChildren().addAll(icon, descriptionPane);
	}
	
	public void setActive(boolean active) {
		nameLabel.getStyleClass().clear();
		descriptionLabel.getStyleClass().clear();
		if(active) {
			nameLabel.getStyleClass().add("process-step-header-active");
			descriptionLabel.getStyleClass().add("process-step-description-active");			
		} else {
			nameLabel.getStyleClass().add("process-step-header");
			descriptionLabel.getStyleClass().add("process-step-description");
		}
	}
}