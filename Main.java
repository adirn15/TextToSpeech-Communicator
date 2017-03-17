package application;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.sun.glass.events.KeyEvent;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
	 
public class Main extends Application{
		private MenuNavigator menu; //the object that iterates over the excel file and plays audio
		private Switch button; //the SelectTitleButton actions
		
		private Button pause_btn,start_btn; //interface buttons
		private Circle light;  //the light indicator if the program is running/pause/stopped
		private File paths; //the file that indicates the sleep duration and iteration length of reading titles
		private LinkedList<Cell> phrase; //the excel cells of the last round of titles that have been chosen
		private Stage stage; //the main stage (primary window) of the app
		private Robot r; //object that moves the cursor and types "q" and "w" to change audio input (using the "hot keys" external app)
		private Workbook wb; //the excel workbook
		private Thread t1; //the thread that runs on the menunavigator
		private PrimaryStageStyle intPrimaryWin; //the buttons design on the primary window interface
		private SettingsWindowStyle intSettings=null; //the button design in the settings window interface
		private ComboBox menutitles=null; //the box in the primary window for choosing a title to begin with the iteration

		private int loop_lines,sleep_time; //number of lines per iteration, sleep duration between audios in a slow iteration
		private TextArea loop_field,sleep_field,excel_path,audio_path; //text areas to insert these values
		private String excelstring,audiostring;
		private boolean excelUpdateNeeded=false; //was the excel path updated but not implemented
		private Object restart_lock; //the navigator thread waits on the lock after the end of each round. the restart button awakes him
		private Console console;	//the console screen on the interface
		private Pane settingsroot=null, historyroot=null; // settings and history windows objects
		private Stage settingstage=null, historystage=null;
		private Bool autoMove_cursor=new Bool();
		
		
		public static void main(String[] args) {
		    launch(args);
		}

