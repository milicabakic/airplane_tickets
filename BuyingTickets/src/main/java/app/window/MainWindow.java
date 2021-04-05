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
import app.entities.Karta;
import app.entities.KreditnaKartica;
import app.entities.Let;
import app.entities.User;
import app.forms.KartaForm;
import app.utils.Utils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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

public class MainWindow extends Application {

	private static MainWindow instance;

	private TableView<Let> table;
	private ComboBox<KreditnaKartica> kartice = new ComboBox<KreditnaKartica>();

	private User user;
	private Map<String, String> rute;
	String service1Name;
	String service2Name;
	String service3Name;

	private Label ime;
	private Label email;
	private Label pasos;
	private Label milje;
	private Label rank;

	RestTemplate restTemplate = new RestTemplate();
	public static HttpHeaders headers;

	private MainWindow() {
		rute = new HashMap<String, String>();
		service1Name = "registrator-service";
		service3Name = "flightticktes-service";
		service2Name = "flights-service";
	}

	public static MainWindow getInstance() {
		if (instance == null)
			instance = new MainWindow();
		return instance;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		headers = new HttpHeaders();
		primaryStage.setTitle("Kupovina avionske karte");

		BorderPane layout = new BorderPane();

		Label b1 = new Label();
		b1.setPrefWidth(40);
		Label b2 = new Label();
		b2.setPrefWidth(40);
		Label b3 = new Label();
		b3.setPrefWidth(40);
		Label b4 = new Label();
		b4.setPrefWidth(40);
		Label b5 = new Label();
		b5.setPrefWidth(40);
		Label b6 = new Label();
		b6.setPrefWidth(40);
		ime = new Label("Ime: " + user.getIme() + " " + user.getPrezime());
		email = new Label("E-mail: " + user.getEmail());
		pasos = new Label("Broj pasosa: " + user.getBrPasosa());
		milje = new Label("Milje: " + user.getBrojMilja());
		rank = new Label("Rank: " + user.getRank());

		Label kk = new Label("Kreditna kartica:");

		ime.setTextFill(Color.DEEPSKYBLUE);
		email.setTextFill(Color.DEEPSKYBLUE);
		pasos.setTextFill(Color.DEEPSKYBLUE);
		milje.setTextFill(Color.DEEPSKYBLUE);
		rank.setTextFill(Color.DEEPSKYBLUE);

		ime.setPrefWidth(250);

		Button update = new Button("Uredi profil");

		kartice.setItems(FXCollections.observableArrayList(user.getKartice()));
		kartice.setPrefWidth(200);
		Button dodajKarticu = new Button("Dodaj karticu");

		table = new TableView<Let>();
		table.setPrefWidth(800);
		table.setPrefHeight(200);

		TextField searchTf = new TextField();
		searchTf.setPrefWidth(700);
		Button search = new Button("Pretrazi letove");
		Label alert = new Label();

		update.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stage = new Stage();
				UpdateWindow uw = new UpdateWindow(user, rute);
				try {
					uw.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		dodajKarticu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				AddCardWindow kw = new AddCardWindow(user, rute);
				Stage stage = new Stage();
				try {
					kw.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
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

				String route = Utils.getKeyByValue(rute, service2Name);

				if (searchTf.getText().equals("")) {

					HttpEntity<String> entity = new HttpEntity<String>(null, headers);
					ResponseEntity<List<Let>> response = restTemplate.exchange(
							"http://localhost:8762" + route + "flights", HttpMethod.GET, entity,
							new ParameterizedTypeReference<List<Let>>() {
							});

					ObservableList<Let> letovi = FXCollections.observableList(response.getBody());
					table.setItems(letovi);
				} else {
					try {
						HttpEntity<String> entity = new HttpEntity<String>(null, headers);
						ResponseEntity<List<Let>> response = restTemplate.exchange(
								"http://localhost:8762" + route + "flights?search=" + searchTf.getText(),
								HttpMethod.GET, entity, new ParameterizedTypeReference<List<Let>>() {
								});

						ObservableList<Let> letovi = FXCollections.observableList(response.getBody());
						table.setItems(letovi);
					} catch (Exception e) {
						alert.setText("Pretraga je neuspesna. Pokusajte ponovo!");
						alert.setTextFill(Color.FIREBRICK);
					}

				}

			}
		});

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

		Label datum = new Label("Datum kupovine karte:");
		DatePicker dp = new DatePicker();
		dp.setPrefWidth(350);
		dp.setEditable(false);
		Label b10 = new Label();
		b10.setPrefHeight(20);
		Label b100 = new Label();
		b100.setPrefHeight(20);

		Button kupiKartu = new Button("Kupi kartu");
		Label alertKupovina = new Label();

		Button korpa = new Button("Moja korpa");
		// Label hh = new Label();
		// hh.setPrefWidth(150);

