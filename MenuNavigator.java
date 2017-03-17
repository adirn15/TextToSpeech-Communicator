package application;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;


import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.sun.glass.events.KeyEvent;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javazoom.jl.player.Player;

public class MenuNavigator implements Runnable {
	
	public static int SLEEP=-1; //how much time to wait between playing each cell
	public static int ITERATION=-1; //how many rows to go through in each mini-iteration
	
	private String cur_audio_path; //the directory of the current audio file (chosen from the current cell in the menu)  
	private LinkedList<Cell> my_phrase; //the list of audio files chosen from cells in different menus, that creates a full sentence
	private LinkedList<Cell> last_phrase; // the sentence that was built in the last iteration (in case after the iteration you want to hear again the result, while another iteration is running)
	
	private Workbook wb; // Excel workbook - composed of different sheets (in this case we use only 1 sheet)
	private XSSFSheet sheet; // the Excel sheet in the workbook we are using
	private XSSFRow row; // the current row in the excel sheet we are on
	private Cell cur_cell; //the current cell we are on (in the current row)
	private Circle light; // a light indicator if we are during iteration (red=stopped /yellow=pause / green=going )
	
	private String cur_table; //current table name we are iterating over
	private Player playMP3; // mp3 player used to play the sentences of each cell
	private Switch button; // the button used to select a cell to navigate through menus
	private Button select_butt; //select button - used to change color when enabled/disabled
	private boolean should_pause=false; // activated by pause button
	private boolean should_run=true; // activated by stop button
	private boolean end_program=false; // should the program end  
	private boolean currently_iterating_table=false; //are we currently during iteration
	private Console console; // the screen console showing the text that is being played to the user at each moment
	private Object restart_lock; // a lock used in the restart_app function
	private Text history_window;
	private int beggining_column=0;
	
	private File durations; // a file in the syntax "number newline number" - first number= ITERATIONS, second number= SLEEP
	private PrintWriter pw; // a printer that prints text to the console
	private Robot robot; // the robot is used for his "keypress" functions, in order to press keyboard buttons as 'hot keys' to navigate through the speaker sound card and the headphones sound card 
	

	private Bool mouse_move;

	
	/* *****constructor ***** */ 
	public MenuNavigator(Workbook wb,Switch b,Button select, Circle l,LinkedList<Cell> p, Object lock, Console c, String audiopath, Bool bool) {
		this.wb=wb;  
		button=b; 
		select_butt=select;
		light=l;
		cur_audio_path=audiopath;
		my_phrase = new LinkedList<Cell>();
		last_phrase = p;
		restart_lock=lock;
		console=c;
		history_window= new Text("");
		history_window.setFont(Font.font("Guttman David",20));
		history_window.setLayoutX(30);
		history_window.setLayoutY(80);
		history_window.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
	
		mouse_move=bool;
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	/*Run
	  1. reads the durations file and updates SLEEP,ITERATIONS
	  2. reads the first sheet out of the excel workbook
	  3. activates in a loop iterate_table from column "column" until it gets a null or "exit" as a return value
	  4. replaces "last_phrase" with the new phrase built
	  5. switches to speaker sound card, plays the phrase, and switches back to headphones soundcard
	  6. hold thread until the user wants to start over the iteration process (from column 0)
	  7. if he does, go back to step 3, otherwise turn off the switch and mp3player
	 */
	public void run(){		
		durations = new File("durations.txt");
		BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(durations));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			ITERATION = Integer.parseInt(br.readLine());
			SLEEP = Integer.parseInt(br.readLine());
			br.close();
		} catch (NumberFormatException | NullPointerException | IOException e1) {
			if (ITERATION==-1)
				ITERATION=4;
			if (SLEEP==-1)
				SLEEP=5;
			write_new_vals_to_file();
		}
		sheet = (XSSFSheet)wb.getSheetAt(0);
		
		if (mouse_move.getBool())
			robot.mouseMove(1500,1155);
		
