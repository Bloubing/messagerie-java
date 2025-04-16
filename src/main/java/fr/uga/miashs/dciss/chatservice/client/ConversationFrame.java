package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import fr.uga.miashs.dciss.chatservice.common.Message;


public class ConversationFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel listeMessages;

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
	public ConversationFrame(ClientMsg c, int a) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JButton refresh = new JButton("Rafraichir");
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rafraichir();
			}
		});
		contentPane.add(refresh, BorderLayout.SOUTH);
		
		listeMessages = new JPanel();
		contentPane.add(listeMessages, BorderLayout.CENTER);
		listeMessages.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel titre = new JLabel("Votre conversation avec "+a);
		titre.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titre, BorderLayout.NORTH);
		TreeSet<Message> messages = c.getDb().messages_tous(a);
		for ( Message message : messages) {
			String a_mettre;
			if( message.getSrcId() == c.getIdentifier()) {
				a_mettre = "Vous avez dis : "+message.getMessage();
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
			TreeSet<Integer> messages = new TreeSet<Integer>();
			messages.add(1);
			messages.add(3);
			messages.add(2);
			for ( Integer message : messages) {
				JLabel messageCourant = new JLabel(message+" Coucou");
				if ( message % 2 == 0) messageCourant.setHorizontalAlignment(SwingConstants.RIGHT);
				else {
					messageCourant.setHorizontalAlignment(SwingConstants.LEFT);
				}
				listeMessages.add(messageCourant);

				}
			this.revalidate();
		}
	}

