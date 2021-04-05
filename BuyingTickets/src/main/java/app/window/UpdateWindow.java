package app.window;

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
import javafx.stage.Stage;

public class UpdateWindow extends Application {

	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;

	private User user;
	private Map<String, String> rute;
	String serviceName;

	private TextField imeTf = new TextField();
	private TextField prezimeTf = new TextField();
	private TextField brPasosaTf = new TextField();
	private TextField emailTf = new TextField();
	private PasswordField sifraTf = new PasswordField();

	public UpdateWindow(User user, Map<String, String> rute) {
		this.user = user;
		this.imeTf.setText(user.getIme());
		this.prezimeTf.setText(user.getPrezime());
		this.emailTf.setText(user.getEmail());
		this.brPasosaTf.setText(user.getBrPasosa());
		this.rute = rute;
		this.serviceName = "registrator-service";
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders();
		primaryStage.setTitle("Uredjivanje profila");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label ime = new Label("Ime:");
		grid.add(ime, 0, 1);
		grid.add(imeTf, 1, 1);

		Label prezime = new Label("Prezime:");
		grid.add(prezime, 0, 2);
		grid.add(prezimeTf, 1, 2);

		Label brPasosa = new Label("Broj pasosa:");
		grid.add(brPasosa, 0, 3);
		grid.add(brPasosaTf, 1, 3);

		Label email = new Label("E-mail:");
		grid.add(email, 0, 4);
		grid.add(emailTf, 1, 4);

		Label sifra = new Label("Sifra:");
		grid.add(sifra, 0, 5);
		grid.add(sifraTf, 1, 5);

		Button btn = new Button("Sacuvaj");

		Label alert = new Label();

		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 6);
		grid.add(alert, 1, 7);

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				if (sifraTf.getText().equals("")) {
					alert.setText("Morate uneti sifru.");
					alert.setTextFill(Color.FIREBRICK);

				} else {
					alert.setText("");

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

					RegistrationForm rf = new RegistrationForm(imeTf.getText(), prezimeTf.getText(), emailTf.getText(),
							sifraTf.getText(), brPasosaTf.getText());

					HttpEntity<RegistrationForm> entity = new HttpEntity<RegistrationForm>(rf, headers);

					ResponseEntity<User> response = restTemplate.exchange(
							"http://localhost:8762" + route + "updateUser/" + user.getId(), HttpMethod.PUT, entity,
							User.class);

					user = response.getBody();
					MainWindow.getInstance().setUser(user);
					MainWindow.getInstance().getIme().setText("Ime: " + user.toString());
					MainWindow.getInstance().getEmail().setText("E-mail: " + user.getEmail());
					MainWindow.getInstance().getPasos().setText("Broj pasosa: " + user.getBrPasosa());
					primaryStage.close();
				}
			}
		});

		Scene scene = new Scene(grid, 450, 350);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TextField getImeTf() {
		return imeTf;
	}

	public TextField getPrezimeTf() {
		return prezimeTf;
	}

	public TextField getEmailTf() {
		return emailTf;
	}

	public TextField getBrPasosaTf() {
		return brPasosaTf;
	}

	public PasswordField getSifraTf() {
		return sifraTf;
	}

}
