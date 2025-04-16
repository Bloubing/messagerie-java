package fr.uga.miashs.dciss.chatservice.client;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ListeConversationsPanel extends JPanel {
	JPanel liste;
	JScrollPane scrollPane;
	ClientMsg c;
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ListeConversationsPanel(ClientMsg c) {
		this.c = c;
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Vos conversations :");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblNewLabel, BorderLayout.NORTH);
		
		JButton refresh = new JButton("Rafraichir");
		
		add(refresh, BorderLayout.SOUTH);
		
		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		liste = new JPanel();
		scrollPane.setViewportView(liste);
		liste.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel abe = new JLabel("Vos conversations :");
		ArrayList<Integer> conversations = c.getDb().conversations();
		for ( Integer i : conversations) {
			JPanel panelCourant = new JPanel();
			panelCourant.setLayout(new GridLayout(1,0,0,0));
			JLabel lblCourant = new JLabel("Conversation avec : "+i);
			panelCourant.add(lblCourant);
			JButton voir = new JButton("ouvrir conversation");
			panelCourant.add(voir);
			voir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ConversationFrame conv = new ConversationFrame(c, i);
					conv.setVisible(true);
					conv.setDefaultCloseOperation(conv.DISPOSE_ON_CLOSE);
				}
			});
			liste.add(panelCourant);			
		}
		
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rafraichir();
			}
		});
	
	}
	public void rafraichir() {
		liste.removeAll();
		ArrayList<Integer> conversations = c.getDb().conversations();
		
		for ( Integer i : conversations) {
			JPanel panelCourant = new JPanel();
			panelCourant.setLayout(new GridLayout(1,0,0,0));
			JLabel lblCourant = new JLabel("Conversation avec : "+i);
			panelCourant.add(lblCourant);
			JButton voir = new JButton("ouvrir conversation");
			panelCourant.add(voir);
			voir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ConversationFrame conv = new ConversationFrame(c, i);
					conv.setVisible(true);
					conv.setDefaultCloseOperation(conv.DISPOSE_ON_CLOSE);

				}
			});
			liste.add(panelCourant);
			this.revalidate();
			
		}
	}

}
