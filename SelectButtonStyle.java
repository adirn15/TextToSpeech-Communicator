package application;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/* Light Button- this class builds the grphical interface of the "Select title Button"
 * the button is composed of 2 circles and one circular button that changes color between red/yellow/green
 * Once a title is chosen- a checksign mark (press_txt) will show to indicate a choice was made
 */
public class SelectButtonStyle {
	private Rectangle outter;
	private Button butt;
	
	public SelectButtonStyle(int x,int y){
   	    
	    outter=new Rectangle();
	    outter.setFill(Color.LIGHTSKYBLUE);
	    outter.setLayoutX(x+22); 
	    outter.setLayoutY(y-8); 
	    outter.setWidth(486);
	    outter.setHeight(96);
	    outter.setArcWidth(20);
		outter.setArcHeight(20);
		
		butt = new Button("\uD83D\uDD08"); //no sound: \uD83D\uDD08   with sound: \uD83D\uDD0A
	    butt.setFont(Font.font("Guttman Haim",36));
		butt.setMinHeight(90);
		butt.setMinWidth(480);
		butt.setLayoutX(x+25);
		butt.setLayoutY(y-5);
	    butt.setStyle("-fx-border-color:  #545454");
	    butt.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));

	    butt.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	butt.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    butt.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	butt.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	}

	public Rectangle getOutter() {
		return outter;
	}
	
	public Button getSelectionButton(){
		return butt;
	}

}