		@Override
		public void start(Stage primarystage) {
			
			/*******************************************
			 * Create Main Stage background and headline
			 ******************************************/
			stage=primarystage;			
		    stage.setTitle("");
		    Pane root = new Pane();
		    Text headline = new Text("Text to Speech");
		    headline.setFont(Font.font("Edwardian Script ITC",FontWeight.EXTRA_BOLD,58));
		    headline.setLayoutX(210);
		    headline.setLayoutY(75);
		    Rectangle background = create_background();
		    root.getChildren().add(background);
		    root.getChildren().add(headline);
		    
			restart_lock= new Object();
			wb=null;
			phrase = new LinkedList<Cell>();
			
			
			/************************************
			/*read from file the updated paths
			 ************************************/
			
			paths = new File("paths.txt");
			try{
				if (!paths.exists())
					paths.createNewFile();
				BufferedReader bfr;
				bfr=new BufferedReader(new FileReader(paths));
				excelstring= bfr.readLine();
				if (excelstring==null)
					excelstring="Enter Path of Excel Table Here";
				audiostring= bfr.readLine();
				if (audiostring==null)
					audiostring="Enter Path of Audio Table Here";
				excel_path = new TextArea(excelstring);
				audio_path = new TextArea(audiostring);
				bfr.close();
				call_path_colors();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		    
		    
		    
		    intPrimaryWin = new PrimaryStageStyle(600,130,138,444);
		    light = intPrimaryWin.getStatusLight();
		    for (Button b : intPrimaryWin.get_btns()){
				init_remote_btn(b);		    		
		    }
		    
		    intPrimaryWin.getLight_btn().getSelectionButton().setOnMouseClicked(new EventHandler<MouseEvent>(){
				public void handle(MouseEvent arg0) {
					if (menu==null || !menu.isCurrently_iterating_table())
						start_reset();
					else if (menu!=null && button!=null){
						if (button.is_active()){
							button.be_unActive();
							button.switch_on();
							intPrimaryWin.getLight_btn().getSelectionButton().setText("\uD83D\uDD0A"); //with sound
						}
					}
				}});
		    
		    intPrimaryWin.getExitbutton().setOnAction(new EventHandler<ActionEvent>() {
		        public void handle(ActionEvent event) {
		            end_program();
		        }
		    });	  
		    
		    /********************************
		     * History window initialization
		     ********************************/
		    
		    intPrimaryWin.getHistorybutton().setOnAction(new EventHandler<ActionEvent>() {
		        public void handle(ActionEvent event) {
		        	if (menu==null)
		        		return;
		        	else if (historyroot!=null)
		            	historystage.show();
		            else{
			        	historyroot = new Pane();
			        	historystage= new Stage();
			        	historystage.setScene(new Scene(historyroot,240,300));
			        	historystage.setMaximized(false);
			        	historystage.setResizable(false);
			        	historystage.setX(442);
			        	historystage.setY(180);
			            Rectangle bg2 = new Rectangle(750, 490,
							     new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new 
							         Stop[]{
							            new Stop(0, Color.POWDERBLUE),
							            new Stop(0.6, Color.LIGHTSTEELBLUE),
							         }));
			            historyroot.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			            Text headline= new Text("History");
			            headline.setFont(Font.font("Edwardian Script ITC",FontWeight.EXTRA_BOLD,38));
			            headline.setLayoutX(85);
			            headline.setLayoutY(35);
			            Rectangle page= new Rectangle();
			            page.setLayoutX(5);
			            page.setLayoutY(50);
			            page.setHeight(255);
			            page.setWidth(240);
			            page.setFill(Color.WHITESMOKE);
			            page.setStyle("-fx-border-color:  #545454");
			            page.setArcWidth(2);
			            page.setArcHeight(3);
			            historyroot.getChildren().addAll(bg2,page,headline,menu.get_history());

			            historystage.show();		            }
		        }
		    });	  
		    
		        
		    for (Node o : intPrimaryWin.get_to_stage()){
		    	root.getChildren().add(o);
		    }
		    
		    /*open text file with information of sleep+iteration numbers*/
		    
		    loop_lines=0;
		    sleep_time=0;
		    File durations = new File("durations.txt");
		    BufferedReader br=null;
		    try{
		    if (!durations.exists())
				durations.createNewFile();
			br = new BufferedReader(new FileReader(durations));
			loop_lines = Integer.parseInt(br.readLine());
			if (loop_lines<0)
				loop_lines=1;
			else if (loop_lines>15)
				loop_lines=15;	
		    }catch(IOException e){
		    	console.println("error opening file");
		    }catch (NumberFormatException e){
		    	loop_lines=1;
		    }
		    try{
		    	sleep_time=Integer.parseInt(br.readLine());
		    	if (sleep_time<0)
		    		sleep_time=0;
		    	else if (sleep_time>15)
		    		sleep_time=15;
		    	br.close();
		    } catch (NumberFormatException | IOException e){
		    	sleep_time=0;
		    }
		    
		    
		    /****************************************
		     * Settings window initialization
		     *****************************************/
		    
		    intPrimaryWin.getSettingsbutton().setOnAction(new EventHandler<ActionEvent>() {
		        public void handle(ActionEvent event) {
		            if (intSettings!=null || settingsroot!=null)
		            	settingstage.show();
		            else{
			        	settingsroot = new Pane();
			        	settingstage= new Stage();
			            settingstage.setScene(new Scene(settingsroot,330,300));
			            settingstage.setMaximized(false);
			            settingstage.setResizable(false);
			            settingstage.setX(426);
			            settingstage.setY(180);
			            Rectangle bg = new Rectangle(750, 490,
							     new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new 
							         Stop[]{
							            new Stop(0, Color.AZURE),
							            new Stop(0.6, Color.WHITE),
							         }));
						settingsroot.getChildren().add(bg);
			            intSettings = new SettingsWindowStyle(30,35,94,25,loop_lines,sleep_time,excel_path,audio_path,autoMove_cursor);

					    for (Button b : intSettings.get_btns()){
					    	init_board_btn(b);
					    }	    
					    sleep_field = intSettings.get_sleepfield();
					    loop_field = intSettings.get_loopfield();
			            intSettings.add_mbb_to_stage(settingsroot);
			            settingstage.show();
		            }
		        }
		    });	  
		  
		    	    
		    /*************************
		     * Console initialization
		      ************************/
		    
