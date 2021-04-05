package app.window;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import app.entities.User;
import app.forms.RegistrationForm;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterWindow extends Application {

	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;
    private Map<String, String> rute;
    private String serviceName;
	
	public RegisterWindow() {
		rute = new HashMap<String, String>();
		this.serviceName = "registrator-service";
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders(); 
		
		primaryStage.setTitle("Registracija");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Dobrodošli!");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label ime = new Label("Ime:");
		grid.add(ime, 0, 1);
		TextField imeTf = new TextField();
		grid.add(imeTf, 1, 1);

		Label prezime = new Label("Prezime:");
		grid.add(prezime, 0, 2);
		TextField prezimeTf = new TextField();
		grid.add(prezimeTf, 1, 2);

		Label brPasosa = new Label("Broj pasoša:");
		grid.add(brPasosa, 0, 3);
		TextField brPasosaTf = new TextField();
		grid.add(brPasosaTf, 1, 3);

		Label email = new Label("E-mail:");
		grid.add(email, 0, 4);
		TextField emailTf = new TextField();
		grid.add(emailTf, 1, 4);

		Label sifra = new Label("Šifra:");
		grid.add(sifra, 0, 5);
		PasswordField sifraTf = new PasswordField();
		grid.add(sifraTf, 1, 5);

		Button btn = new Button("Registruj se");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 6);

		final Text actiontarget = new Text();
		grid.add(actiontarget, 1, 7);
		actiontarget.setFill(Color.FIREBRICK);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				actiontarget.setText("");
				
				RegistrationForm registrationForm = new RegistrationForm(imeTf.getText(), prezimeTf.getText(),
						emailTf.getText(), sifraTf.getText(), brPasosaTf.getText());

				HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
				ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes", 
						 HttpMethod.GET, httpEntity, String.class);	
				
				Utils.parseAllRoutes(rute, routes.getBody());
				
				if(!(rute.containsValue("registrator-service"))) {
					actiontarget.setText("Molimo sacekajte...");
					ResponseEntity<String> sveRute = restTemplate.exchange("http://localhost:8762/actuator/routes", 
							 HttpMethod.GET, httpEntity, String.class);	
					
					Utils.parseAllRoutes(rute, sveRute.getBody());
				}
				
				if(!(rute.containsValue("registrator-service"))) {
					actiontarget.setText("Doslo je do greske! Pokusajte kasnije.");
					return;
				}
				actiontarget.setText("");
					
				String route = Utils.getKeyByValue(rute, serviceName);
				
				HttpEntity<RegistrationForm> entity = new HttpEntity<RegistrationForm>(registrationForm, headers);

				ResponseEntity<User> response = restTemplate.exchange("http://localhost:8762"+route+"register", 
						HttpMethod.POST, entity, User.class);
				
				if (response.getBody() != null) {
					Stage stage = new Stage();
					MainWindow mw = MainWindow.getInstance();
					mw.setUser(response.getBody());
					mw.setRute(rute);
					primaryStage.close();
					try {
						mw.start(stage);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}else {
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Došlo je do greške, pokušajte ponovo!");
				}
			}
		});

		Scene scene = new Scene(grid, 450, 350);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
