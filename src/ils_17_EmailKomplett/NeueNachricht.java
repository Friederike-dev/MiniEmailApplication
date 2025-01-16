package ils_17_EmailKomplett;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class NeueNachricht extends JDialog {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//f�r die Eingabefelder
	private JTextField empfaenger, betreff;
	private JTextArea inhalt;
	//f�r die Schaltfl�chen 
	private JButton ok, abbrechen;
	
	//##################################################### f�r Einsendeaufgabe 2 #####################################################
	//Variablen f�r die gespeicherten Daten aus der Nachricht, die beantwortet oder weitergeleitet wird
	private String absender ="";
	private String inhaltString = "";
	private String betreffUebergeben ="";
	//##################################################### f�r Einsendeaufgabe 1 #####################################################
	private String benutzername;

	//die innere Klasse f�r den ActionListener
	class NeuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//wurde auf OK geklickt?
			if (e.getActionCommand().equals("senden"))
				//dann die Daten �bernehmen
				senden();
			
			
			//############################################################################### hier scheint etwas falsch zu laufen
			//wurde auf Abbrechen geklickt?
			if (e.getActionCommand().equals("abbrechen"))
				//dann Dialog schlie�en
				dispose();
		}
	}

	//�berladene Konstruktoren
	//der Konstruktor mit zwei Parametern
	public NeueNachricht(JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Neue Nachricht");
		//die Oberfl�che erstellen
		initGui();

		//Standardoperation setzen
		//hier den Dialog ausblenden und l�schen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	//##################################################### f�r Einsendeaufgabe 2 #####################################################
	//der Konstruktor mit drei Parametern f�r das Weiterleiten
	public NeueNachricht(JDialog parent, boolean modal, String betreff, String inhalt) {
		super(parent, modal);
		setTitle("Nachricht weiterleiten");
		
		betreffUebergeben = betreff;
		this.inhaltString = "\n\n" + "----- Text der urspr�nglichen Nachricht ----" + "\n" + inhalt;
		
		//die Oberfl�che erstellen
		initGui();

		//Standardoperation setzen
		//hier den Dialog ausblenden und l�schen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	//##################################################### f�r Einsendeaufgabe 2 #####################################################
	//der Konstruktor mit vier Parametern f�r das Antworten
		public NeueNachricht(JDialog parent, boolean modal, String absender, String betreff, String inhalt) {
			super(parent, modal);
			setTitle("Antwort");
			
			this.absender = absender;
			betreffUebergeben = betreff;
			this.inhaltString = "\n\n" + "----- Text der urspr�nglichen Nachricht ----" + "\n" +  inhalt;
			
			//die Oberfl�che erstellen
			initGui();
			
			
			
//			// Fokus auf das JTextArea setzen 
//			inhalt.requestFocusInWindow();		// geht nicht, vielleicht ein timing problem
//			
//			// Focusing on the JTextArea after the GUI is built
//			SwingUtilities.invokeLater(new Runnable() {
//			    public void run() {
//			        inhalt.requestFocusInWindow();
//			    }
//			});
//			
			
			

			//Standardoperation setzen
			//hier den Dialog ausblenden und l�schen
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}


	private void initGui() {
		setLayout(new BorderLayout());
		JPanel oben = new JPanel();
		oben.setLayout(new GridLayout(0, 2));
		oben.add(new JLabel("Empf�nger:"));
		empfaenger = new JTextField();
		//################################################################# f�r Einsendeaufgabe 2 #########################
		empfaenger.setText(absender);
		oben.add(empfaenger);
		oben.add(new JLabel("Betreff:"));
		betreff = new JTextField();
		//################################################################# f�r Einsendeaufgabe 2 #########################
		betreff.setText(betreffUebergeben);
		oben.add(betreff);
		add(oben, BorderLayout.NORTH);
		inhalt = new JTextArea();
		//den Zeilenumbruch aktivieren
		inhalt.setLineWrap(true);
		inhalt.setWrapStyleWord(true);
		//das Feld setzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(inhalt);
		//################################################################# f�r Einsendeaufgabe 2 #########################
		inhalt.setText(inhaltString);
		inhalt.setCaretPosition(0);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);

		JPanel unten = new JPanel();
		//die Schaltfl�chen
		ok = new JButton("Senden");
		ok.setActionCommand("senden");
		abbrechen = new JButton("Abbrechen");
		abbrechen.setActionCommand("abbrechen");

		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);
		abbrechen.addActionListener(listener);

		unten.add(ok);
		unten.add(abbrechen);
		add(unten, BorderLayout.SOUTH);

		//anzeigen
		setSize(600, 300);
		setVisible(true);
	}

	//die Methode verschickt die Nachricht
	private void senden() {
		//f�r die Sitzung
		Session sitzung;

		//die Verbindung herstellen
		sitzung = verbindungHerstellen();
		//die Nachricht verschicken und speichern
		nachrichtVerschicken(sitzung);
		nachrichtSpeichern();
	}

	private Session verbindungHerstellen() {
		
		//##################################################### f�r Einsendeaufgabe 1 #####################################################
		//die Zugangsdaten
		//Aufrufen der statischen Methoden der Klasse Nutzerdaten 
		benutzername = Nutzerdaten.getEmailAdresse(); 
		String kennwort = Nutzerdaten.getKennwort();

		//der Server
		String server = "mail.gmx.net";

		//die Eigenschaften setzen
		Properties eigenschaften = new Properties();
		//die Authentifizierung �ber TLS
		eigenschaften.put("mail.smtp.auth", "true");
		eigenschaften.put("mail.smtp.starttls.enable", "true");
		//der Server
		eigenschaften.put("mail.smtp.host", server);
		//der Port zum Versenden
		eigenschaften.put("mail.smtp.port", "587");

		// das Session-Objekt erstellen
		Session sitzung = Session.getInstance(eigenschaften, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(benutzername, kennwort);
			}
		});

		return sitzung;
	}

	private void nachrichtVerschicken(Session sitzung) {
		
		//##################################################### f�r Einsendeaufgabe 1 #####################################################
		//der Absender
		String absender = benutzername;

		try {
			//eine neue Nachricht vom Typ MimeMessage erzeugen
			MimeMessage nachricht = new MimeMessage(sitzung);
			//den Absender setzen
			nachricht.setFrom(new InternetAddress(absender));
			//den Empf�nger
			nachricht.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger.getText()));
			//den Betreff
			nachricht.setSubject(betreff.getText());
			//und den Text
			nachricht.setText(inhalt.getText());
			//die Nachricht verschicken
			Transport.send(nachricht);

			JOptionPane.showMessageDialog(this, "Die Nachricht wurde verschickt."); 

			//hier wird der Dialog dann schlie�en
			dispose();
		} 
		catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString()); 
		}		
	}

	private void nachrichtSpeichern() {
		//f�r die Verbindung
		Connection verbindung;

		//die Datenbank �ffnen
		verbindung=MiniDBTools.oeffnenDB("jdbc:derby:mailDB");
		try {
			//einen Eintrag in der Tabelle empfangen anlegen
			//�ber ein vorbereitetes Statement
			PreparedStatement prepState;
			//SQL-Anweisung zum Einf�gen der Werte in die Tabelle 'gesendet'. '?' sind Platzhalter
			prepState = verbindung.prepareStatement("insert into gesendet (empfaenger, betreff, inhalt) values (?, ?, ?)");
			prepState.setString(1, empfaenger.getText());
			prepState.setString(2, betreff.getText());
			prepState.setString(3, inhalt.getText());
			//das Statement ausf�hren
			prepState.executeUpdate();
			verbindung.commit();

			//Verbindung schlie�en
			prepState.close();
			verbindung.close();
			//und die Datenbank schlie�en
			MiniDBTools.schliessenDB("jdbc:derby:mailDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
}
