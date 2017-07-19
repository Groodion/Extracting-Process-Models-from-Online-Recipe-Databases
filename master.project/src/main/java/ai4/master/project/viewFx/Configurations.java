package ai4.master.project.viewFx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public final class Configurations {
	
	private static final String DEFAULT_LIB_LOCATION = "resources/Lib.xml";
	private static final String DEFAULT_PARSER_CONFIGURATION = "lib/models/german-fast.tagger";
	private static final String DEFAULT_BPMN_LOCATION = "";
	private static final Color DEFAULT_INGREDIENT_COLOR = Color.GREEN;
	private static final Color DEFAULT_GROUPS_COLOR = Color.GREENYELLOW;
	private static final Color DEFAULT_TOOL_COLOR = Color.BLUE;
	private static final Color DEFAULT_COOKING_ACTION_COLOR = Color.RED;
	private static final double DEFAULT_VIEW_WIDTH = 1024d;
	private static final double DEFAULT_VIEW_HEIGHT = 720d;
	
	public static final ObjectProperty<File> LIB_LOCATION = new SimpleObjectProperty<File>();
	public static final ObjectProperty<File> PARSER_CONFIGURATION = new SimpleObjectProperty<File>();
	
	public static final ObjectProperty<File> BPMN_LOCATION = new SimpleObjectProperty<File>();
	
	public static final ObjectProperty<Color> INGREDIENT_COLOR = new SimpleObjectProperty<Color>();
	public static final ObjectProperty<Color> TOOL_COLOR = new SimpleObjectProperty<Color>();
	public static final ObjectProperty<Color> GROUPS_COLOR = new SimpleObjectProperty<Color>();
	public static final ObjectProperty<Color> COOKING_ACTION_COLOR = new SimpleObjectProperty<Color>();

	public static final DoubleProperty VIEW_WIDTH = new SimpleDoubleProperty();
	public static final DoubleProperty VIEW_HEIGHT = new SimpleDoubleProperty();
	
	public static void save() {
		Properties properties = new Properties();
		
		properties.setProperty("libLocation", LIB_LOCATION.get().getAbsolutePath());
		properties.setProperty("parserConfiguration", PARSER_CONFIGURATION.get().getAbsolutePath());
		properties.setProperty("bpmnLocation", BPMN_LOCATION.get().getAbsolutePath());
		
		properties.setProperty("ingredientColor", INGREDIENT_COLOR.get().toString());
		properties.setProperty("groupsColor", GROUPS_COLOR.get().toString());
		properties.setProperty("toolColor", TOOL_COLOR.get().toString());
		properties.setProperty("cookingActionColor", COOKING_ACTION_COLOR.get().toString());

		properties.setProperty("viewWidth", String.valueOf(VIEW_WIDTH.get()));
		properties.setProperty("viewHeight", String.valueOf(VIEW_HEIGHT.get()));
		
		try {
			properties.store(new FileOutputStream("configurations.prop"), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		File file = new File("configurations.prop");
		if(file.exists()) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(properties.containsKey("libLocation"))	LIB_LOCATION.set(new File(properties.getProperty("libLocation")));
			else 										LIB_LOCATION.set(new File(DEFAULT_LIB_LOCATION));
			if(properties.containsKey("parserConfiguration"))	PARSER_CONFIGURATION.set(new File(properties.getProperty("parserConfiguration")));
			else												PARSER_CONFIGURATION.set(new File(DEFAULT_PARSER_CONFIGURATION));
			if(properties.containsKey("bpmnLocation"))	BPMN_LOCATION.set(new File(properties.getProperty("bpmnLocation")));
			else										BPMN_LOCATION.set(new File(DEFAULT_BPMN_LOCATION));
			if(properties.containsKey("ingredientColor"))	INGREDIENT_COLOR.set(Color.valueOf(properties.getProperty("ingredientColor")));
			else											INGREDIENT_COLOR.set(DEFAULT_INGREDIENT_COLOR);
			if(properties.containsKey("groupsColor"))	GROUPS_COLOR.set(Color.valueOf(properties.getProperty("groupsColor")));
			else										GROUPS_COLOR.set(DEFAULT_GROUPS_COLOR);
			if(properties.containsKey("toolColor"))	TOOL_COLOR.set(Color.valueOf(properties.getProperty("toolColor")));
			else									TOOL_COLOR.set(DEFAULT_TOOL_COLOR);
			if(properties.containsKey("cookingActionColor"))	COOKING_ACTION_COLOR.set(Color.valueOf(properties.getProperty("cookingActionColor")));
			else												COOKING_ACTION_COLOR.set(DEFAULT_COOKING_ACTION_COLOR);
			if(properties.containsKey("viewWidth"))	VIEW_WIDTH.set(Double.parseDouble(properties.getProperty("viewWidth")));
			else									VIEW_WIDTH.set(DEFAULT_VIEW_WIDTH);
			if(properties.containsKey("viewHeight"))	VIEW_HEIGHT.set(Double.parseDouble(properties.getProperty("viewHeight")));
			else										VIEW_HEIGHT.set(DEFAULT_VIEW_HEIGHT);
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			restoreDefaultValues();
		}
	}

	public static void restoreDefaultValues() {
		LIB_LOCATION.set(new File(DEFAULT_LIB_LOCATION));
		PARSER_CONFIGURATION.set(new File(DEFAULT_PARSER_CONFIGURATION));
		BPMN_LOCATION.set(new File(DEFAULT_BPMN_LOCATION));
		INGREDIENT_COLOR.set(DEFAULT_INGREDIENT_COLOR);
		GROUPS_COLOR.set(DEFAULT_GROUPS_COLOR);
		TOOL_COLOR.set(DEFAULT_TOOL_COLOR);
		COOKING_ACTION_COLOR.set(DEFAULT_COOKING_ACTION_COLOR);
		VIEW_WIDTH.set(DEFAULT_VIEW_WIDTH);
		VIEW_HEIGHT.set(DEFAULT_VIEW_HEIGHT);
		
		save();
		
	}
	private Configurations() {
		
	}
}


