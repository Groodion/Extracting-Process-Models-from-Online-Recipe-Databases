package ai4.master.project.viewFx.components;

import java.net.MalformedURLException;
import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class DiagramViewer extends HBox {
	
	private WebEngine		engine;
	private WebView			webView;
	
	public DiagramViewer() {
		this.webView = new WebView();
		ScrollPane scroll 		= new ScrollPane(webView);

		// creating bridgeObjID for javascript injection
		BridgeObjID	bridgeObjID = new BridgeObjID();
		bridgeObjID.getProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				System.out.println("selected ObjID: "+newValue);
			}
		});
		
		BridgeSize	bridgeSize	= new BridgeSize();
		this.engine 	= webView.getEngine();
		this.engine.load(DiagramViewer.class.getResource("indexFX.html").toExternalForm());

	}
	
	public void showDiagram(String path) throws MalformedURLException {
		String js2 = "showBpmnDiagram('"+new URL("file", "", path).toExternalForm()+"');";
		System.out.println(js2);
		engine.executeScript(js2);
		System.out.println("Diageam painted");
	}
	
	

}
