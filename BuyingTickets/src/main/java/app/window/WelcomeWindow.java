package app.window;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class WelcomeWindow extends Application{
	
	private static WelcomeWindow instance;
	
	private WelcomeWindow() {
		
	}
	
	public static WelcomeWindow getInstance() {
		if (instance == null) {
			instance = new WelcomeWindow();
		}
		return instance;
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Avionske karte");

		Label welcomeLabel = new Label("Dobrodošli u internet prodavnicu avionskih karata!");
		
		Button login = new Button("Login");
		
		Button registration = new Button("Registracija");
		
		Button admin = new Button("Admin");
		
		login.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event){
				Stage stage = new Stage();
				LoginWindow loginW = new LoginWindow();
				try {
					loginW.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
	
			}
		});
		
		registration.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stage = new Stage();
				RegisterWindow rw = new RegisterWindow();
				try {
					rw.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		admin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stage = new Stage();
				LoginAdmin rw = new LoginAdmin();
				try {
					rw.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
				
        
		GridPane layout = new GridPane();
		layout.setAlignment(Pos.CENTER);
		
		layout.setVgap(20);
		layout.setHgap(10);
		
		HBox boxH1 = new HBox(20);
		boxH1.setAlignment(Pos.BOTTOM_CENTER);
		boxH1.getChildren().addAll(login,registration,admin);
		

		layout.add(welcomeLabel, 0, 0);
		layout.add(boxH1, 0, 1);

		Scene scene = new Scene(layout, 450, 350);

		primaryStage.setScene(scene);
		primaryStage.show();

		
	}

}