		while (!end_program){ // end program = true at end of program	
	        currently_iterating_table=true;
			String result="";
	        int column=beggining_column;
			while (should_run && result!=null){
				result =iterate_table(column); //returns next table first cell
				if (result!=null && result.equals("exit")){
				    if (playMP3!=null)
				    	playMP3.close();
				    return;
				}	
				if (result!=null){
					result = result.split("!")[1];
					CellReference ref = new CellReference(result);
					column= ref.getCol();
				}
			}
			last_phrase.clear();
			last_phrase.addAll(my_phrase);
			if (should_run && !end_program){ //play chosen words
				robot.keyPress(KeyEvent.VK_Q);
				robot.keyRelease(KeyEvent.VK_Q);
				
				console.clear();
				console.println("\n\t\t\t מנגן משפט");
				
				for (Cell audio_c : my_phrase){
					play_audio(audio_c.getRowIndex(),audio_c.getColumnIndex());
				}
				robot.keyPress(KeyEvent.VK_W);
				robot.keyRelease(KeyEvent.VK_W);
			}
			my_phrase.clear();
		    light.setFill(Color.DARKRED);
		    
		    console.clear();
		    console.println("	      לחץ על \u21ba על מנת להתחיל");
		    
		    synchronized(this){ //wait until telling you if continue or finish program
				try {
					currently_iterating_table=false;
					synchronized(restart_lock){
						restart_lock.notifyAll();
					}
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		    button.switch_off();
		    
		    GUIUtils. runSafe(() -> {
		    	select_butt.setText("\uD83D\uDD08"); //no sound
		    	//select_butt.setTextFill(Color.BLACK);
		    	});
		    
		    should_run=true;
		    
		    if (mouse_move.getBool())
		   		robot.mouseMove(1500,1155);	
		    
		    }
	    playMP3.close();
	}

	public String iterate_table(int column){
		if (column==0)
			clear_history();
		int sleep_time=0; //changes between SLEEP(slow loop) and 0 (fast loop)
		boolean first_loop=true; //first = quick read, second = slow read to enable press 
		int col_index=1; //beginning of table (after table name) 
		XSSFHyperlink link_next=null; //the link that connects us to the first cell of the next menu
		int num_of_rows = sheet.getPhysicalNumberOfRows(); //total number of rows in the excel sheet
//		int print_iter = Math.min(ITERATION, num_of_rows); //number of rows in the CURRENT mini iteration
		
		console.clear();		
		cur_table = sheet.getRow(0).getCell(column).toString();
		
		GUIUtils.runSafe(() -> 
		{select_butt.setText("\uD83D\uDD08"); //no sound
    	 select_butt.setTextFill(Color.TURQUOISE);});
		
		// RUN OVER THE COLUMN OF THE TABLE
		for (int i=col_index; i<num_of_rows && should_run; i++){
			synchronized(this){
				if (should_pause)
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
        	// BUTTON WAS CLICKED = MOVE TO THE NEXT TABLE AND SAVE THE AUDIO IN THE PHRASE LIST
        	if (button.is_switch()){	 // play the audio file by the name in the cell
        		Cell audiocell = cur_cell;      		
        		if (audiocell==null){
        			console.clear();
        			console.println("לא קיים קובץ אודיו עבור תא זה");
        		}
        		else if (audiocell.getCellType()==Cell.CELL_TYPE_STRING){
        				my_phrase.add(audiocell);
        				update_history(audiocell.getStringCellValue());
        				/*play the current word through speakers*/
        				robot.keyPress(KeyEvent.VK_Q);
        				robot.keyRelease(KeyEvent.VK_Q);
                		play_audio(cur_cell.getRowIndex(),column);
                		robot.keyPress(KeyEvent.VK_W);
        				robot.keyRelease(KeyEvent.VK_W);
        		}
        		Cell link_cell = cur_cell.getRow().getCell(column+1); //retrieve the cell of the link to the related sub menu of the cell chosen
        		if (link_cell==null || link_cell.getCellType()==Cell.CELL_TYPE_BLANK){
        			return null;
        		}
        		link_next= (XSSFHyperlink)link_cell.getHyperlink(); //retrieve the hyperlink from the link cell
        	   	button.switch_off(); // button is back to unpressed mode
            	GUIUtils.runSafe(() -> 
            		{select_butt.setText("\uD83D\uDD08"); //no sound
                	 select_butt.setTextFill(Color.TURQUOISE);});
        	   	if (link_next!=null)
        	   		return link_next.getLocation();
        	   	return null;
        	}
        	// GET THE CURRENT CELL
        	row = sheet.getRow(i);
        	cur_cell=null;
        	if (row!=null)
        		cur_cell =row.getCell(column); // CELL number "cell_number"
        	
        	// Finished iterating "loop" headlines (first/second loop case)
        	if (i==col_index+ITERATION || cur_cell==null || cur_cell.getCellType()==Cell.CELL_TYPE_BLANK ){
        		if (first_loop){
         			button.be_active(); //slow passing - can press now			
        			GUIUtils.runSafe(() -> 
            		select_butt.setTextFill(Color.BLACK));
        			
        			first_loop=false;
        			i=col_index-1;
        			sleep_time=SLEEP;
        		}
        		else if (col_index+ITERATION<num_of_rows){ // we are in a slow loop, end of mini iteration
  //      				print_iter = Math.min(ITERATION,num_of_rows-i);
        				button.be_unActive();  //headlines introduction- no pressing 

        				GUIUtils.runSafe(() -> 
                		select_butt.setTextFill(Color.TURQUOISE));
        				
        				sleep_time=0;
            			first_loop=true;
        				col_index+=ITERATION;
        				i--; //after col_index was fixed - activate audio of this cell
        		}

        	}
        	// ELSE = NO RESPONSE FROM BUTTON YET, play next headline
        	else{ 
        		// if there is no audio file name in the cell
	        	if (cur_cell==null||cur_cell.getCellType()==Cell.CELL_TYPE_BLANK){
	        		console.clear();
	        		console.println("התא הנוכחי בקובץ האקסל ריק");
	        		break;
	        	}
	        	else{
	        		// there is an audio file
	        		int celltype = cur_cell.getCellType();
	        		if (celltype==Cell.CELL_TYPE_STRING)
	        			print_audio_to_console(cur_cell.toString());
	        		play_audio(row.getRowNum(),column);
	        	}
	            try {
	            	if (sleep_time!=0)
	            		Thread.sleep(sleep_time*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}  
	       }
        }
		if (end_program)
			return "exit";
		return null;
    }

	
	/*
		Play Audio:
		1. gets the cell with the file name of the audio text from the row and column
		2. opens a file input stream from the audio path with the file name
		3. plays it through the MP3 player object
	 */
	private void play_audio(int row, int column) {		
		Cell audiocell=sheet.getRow(row).getCell(column);
		if (audiocell==null || audiocell.getCellType()!=Cell.CELL_TYPE_STRING)
			return;
		try{
		    FileInputStream fis = new FileInputStream(cur_audio_path+"/"+audiocell.toString()+".mp3");
		    playMP3 = new Player(fis);
		    playMP3.play();
		}
		catch(Exception exc){
			console.clear();
		    console.println("לא ניתן לנגן קובץ זה: "+audiocell.getStringCellValue());
		}
	}
	
	/*
	 * SHOULD RUN- receives a boolean and changes it accordingly 
	 * if the stop button was pressed, it will be false
	 * if the start button is pressed it will be true 
	 */
	public void set_shouldRun(Boolean b){
		should_run=b;
	}
	
	
	/*
	 * PLAY LAST: plays the last phrase created by the user
	 * 1. opens a new thread
	 * 2. pauses the current iteration
	 * 3. plays the whole list of audio files saved in "last_phrase" 
	 * 4. resume the iteration of the menus
	 */
	@SuppressWarnings("restriction")
	public void play_last(){
		if (last_phrase.isEmpty())
			return;
		new Thread(()->{
			if (isCurrently_iterating_table()){
				pause();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			robot.keyPress(KeyEvent.VK_Q);
			robot.keyRelease(KeyEvent.VK_Q);
			for (Cell audio_c : last_phrase){
				play_audio(audio_c.getRowIndex(),audio_c.getColumnIndex());
			}
			robot.keyPress(KeyEvent.VK_W);
			robot.keyRelease(KeyEvent.VK_W);
			if (isCurrently_iterating_table())
				forward();
		}).start();	
	}
	
	/*
	 * PAUSE: pauses the iteration (pause=true)
	 */
	public synchronized void pause(){
		should_pause=true;
	}
	
	/*
	 * FORWARD: resumes the iteration (pause=false) and awakes all waiting threads on the menu
	 */
	public synchronized void forward(){
		should_pause=false;
		notifyAll();
	}
	
	public boolean getShouldPause(){
		return should_pause;
	}

	public void set_endProgram(boolean b) {
		end_program=b;
	}

	public boolean isCurrently_iterating_table() {
		return currently_iterating_table;
	}

	public void setCurrently_iterating_table(boolean currently_iterating_table) {
		this.currently_iterating_table = currently_iterating_table;
	}

	public void update_sleep(String s){
		try{
			int num =Integer.parseInt(s);
			if (num<1 || num>15){
				console.clear();
				console.println("נא להכניס מספר בין 1 ל15");
				return;
			}
			SLEEP=num;
		} catch (NumberFormatException e){
			console.clear();
			console.println("נא להכניס מספר בין 1 ל15");
		}
		write_new_vals_to_file();
	}
	
	public void update_loops(String s){
		try{
			int num =Integer.parseInt(s);
			if (num<1 || num>40){
				console.clear();
				console.println("נא להכניס מספר בין 1 ל15");
				return;
			}
			ITERATION=num;
		} catch (NumberFormatException e){
			console.clear();
			console.println("נא להכניס מספר בין 1 ל15");
		}
		write_new_vals_to_file();
	}
	
	public void print_audio_to_console(String s){
		String[] ans = s.split("_");
		console.clear();
		console.println("טבלה: "+cur_table+"\n");
		for (int i=0; i<ans.length; i++){
			console.print(ans[i]+" ");
		}
	}
	
	private String get_cell_name(String s){
		String answer="";
		String[] ans = s.split("_");
		for (int i=0; i<ans.length; i++){
			answer+=ans[i]+" ";
		}
		return answer;
	}
	
	public void set_workbook(Workbook w){
		this.wb=w;
		sheet=(XSSFSheet) wb.getSheetAt(0);
	}
	
	public void set_audio_path(String s){
		cur_audio_path=s;
	}
	
	public void write_new_vals_to_file(){
		try {
			pw =new PrintWriter(durations);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pw.println(""+ITERATION);
		pw.println(""+SLEEP);
		pw.close();
	}
	
	public Text get_history(){
		return history_window;
	}
	
	private void update_history(String val){
		history_window.setText(history_window.getText()+"\u2022 "+get_cell_name(val)+"\n");
	}
	
	private void clear_history(){
		history_window.setText("");
	}
	
	public LinkedList<String> getMenuTitles(){
		LinkedList<String> titles= new LinkedList<String>();
		Row menurow = sheet.getRow(0);
		int i=0;
		Cell curtitle= menurow.getCell(i);
		while (curtitle.getStringCellValue()!=null && curtitle.getStringCellValue()!="" ){
			titles.addLast(curtitle.getStringCellValue());
			i+=2;
		}
		return titles;
	}

	public void set_beggining_column(int columnIndex) {
		beggining_column=columnIndex;
	}
	
}
