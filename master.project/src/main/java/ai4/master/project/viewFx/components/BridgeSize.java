package ai4.master.project.viewFx.components;

import java.io.Serializable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class BridgeSize implements Serializable{

	private static final long serialVersionUID = 1L;
	private final ObjectProperty<SizeClass> size = new SimpleObjectProperty<SizeClass>();

	public void set(String x, String y, String width, String height){
		double _x, _y, _width, _height;
		try{_x	= Double.parseDouble(x);}
		catch(NumberFormatException e){_x	= 0.0;}
		try{_y  = Double.parseDouble(y);}
		catch(NumberFormatException e){_y	= 0.0;}
		try{_width	= Double.parseDouble(width);}
		catch(NumberFormatException e){_width	= 0.0;}
		try{_height	= Double.parseDouble(height);}
		catch(NumberFormatException e){_height	= 0.0;}

		this.size.set(new SizeClass(_x, _y, _width, _height));
	}

	public double getX(){
		return this.size.get().getX();
	}

	public double getY(){
		return this.size.get().getY();
	}

	public double getWidth(){
		return this.size.get().getWidth();
	}

	public double getHeight(){
		return this.size.get().getHeight();
	}

	public ObjectProperty<SizeClass> getProperty(){
		return size;
	}

	class SizeClass {
		double x, y, width, height;
		public SizeClass(double x, double y, double width, double height){
			this.x	= x;
			this.y	= y;
			this.width	= width;
			this.height	= height;
		}
		public double getX(){ return this.x; }
		public double getY(){ return this.y; }
		public double getWidth(){ return this.width; }
		public double getHeight(){ return this.height; }
	}
}
