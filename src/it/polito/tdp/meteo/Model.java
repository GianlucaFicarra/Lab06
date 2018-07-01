package it.polito.tdp.meteo;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	/*(3) mi serve un eleneco delle città presenti nel DB su cui calcolare l'umidita
	 * quando costruisco il modello questo si chiedera quali sono le città nel DB e le chiederà al dao
	 * costruisco un meteoDAO nel costruttore a cui chiedero di popolare le citta*/
	
	private MeteoDAO dao; 
    private  List<Citta> leCitta;
    private  List<SimpleCity> best;
    
	private double minorPunteggio;
	
	public Model() {

		dao= new MeteoDAO();
		this.leCitta= dao.getAllCitta();//creata nel dao
		//restituisce lista di citta presenti del DB salvate cosi nel MODEL
	}

	public Double getUmiditaMedia(Month mese, Citta citta) {//(4) inizio punto 1

		/*data citta e mese dammi il numero che rappresenta umidita media,
		 * sfrutto la query per farmi svolgere questa operazione,
		 *questa info ce l'ha il DB a cui chiedo le info dove citta è torino e mese corrente
		 *OSS nel DB non ho mese ma data completa, uso MONTH(data)=numero del mese corrente
		 *usata per capire se la data ha il mio mese corrente.
		 *
		 *-->questo fatto con un metodo del dao a cui passo citta e mese*/
		MeteoDAO dao= new MeteoDAO();
		return dao.getUmiditaMedia(mese, citta);
	}
	
	
	public List<Citta> getLeCitta() {
		return leCitta;
	}


	
	public String trovaSequenza(Month mese) {

		this.minorPunteggio = Double.MAX_VALUE;
		this.best = new ArrayList <> ();
		
		List <SimpleCity> parziale = new ArrayList <> ();
		
		// reset dei rilevamenti e del contatore delle città
		for (Citta c : leCitta) {
			c.setRilevamenti(this.dao.getAllRilevamentiLocalitaMese(mese.getValue(), c.getNome()));
			c.setCounter(0);
		}
		
		// la ricorsiva non ha bisogno della dimensione poichè è una costante
		cerca(parziale, 0);
		
		if(best != null) {
			System.out.println("Punteggio della soluzione ottima: " + this.punteggioSoluzione(best));
			return best.toString();
		}
			
		return "Soluzione non trovata";	
	
	}
	
	
	/*(6) INIZIO SECONDO PUNTO
	 * metodo che model offre al controller per la sequenza più conveniente
	 * scelto il mese, utente clicca su calcola, e controller chiama questa
	 * che restituisce lista che controller stampera.
	 * per creare la lista uso la ricorsiva CERCA che riceve soluz parziale, liv,
	 */
	private void cerca(List<SimpleCity> parziale, int livello) {
		
		//A caso terminale
		if(livello>=NUMERO_GIORNI_TOTALI ) {
			
			//(9)calcolo il costo della soluzione e vedo se è il migliore
			double costo=punteggioSoluzione(parziale);
			if(best==null || costo<minorPunteggio) {
				this.minorPunteggio = costo;
				best = new ArrayList<>(parziale); //deepcopy
			}
			System.out.println(parziale);
			return;
			
		}
		//altrimenti provo nuove soluzioni
			
			//caso intermedio provo una alla volta le varie citta per vedere se non viola le regole
			for(Citta c: leCitta) {
				
				// dalla lista dei rilevamenti della citta considero l'umidità al livello = giorno
				int umidita = c.getRilevamenti().get(livello).getUmidita();
				
				SimpleCity sc = new SimpleCity (c.getNome(), umidita);
				parziale.add(sc);
				
				c.increaseCounter();
				
				
				//C filtro
				if(controllaParziale(parziale))  //solo se sara valida la aggiungo
				cerca(parziale, livello+1);  //avvio nuova sluz parziale
					
				//D back track elimino ultimo elemento messo 
				parziale.remove(parziale.size()-1);
				c.decreaseCounter();
				
			}
			
		
	}
	
	

	/*private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		
		//verifica giorni max: quante volte nell aparziale già compare la sluz prova
		int cont=0;
		for(Citta precedente: parziale) {
			if(precedente.equals(prova)) //equalse confronta solo il nome
				cont++;
		}
		
		if(cont>=NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		//verifica giorni minimi: posso cambiare citta solo dopo 3 di fila
		if(parziale.size()==0) //se prima soluzione ogni citta va bene
			return true;
		
		if(parziale.size()==1 || parziale.size()==2) {//se seconda e terza citta allora deve essere uguale alla precedente e non posso cambiare
			//ti dico true se prova è uguale all'ultima città vista
			return parziale.get(parziale.size()-1).equals(prova);
			//ti dico false se prova non è uguale all'ultima città vista
		}
		
		if(parziale.get(parziale.size()-1).equals(prova)) //giorni successivi al terzo posso sempre rimanere
	        return true;
		
		//è giorno dopo i primi 3 e decido di cambiare, va bene se gli ultimi 3 sono uguali
		if(parziale.get(parziale.size()-1).equals(parziale.size()-2) &&
				parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
			return true;
		
		
		return false;
		
	}
	
	
	
	
	//(8)
	public List<Citta> calcolaSequenza(Month mese){
		//chiama lista parziale inizialmente vuota
		List<Citta> parziale= new ArrayList<>();
		this.best=null; //azzerro soluz precedente e e faccio una nuova
		
		/*guardo nel DB i rilevamenti del mese corrente di interesse
		 * memorizzandoli in rilevamenti della classe citta,e le uso 
		 * dentro calcola costo*/
		
		/*...carico dentro ciuascuna delle citta la lista dei rilevamenti del mese considerato
		li salvo in cita.setRilevamenti...
		.
		.
		.
		.
		.
		
		cerca(parziale,0);
		
		return best;
	}
	
	
	*/
	
	
	
	//sapendo citta della lista e mese e rilevamenti mi dice il costo
	private Double punteggioSoluzione(List<SimpleCity> parziale) {
		/*calcolo costo solo nella soluz terminale, calcolarlo nelle soluzioni parziali
		 * non mi da nessun aiuto in più nel filtraggio delle soluzioni,
		 * 
		 * sommatoria di tutte le unita in ogni citta considerando il rilevamento del giorno giusto
		 * prendo la citta in quel giorno e ne prendo l'umidita
		 */

		// tutte le città devono essere visitate almeno una volta
		for (Citta c : leCitta) {
			SimpleCity sc = new SimpleCity(c.getNome());
			if (!parziale.contains(sc))
				return Double.MAX_VALUE;
		}
		
		SimpleCity precedente = parziale.get(0);
		double score = 0.0;
		
		
		for (SimpleCity sc : parziale) {
			if (!precedente.equals(sc))
				score += this.COST;
			
			precedente = sc;
			score += sc.getCosto();
		}
		
		return score;
	}
	

	private boolean controllaParziale(List<SimpleCity> parziale) {
		for (Citta c : leCitta)
			if (c.getCounter() > this.NUMERO_GIORNI_CITTA_MAX)
				return false;
		
		SimpleCity precedente = parziale.get(0);
		int permanenzaCitta = 0;
		
		for (SimpleCity sc : parziale) {
			if (!precedente.equals(sc)) {
				// tecnico cambia città
				if (permanenzaCitta < this.NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)
					return false;

				precedente = sc;				
				// se il tecnico cambia citta allora rimane in tale città almeno un giorno
				permanenzaCitta = 1;
			
			}else
				permanenzaCitta ++;
		}
		
		return true;
	}

}
