package application;

import java.util.LinkedList;

import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/* ConsoleStyle - this class is the graphical interface for the Console part of the screen
 *  Made from: the console object and a rectangle
 */

public class ConsoleStyle{
	private Console console;
	private int x,y,w,h;
	private Rectangle r;
	
	public ConsoleStyle(int x,int y,int w,int h){
		console=new Console(131,120,400,190);
		console.setStyle("-fx-border-color:  #545454");
		r = new Rectangle();
		r.setWidth(w);
	    r.setHeight(h);
	    r.setX(x-50);
	    r.setY(y-15);
	    r.setFill(Color.WHITESMOKE);
	    r.setArcWidth(20);
	    r.setArcHeight(20);	
	}
    
    public Rectangle get_rect(){
    	return r;
    }
    public Console get_console(){
    	return console;
    }
}
