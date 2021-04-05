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
import app.entities.Let;
import app.forms.LetForm;
import app.utils.Utils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddFlightWindow extends Application{
	
	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;
    private Map<String,String> rute;
	private String service2Name;
    
	public AddFlightWindow(Map<String,String> rute,String service2Name) {
		this.rute = rute;
		this.service2Name = service2Name;
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders();
		
		primaryStage.setTitle("Dodavanje novog leta");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Label avion = new Label("Avion:");
		grid.add(avion, 0, 0);
		
		ComboBox<Avion> avioni = new ComboBox<Avion>();
		avioni.setItems(AdminMainWindow.getInstance().getAvioni());
		grid.add(avioni, 1, 0);

		Label userName = new Label("Pocetna destinacija:");
		grid.add(userName, 0, 1);

		TextField startTf = new TextField();
		grid.add(startTf, 1, 1);

		Label pw = new Label("Krajnja destinacija:");
		grid.add(pw, 0, 2);

		TextField krajTf = new TextField();
		grid.add(krajTf, 1, 2);
		
		Label duzina = new Label("Duzina leta:");
		grid.add(duzina, 0, 3);

		TextField duzinaTf = new TextField();
		grid.add(duzinaTf, 1, 3);
		
		Label cena = new Label("Cena u E:");
		grid.add(cena, 0, 4);

		TextField cenaTf = new TextField();
		grid.add(cenaTf, 1, 4);

		Button btn = new Button("Dodaj");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 6);

		Label alert = new Label();
		alert.setTextFill(Color.FIREBRICK);
		grid.add(alert, 1, 5);

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
					
					LetForm letForm = new LetForm(avioni.getSelectionModel().getSelectedItem(), startTf.getText(), krajTf.getText(),
							Long.parseLong(duzinaTf.getText()), Integer.parseInt(cenaTf.getText()));
					HttpEntity<LetForm> entity = new HttpEntity<LetForm>(letForm, headers);
					ResponseEntity<List<Let>> response = restTemplate.exchange("http://localhost:8762" + route + "addFlight",
							HttpMethod.POST, entity, new ParameterizedTypeReference<List<Let>>() {});
					
					if (response.getStatusCode() == HttpStatus.ACCEPTED) {
						AdminMainWindow.getInstance().getLetovi().setAll(response.getBody());
						primaryStage.close();
					} else {
						alert.setText("Neuspesno dodavanje leta.");
					}

				} catch (Exception e) {
					alert.setText("Neuspesno dodavanje leta.");
				}

			}
		});

		Scene scene = new Scene(grid, 450, 350);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

}
