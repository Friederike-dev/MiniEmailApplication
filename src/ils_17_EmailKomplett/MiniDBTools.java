package ils_17_EmailKomplett;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class MiniDBTools {
	
	//die Methode stellt die Verbindung her
	//zur�ckgeliefert wird ein Connection-Objekt
	//�bergeben werden Argumente f�r das �ffnen
	public static Connection oeffnenDB(String arg) {
		Connection verbindung = null;
		try {
			//die Datenbank wird �ber 'arg' festgelegt
			verbindung = DriverManager.getConnection(arg);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem: \n" + e.toString());
		}
		return verbindung;
	}
	
	//die Methode liefert eine Ergebnismenge
	//�bergeben werden die Verbindung und der SQL-Ausdruck
	public static ResultSet liefereErgebnis(Connection verbindung, String sqlAnweisung) {
		ResultSet ergebnisMenge = null;
		try {
			//durch das 'TYPE_SCROLL_SENSITIVE' statement kann man sich dann auch nicht nur vorw�rts durch die Ergebnismenge bewegen!
			Statement state = verbindung.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ergebnisMenge = state.executeQuery(sqlAnweisung);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem: \n" + e.toString());
		}
		return ergebnisMenge;
	}
	
	//die Methode f�hrt das gesamte Datenbanksystem herunter
	//das wird bei der "embedded" Version von Apache Derby ausdr�cklich empfohlen
	//�bergeben wird das Protokoll
	public static void schliessenDB (String protokoll) {
		//das Herunterfahren l�st bei Erfolg eine Exception aus!!!
		boolean erfolg = false;
		
		//hier f�hrt das erfolgreiche Herunterfahren zu einer Ausnahme und nicht das Scheitern
		try {
			DriverManager.getConnection(protokoll + "mailDB; shutdown=true");
		}
		catch (SQLException e) {
			erfolg = true;
		}
		if (erfolg != true) {
			JOptionPane.showMessageDialog(null, "Das DBMS konnte nicht heruntergefahren werden.");
		}
	}

}
