package app.window;

import java.time.LocalDate;

import app.entities.Karta;
import app.entities.User;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CartWindow extends Application{
	
	private TableView<Karta> table;
	private ObservableList<Karta> karte;
	private User user;
	
	
	public CartWindow(User user) {
		this.user = user;
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Kupljene karte za " + user);
		
		table = new TableView<Karta>();
		table.setPrefWidth(400);
		table.setPrefHeight(200);
		
		TableColumn<Karta, Long> userCol = new TableColumn<Karta, Long>("Korisnik");
		TableColumn<Karta, Long> letCol = new TableColumn<Karta, Long>("Let");
		TableColumn<Karta, LocalDate> datumCol = new TableColumn<Karta, LocalDate>("Datum kupovine");
		table.getColumns().addAll(userCol, letCol, datumCol);
		
		userCol.setCellValueFactory(new PropertyValueFactory<Karta, Long>("idKorisnika"));
		letCol.setCellValueFactory(new PropertyValueFactory<Karta, Long>("idLeta"));
		datumCol.setCellValueFactory(new PropertyValueFactory<Karta, LocalDate>("datumKupovine"));

		table.setItems(karte);
		
		BorderPane layout = new BorderPane();
		layout.setCenter(table);
		Scene scene = new Scene(layout, 450, 350);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public ObservableList<Karta> getKarte() {
		return karte;
	}
	
	public void setKarte(ObservableList<Karta> karte) {
		this.karte = karte;
	}

}
