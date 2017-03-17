package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
/*
 * This is an external console screen that can be attached to a GUI
 * uses the runsafe() method from the GUIUtils class
 * taken from stack overflow (more options in the full class over there)
 */
public class Console extends BorderPane {
    protected final TextArea output = new TextArea();

    public Console(int x, int y, int w, int h) {
    	output.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT); //Hebrew alignment
    	output.setFont(Font.font("Arial",20));
    	output.setEditable(false); //screen cannot be rewritten
        setCenter(output);
        this.setMaxSize(w,h); //478 220
        this.setMinSize(w,h); // 400,190
        println("	      לחץ על \u25B6 על מנת להתחיל");
	    this.setLayoutX(x); //62
	    this.setLayoutY(y); //272
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
    }

    //Clear - deletes all content from the console screen
    public void clear() {
    	GUIUtils. runSafe(() -> output.clear());
    }

    //Print - equal to syso.print()
    public void print(final String text) {
        Objects.requireNonNull(text, "text");
        GUIUtils.runSafe(() -> output.appendText(text));

    }
    
    //Println - equal to syso.println()
    public void println(final String text) {
        Objects.requireNonNull(text, "text");
        GUIUtils.runSafe(() -> output.appendText("\n" +text + System.lineSeparator()));
    }

    //Println with no text
    public void println() {
    	GUIUtils.runSafe(() -> output.appendText(System.lineSeparator()));
    }

}

