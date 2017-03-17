package application;

import java.util.Objects;

import javafx.application.Platform;

//a UTIL class to help modify UI elements outside of the JAVAFX thread 
public final class GUIUtils {
    
	//there cannot be an instance of this class. used only for its static "run safe" function
	private GUIUtils() {
        throw new UnsupportedOperationException();
    }
    
    //makes sure the variables the runnable receives are not null, and that it is running on the JAVAFX app thread.
    public static void runSafe(final Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.runLater(runnable);
        }
    }
}
