package ai4.master.project.viewFx.components.editorViews;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.viewFx.components.editorViews.entries.ToolEntry;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ToolsEditorView extends EditorView {

	private ObjectProperty<KeyWordDatabase> kwdb;
	private ObservableList<BaseTool> tools;
	private TableView<ToolEntry> toolsTable;

	
	@SuppressWarnings("unchecked")
	public ToolsEditorView(ObservableList<BaseTool> tools, ObjectProperty<KeyWordDatabase> kwdb) {
		this.kwdb = kwdb;
		this.tools = tools;
		
		setSpacing(10);
		
		/*
		 * Add new Tool
		 */
		
		HBox addToolPane = new HBox();
		addToolPane.setSpacing(10);

		TextField addToolNameTF = new TextField();
		addToolNameTF.setPromptText("Name");
		TextField addToolSynonymsTF = new TextField();
		addToolSynonymsTF.setPromptText("Synonymes");
		addToolSynonymsTF.disableProperty().bind(addToolNameTF.textProperty().isEmpty());
		Button addToolBtn = new Button("Add");
		addToolBtn.disableProperty().bind(addToolNameTF.textProperty().isEmpty());

		addToolPane.getChildren().addAll(
				new Label("Name: "),
				addToolNameTF,
				new Label("Synonyme: "),
				addToolSynonymsTF,
				addToolBtn
		);
		
		/*
		 * Edit Tools
		 */
		
		toolsTable = new TableView<ToolEntry>();
		toolsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		toolsTable.setEditable(true);
		VBox.setVgrow(toolsTable, Priority.ALWAYS);

		toolsTable.getColumns().addAll(
				nameColumn(),
				synonymsColumn()
		);
		
		/*
		 * Add-Btn Logic
		 */
		
		addToolBtn.setOnAction(e -> {
			BaseTool tool = new BaseTool();
			tool.addName(addToolNameTF.getText());
			String s = addToolSynonymsTF.getText();

			for (String t : s.split(";")) {
				tool.getNames().add(t);
			}

			for (String name : tool.getNames()) {
				if (kwdb.get().findTool(name) != null) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setContentText("The given name " + name + " already exists!");

					alert.showAndWait();
					return;
				}
			}
			
			tools.add(tool);
			
			addToolNameTF.setText("");
			addToolSynonymsTF.setText("");
		});
		
		/*
		 * tools-changed
		 */
		
		ListChangeListener<BaseTool> toolsChanged = changed -> {
			toolsTable.getItems().clear();
			for(BaseTool tool : tools) {
				toolsTable.getItems().add(new ToolEntry(tool, tools));
			}
		};
		tools.addListener(toolsChanged);
		
		/*
		 * Context Menu
		 */
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem remove = new MenuItem("Remove Tool");
		remove.disableProperty().bind(toolsTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction(e -> tools.remove(toolsTable.getSelectionModel().getSelectedIndex()));
		
		cm.getItems().add(remove);

		
		toolsTable.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				cm.show(toolsTable, e.getScreenX(), e.getScreenY());
			} else {
				cm.hide();
			}
		});
		
		/*
		 * Add components to view
		 */
		
		getChildren().addAll(
				addToolPane,
				toolsTable
		);
	}
	
	private TableColumn<ToolEntry, String> nameColumn() {
		TableColumn<ToolEntry, String> nameColumn = new TableColumn<ToolEntry, String>("Name");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<ToolEntry, String>("toolName"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		return nameColumn;
	}
	private TableColumn<ToolEntry, ObservableList<String>> synonymsColumn() {
		TableColumn<ToolEntry, ObservableList<String>> synonymsColumn = new TableColumn<ToolEntry, ObservableList<String>>("Synonyms");
		
		synonymsColumn.setCellValueFactory(new PropertyValueFactory<ToolEntry, ObservableList<String>>("synonyms"));
		synonymsColumn.setCellFactory(column -> new SynonymsCell());
		
		return synonymsColumn;
	}

	public boolean contains(String word) {
		return kwdb.get().findTool(word) != null;
	}
	public void scrollTo(String word) {
		scrollTo(kwdb.get().findTool(word));
	}
	public void scrollTo(BaseTool tool) {
		toolsTable.scrollTo(tools.indexOf(tool));
	}
	
	private class SynonymsCell extends TableCell<ToolEntry, ObservableList<String>> { 
		@Override
		public void updateItem(ObservableList<String> synonyms, boolean empty) {
			super.updateItem(synonyms, empty);
			VBox layout = new VBox();

			ListView<String> synonymsView = new ListView<String>();
			synonymsView.setCellFactory(TextFieldListCell.forListView());
			synonymsView.setMinHeight(0);
			synonymsView.setPrefHeight(50);
			synonymsView.setEditable(true);

			layout.getChildren().add(synonymsView);

			ContextMenu synonymCm = new ContextMenu();
			MenuItem removeSynonymItem = new MenuItem("Remove synonym");
			MenuItem addSynonymItem = new MenuItem("Add new synonym");
			synonymCm.getItems().addAll(removeSynonymItem, addSynonymItem);
			
			removeSynonymItem.disableProperty().bind(synonymsView.getSelectionModel().selectedItemProperty().isNull());
			removeSynonymItem.setOnAction(e -> {
				int index = synonymsView.getSelectionModel().getSelectedIndex();
				synonymsView.getItems().remove(index);
			});
			
			addSynonymItem.setOnAction(e -> {
				synonyms.add("new Synonym " + synonyms.size());
				synonymsView.requestFocus();
			});
			
			if (synonyms != null) {
				synonymsView.setItems(synonyms);
			}

			synonymsView.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.SECONDARY) {
					synonymCm.show(synonymsView, e.getScreenX(), e.getScreenY());
				}
				else {
					synonymCm.hide();
				}
			});
			
			setGraphic(layout);
		}
	}
}