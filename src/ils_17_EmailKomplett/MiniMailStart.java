package ils_17_EmailKomplett;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MiniMailStart extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//die innere Klasse fuer den ActionListener 
	class MeinListener implements ActionListener { 
		@Override 
		public void actionPerformed(ActionEvent e) { 
			//wurde auf Senden geklickt? 
			if (e.getActionCommand().equals("senden")) 
				//dann das Senden starten 
				senden(); 
			//wurde auf Empfangen geklickt? 
			if (e.getActionCommand().equals("empfangen")) 
				//dann das Empfangen starten 
				empfangen(); 
			//wurde auf Beenden geklickt? 
			if (e.getActionCommand().equals("ende")) 
				//dann beenden 
				beenden();

			//################################################################# für Einsendeaufgabe 1 #########################
			//wurde auf Zugangsdaten geklickt?
			if (e.getActionCommand().equals("zugang"))
				zugangsdaten();
		} 
	} 

	//der Konstruktor 
	public MiniMailStart(String titel) { 
		super(titel); 
		//ein FlowLayout 
		setLayout(new FlowLayout(FlowLayout.LEFT)); 

		//die Schaltflaechen 
		JButton liste = new JButton("Senden"); 
		liste.setActionCommand("senden"); 
		JButton einzel = new JButton("Empfangen"); 
		einzel.setActionCommand("empfangen"); 
		JButton beenden = new JButton("Beenden"); 
		beenden.setActionCommand("ende");

		//################################################################# für Einsendeaufgabe 1 #########################
		JButton zugangsdaten = new JButton("Zugangsdaten");
		zugangsdaten.setActionCommand("zugang");

		MeinListener listener = new MeinListener(); 
		liste.addActionListener(listener); 
		einzel.addActionListener(listener); 
		beenden.addActionListener(listener);

		//################################################################# für Einsendeaufgabe 1 #########################
		zugangsdaten.addActionListener(listener);

		add(liste); 
		add(einzel); 
		add(beenden);

		//################################################################# für Einsendeaufgabe 1 #########################
		add(zugangsdaten);

		//Groesse setzen, Standardverhalten festlegen und anzeigen 
		setSize(420, 90);
//		pack(); //nur, wenn nicht setSize genutzt wird
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		setVisible(true); 
	} 

	//die Methoden zum Ausführen der Nutzerauswahl
	private void senden() { 
		new Senden();
	} 

	private void empfangen() { 
		new Empfangen();
	} 

	private void beenden() { 
		System.exit(0); 
	} 

	//################################################################# für Einsendeaufgabe 1 #########################
	private void zugangsdaten() {
		new Nutzerdaten(this, true);
	}

}
