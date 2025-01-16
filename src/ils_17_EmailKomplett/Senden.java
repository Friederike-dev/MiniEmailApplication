package ils_17_EmailKomplett;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class Senden extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//die Instanzvariablen

	//fuer die Aktionen
	//################################################################# für Einsendeaufgabe 2 #########################
	private MeineAktionen sendenAct, antwortenAct, weiterleitenAct;

	//fuer die Tabelle
	private JTable tabelle;

	//fuer das Modell
	private DefaultTableModel modell;

	//################################################################# für Einsendeaufgabe 2 #########################
	//fuer die Zeilennummer der aktuell markierten Zeile
	private int selectedRow;


	//der Konstruktor
	Senden() {
		super();

		setTitle("E-Mail senden");

		//wir nehmen ein Border-Layout
		setLayout(new BorderLayout());

		//die Aktionen erstellen
		sendenAct = new MeineAktionen("Neue E-Mail", new ImageIcon("icons/mail-generic.gif"), "Erstellt eine neue E-Mail", null, "senden");
		//################################################################# für Einsendeaufgabe 2 #########################
		antwortenAct = new MeineAktionen("Antwort", new ImageIcon("icons/mail-reply.gif"), "Auf die markierte Email antworten", null, "antworten");
		weiterleitenAct = new MeineAktionen("Weiterleiten", new ImageIcon("icons/mail-forward.gif"), "Die markierte Email weiterleiten", null, "weiterleiten");

		//die Symbolleiste oben einfuegen
		add(symbolleiste(), BorderLayout.NORTH);

		setVisible(true);
		setSize(700, 300);
		
		//die Standardoperation setzen
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//die Tabelle erstellen und anzeigen
		tabelleErstellen();
		tabelleAktualisieren();
	}

	//die Symbolleiste erzeugen und zurueckgeben
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();
		//die Symbole ueber die Aktionen einbauen
		leiste.add(sendenAct);
		//################################################################# für Einsendeaufgabe 2 #########################
		leiste.add(antwortenAct);
		leiste.add(weiterleitenAct);

		//die Leiste zurueckgeben
		return (leiste);
	}


	//zum Erstellen der Tabelle
	private void tabelleErstellen() {
		//fuer die Spaltenbezeichner
		String[] spaltenNamen = {"ID", "Empfänger", "Betreff", "Text"};

		//ein neues Standardmodell erstellen
		modell = new DefaultTableModel();
		//die Spaltennamen setzen
		modell.setColumnIdentifiers(spaltenNamen);
		//die Tabelle erzeugen
		tabelle = new JTable();
		//und mit dem Modell verbinden
		tabelle.setModel(modell);
		//wir haben keinen Editor, können die Tabelle also nicht bearbeiten
		tabelle.setDefaultEditor(Object.class, null);
		//es sollen immer alle Spalten angepasst werden
		tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//und die volle Größe genutzt werden
		tabelle.setFillsViewportHeight(true);
		//die Tabelle setzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(tabelle);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);

		//einen Maus-Listener ergänzen
		tabelle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//war es ein Doppelklick?
				if (e.getClickCount() == 2) {
					//die Zeile beschaffen
					int zeile = tabelle.getSelectedRow();
					//die Daten beschaffen
					String empfaenger, betreff, inhalt, ID;
					ID = tabelle.getModel().getValueAt(zeile, 0).toString();
					empfaenger = tabelle.getModel().getValueAt(zeile, 1).toString();
					betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
					inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
					//und anzeigen
					//uebergeben wird der Frame der äußeren Klasse
					new Anzeige(Senden.this, true, ID, empfaenger, betreff, inhalt);
				}
			}
		});
	}

	//Methode zum Aktualisieren der Tabelle
	public void tabelleAktualisieren() {
		//fuer den Datenbankzugriff
		Connection verbindung;
		ResultSet ergebnisMenge;

		//fuer die Spalten
		String empfaenger, betreff, inhalt, ID;
		//die Inhalte loeschen
		modell.setRowCount(0);

		try{
			//Verbindung herstellen und Ergebnismenge beschaffen
			verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM gesendet");
			//die Eintraege in die Tabelle schreiben
			while (ergebnisMenge.next()) {
				ID = ergebnisMenge.getString("iNummer");
				empfaenger = ergebnisMenge.getString("empfaenger");
				betreff = ergebnisMenge.getString("betreff");
				//den Inhalt vom CLOB beschaffen und in einen String umbauen
				Clob clob;
				clob = ergebnisMenge.getClob("inhalt");
				inhalt = clob.getSubString(1, (int) clob.length());

				//die Zeile zum Modell hinzufuegen
				//dazu benutzen wir ein Array vom Typ Object
				modell.addRow(new Object[] {ID, empfaenger, betreff, inhalt} );
			}
			//die Verbindungen wieder schließen und trennen
			ergebnisMenge.close();
			verbindung.close();
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//innere Klasse für die Aktionen
	public class MeineAktionen extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//der Konstruktor 
		public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			//den Konstruktor der übergeordneten Klasse mit dem Text und dem Icon aufrufen
			super(text, icon);
			//die Beschreibung setzen für den Bildschirmtipp
			putValue(SHORT_DESCRIPTION, beschreibung);
			//den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			//das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			//für die Neue Nachricht
			if (e.getActionCommand().equals("senden"))
				senden();
			
			//################################################################# für Einsendeaufgabe 2 #########################
			//für das Antworten oder Weiterleiten
			if (e.getActionCommand().equals("antworten"))
				antworten();
			if (e.getActionCommand().equals("weiterleiten"))
				weiterleiten();
		}
	}

	//Methode zum Senden
	public void senden() {
		//den Dialog für eine neue Nachricht modal anzeigen
		new NeueNachricht(null, true);
		//nach dem Versenden lassen wir die Anzeige aktualisieren
		tabelleAktualisieren();
	}

	//################################################################# für Einsendeaufgabe 2 #########################
	//Methode zum Antworten
	public void antworten() { 
		// den Dialog für eine neue Nachricht modal anzeigen 
		// Überprüfen Sie, ob eine Zeile ausgewählt wurde
		selectedRow = tabelle.getSelectedRow();
		if (selectedRow >= 0) {
			String empfaenger = modell.getValueAt(selectedRow, 1).toString(); 
			String betreff ="AW " + modell.getValueAt(selectedRow, 2).toString(); 
			String inhalt = modell.getValueAt(selectedRow, 3).toString(); 
			new NeueNachricht(null, true, empfaenger, betreff, inhalt); 
			// nach dem Versenden lassen wir die Anzeige aktualisieren 
			tabelleAktualisieren(); 
		} else {
			JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Nachricht aus, um sie weiterzuleiten.");
		}
	} 
	
	//################################################################# für Einsendeaufgabe 2 #########################
	//Methode zum Weiterleiten
	public void weiterleiten() { 
		// den Dialog für eine neue Nachricht modal anzeigen 
		// Überprüfen Sie, ob eine Zeile ausgewählt wurde
		selectedRow = tabelle.getSelectedRow();
		if (selectedRow >= 0) {
			String inhalt = modell.getValueAt(selectedRow, 3).toString(); 
			String betreff ="WG " + modell.getValueAt(selectedRow, 2).toString();
			new NeueNachricht(null, true, betreff, inhalt); 
			// nach dem Versenden lassen wir die Anzeige aktualisieren 
			tabelleAktualisieren();
		} else {
			JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Nachricht aus, um sie weiterzuleiten.");
		}
	}
}