		    ConsoleStyle console_style = new ConsoleStyle(140,120,478,220);    
		    console = console_style.get_console();
		    root.getChildren().add(console_style.get_rect());
		    root.getChildren().add(console_style.get_console());
		    root.setStyle("-fx-background-color: transparent;");
		    root.setId("pane");
		    		 
			/************************** 
			 * ComboBox initialization    
			 **************************/
		    
			menutitles= new ComboBox<>();
			menutitles.setPromptText("כוונות תקשורתיות");
			menutitles.setPrefWidth(135);
			menutitles.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			menutitles.setLayoutX(595);
			menutitles.setLayoutY(95);
			menutitles.setVisibleRowCount(10);
		    root.getChildren().add(menutitles);
		    
		    
		    /***************************
		     * Main Stage initialization
		     ***************************/
		    
		    stage.setScene(new Scene(root,background.getWidth(), background.getHeight()));
		    stage.setMaximized(false);
		    stage.sizeToScene();
		    stage.setResizable(false);
		    stage.show();
		}

		
		private void init_board_btn(Button b) {
			if (b.getId().equals("sleep")){
				b.setOnAction(new EventHandler<ActionEvent>() {
				       
					  public void handle(ActionEvent event) {
						  if (menu!=null)
				            	menu.update_sleep(sleep_field.getText());
						  else{
							  update_durations_file("sleep");
						  }
				        }
				    });	
			}
			else if (b.getId().equals("loop")){
				b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	if (menu!=null)
			        		menu.update_loops(loop_field.getText());
			        	else{
							  update_durations_file("loop");
			        	}
			        }
			    });
			}	
			else if (b.getId().equals("audio")){
	    		b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			    		audiostring=audio_path.getText();
			    		update_paths();
			    		call_path_colors();
			    		if (menu!=null)
			    			menu.set_audio_path(audiostring);
			        }
			    });
	    	}
	    	else if (b.getId().equals("excel")){
	    		b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
				        excelstring = excel_path.getText();
				        update_paths();
				        call_path_colors();
				        excelUpdateNeeded=true;
			        }
			    });
	    	}
		}


		public Rectangle create_background(){
			Rectangle colors = new Rectangle(750, 490,
				     new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new 
				         Stop[]{
				            new Stop(0, Color.AZURE),
				            new Stop(0.6, Color.WHITE),
				         }));
			return colors;
		}
	
		
		public void start_reset(){
			if (start_btn.getText().equals("\u25B6")) //התחל
				start_app();
			else
				restart_app();
		}
		
		public void restart_app(){
			if (menu==null)
				return;
			if (menu.isCurrently_iterating_table()){ //pressed restart before pressing stop (program was running)
				stop_app();	
				intPrimaryWin.getLight_btn().getSelectionButton().setTextFill(Color.TURQUOISE);
				synchronized(restart_lock){
					try {
						restart_lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			FileInputStream inp=null;
			if (excelUpdateNeeded){
				try{
					wb.close();
					inp = new FileInputStream(excelstring);
					wb = WorkbookFactory.create(inp);
					if (wb==null){
						console.println("שגיאה בפתיחת קובץ האקסל");
						return;
					}
					menu.set_workbook(wb);
					updatemenutitles((XSSFRow) wb.getSheetAt(0).getRow(0));
				}
				catch(IOException e){
					console.println(e.getStackTrace().toString());
				} catch (EncryptedDocumentException e) {
					console.println(e.getStackTrace().toString());
				} catch (InvalidFormatException e) {
					console.println(e.getStackTrace().toString());
				}
			}
			
			light.setFill(Color.GREEN);
			start_btn.setText("\u21ba"); //refresh
			console.println("notifying");
			
			synchronized(menu){
				menu.notifyAll();
			}
		}
		
		public void start_app(){
			try {
				if (r==null)
					r = new Robot();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
			r.keyPress(KeyEvent.VK_W); //START WITH HEADSETS
			if (menu!=null){
				synchronized(menu){
					menu.notifyAll();
				}
		    }	
			InputStream inp= null;
			try {
				excelstring=excel_path.getText();
				inp = new FileInputStream(excelstring);
			} catch (FileNotFoundException e) {
				console.println(" קובץ האקסל אינו קיים, בדוק שהתייקייה נכונה");
				return;
			}
		    light.setFill(Color.GREEN);
			call_path_colors();
			try {
				if (wb==null){
					wb = WorkbookFactory.create(inp);
					updatemenutitles((XSSFRow) wb.getSheetAt(0).getRow(0));
				}
			} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e){
				console.clear();
				console.println("קובץ אקסל גדול מדי, הפעל מחדש את התוכנה ");
			}
			if (button==null){
				button = new Switch();
				menu= new MenuNavigator(wb,button,intPrimaryWin.getLight_btn().getSelectionButton(),light,phrase,restart_lock,console,audiostring,autoMove_cursor);
				t1 = new Thread(menu);
				start_btn.setText("\u21ba"); //refresh
				intPrimaryWin.getLight_btn().getSelectionButton().setTextFill(Color.TURQUOISE);
				t1.start();
			}
			else{
				synchronized(menu){
					menu.notifyAll();
				}
			}
		}
		
		public void stop_app(){
			if (menu==null || menu.isCurrently_iterating_table()==false)
				return;
		    light.setFill(Color.DARKRED);
			menu.set_shouldRun(false);
			if (menu.getShouldPause()) //release if paused
				pause_app();
			button.switch_off();
			intPrimaryWin.getLight_btn().getSelectionButton().setText("\uD83D\uDD08"); //no sound
			intPrimaryWin.getLight_btn().getSelectionButton().setTextFill(Color.BLACK);
			pause_btn.setText("\u23f8");
		}
		
		public void end_program(){
			if (menu!=null)
				menu.set_endProgram(true);
			stop_app();	
			start_btn.setText("\u25B6");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (menu!=null){
				synchronized(menu){
					try {
							menu.notifyAll();	
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				if (wb!=null)
					wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stage.close();
		}
		
		public void play_last(){
			if (menu!=null)
				menu.play_last();
		}
		
		public void pause_app(){
			if (menu==null || menu.isCurrently_iterating_table()==false)
				return;
			if (menu.getShouldPause()){
				menu.forward();
			    light.setFill(Color.GREEN);
			    pause_btn.setText("\u23f8");
				pause_btn.setTextFill(Color.BLACK);
			}
			else{
				menu.pause();
			    light.setFill(Color.YELLOW);
			    pause_btn.setText("\u25B6");
			}
		}
		
	
		
		public void init_remote_btn(Button b){
			if (b.getText().equals("\u25B6")){
				b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	start_reset();
			        }
			    });
				start_btn=b;
			}
			else if (b.getText().equals("\u23f8")){ //PAUSE
				b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	pause_app();
			        }
			    });
				pause_btn=b;
			}
			else if (b.getText().equals("\u25a0")){ //STOP
				b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	stop_app();
			        }
			    });
			}
			else if (b.getText().equals("\u266a")){ //MUSIC NOTE
				b.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	play_last();
			        }
			    });
			}
		}
		
		public void update_paths(){
			PrintWriter pw=null;
			try {
				pw = new PrintWriter(paths);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			pw.println(excelstring);
			pw.println(audiostring);
			pw.close();
		}
		
		public void call_path_colors(){
			int l1= excelstring.length();
			String sub="",sub2="";
			if (l1>=5){
				sub = excelstring.substring(l1-5,l1);
				sub2 = excelstring.substring(0,3);
			}
			if (l1==0 || !sub.equals(".xlsx") || 
					(!sub2.equals("C:\\") && !sub2.equals("D:\\") && !sub2.equals("c:\\") && !sub2.equals("d:\\") ))
				excel_path.setBackground(new Background
						(new BackgroundFill(Color.LIGHTCORAL,CornerRadii.EMPTY,Insets.EMPTY)));
			else {
				excel_path.setBackground(new Background
						(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
			}
			int l2=audiostring.length();
			if (l2>=3){
				sub=audiostring.substring(0,3);
				sub2=audiostring.substring(l2-1,l2);
			}
			if (l2==0 || sub2.equals("\\") || 
					(!sub.equals("C:\\") && !sub.equals("D:\\") && !sub.equals("c:\\") && !sub.equals("d:\\") )){
				audio_path.setBackground(new Background
						(new BackgroundFill(Color.LIGHTCORAL,CornerRadii.EMPTY,Insets.EMPTY)));
			}	
			else {
				audio_path.setBackground(new Background
						(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
			}
		}
		
		private void update_durations_file(String who){
			File durations = new File("durations.txt");
		    BufferedReader br=null;
		    int fileloops=-1,filesleeps=-1;
		    try{
				br = new BufferedReader(new FileReader(durations));
				fileloops = Integer.parseInt(br.readLine());
				if (fileloops<0)
					fileloops=1;
				else if (fileloops>15)
					fileloops=15;	
		    }catch(IOException e){
		    	console.println("error opening durations file");
		    }catch (NumberFormatException e){
		    	fileloops=1;
		    }
		    try{
		    	filesleeps=Integer.parseInt(br.readLine());
		    	if (filesleeps<0)
		    		filesleeps=0;
		    	else if (filesleeps>15)
		    		filesleeps=15;
		    	br.close();
		    } catch (NumberFormatException | IOException e){
		    	filesleeps=0;
		    }
		    PrintWriter pw=null;
		    try {
				pw = new PrintWriter(durations);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		    if (who.equals("sleep")){
		    	pw.println(""+fileloops);
		    	try{
		    		int newval= Integer.parseInt(sleep_field.getText());
		    		if (newval>15 || newval<0){
		    			console.println("הכנס מספר בין 0 ל-15");
			    		pw.println(""+filesleeps);
		    			pw.close();
		    			return;
		    		}
		    		pw.println(""+newval);
		    		pw.close();
		    	}catch (NumberFormatException e){
		    		console.println("שגיאה: הכנס מספר שנית");
		    		pw.println(""+filesleeps);
		    		pw.close();
		    		return;
		    	}
		    }
		    else{ //loops
		    	try{
		    		int newval= Integer.parseInt(loop_field.getText());
		    		if (newval>15 || newval<0){
		    			console.println("הכנס מספר בין 0 ל-15");
		    			pw.close();
		    			return;
		    		}
		    		pw.println(""+newval);
		    		pw.println(""+filesleeps);
		    		pw.close();
		    	}catch (NumberFormatException e){
		    		console.println("שגיאה: הכנס מספר שנית");
		    		pw.println(""+fileloops);
		    		pw.println(""+filesleeps);
		    		pw.close();
		    		return;
		    	}
		    }
		}
		
		private void updatemenutitles(XSSFRow row){
			int i=0;
			Cell curr= row.getCell(i);
			if (curr==null)
				return;
			String name= curr.getStringCellValue();
			while (curr!=null && name!=null && name!=""){
				String title=""+(i/2+1)+". "+name;
				menutitles.getItems().add(title);
				i+=2;
				curr=row.getCell(i);
				if (curr!=null)
					name=curr.getStringCellValue();
			}
			menutitles.setOnAction(new EventHandler<ActionEvent>() {
			        public void handle(ActionEvent event) {
			        	String[] arr=((String) menutitles.getValue()).split(". ");
			        	int column = Integer.parseInt(arr[0]);
			        	column=(column-1)*2;
			        	menu.set_beggining_column(column);
			        }
			    });
		}
}