		kupiKartu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alertKupovina.setText("");
				if (table.getSelectionModel().getSelectedItem() == null) {
					alertKupovina.setText("Morate selektovati let za koji zelite da kupite kartu.");
					alertKupovina.setTextFill(Color.FIREBRICK);
				} else if (kartice.getItems().size() == 0) {
					alertKupovina.setText("Morate dodati kreditnu karticu kojom cete platiti kartu.");
					alertKupovina.setTextFill(Color.FIREBRICK);
				} else if (kartice.getSelectionModel().getSelectedItem() == null) {
					alertKupovina.setText("Morate izabrati kreditnu karticu kojom cete platiti kartu.");
					alertKupovina.setTextFill(Color.FIREBRICK);
				} else if (dp.getValue() == null) {
					alertKupovina.setText("Morate izabrati datum kupovine karte.");
					alertKupovina.setTextFill(Color.FIREBRICK);
				} else {
					try {
						if (!rute.containsValue(service3Name)) {
							HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
							ResponseEntity<String> routes = restTemplate.exchange(
									"http://localhost:8762/actuator/routes", HttpMethod.GET, httpEntity, String.class);

							Utils.parseAllRoutes(rute, routes.getBody());
						}

						if (!(rute.containsValue(service3Name))) {
							alertKupovina.setText("Doslo je do greske! Pokusajte kasnije.");
							return;
						}

						String route = Utils.getKeyByValue(rute, service3Name);

						KartaForm kf = new KartaForm(user.getId(), table.getSelectionModel().getSelectedItem().getId(),
								dp.getValue());
						HttpEntity<KartaForm> entity = new HttpEntity<KartaForm>(kf, headers);
						ResponseEntity<String> response = restTemplate.exchange(
								"http://localhost:8762" + route + "buyTicket", HttpMethod.POST, entity, String.class);
						if (response.getStatusCode() == HttpStatus.ACCEPTED) {
							alertKupovina.setText(response.getBody());
							alertKupovina.setTextFill(Color.GREEN);

							if (!(rute.containsValue(service1Name))) {
								alertKupovina.setText("Doslo je do greske! Pokusajte kasnije.");
								return;
							}

							route = Utils.getKeyByValue(rute, service1Name);

							HttpEntity<String> en = new HttpEntity<String>(null, headers);
							ResponseEntity<User> res = restTemplate.exchange(
									"http://localhost:8762" + route + "user/" + user.getId(), HttpMethod.GET, en,
									User.class);
							user = res.getBody();
							milje.setText("Milje: " + user.getBrojMilja());
							rank.setText("Rank: " + user.getRank());
						}
						else {
							alertKupovina.setTextFill(Color.FIREBRICK);
							alertKupovina.setText("Kupovina nije uspela!Pretražite letove ponovo.");
						}
					} catch (Exception e) {
						alertKupovina.setTextFill(Color.FIREBRICK);
						alertKupovina.setText("Kupovina nije uspela!Pretražite letove ponovo.");
					}
				}
			}
		});

		korpa.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!rute.containsValue(service3Name)) {
					HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
					ResponseEntity<String> routes = restTemplate.exchange("http://localhost:8762/actuator/routes",
							HttpMethod.GET, httpEntity, String.class);

					Utils.parseAllRoutes(rute, routes.getBody());
				}

				if (!(rute.containsValue(service3Name))) {
					alert.setText("Doslo je do greske! Pokusajte kasnije.");
					return;
				}

				String route = Utils.getKeyByValue(rute, "flightticktes-service");

				Stage s = new Stage();
				CartWindow cw = new CartWindow(user);
				HttpEntity<String> en = new HttpEntity<String>(null, headers);
				ResponseEntity<List<Karta>> res = restTemplate.exchange(
						"http://localhost:8762" + route + "ticketsByIdKorisnika/" + user.getId(), HttpMethod.GET, en,
						new ParameterizedTypeReference<List<Karta>>() {
						});
				if (res.getStatusCode() == HttpStatus.ACCEPTED) {
					cw.setKarte(FXCollections.observableList(res.getBody()));
					try {
						cw.start(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		GridPane centerGrid = new GridPane();
		centerGrid.setAlignment(Pos.CENTER);
		centerGrid.setHgap(10);
		centerGrid.setVgap(10);
		HBox hBox = new HBox(10);
		hBox.getChildren().addAll(searchTf, search);
		centerGrid.add(hBox, 0, 0);
		centerGrid.add(alert, 0, 1);
		centerGrid.add(table, 0, 2);

		GridPane topGrid = new GridPane();
		topGrid.setAlignment(Pos.CENTER_LEFT);
		topGrid.setHgap(10);
		topGrid.setVgap(10);
		topGrid.add(b1, 0, 1);
		topGrid.add(ime, 1, 1);
		topGrid.add(b2, 0, 2);
		topGrid.add(email, 1, 2);
		topGrid.add(b3, 0, 3);
		topGrid.add(pasos, 1, 3);
		topGrid.add(b4, 0, 4);
		topGrid.add(milje, 1, 4);
		topGrid.add(b5, 0, 5);
		topGrid.add(rank, 1, 5);
		topGrid.add(b6, 0, 6);
		topGrid.add(update, 1, 6);
		topGrid.add(kk, 5, 2);
		topGrid.add(kartice, 5, 3);
		topGrid.add(dodajKarticu, 6, 3);

		GridPane bottomGrid = new GridPane();
		bottomGrid.setAlignment(Pos.CENTER);
		bottomGrid.setHgap(10);
		bottomGrid.setVgap(10);

		bottomGrid.add(datum, 1, 1);
		bottomGrid.add(dp, 1, 2);
		bottomGrid.add(b100, 1, 3);
		bottomGrid.add(kupiKartu, 1, 4);
		// bottomGrid.add(hh, 2, 4);
		bottomGrid.add(korpa, 3, 4);
		bottomGrid.add(alertKupovina, 1, 5);
		bottomGrid.add(b10, 1, 6);

		layout.setCenter(centerGrid);
		layout.setTop(topGrid);
		layout.setBottom(bottomGrid);

		Scene scene = new Scene(layout, 900, 700);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ComboBox<KreditnaKartica> getKartice() {
		return kartice;
	}

	public void setKartice(ComboBox<KreditnaKartica> kartice) {
		this.kartice = kartice;
	}

	public Label getIme() {
		return ime;
	}

	public Label getEmail() {
		return email;
	}

	public Label getPasos() {
		return pasos;
	}

	public void setRute(Map<String, String> rute) {
		this.rute = rute;
	}
}
