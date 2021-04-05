package app.window;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import app.entities.User;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginWindow extends Application {

	@Autowired
	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;

	Map<String, String> rute;
	String serviceName;

	public LoginWindow() {
		rute = new HashMap<String, String>();
		serviceName = "registrator-service";
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Login");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Dobrodošli!");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("E-mail:");
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
				headers = new HttpHeaders();
				actiontarget.setText("");

				Login_Form login_form = new Login_Form(userTextField.getText(), pwBox.getText());
				HttpEntity<Login_Form> entity = new HttpEntity<Login_Form>(login_form, headers);
				try {
					HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
					ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes",
							HttpMethod.GET, httpEntity, String.class);

					System.out.println(routes.getBody());

					Utils.parseAllRoutes(rute, routes.getBody());
					System.out.println(rute);

					if (!(rute.containsValue(serviceName))) {
						actiontarget.setText("Molimo Vas sacekajte...");
						HttpEntity<String> httpEnt = new HttpEntity<String>(null, headers);
						ResponseEntity<String> r = restTemplate.exchange("http://localhost:8762/actuator/routes",
								HttpMethod.GET, httpEnt, String.class);

						Utils.parseAllRoutes(rute, r.getBody());
					}

					if (!(rute.containsValue(serviceName))) {
						actiontarget.setText("Doslo je do greske! Pokusajte kasnije.");
						return;
					}
					actiontarget.setText("");

					String route = Utils.getKeyByValue(rute, serviceName);

					ResponseEntity<String> response = restTemplate.exchange("http://localhost:8762" + route + "login",
							HttpMethod.POST, entity, String.class);

					if (response.getStatusCode().equals(HttpStatus.OK)) {

						headers.add("Authorization", response.getHeaders().getFirst("Authorization"));
						HttpEntity<String> en = new HttpEntity<String>(null, headers);
						ResponseEntity<User> res = restTemplate.exchange("http://localhost:8762" + route + "whoAmI",
								HttpMethod.GET, en, User.class);
						Stage stage = new Stage();
						MainWindow mw = MainWindow.getInstance();
						mw.setUser(res.getBody());
						mw.setRute(rute);
						primaryStage.close();
						try {
							mw.start(stage);
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
