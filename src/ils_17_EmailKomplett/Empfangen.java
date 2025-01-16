package ils_17_EmailKomplett;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class Empfangen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//die Instanzvariablen

	//f¸r die Tabelle
	private JTable tabelle;

	//f¸r das Modell
	private static DefaultTableModel modell;

	//##################################################### f¸r Einsendeaufgabe 2 #####################################################
	//f¸r die Aktionen
	private MeineAktionen sendenAct, antwortenAct, weiterleitenAct;
	//f¸r die Zeile, die ggf. ausgew‰hlt wurde
	private int selectedRow;


	//eine innere Klasse f¸r den WindowListener und den ActionListener
	//die Klasse ist von WindowAdapter abgeleitet
	class MeinWindowAdapter extends WindowAdapter{
		//f¸r das ÷ffnen des Fensters
		@Override
		public void windowOpened(WindowEvent e) {
			//die Methode nachrichtenEmpfangen() aufrufen
			nachrichtenEmpfangen();
		}
	}

	//der Konstruktor
	Empfangen() {
		super();

		setTitle("E-Mail empfangen");
		//wir nehmen ein Border-Layout
		setLayout(new BorderLayout());

		// Aktionen erstellen
		sendenAct = new MeineAktionen("Neue E-Mail", new ImageIcon("icons/mail-generic.gif"), "Erstellt eine neue E-Mail", null, "senden");
		//##################################################### f¸r Einsendeaufgabe 2 #####################################################
		antwortenAct = new MeineAktionen("Antwort", new ImageIcon("icons/mail-reply.gif"), "Auf die markierte Email antworten", null, "antworten");
		weiterleitenAct = new MeineAktionen("Weiterleiten", new ImageIcon("icons/mail-forward.gif"), "Die markierte Email weiterleiten", null, "weiterleiten");

		//die Symbolleiste oben einf¸gen
		add(symbolleiste(), BorderLayout.NORTH);

		setVisible(true);
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//den Listener verbinden
		addWindowListener(new MeinWindowAdapter());

		//die Tabelle erstellen und anzeigen
		tabelleErstellen();
		tabelleAktualisieren();

		//zun‰chst ist keine Zeile ausgew‰hlt
		selectedRow = -1;
	}


	//Methode zum Erstellen der Tabelle
	private void tabelleErstellen() {

		//f¸r die Spaltenbezeichner
		String[] spaltenNamen = {"ID", "Sender", "Betreff", "Text"};

		//ein neues Standardmodell erstellen
		modell = new DefaultTableModel();
		//die Spaltennamen setzen
		modell.setColumnIdentifiers(spaltenNamen);
		//die Tabelle erzeugen
		tabelle = new JTable();
		//und mit dem Modell verbinden
		tabelle.setModel(modell);
		//wir haben keinen Editor, kˆnnen die Tabelle also nicht bearbeiten
		tabelle.setDefaultEditor(Object.class, null);
		//es sollen immer alle Spalten angepasst werden
		tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//und die volle Grˆﬂe genutzt werden
		tabelle.setFillsViewportHeight(true);
		//die Tabelle setzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(tabelle);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


		add(scroll);

		//einen Maus-Listener erg‰nzen
		tabelle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//war es ein Doppelklick?
				if (e.getClickCount() == 2) {
					//die Zeile beschaffen
					int zeile = tabelle.getSelectedRow();
					//die Daten beschaffen
					String sender, betreff, inhalt, ID;
					ID = tabelle.getModel().getValueAt(zeile, 0).toString();
					sender = tabelle.getModel().getValueAt(zeile, 1).toString();
					betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
					inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
					//und anzeigen
					//¸bergeben wird der Frame der ‰uﬂeren Klasse
					new Anzeige(Empfangen.this, true, ID, sender, betreff, inhalt);
				}
			}
		});
	}

	//Methode zum Erzeugen und Zur¸ckgeben der Symbolleiste
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();
		//die Symbole ¸ber die Aktionen einbauen
		leiste.add(sendenAct);
		//##################################################### f¸r Einsendeaufgabe 2 #####################################################
		leiste.add(antwortenAct);
		leiste.add(weiterleitenAct);

		//die Leiste zur¸ckgeben
		return (leiste);
	}

	//Methode zum Aktualisieren der Tabelle
	private void tabelleAktualisieren() {
		//f¸r den Datenbankzugriff
		Connection verbindung;
		ResultSet ergebnisMenge;

		//f¸r die Spalten
		String sender, betreff, inhalt, ID;
		//die Inhalte lˆschen
		modell.setRowCount(0);  

		try{
			//Verbindung herstellen und Ergebnismenge beschaffen
			verbindung = MiniDBTools.oeffnenDB("jdbc:derby:mailDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM empfangen");

			//die Eintr‰ge in die Tabelle schreiben
			while (ergebnisMenge.next()) {
				ID = ergebnisMenge.getString("iNummer");
				sender = ergebnisMenge.getString("sender");
				betreff = ergebnisMenge.getString("betreff");
				//den Inhalt vom CLOB beschaffen und in einen String umbauen
				Clob clob;
				clob = ergebnisMenge.getClob("inhalt");
				inhalt = clob.getSubString(1, (int) clob.length());

				//die Zeile zum Modell hinzuf¸gen
				//dazu benutzen wir ein Array vom Typ Object
				modell.addRow(new Object[] {ID, sender, betreff, inhalt} );
			}
			//die Verbindungen wieder schlieﬂen und trennen
			ergebnisMenge.close();
			verbindung.close();
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
			System.out.println("Fehler: " + e.toString());
		}

	}

	//Methode zum Empfangen der Nachrichten und Einf¸gen in die Tabelle
	private void nachrichtenEmpfangen() {
		nachrichtenAbholen();
		//nach dem Empfangen lassen wir die Anzeige aktualisieren
		tabelleAktualisieren();
	}

	//Methode zum holen der Nachrichten
	private void nachrichtenAbholen() {
		//die Zugangsdaten
		//##################################################### f¸r Einsendeaufgabe 1 #####################################################
		// Aufrufen der statischen Methoden der Klasse Nutzerdaten 
		String benutzername = Nutzerdaten.getEmailAdresse(); 
		String kennwort = Nutzerdaten.getKennwort();

		//der Server
		String server = "pop.gmx.net";

		//die Eigenschaften setzen
		Properties eigenschaften = new Properties();
		//das Protokoll
		eigenschaften.put("mail.store.protocol", "pop3");
		//den Host
		eigenschaften.put("mail.pop3.host", server);
		//den Port zum Empfangen
		eigenschaften.put("mail.pop3.port", "995");
		//die Authentifizierung ¸ber TLS       
		eigenschaften.put("mail.pop3.starttls.enable", "true");
		//das Session-Objekt erstellen
		Session sitzung = Session.getDefaultInstance(eigenschaften);

		//das Store-Objekt ¸ber die Sitzung erzeugen
		try (Store store = sitzung.getStore("pop3s")){
			//und verbinden
			store.connect(server, benutzername, kennwort);
			//ein Ordnerobjekt f¸r den Posteingang erzeugen     
			Folder posteingang  = store.getFolder("INBOX");
			//und ˆffnen
			//dabei sind auch ƒnderungen zugelassen
			posteingang.open(Folder.READ_WRITE);

			//die Nachrichten beschaffen
			Message nachrichten[] = posteingang.getMessages();

			//gibt es neue Nachrichten?
			if (nachrichten.length != 0) {
				//dann die Anzahl zeigen
				JOptionPane.showMessageDialog(this, "Es gibt "+ posteingang.getUnreadMessageCount() + " neue Nachrichten.");
				//jede Nachricht verarbeiten
				for(Message nachricht : nachrichten)
					nachrichtVerarbeiten(nachricht);
			}				
			else
				JOptionPane.showMessageDialog(this, "Es gibt keine neue Nachrichten.");

			//den Ordner schlieﬂen
			//durch das Argument true werden die Nachrichten gelˆscht
			posteingang.close(true);
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//Methode zum Verarbeiten einer Nachricht
	private void nachrichtVerarbeiten(Message nachricht) {
		try {
			//ist es einfacher Text?
			if (nachricht.isMimeType("text/plain")) {
				//den ersten Sender beschaffen
				String sender = nachricht.getFrom()[0].toString();
				//den Betreff
				String betreff = nachricht.getSubject();
				//den Inhalt
				String inhalt = nachricht.getContent().toString();
				//und die Nachricht speichern
				nachrichtSpeichern(sender, betreff, inhalt);
				//und zum Lˆschen markieren
				nachricht.setFlag(Flags.Flag.DELETED, true);
			} 

			else if (nachricht.isMimeType("text/html")) {
				//den ersten Sender beschaffen
				String sender = nachricht.getFrom()[0].toString();
				//den Betreff
				String betreff = nachricht.getSubject();
				//den Inhalt
				String inhalt = nachricht.getContent().toString();
				//und die Nachricht speichern
				nachrichtSpeichern(sender, betreff, inhalt);
				//und zum Lˆschen markieren
				nachricht.setFlag(Flags.Flag.DELETED, true);
			} 

			//sonst geben wir eine Meldung aus
			else {
				JOptionPane.showMessageDialog(this, "Der Typ der Nachricht " + nachricht.getContentType() + "kann nicht verarbeitet werden.");
				System.out.println("Der Typ der Nachricht kann nicht verarbeitet werden");}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
			System.out.println(e + e.toString());
		}
	}


	private void nachrichtSpeichern(String sender, String betreff, String inhalt) {
		//f¸r die Verbindung
		Connection verbindung;

		//die Datenbank ˆffnen
		verbindung=MiniDBTools.oeffnenDB("jdbc:derby:mailDB");
		try {
			//einen Eintrag in der Tabelle empfangen anlegen
			//¸ber ein vorbereitetes Statement
			PreparedStatement prepState;
			prepState = verbindung.prepareStatement("insert into empfangen (sender, betreff, inhalt) values (?, ?, ?)");
			prepState.setString(1, sender);
			prepState.setString(2, betreff);
			prepState.setString(3, inhalt);
			//das Statement ausf¸hren
			prepState.executeUpdate();
			verbindung.commit();

			//Verbindung schlieﬂen
			prepState.close();
			verbindung.close();
			//und die Datenbank schlieﬂen
			MiniDBTools.schliessenDB("jdbc:derby:");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
			System.out.println(e + e.toString());
		}
	}

	//innere Klasse f¸r die Aktionen
	class MeineAktionen extends AbstractAction {


		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//der Konstruktor 
		public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			//den Konstruktor der ¸bergeordneten Klasse mit dem Text und dem Icon aufrufen
			super(text, icon);
			//die Beschreibung setzen f¸r den Bildschirmtipp
			putValue(SHORT_DESCRIPTION, beschreibung);
			//den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			//das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			//f¸r die Neue Nachricht
			if (e.getActionCommand().equals("senden"))
				senden();

			//##################################################### f¸r Einsendeaufgabe 2 #####################################################
			//f¸r das Antworten oder Weiterleiten aus dem Empfangen-Fenster
			if (e.getActionCommand().equals("antworten"))
				antworten();
			if (e.getActionCommand().equals("weiterleiten"))
				weiterleiten();
		}
	}

	//Methode zum Senden
	public void senden() {
		//den Dialog f¸r eine neue Nachricht modal anzeigen
		new NeueNachricht(null, true);
	}

	//##################################################### f¸r Einsendeaufgabe 2 #####################################################
	//Methode zum Antworten
	public void antworten() { 
		selectedRow = tabelle.getSelectedRow();
		if (selectedRow >= 0) {
			// den Dialog f¸r eine neue Nachricht modal anzeigen 
			String empfaenger = modell.getValueAt(selectedRow, 1).toString(); 
			String betreff ="AW " + modell.getValueAt(selectedRow, 2).toString(); 
			String inhalt = modell.getValueAt(selectedRow, 3).toString(); 
			new NeueNachricht(null, true, empfaenger, betreff, inhalt); 
			
		} else {
			JOptionPane.showMessageDialog(this, "Bitte w‰hlen Sie eine Nachricht aus, um sie weiterzuleiten.");
		}
	} 

	//##################################################### f¸r Einsendeaufgabe 2 #####################################################
	//Methode zum Weiterleiten
	public void weiterleiten() { 
		selectedRow = tabelle.getSelectedRow();
		if (selectedRow >= 0) {
			// den Dialog f√ºr eine neue Nachricht modal anzeigen 
			String inhalt = modell.getValueAt(selectedRow, 3).toString(); 
			String betreff ="WG " + modell.getValueAt(selectedRow, 2).toString();
			new NeueNachricht(null, true, betreff, inhalt); 
			
		} else {
			JOptionPane.showMessageDialog(this, "Bitte w‰hlen Sie eine Nachricht aus, um sie weiterzuleiten.");
		}
	}
}
