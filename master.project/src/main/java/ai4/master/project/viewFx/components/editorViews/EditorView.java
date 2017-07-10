package ai4.master.project.viewFx.components.editorViews;

import javafx.scene.layout.VBox;

public abstract class EditorView extends VBox {
	public abstract boolean contains(String word);
	public abstract void scrollTo(String word);
}
