package it.polito.tdp.meteo;

import java.net.URL;
import java.time.Month;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.bean.Citta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {
	
	private Model model;
	

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Month> boxMese;
	/*(1)qui devo popolare la tendina con i numeri da i-12 tramite Integer
	 * o con i mesi della classe Month da libreria time, per avere l'elenco,
	 * in ogni caso in inizialice inserisco i mesi*/

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;
	
	
	
	public void setModel(Model model) { //continuazione di (START)
		//imposta riferimento al modello
		this.model=model;
		
	}
	

	@FXML
	void doCalcolaSequenza(ActionEvent event) {

		this.txtResult.clear();
		
		/*(7)nel controller calcola sequenza ci metto la chiamata al calcola sequenza del model
		 * che fa partire la ricorsione chiamando cerca
		Month m=boxMese.getValue();
		if(m!= null) {
			this.txtResult.appendText("Sequenza ottima per il mese " + this.boxMese.getValue() + "\n");
			this.txtResult.appendText(model.trovaSequenza(this.boxMese.getValue()));
			
		} else {
			this.txtResult.setText("Selezionare un mese!");
		}*/
		

		if (this.boxMese.getValue() == null)
			this.txtResult.setText("Selezionare un mese!");
		else {
			this.txtResult.appendText("Sequenza ottima per il mese " + this.boxMese.getValue() + "\n");
			this.txtResult.appendText(model.trovaSequenza(this.boxMese.getValue()));
		}
	
	}

	
	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		this.txtResult.clear();
		
		/*(5) dopo aver fatto i collegamenti col dao, aver preso i valori, 
		 * ed aver calcolato la media, il controller collega con interfaccia grafica*/
		Month m=boxMese.getValue();
		if(m!=null) {
			txtResult.appendText(String.format("Dati del mese %s\n", m.toString()));
			for(Citta c: model.getLeCitta()) {//per ogni cita che il model conosce, calcola la media in quel mese e stampala
				Double u= model.getUmiditaMedia(m, c);
				txtResult.appendText(String.format("Citta %s: umidita %f\n", c.getNome(), u));
			}
		} else {
			this.txtResult.setText("Selezionare un mese!");
		}

	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
		//(2) inserisco i mesi
		//boxMese.getItems().add(Month.JANUARY); OPPURE COL CICLO
		for(int mese=1; mese<=12; mese++ ) {
			boxMese.getItems().add(Month.of(mese)); //.of riceve come parametro il numero intero del mese e da mese corrispondente
		}
	}

	

}
