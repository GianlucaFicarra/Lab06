package it.polito.tdp.meteo;

public class TestModel {

	public static void main(String[] args) {

		Model m = new Model();
		
		System.out.println(m.getUmiditaMedia(5));
		
		System.out.println(m.trovaSequenza(1));
		
	    System.out.println(m.trovaSequenza(2));
	    System.out.println(m.trovaSequenza(3)); 
	    System.out.println(m.trovaSequenza(4));
	    System.out.println(m.trovaSequenza(5));
	    System.out.println(m.trovaSequenza(6));
	    System.out.println(m.trovaSequenza(7));
	    System.out.println(m.trovaSequenza(8));
	    System.out.println(m.trovaSequenza(9));
	    System.out.println(m.trovaSequenza(10));
	    System.out.println(m.trovaSequenza(11));
	    System.out.println(m.trovaSequenza(12));
	}

}
