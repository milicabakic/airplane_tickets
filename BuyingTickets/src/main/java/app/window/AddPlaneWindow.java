package app.window;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import app.entities.Avion;
import app.forms.AvionForm;
import app.utils.Utils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddPlaneWindow extends Application {

	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers = new HttpHeaders();
    private Map<String,String> rute;
	private String service2Name;
    
	public AddPlaneWindow(Map<String, String> rute, String serviceName) {
		this.rute = rute;
		this.service2Name = serviceName;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Dodavanje novog aviona");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label userName = new Label("Naziv:");
		grid.add(userName, 0, 1);

		TextField nazivTf = new TextField();
		grid.add(nazivTf, 1, 1);

		Label pw = new Label("Kapacitet putnika:");
		grid.add(pw, 0, 2);

		TextField kapacitetTf = new TextField();
		grid.add(kapacitetTf, 1, 2);

		Button btn = new Button("Dodaj");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);

		Label alert = new Label();
		alert.setTextFill(Color.FIREBRICK);
		grid.add(alert, 1, 3);

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					alert.setText("");
					
					if (!rute.containsValue(service2Name)) {
						HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
						ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes",
								HttpMethod.GET, httpEntity, String.class);

						Utils.parseAllRoutes(rute, routes.getBody());
					}
					
					if (!(rute.containsValue(service2Name))) {
						alert.setText("Doslo je do greske! Pokusajte kasnije.");
						return;
					}
					
					String route = Utils.getKeyByValue(rute, service2Name);
					
					AvionForm af = new AvionForm(nazivTf.getText(), Integer.parseInt(kapacitetTf.getText()));
					HttpEntity<AvionForm> entity = new HttpEntity<AvionForm>(af, headers);
					ResponseEntity<List<Avion>> response = restTemplate.exchange("http://localhost:8762" + route + "addPlane",
							HttpMethod.POST, entity, new ParameterizedTypeReference<List<Avion>>() {});
					
					if (response.getStatusCode() == HttpStatus.ACCEPTED) {
						AdminMainWindow.getInstance().getAvioni().setAll(response.getBody());
						primaryStage.close();
					} else {
						alert.setText("Neuspesno dodavanje aviona.");
					}

				} catch (Exception e) {
					alert.setText("Neuspesno dodavanje aviona.");
				}

			}
		});

		Scene scene = new Scene(grid, 350, 250);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
