package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import fr.uga.miashs.dciss.chatservice.common.Message;
import javax.swing.JTextArea;


public class ConversationFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ClientMsg c;
	private int interlocuteur;
	private JPanel listeMessages;
	private JPanel panel;
	private JPanel panelDenvoi;
	private JTextArea messageInput;
	private JButton envoyer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConversationFrame frame = new ConversationFrame(new ClientMsg("localhost",1666), 1);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConversationFrame(ClientMsg c, int interlocuteur) {
		this.c = c;
		this.interlocuteur = interlocuteur;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		listeMessages = new JPanel();
		contentPane.add(listeMessages, BorderLayout.CENTER);
		listeMessages.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel titre = new JLabel("Votre conversation avec "+interlocuteur);
		titre.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titre, BorderLayout.NORTH);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		panelDenvoi = new JPanel();
		panel.add(panelDenvoi);
		panelDenvoi.setLayout(new GridLayout(1, 0, 0, 0));
		
		messageInput = new JTextArea();
		panelDenvoi.add(messageInput);
		
		envoyer = new JButton("Envoyer");
		panelDenvoi.add(envoyer);
		envoyer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( !(messageInput.getText().equals(""))) {
					// si l'id et le message sont remplis on envoi
					int dest = interlocuteur;
					c.sendPacket(dest, messageInput.getText().getBytes());
					c.getDb().ajouterMessage(messageInput.getText(), c.getIdentifier(), dest);
					messageInput.setText("");
					rafraichir();
				}
			}
		});
		
		JButton refresh = new JButton("Rafraichir");
		panel.add(refresh);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rafraichir();
			}
		});
		TreeSet<Message> messages = c.getDb().messages_tous(interlocuteur);
		for ( Message message : messages) {
			String a_mettre;
			if( message.getSrcId() == c.getIdentifier()) {
				a_mettre = "Vous avez dit : "+message.getMessage();
			}
			else {
				a_mettre = message.getSrcId() +" : "+message.getMessage();
			}
			JLabel messageCourant = new JLabel(a_mettre);
			if ( message.getSrcId()== c.getIdentifier()) messageCourant.setHorizontalAlignment(SwingConstants.LEFT);
			else {
				messageCourant.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			listeMessages.add(messageCourant);

			}
		
		}
		public void rafraichir() {
			listeMessages.removeAll();
			TreeSet<Message> messages = c.getDb().messages_tous(interlocuteur);
			for ( Message message : messages) {
				String a_mettre;
				if( message.getSrcId() == c.getIdentifier()) {
					a_mettre = "Vous avez dit : "+message.getMessage();
				}
				else {
					a_mettre = message.getSrcId() +" : "+message.getMessage();
				}
				JLabel messageCourant = new JLabel(a_mettre);
				if ( message.getSrcId()== c.getIdentifier()) messageCourant.setHorizontalAlignment(SwingConstants.LEFT);
				else {
					messageCourant.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				listeMessages.add(messageCourant);

				}
			this.revalidate();
		}
	}

