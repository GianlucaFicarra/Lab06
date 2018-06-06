package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;



public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private double costoMinimo= Double.MAX_VALUE;
	
	private int livello;
	private List<Citta> localita; //torino milano genova da caricare
	private LinkedList<SimpleCity> soluzioneMinima=null;

	//dichiaro DAO e instanzio
	private MeteoDAO dao=new MeteoDAO();
	
	public Model() {		

		localita = dao.salvaLocalita(); //salvo le 3 localita dal dao
		System.out.println("Localita: "+localita);
		
		
	}

	public String getUmiditaMedia(int mese) {//punto 1
		
			MeteoDAO dao = new MeteoDAO();
			List<Rilevamento> rilevamenti = dao.getAllRilevamentiMese(mese);
			String result = "";
			for(Rilevamento r : rilevamenti) {
				result += r.getLocalita()+": "+r.getMediaUmidita()+"\n";
			}
			return result;
	
	}

	
	
	
	public String trovaSequenza(int mese) { //punto 2
		
		int livello = 0;
		LinkedList<SimpleCity> parziale = new LinkedList<SimpleCity>();

		recoursive(livello, parziale, mese);
		//funzione ricorsiva viene invocata qui, cambia il valore della minima
		
		System.out.println("Minima: "+soluzioneMinima);
		System.out.println("Costo_minima: "+costoMinimo);

		if(soluzioneMinima!=null) {
			
		
		String soluzioneFinale="";
		for(SimpleCity s: soluzioneMinima) {
			soluzioneFinale+=s.getNome()+"\n";
		}
		return soluzioneFinale;
		}
		
		return "Nessuna soluzione trovata";
		
	}
	
	
	public void recoursive(int livello, LinkedList<SimpleCity> parziale, int mese ) {
		//E: istruzione sempre eseguita scelgo debug con stampa appena entro nella ricorsiva
		//System.out.println("Parziali: "+parziale);
		
		if (livello == NUMERO_GIORNI_TOTALI) {     //A: CONDIZIONE DI TERMINAZIONe
			if(this.punteggioSoluzione(parziale)<costoMinimo) {
		
				costoMinimo=this.punteggioSoluzione(parziale);
				soluzioneMinima= new LinkedList<SimpleCity>(parziale);
			}
			return;
		}
		
		
		//B: creo nuova soluzione parziale
		//per ogni citta
		for (int i = 0; i <localita.size(); i++) {//itero sula lista di candidati
			//per ogni rilevamento
			for(int j=0; j<localita.get(i).getRilevamenti().size(); j++) {
				
			//se rilevamento fatto nel giorno=livello e mese passato
			if(localita.get(i).getRilevamenti().get(j).getData().getDate() == livello+1 &&
					localita.get(i).getRilevamenti().get(j).getData().getMonth() == mese+1	) {
				                            // dato che month 0 11e date 0 30
				
				
			//alternativa al new clone fatto a punto D
			//LinkedList<SimpleCity> cloneParziale=(LinkedList<SimpleCity>)parziale.clone();
			                     
			                       //nome + rilevamento
			parziale.add(new SimpleCity(localita.get(i).getNome(), localita.get(i).getRilevamenti().get(j).getUmidita() ));
		    localita.get(i).increaseCounter();//passo dalla citta //es torino seleziona = T 
			
			//C ricorsiva: filtro
			if(controllaParziale(parziale)==true) {
			
				//passo livello successivo, soluz aggiornata e dim costante
				recoursive(livello +1, parziale, mese);
			}
			
			//D aggiungo, faccio ricorsione e per tornare indietro rimuovo per esplorare nuove soluzioni
				parziale.remove(livello); //elimino l'ultimo aggiunto
				//localita.get(i).setCounter(localita.get(i).getCounter()-1); //riaggiorno il contatore per iniziare nuovo ramo ricorsione
			    localita.get(i).decreaseCounter();
			
			}
		  }
		}
	
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		//controllo lista sia nulla o vuota
		if(soluzioneCandidata == null || soluzioneCandidata.size() == 0)
			return Double.MAX_VALUE;
		
		//controllo soluzione contenga tutte le citta
		for(Citta c: localita) {
		if(!soluzioneCandidata.contains(new SimpleCity(c.getNome())))
			return Double.MAX_VALUE;
		}
		
		//data soluzione ottengo costo: 100(se mi sposto)+umidita del giorno e citta corrente
		double score = 0.0;
		SimpleCity precedente=soluzioneCandidata.get(0);//prendo primo elemento lista
		
		for(SimpleCity s: soluzioneCandidata) {
			
			if(!precedente.equals(s)) {
				score+=100;
			}
			
			precedente=s;
			score+=s.getCosto();
		}
	
		return score;
	}
	
	
	
	

	private boolean controllaParziale(List<SimpleCity> parziale) {
		
		//se soluzione è nulla: non valida
		if(parziale==null)
			return false;
		
		//se soluzione è vuota: è valida
		if(parziale.size()==0)
			return true;
		
		
		int permanenza=0;
		SimpleCity precedente=null;
		
		for(SimpleCity s: parziale) {
			for(Citta c:localita) {
				
			if(c.getNome().compareTo(s.getNome())==0) { //oppure indexof
				
				
				if(precedente==null || precedente.equals(s)) { //se è la prima iterazione o la precedente è uguale a corrente
					permanenza++;
				}else {//reset
					
					if(permanenza<3)//tutti almeno 3 giorni di fila
						return false;
					
					permanenza=1;
					
				}
				
				
				if(c.getCounter()>6) {
					return false;
				}
				
				precedente=s; //la precedente futura è il mio attuale simple
				
			}
			
			}
		}
		return true;
	}
	
	
	
	

}
