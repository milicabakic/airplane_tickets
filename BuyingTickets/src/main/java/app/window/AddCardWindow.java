package app.window;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import app.entities.User;
import app.forms.KreditnaKarticaForm;
import app.utils.Utils;
import javafx.application.Application;
import javafx.collections.FXCollections;
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
import javafx.stage.Stage;

public class AddCardWindow extends Application {

	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;

	private User user;
	private String serviceName;
	private Map<String, String> rute;

	public AddCardWindow(User user, Map<String, String> rute) {
		this.user = user;
		this.rute = rute;
		this.serviceName = "registrator-service";
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders();
		primaryStage.setTitle("Dodavanje nove kreditne kartice");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label broj = new Label("Broj kartice:");
		grid.add(broj, 0, 1);

		TextField brojTf = new TextField();
		grid.add(brojTf, 1, 1);

		Label pin = new Label("PIN:");
		grid.add(pin, 0, 2);

		PasswordField pinTf = new PasswordField();
		grid.add(pinTf, 1, 2);

		Label alert = new Label();
		alert.setTextFill(Color.FIREBRICK);

		Button btn = new Button("Dodaj");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		grid.add(alert, 0, 5);

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!rute.containsValue(serviceName)) {
					HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
					ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes",
							HttpMethod.GET, httpEntity, String.class);
					Utils.parseAllRoutes(rute, routes.getBody());
				}
				
				if (!(rute.containsValue(serviceName))) {
					alert.setText("Doslo je do greske! Pokusajte kasnije.");
					return;
				}
				
				String route = Utils.getKeyByValue(rute, serviceName);

				KreditnaKarticaForm kf = new KreditnaKarticaForm(brojTf.getText(), pinTf.getText());
				HttpEntity<KreditnaKarticaForm> entity = new HttpEntity<KreditnaKarticaForm>(kf, headers);
				ResponseEntity<User> response = restTemplate.exchange(
						"http://localhost:8762"+route+"creditCard/" + user.getId(), HttpMethod.POST, entity, User.class);

				user = response.getBody();
				MainWindow.getInstance().setUser(user);

				MainWindow.getInstance().getKartice().setItems(FXCollections.observableList(user.getKartice()));

				primaryStage.close();
			}
		});

		Scene scene = new Scene(grid, 300, 275);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
