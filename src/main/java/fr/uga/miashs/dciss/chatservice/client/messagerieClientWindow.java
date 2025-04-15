package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import java.net.UnknownHostException;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class messagerieClientWindow {

	private JFrame frame;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private ClientMsg c;
	private JTextArea message_du_serveur;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					messagerieClientWindow window = new messagerieClientWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public messagerieClientWindow() throws Exception {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {
		c = new ClientMsg("localhost", 1666);
		// add a connection listener that exit application when connection closed
		c.addConnectionListener(active -> {
			if (!active)
				System.exit(0);
		});
		c.startSession();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 1400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		JLabel titre = new JLabel("Application de Messagerie , ton ID = "+c.getIdentifier());
		panel.add(titre);

		JPanel panelOptions = new JPanel();
		frame.getContentPane().add(panelOptions, BorderLayout.WEST);
		panelOptions.setLayout(new GridLayout(0, 1, 0, 0));

		JRadioButton envoyer_message = new JRadioButton("Envoyer ");
		buttonGroup.add(envoyer_message);
		panelOptions.add(envoyer_message);

		JRadioButton creer_groupe = new JRadioButton("Créer groupe");
		buttonGroup.add(creer_groupe);
		panelOptions.add(creer_groupe);

		JButton valider = new JButton("Valider");
	
		panelOptions.add(valider);

		JPanel messages = new JPanel();
		frame.getContentPane().add(messages, BorderLayout.CENTER);
		messages.setLayout(new BorderLayout(0, 0));

		message_du_serveur = new JTextArea();
		message_du_serveur.setFont(new Font("Dialog", Font.PLAIN, 25));
		message_du_serveur.setEditable(false);
		message_du_serveur.setBackground(new Color(154, 153, 150));
		messages.add(message_du_serveur);
		c.addMessageListener(p -> message_du_serveur.setText(p.srcId + " says to " + p.destId + ": " + new String(p.data)));

		JLabel messageServeurTitre = new JLabel("DERNIER MESSAGE : ");
		messageServeurTitre.setHorizontalAlignment(SwingConstants.CENTER);
		messages.add(messageServeurTitre, BorderLayout.NORTH);
		valider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( envoyer_message.isSelected()) {
					SendingFrame sf = new SendingFrame (c);
					sf.setVisible(true);
				}
				else if(creer_groupe.isSelected()) {
					CreateGroupFrame cpf = new CreateGroupFrame(c);
					cpf.setVisible(true);
				}
			}
		});
	}

}
