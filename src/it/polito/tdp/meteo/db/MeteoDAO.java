package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
public List<Rilevamento> getAllRilevamentiMese(int mese) {
		
		final String sql = "SELECT Localita, AVG(Umidita) " + 
				"FROM situazione " + 
				"WHERE MONTH(Data) = ? " + 
				"GROUP BY Localita";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1, mese);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDouble("AVG(Umidita)"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/*public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		return null;
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		return 0.0;
	}*/

	public List<Citta> salvaLocalita(){
		List<Citta> risultatoCitta= new LinkedList<>();
		
		for(Rilevamento r: this.getAllRilevamenti()) {
			
			Citta c=new Citta(r.getLocalita()); //localita è il nome dela citta
			
			if(risultatoCitta.contains(c)) { //aggiungo solo rilevamento
				risultatoCitta.get(risultatoCitta.indexOf(c)).addNewRilevamento(r);
				/*se ho già torino, mi ricollego alla riga torino che ho gia,
				 * devo ottenere il riferimento a quella originale, tramite
				 * indefof cioè l'indice della originale, a questo aggiungo nuovo elemento rilevamento*/
			}else {
				c.addNewRilevamento(r); //c non ha rilevamento da db lo aggiungo e poi salvo
				risultatoCitta.add(c);
				
			}
		}
		return risultatoCitta;
	}
}
