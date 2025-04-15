package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;

public class DeleteGroupFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeleteGroupFrame frame = new DeleteGroupFrame(new ClientMsg("localHost", 1666));
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
	public DeleteGroupFrame(ClientMsg c) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel titre = new JLabel("Supprimer un groupe");
		titre.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titre, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JLabel groupe = new JLabel("Nom du groupe à supprimer :");
		panel.add(groupe);
		
		JTextArea nom = new JTextArea();
		panel.add(nom);
		
		JButton quitter = new JButton("Supprimer ce groupe");
		quitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( nom.getText().length() > 0) {
					String nomGroupe = nom.getText();
					ByteBuffer data = ByteBuffer.allocate(4+(nomGroupe.length()*2));
					data.putInt(3);
					// on met le type de paquet ( 3 pour supprimer un groupe )
					for(int i = 0; i < nomGroupe.length(); i++) {
						data.putChar(nomGroupe.charAt(i));
					}
					c.sendPacket(0, data.array());
					// On envoit au destinaire 0 car création groupe, et avec le tableau de bytes;
					JOptionPane.showMessageDialog(null, "Groupe supprimé si existant, et si vous en êtes le créateur");
					fermer();
				}
			}
		});
		contentPane.add(quitter, BorderLayout.SOUTH);
		
	}
	public void fermer() {
		//fermeture automatique de la frame après creationGroupe
		this.dispose();
	}

}
