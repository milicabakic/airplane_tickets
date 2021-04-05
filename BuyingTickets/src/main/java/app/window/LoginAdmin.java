package app.window;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import app.forms.Login_Form;
import app.utils.Utils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginAdmin extends Application {

	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;
	
	Map<String, String> rute;
    String serviceName;

	public LoginAdmin() {
		rute = new HashMap<String, String>();
		serviceName =  "registrator-service";
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders();
		
		primaryStage.setTitle("Login Admin");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label userName = new Label("Korisnicko ime:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Šifra:");
		grid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);
		

		Button btn = new Button("Uloguj se");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		
		final Text actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		grid.add(actiontarget, 1, 6);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {
					actiontarget.setText("");
					
					HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
					ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes", 
							 HttpMethod.GET, httpEntity, String.class);	
					
					Utils.parseAllRoutes(rute, routes.getBody());
					
					if(!rute.containsValue(serviceName)) {
						HttpEntity<String> httpEnt = new HttpEntity<String>(null, headers);
						ResponseEntity<String> r = restTemplate.exchange("http://localhost:8762/actuator/routes", 
								 HttpMethod.GET, httpEnt, String.class);	
						
						Utils.parseAllRoutes(rute, r.getBody());
					}
					
					if(!rute.containsValue(serviceName)) {
						actiontarget.setText("Connection error! Please try later.");
						return;
					}
					
					String route = Utils.getKeyByValue(rute, serviceName);
					
					Login_Form login_form = new Login_Form(userTextField.getText(), pwBox.getText());
					HttpEntity<Login_Form> entity = new HttpEntity<Login_Form>(login_form, headers);
					ResponseEntity<String> response = restTemplate.exchange("http://localhost:8762"+route+"login",
							HttpMethod.POST, entity, String.class);

					if (response.getStatusCode().equals(HttpStatus.OK)) {

						Stage stage = new Stage();
						primaryStage.close();

						try {
							AdminMainWindow am = AdminMainWindow.getInstance();
							am.start(stage);
							am.setRute(rute);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					} else {
						actiontarget.setText("Pogresni kredencijali!");
					}
				} catch (Exception e1) {
					actiontarget.setText("Pogresni kredencijali!");
				}
			}
		});

		Scene scene = new Scene(grid, 450, 350);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
