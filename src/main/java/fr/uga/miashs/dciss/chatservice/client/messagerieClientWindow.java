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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	 * 
	 * @throws Exception
	 */
	public messagerieClientWindow() throws Exception {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
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
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Je ferme");
				c.getDb().fermerConnexion();
			}
		});
		frame.setBounds(100, 100, 1400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		JLabel titre = new JLabel("Application de Messagerie , ton ID = " + c.getIdentifier());
		panel.add(titre);

		JPanel panelOptions = new JPanel();
		frame.getContentPane().add(panelOptions, BorderLayout.WEST);
		panelOptions.setLayout(new GridLayout(0, 1, 0, 0));

		JRadioButton envoyer_message = new JRadioButton("Envoyer message");
		buttonGroup.add(envoyer_message);
		panelOptions.add(envoyer_message);

		JRadioButton creer_groupe = new JRadioButton("Créer groupe");
		buttonGroup.add(creer_groupe);
		panelOptions.add(creer_groupe);

		JRadioButton quitter_groupe = new JRadioButton("Quitter un groupe");
		buttonGroup.add(quitter_groupe);
		panelOptions.add(quitter_groupe);

		JRadioButton supprimer_groupe = new JRadioButton("Supprimer un groupe");
		buttonGroup.add(supprimer_groupe);
		panelOptions.add(supprimer_groupe);

		JRadioButton ajouter_membre = new JRadioButton("Ajouter un membre à un groupe");
		buttonGroup.add(ajouter_membre);
		panelOptions.add(ajouter_membre);

		JRadioButton supprimer_membre = new JRadioButton("Retirer membre d'un groupe");
		buttonGroup.add(supprimer_membre);
		panelOptions.add(supprimer_membre);

		JRadioButton renommer_groupe = new JRadioButton("Renommer un groupe");
		buttonGroup.add(renommer_groupe);
		panelOptions.add(renommer_groupe);

		JButton valider = new JButton("Valider");

		panelOptions.add(valider);

		JPanel messages = new JPanel();
		frame.getContentPane().add(messages, BorderLayout.CENTER);
		messages.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_1 = new JPanel();
		messages.add(panel_1);
				panel_1.setLayout(new BorderLayout(0, 0));
		
				JLabel messageServeurTitre = new JLabel("DERNIER MESSAGE : ");
				panel_1.add(messageServeurTitre, BorderLayout.NORTH);
				messageServeurTitre.setHorizontalAlignment(SwingConstants.CENTER);
				
						message_du_serveur = new JTextArea();
						panel_1.add(message_du_serveur);
						message_du_serveur.setFont(new Font("Dialog", Font.PLAIN, 25));
						message_du_serveur.setEditable(false);
						message_du_serveur.setBackground(new Color(154, 153, 150));
						c.addMessageListener(
								p -> message_du_serveur.setText(c.formatageMessage(p)));
						
						ListeConversationsPanel panel_2 = new ListeConversationsPanel(c);
						messages.add(panel_2);
		valider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (envoyer_message.isSelected()) {
					SendingFrame sf = new SendingFrame(c);
					sf.setVisible(true);
					sf.setDefaultCloseOperation(sf.DISPOSE_ON_CLOSE);
				} else if (creer_groupe.isSelected()) {
					CreateGroupFrame cpf = new CreateGroupFrame(c);
					cpf.setVisible(true);
					cpf.setDefaultCloseOperation(cpf.DISPOSE_ON_CLOSE);

				} else if (quitter_groupe.isSelected()) {
					LeaveGroupFrame lgf = new LeaveGroupFrame(c);
					lgf.setVisible(true);
					lgf.setDefaultCloseOperation(lgf.DISPOSE_ON_CLOSE);
				} else if (supprimer_groupe.isSelected()) {
					DeleteGroupFrame dgf = new DeleteGroupFrame(c);
					dgf.setVisible(true);
					dgf.setDefaultCloseOperation(dgf.DISPOSE_ON_CLOSE);
				} else if (ajouter_membre.isSelected()) {
					AddMemberFrame amf = new AddMemberFrame(c);
					amf.setVisible(true);
					amf.setDefaultCloseOperation(amf.DISPOSE_ON_CLOSE);
				} else if (supprimer_membre.isSelected()) {
					RemoveMemberFrame rmf = new RemoveMemberFrame(c);
					rmf.setVisible(true);
					rmf.setDefaultCloseOperation(rmf.DISPOSE_ON_CLOSE);
				} else if (renommer_groupe.isSelected()) {
					RenameGroupFrame rgf = new RenameGroupFrame(c);
					rgf.setVisible(true);
					rgf.setDefaultCloseOperation(rgf.DISPOSE_ON_CLOSE);
				}

			}
		});
	}
	

}
