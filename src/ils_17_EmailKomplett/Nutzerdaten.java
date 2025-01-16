//################################################################# f�r Einsendeaufgabe 1 #########################
package ils_17_EmailKomplett;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Nutzerdaten extends JDialog {

	/**
	 * 
	 */

	// Variablen f�r die Felder, Nutzereingaben und Buttons
	private static String emailAdresse, kennwort;
	private JTextField emailFeld, kennwortFeld;
	private JButton ok, abbrechen;

	private static final long serialVersionUID = 1L;

	//innere Klasse f�r den Listener
	class Listener implements ActionListener {
		@Override
		public void actionPerformed (ActionEvent e) {

			//wurde auf OK geklickt?
			if(e.getActionCommand().equals("ok"))
				//dann die Daten �bernehmen
				uebernehmen();

			//wurde auf Abbrechen geklickt?
			if (e.getActionCommand().equals("abbrechen"))
				//dann den Dialog schlie�en
				dispose();
		}
	}

	//der Konstruktor
	public Nutzerdaten(JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Nutzerdaten");
		//die Oberfl�che erstellen
		initGui();

		//Standardoperation setzen
		//hier den Dialog ausblenden und l�schen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	//Methode zum Erstellen der Oberfl�che
	private void initGui() {

		//ein Hauptpanel mit einem BoxLayout erstellen
		JPanel meinPanel = new JPanel();
		meinPanel.setLayout (new BoxLayout(meinPanel, BoxLayout.Y_AXIS));

		// Erstellen eines Panels f�r Eingabe-Information 
		JPanel infoPanel = new JPanel(); 
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); 
		// Hinzuf�gen eines JLabels f�r die Info an den Nutzer 
		JLabel infoLabel = new JLabel("<html><br>Geben Sie hier Ihre gmx-Email und Ihr Passwort ein:<br>(zuvor gespeicherte Nutzerdaten werden gel�scht)<br><br></html>");

		//den Text in das Panel einf�gen
		infoPanel.add(infoLabel);

		//das Infopanel dem Hauptpanel hinzuf�gen 
		meinPanel.add(infoPanel); 

		//das Panel f�r die Felder
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout (new GridLayout(0,2,10,10));
		//f�r die Eingabe
		gridPanel.add(new JLabel("Email:"));
		emailFeld = new JTextField();
		gridPanel.add(emailFeld);
		gridPanel.add(new JLabel("Kennwort:"));
		kennwortFeld = new JTextField();
		gridPanel.add(kennwortFeld);


		//die Schaltfl�chen
		ok = new JButton("OK");
		ok.setActionCommand("ok");
		abbrechen = new JButton("Abbrechen");
		abbrechen.setActionCommand("abbrechen");

		//der Listener f�r die Buttons
		Listener listener = new Listener();
		ok.addActionListener(listener);
		abbrechen.addActionListener(listener);

		//auch die Buttons hinzuf�gen
		gridPanel.add(ok);
		gridPanel.add(abbrechen);
		//und das Panel mit den Feldern dem Hauptpanel hinzuf�gen
		meinPanel.add(gridPanel);

		//das Hauptpanel dem JFrame hinzuf�gen
		add(meinPanel);

		//packen und anzeigen
		pack();
		setVisible(true);
	}

	//Methode zum �bernehmen der vom Nutzer eingegebenen Daten und speichern in einer eigenen Datei
	private void uebernehmen() {

		//Wenn nicht in beiden Feldern etwas eingegeben wurde, die Methode verlassen
		if (emailFeld.getText().isEmpty() || kennwortFeld.getText().isEmpty()) { 
			JOptionPane.showMessageDialog(this, "Bitte f�llen Sie beide Felder aus."); 
			return; 
		}

		//wenn die Datei noch nicht existiert, dann erstellen wir sie hier
		try (RandomAccessFile datei = new RandomAccessFile("nutzerdaten.bin", "rw")) {

			//Falls die Datei schon mit Eintr�gen besteht, sollen diese gel�scht werden
			datei.setLength(0);

			// Emailadresse des Nutzers in die Datei schreiben
			datei.writeUTF(emailFeld.getText());
			// Kennwort des Nutzers in die Datei schreiben
			datei.writeUTF(kennwortFeld.getText());

		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Beim Speichern der Daten ist ein Problem aufgetreten");
		}
		//mit dem Verlassen des try-Blocks wird die Datei wieder geschlossen (man muss sie hier nicht explizit schlie�en)

		dispose();
	}

	//Methode zum Lesen der Datei; die Daten werden f�r die aktuelle Session bzw. die Laufzeit in Variablen gespeichert
	private static void lesen() {
		try (RandomAccessFile datei = new RandomAccessFile("nutzerdaten.bin", "r")) {
			datei.seek(0);
			emailAdresse = datei.readUTF();
			kennwort = datei.readUTF();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Beim Lesen der Datei ist ein Problem aufgetreten");
		}

	}

	//Methode zum zur�ckgeben der Emailadresse
	//sie ist 'public static' und damit in anderen Klassen aufrufbar.
	public static String getEmailAdresse() {
		lesen();
		return emailAdresse;
	}

	//Methode zum zur�ckgeben des Kennwortes
	//sie ist 'public static' und damit in anderen Klassen aufrufbar.
	public static String getKennwort() {
		lesen();
		return kennwort;
	}

}
