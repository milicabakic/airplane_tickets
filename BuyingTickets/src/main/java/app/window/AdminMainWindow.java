package app.window;

import java.util.HashMap;
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
import app.utils.Utils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AdminMainWindow extends Application{
	
	private static AdminMainWindow instance;
	
	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;
	
	private Map<String,String> rute;
	private String service2Name;
	
	private TableView<Let> table;
	private ObservableList<Let> letovi;
	
	private TableView<Avion> tableAvioni;
	private ObservableList<Avion> avioni;
	
	private AdminMainWindow() {
		rute = new HashMap<String, String>();
		service2Name = "flights-service";
	}
	
	public static AdminMainWindow getInstance() {
		if (instance == null) instance = new AdminMainWindow();
		return instance;
	}
	
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders();
		
		primaryStage.setTitle("Dobrodosao admine!");
		BorderPane layout = new BorderPane();
		
		Button dodajLet = new Button("Dodaj let");
		Button obrisiLet = new Button("Obrisi let");
		Button dodajAvion = new Button("Dodaj avion");
		Button obrisiAvion = new Button("Obrisi avion");
		
		TextField searchTf = new TextField();
		searchTf.setPrefWidth(700);
		Button search = new Button("Pretrazi letove");
		Label alert = new Label();

		
		
		table = new TableView<Let>();
		table.setPrefWidth(800);
		table.setPrefHeight(200);
		
		TableColumn<Let, Avion> avionCol = new TableColumn<Let, Avion>("Avion");
		TableColumn<Let, String> pocDestCol = new TableColumn<Let, String>("Pocetna destinacija");
		TableColumn<Let, String> krajDestCol = new TableColumn<Let, String>("Krajnja destinacija");
		TableColumn<Let, Long> duzinaCol = new TableColumn<Let, Long>("Duzina leta");
		TableColumn<Let, Integer> cenaCol = new TableColumn<Let, Integer>("Cena");
		TableColumn<Let, String> stanjeCol = new TableColumn<Let, String>("Stanje");
		table.getColumns().addAll(avionCol, pocDestCol, krajDestCol, duzinaCol, cenaCol, stanjeCol);

		avionCol.setCellValueFactory(new PropertyValueFactory<Let, Avion>("avion"));
		pocDestCol.setCellValueFactory(new PropertyValueFactory<Let, String>("pocetnaDestinacija"));
		krajDestCol.setCellValueFactory(new PropertyValueFactory<Let, String>("krajnjaDestinacija"));
		duzinaCol.setCellValueFactory(new PropertyValueFactory<Let, Long>("duzinaLeta"));
		cenaCol.setCellValueFactory(new PropertyValueFactory<Let, Integer>("cena"));
		stanjeCol.setCellValueFactory(new PropertyValueFactory<Let, String>("stanje"));
		
		tableAvioni = new TableView<Avion>();
		tableAvioni.setPrefWidth(400);
		tableAvioni.setPrefHeight(200);
		
		TableColumn<Avion, Long> idCol = new TableColumn<Avion, Long>("ID Aviona");
		TableColumn<Avion, String> nazivCol = new TableColumn<Avion, String>("Naziv");
		TableColumn<Avion, String> kapacitetPutnikaCol = new TableColumn<Avion, String>("Kapacitet putnika");
		tableAvioni.getColumns().addAll(idCol, nazivCol, kapacitetPutnikaCol);
		
		idCol.setCellValueFactory(new PropertyValueFactory<Avion, Long>("id"));
		nazivCol.setCellValueFactory(new PropertyValueFactory<Avion, String>("naziv"));
		kapacitetPutnikaCol.setCellValueFactory(new PropertyValueFactory<Avion, String>("kapacitetPutnika"));
		
		
		if (!rute.containsValue(service2Name)) {
			HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
			ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes",
					HttpMethod.GET, httpEntity, String.class);

			Utils.parseAllRoutes(rute, routes.getBody());
		}
		
		if (!(rute.containsValue(service2Name))) {
			alert.setText("Connection error! Please try later.");
			return;
		}
		
		String route = Utils.getKeyByValue(rute, service2Name);
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<List<Let>> response = restTemplate.exchange("http://localhost:8762" + route + "allflights",
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Let>>() {
				});

		letovi = FXCollections.observableList(response.getBody());
		table.setItems(letovi);
		
		ResponseEntity<List<Avion>> res = restTemplate.exchange("http://localhost:8762" + route + "planes",
				HttpMethod.GET, entity, new ParameterizedTypeReference<List<Avion>>() {});

		avioni = FXCollections.observableList(res.getBody());
		tableAvioni.setItems(avioni);
		
		Label avionAlert = new Label();
		Label letAlert = new Label();
		
		dodajAvion.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stage = new Stage();
				AddPlaneWindow ap = new AddPlaneWindow(rute, service2Name);
				try {
					ap.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		obrisiAvion.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
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
				
				if (tableAvioni.getSelectionModel().getSelectedItem() != null) {
					long id = tableAvioni.getSelectionModel().getSelectedItem().getId();
					ResponseEntity<List<Avion>> res = restTemplate.exchange("http://localhost:8762"+route+"deletePlane/" + id,
							HttpMethod.DELETE, entity, new ParameterizedTypeReference<List<Avion>>() {});
										
					if (res.getStatusCode() == HttpStatus.ACCEPTED) {
						avionAlert.setText("Avion je uspesno obrisan.");
						avionAlert.setTextFill(Color.GREEN);
						avioni.setAll(res.getBody());
					}else{
						avionAlert.setText("Avion nije obrisan.");
						avionAlert.setTextFill(Color.FIREBRICK);
					}
					
				}else {
					avionAlert.setText("Morate izabrati avion iz tabele.");
					avionAlert.setTextFill(Color.FIREBRICK);
				}
			}
		});
		
		dodajLet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage s = new Stage();
				AddFlightWindow af = new AddFlightWindow(rute, service2Name);
				try {
					af.start(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		obrisiLet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
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
				
				if (table.getSelectionModel().getSelectedItem() != null) {
					long id = table.getSelectionModel().getSelectedItem().getId();
					ResponseEntity<List<Let>> res = restTemplate.exchange("http://localhost:8762" + route + "deleteFlight/" + id,
							HttpMethod.DELETE, entity, new ParameterizedTypeReference<List<Let>>() {
							});
					
					if (res.getStatusCode() == HttpStatus.ACCEPTED) {
						letAlert.setText("Let je uspesno obrisan.");
						letAlert.setTextFill(Color.GREEN);
						letovi.setAll(res.getBody());
					}else{
						letAlert.setText("Let nije obrisan.");
						letAlert.setTextFill(Color.FIREBRICK);
					}
					
				}else {
					letAlert.setText("Morate izabrati let iz tabele.");
					letAlert.setTextFill(Color.FIREBRICK);
				}
			}
		});
		
		search.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
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
			    
				if (searchTf.getText().equals("")) {
					HttpEntity<String> entity = new HttpEntity<String>(null, headers);
					ResponseEntity<List<Let>> response = restTemplate.exchange("http://localhost:8762" + route + "allflights",
							HttpMethod.GET, entity, new ParameterizedTypeReference<List<Let>>() {
							});

					letovi.setAll(response.getBody());
					
				} else {
					try {
						HttpEntity<String> entity = new HttpEntity<String>(null, headers);
						ResponseEntity<List<Let>> response = restTemplate.exchange(
								"http://localhost:8762" + route + "allflights?search=" + searchTf.getText(), HttpMethod.GET, entity,
								new ParameterizedTypeReference<List<Let>>() {});

						letovi.setAll(response.getBody());

					} catch (Exception e) {
						alert.setText("Pretraga je neuspesna. Pokusajte ponovo!");
						alert.setTextFill(Color.FIREBRICK);
					}

				}

			}
		});
		
		GridPane centerGrid = new GridPane();
		centerGrid.setAlignment(Pos.CENTER);
		centerGrid.setHgap(10);
		centerGrid.setVgap(10);
		HBox hBox1 = new HBox(10);
		hBox1.getChildren().addAll(searchTf, search);
		HBox hBox2 = new HBox(10);
		hBox2.setAlignment(Pos.CENTER);
		hBox2.getChildren().addAll(obrisiLet, dodajLet);
		centerGrid.add(hBox1, 0, 0);
		centerGrid.add(alert, 0, 1);
		centerGrid.add(table, 0, 2);
		centerGrid.add(hBox2, 0, 3);
		HBox hBox3 = new HBox(10);
		hBox3.setAlignment(Pos.CENTER);
		hBox3.getChildren().add(letAlert);
		centerGrid.add(hBox3, 0, 4);
		
		GridPane topGrid = new GridPane();
		topGrid.setAlignment(Pos.CENTER);
		topGrid.setHgap(10);
		topGrid.setVgap(10);
		
		topGrid.add(tableAvioni, 1, 1);
		HBox hBox = new HBox(10);
		hBox.getChildren().addAll(dodajAvion, obrisiAvion);
		topGrid.add(hBox, 3, 1);
		topGrid.add(avionAlert, 1, 2);
		
		layout.setCenter(centerGrid);
		layout.setTop(topGrid);
		Scene scene = new Scene(layout, 900, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}
	
	public TableView<Avion> getTableAvioni() {
		return tableAvioni;
	}
	
	public void setTableAvioni(TableView<Avion> tableAvioni) {
		this.tableAvioni = tableAvioni;
	}
	
	public ObservableList<Avion> getAvioni() {
		return avioni;
	}
	public void setAvioni(ObservableList<Avion> avioni) {
		this.avioni = avioni;
	}
	
	public TableView<Let> getTable() {
		return table;
	}
	
	public void setTable(TableView<Let> table) {
		this.table = table;
	}
	
	public ObservableList<Let> getLetovi() {
		return letovi;
	}
	
	public void setLetovi(ObservableList<Let> letovi) {
		this.letovi = letovi;
	}
	
	public void setRute(Map<String, String> rute) {
		this.rute = rute;
	}

}
