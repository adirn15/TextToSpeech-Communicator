package application;

import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PrimaryStageStyle extends Pane {
	private int x;
	private int y;
	private int width;
	private int height;
	private LinkedList<Rectangle> rectangles;
	private VBox buttons;
	private SelectButtonStyle select_btn;
	private LinkedList<Node> add_to_stage;
	private LinkedList<Button> btns;
	private Button exit,settings,history;
	private Circle light_indicator;
	
	public PrimaryStageStyle(int x,int y, int w, int h){
		this.x=x;
		this.y=y;
		width=w;
		height=h;
		
		add_to_stage=new LinkedList<Node>();
		btns = new LinkedList<Button>();
		Button start_btn = new Button("\u25B6");
	    Button pause_btn = new Button("\u23f8");
	    Button play_btn = new Button("\u266a");
	    Button stop_btn = new Button("\u25a0");
	    settings = new Button("\uD83D\uDD27");
	    exit = new Button("\u274c");
	    settings.setShape(new Circle(50));
	    exit.setShape(new Circle(50));
	    history= new Button("\uD83C\uDFBC"); //G key
	    history.setShape(new Circle(50));
	    
	    
	    start_btn.setFont(Font.font("Guttman Haim",26));
	    pause_btn.setFont(Font.font("Guttman Haim",26));
	    stop_btn.setFont(Font.font("Guttman Haim",26));
	    play_btn.setFont(Font.font("Guttman Haim",26));
	    settings.setFont(Font.font("Guttman Haim",15));
	    exit.setFont(Font.font("Guttman Haim",15));
	    history.setFont(Font.font("Guttman Haim",15));
	    
	    start_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    start_btn.setStyle("-fx-border-color:  #545454");
	    start_btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
        	    start_btn.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    start_btn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
        	    start_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    exit.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    exit.setStyle("-fx-border-color:  #545454");
	    exit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	exit.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    exit.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	exit.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	  
	    
	    settings.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    settings.setStyle("-fx-border-color:  #545454");
	    settings.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	settings.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    settings.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	settings.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    history.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    history.setStyle("-fx-border-color:  #545454");
	    history.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	history.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    history.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	history.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    
	    
	    pause_btn.setStyle("-fx-border-color:  #545454");
	    pause_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    pause_btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	pause_btn.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    pause_btn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	pause_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    
	    
	    stop_btn.setStyle("-fx-border-color:  #545454");
	    stop_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    stop_btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	stop_btn.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    stop_btn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	stop_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    
	    
	    play_btn.setStyle("-fx-border-color:  #545454");
	    play_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
	    play_btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	play_btn.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    play_btn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	play_btn.setBackground(new Background(new BackgroundFill(Color.PALETURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        });
	    
	    
	    
	    btns.add(start_btn); btns.add(pause_btn); btns.add(stop_btn); btns.add(play_btn);
	    
	    buttons = new VBox(24);
	    buttons.setSpacing(3);
	    start_btn.setMinWidth(114);
	    stop_btn.setMinWidth(114);
	    pause_btn.setMinWidth(114);
	    play_btn.setMinWidth(114);	    		    
	    start_btn.setMinHeight(55);
	    stop_btn.setMinHeight(55);
	    pause_btn.setMinHeight(55);
	    play_btn.setMinHeight(55);
	    
	    settings.setLayoutX(50);
	    settings.setLayoutY(10);
	    exit.setLayoutX(10);
	    exit.setLayoutY(10);
	    history.setLayoutX(90);
	    history.setLayoutY(10);
	    history.setMinSize(34,34);
	    buttons.getChildren().addAll(start_btn,pause_btn,stop_btn,play_btn);
	    buttons.setLayoutX(x+7); //619
	    buttons.setLayoutY(y+5); //60
	    rectangles=create_control(x-22,y-26);
	    select_btn = new SelectButtonStyle(65,350);
	    
	    Light.Distant l = new Light.Distant();
	    l.setAzimuth(-135.0);
	    Lighting lighting = new Lighting();
	    lighting.setLight(l);
	    lighting.setSurfaceScale(10.0);
	    
	    light_indicator= new Circle();
	    light_indicator.setFill(Color.DARKRED);
	    light_indicator.setEffect(lighting);
	    light_indicator.setCenterX(664);
	    light_indicator.setCenterY(60); //66
		light_indicator.setRadius(13);

		Circle out= new Circle();
		out.setFill(Color.BLACK);
		out.setCenterX(664);
		out.setCenterY(60);
		out.setRadius(16);
	    
		
	    add_to_stage.addAll(rectangles);
	    add_to_stage.add(buttons);
	    add_to_stage.add(select_btn.getOutter());
	    add_to_stage.add(select_btn.getSelectionButton());
	    add_to_stage.add(out);
	    add_to_stage.add(light_indicator);
	    add_to_stage.add(settings);
	    add_to_stage.add(exit);
	    add_to_stage.add(history);

	    
	}
	
	public LinkedList<Rectangle> create_control(int x,int y){
		LinkedList<Rectangle> l=new LinkedList<Rectangle>();
		
		Rectangle r = new Rectangle();
		r.setWidth(width-20);
		r.setHeight(height-188);
		r.setX(x+27);
		r.setY(y+28);	
		r.setFill(Color.LIGHTSKYBLUE);
		r.setArcWidth(20);
		r.setArcHeight(20);
		l.add(r); 
        return l;
	}
	
	public LinkedList<Rectangle> get_rects(){
		return rectangles;
	}

	public VBox getButtons() {
		return buttons;
	}

	public SelectButtonStyle getLight_btn() {
		return select_btn;
	}
	
	public LinkedList<Node> get_to_stage(){
		return add_to_stage;
	}

	public LinkedList<Button> get_btns() {
		return btns;
	}
	
	public Circle getStatusLight(){
		return light_indicator;
	}
	
	public Button getExitbutton(){
		return exit;
	}
	
	public Button getSettingsbutton(){
		return settings;
	}
	
	public Button getHistorybutton(){
		return history;
	}
}
