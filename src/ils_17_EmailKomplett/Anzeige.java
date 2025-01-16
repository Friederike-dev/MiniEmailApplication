package ils_17_EmailKomplett;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;


public class Anzeige extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//f�r die Eingabefelder
	private JTextField empfaengerFeld, betreffFeld;
	private JTextArea inhaltFeld;
	//f�r die Schaltfl�chen 
	private JButton ok;

	private String absender, betreff, inhalt;

	//################################################################# f�r Einsendeaufgabe 2 #########################
	//Variablen f�r die Aktionen antworten und weiterleiten
	MeineAktionen antwortenAct, weiterleitenAct;


	//die innere Klasse f�r den ActionListener
	class NeuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//wurde auf OK geklickt?
			if (e.getActionCommand().equals("ok"))
				//dann Dialog schlie�en
				dispose();
		}
	}

	//################################################################# f�r Einsendeaufgabe 2 #########################
	//innere Klasse f�r die Action-Objekte
	public class MeineAktionen extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//der Konstruktor 
		public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			//den Konstruktor der �bergeordneten Klasse mit dem Text und dem Icon aufrufen
			super(text, icon);
			//die Beschreibung setzen f�r den Bildschirmtipp
			putValue(SHORT_DESCRIPTION, beschreibung);
			//den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			//das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);


		}

		@Override
		public void actionPerformed(ActionEvent e) {	
			//f�r das Antworten oder Weiterleiten aus dem Anzeige-Fenster
			if (e.getActionCommand().equals("antworten"))
				antworten();
			if (e.getActionCommand().equals("weiterleiten"))
				weiterleiten();
		}
	}

	//der Konstruktor
	public Anzeige(JFrame parent, boolean modal, String ID, String empfaenger, String betreff, String inhalt) {
		super(parent, modal);
		setTitle("Anzeige");
		//die Oberfl�che erstellen
		initGui(ID, empfaenger, betreff, inhalt);

		//Standardoperation setzen
		//hier den Dialog ausblenden und l�schen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	//Methode zum Erstellen der User Oberfl�che
	private void initGui(String iD, String empfaenger, String betreff, String inhalt) {
		setLayout(new BorderLayout());

		//################################################################# f�r Einsendeaufgabe 2 #########################
		// Aktionen erstellen
		antwortenAct = new MeineAktionen("Antwort", new ImageIcon("icons/mail-reply.gif"), "Auf diese Email antworten", null, "antworten");
		weiterleitenAct = new MeineAktionen("Weiterleiten", new ImageIcon("icons/mail-forward.gif"), "Diese Email weiterleiten", null, "weiterleiten");

		// Ein Top-Panel erstellen, das die Symbolleiste und das 'oben' Panel enth�lt
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(symbolleiste(), BorderLayout.NORTH);

		this.inhalt = inhalt;
		this.absender = empfaenger;
		this.betreff = betreff;

		JPanel oben = new JPanel();
		oben.setLayout(new GridLayout(0, 2));
		oben.add(new JLabel("Empf�nger:"));
		empfaengerFeld = new JTextField(empfaenger);
		oben.add(empfaengerFeld);
		oben.add(new JLabel("Betreff:"));
		betreffFeld = new JTextField(betreff);
		oben.add(betreffFeld);

		topPanel.add(oben, BorderLayout.SOUTH);
		add(topPanel, BorderLayout.NORTH);

		inhaltFeld = new JTextArea(inhalt);
		// Zeilenumbruch aktivieren
		inhaltFeld.setLineWrap(true);
		inhaltFeld.setWrapStyleWord(true);
		// das Feld in ein Scrollpane setzen
		JScrollPane scroll = new JScrollPane(inhaltFeld);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);

		// Felder auf nicht bearbeitbar setzen
		empfaengerFeld.setEditable(false);
		betreffFeld.setEditable(false);
		inhaltFeld.setEditable(false);

		JPanel unten = new JPanel();
		// Schaltfl�che hinzuf�gen
		ok = new JButton("OK");
		ok.setActionCommand("ok");

		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);

		unten.add(ok);
		add(unten, BorderLayout.SOUTH);

		// Anzeigen
		setSize(600, 300);
		setVisible(true);
	}

	//################################################################# f�r Einsendeaufgabe 2 #########################
	//die Symbolleiste erzeugen und zur�ckgeben
	private JToolBar symbolleiste() {
		//Toolbar erstellen
		JToolBar leiste = new JToolBar();
		//die Symbole �ber die Aktionen einbauen
		leiste.add(antwortenAct);
		leiste.add(weiterleitenAct);

		//die Leiste zur�ckgeben
		return (leiste);
	}

	//################################################################# f�r Einsendeaufgabe 2 #########################
	//Methode f�r das Antworten
	public void antworten() {
		// AW vor dem Betreff der zu beantwortenden Email einf�gen
		betreff = "AW " + betreff;
		//den Dialog f�r eine neue Nachricht modal anzeigen
		new NeueNachricht(null, true, absender, betreff, inhalt);
	}

	//################################################################# f�r Einsendeaufgabe 2 #########################
	//Methode f�r das Weiterleiten
	public void weiterleiten() {
		//WG vor dem Betreff der weiterzuleitenden Email einf�gen
		betreff = "WG " + betreff;
		//den Dialog f�r eine neue Nachricht modal anzeigen
		new NeueNachricht(null, true, betreff, inhalt);
	}

}
