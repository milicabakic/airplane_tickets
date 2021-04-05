package app;

import app.window.WelcomeWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		WelcomeWindow ww = WelcomeWindow.getInstance();
		ww.start(primaryStage);
		
		
	}

}
