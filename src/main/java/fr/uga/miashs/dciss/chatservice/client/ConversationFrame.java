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
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import fr.uga.miashs.dciss.chatservice.common.Message;
import fr.uga.miashs.dciss.chatservice.common.MessageGroupe;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;

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
	private JPanel panelFichier;
	private JTextArea fichierInput;
	private JButton envoyerFichier;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConversationFrame frame = new ConversationFrame(new ClientMsg("localhost", 1666), 1);
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
		listeMessages.setBackground(new Color(153, 193, 241));
		contentPane.add(listeMessages, BorderLayout.CENTER);
		listeMessages.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel titre = interlocuteur > 0 ? new JLabel("Votre conversation avec " + interlocuteur)
				: new JLabel("Votre conversation dans le groupe " + interlocuteur);
		titre.setFont(new Font("Dialog", Font.BOLD, 25));
		titre.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titre, BorderLayout.NORTH);

		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		panelDenvoi = new JPanel();
		panelDenvoi.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.add(panelDenvoi);
		panelDenvoi.setLayout(new GridLayout(1, 0, 0, 0));

		messageInput = new JTextArea();
		panelDenvoi.add(messageInput);

		envoyer = new JButton("Envoyer");
		panelDenvoi.add(envoyer);
		envoyer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!(messageInput.getText().equals(""))) {
					// si l'id et le message sont remplis on envoi
					if (!c.getConnected().contains(interlocuteur) && interlocuteur > 0) {
						JOptionPane.showMessageDialog(null, "L'utilisateur " + interlocuteur + " N'est plus connecté");
						return;
					}
					c.sendPacket(interlocuteur, messageInput.getText().getBytes());

					if (interlocuteur > 0) {
						c.getDb().ajouterMessage(messageInput.getText(), c.getIdentifier(), interlocuteur);
						rafraichir();
					} else {
						c.getDb().ajouterMessageGroupe(messageInput.getText(), c.getIdentifier(), interlocuteur,
								interlocuteur);
						rafraichirGroupe();
					}
					messageInput.setText("");
				}
			}
		});
		if (interlocuteur > 0) {
			panelFichier = new JPanel();
			panelFichier.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.add(panelFichier);
			panelFichier.setLayout(new GridLayout(0, 2, 0, 0));

			fichierInput = new JTextArea();
			panelFichier.add(fichierInput);

			envoyerFichier = new JButton("Transférer ce fichier(path)");
			envoyerFichier.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!fichierInput.getText().isEmpty()) {
						String pathString = fichierInput.getText();

						try {
							c.sendFile(interlocuteur, pathString);

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						fichierInput.setText("");

					}
					rafraichir();
				}
			});
			panelFichier.add(envoyerFichier);
		}
		JButton refresh = new JButton("Rafraichir");
		panel.add(refresh);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (interlocuteur > 0)
					rafraichir();
				else {
					rafraichirGroupe();
				}
			}
		});

		if (interlocuteur > 0)
			rafraichir();
		else {
			rafraichirGroupe();
		}

	}

	public void rafraichir() {
		listeMessages.removeAll();
		TreeSet<Message> messages = c.getDb().messages_tous(interlocuteur);
		for (Message message : messages) {
			String a_mettre;
			if (message.getSrcId() == c.getIdentifier()) {
				a_mettre = "Vous avez dit : " + message.getMessage();
			} else {
				a_mettre = message.getSrcId() + " : " + message.getMessage();
			}
			JLabel messageCourant = new JLabel(a_mettre);
			messageCourant.setFont(new Font("Dialog", Font.BOLD, 20));

			if (message.getSrcId() == c.getIdentifier())
				messageCourant.setHorizontalAlignment(SwingConstants.LEFT);
			else {
				messageCourant.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			listeMessages.add(messageCourant);

		}
		this.revalidate();
	}

	public void rafraichirGroupe() {
		listeMessages.removeAll();
		TreeSet<MessageGroupe> messages = c.getDb().messagesGroupe_tous(interlocuteur);
		for (Message message : messages) {
			String a_mettre;
			if (message.getSrcId() == c.getIdentifier()) {
				a_mettre = "Vous avez dit : " + message.getMessage();
			} else {
				a_mettre = message.getSrcId() + " : " + message.getMessage();
			}
			JLabel messageCourant = new JLabel(a_mettre);
			messageCourant.setFont(new Font("Dialog", Font.BOLD, 20));

			if (message.getSrcId() == c.getIdentifier())
				messageCourant.setHorizontalAlignment(SwingConstants.LEFT);
			else {
				messageCourant.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			listeMessages.add(messageCourant);
		}
		this.revalidate();
	}
}
