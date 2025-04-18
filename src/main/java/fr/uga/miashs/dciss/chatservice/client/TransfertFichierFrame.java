package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;

public class TransfertFichierFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField membreId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SendingFrame frame = new SendingFrame(new ClientMsg("name", 12));
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
	public TransfertFichierFrame(ClientMsg c) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 300, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel title = new JLabel("Transférer un fichier (chemin) :");
		contentPane.add(title, BorderLayout.NORTH);
		
		JTextArea path = new JTextArea();
		contentPane.add(path, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		membreId = new JTextField();
		panel_1.add(membreId, BorderLayout.CENTER);
		membreId.setColumns(10);
		
		JLabel idDest = new JLabel("Id membre ou groupe : ");
		panel_1.add(idDest, BorderLayout.WEST);
		
		JButton envoyer = new JButton("Envoyer");
		envoyer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( (!membreId.getText().equals("")) || !(path.getText().equals(""))) {
					String pathString = path.getText();
					
					int idMembreInt = Integer.parseInt(membreId.getText());
					if( idMembreInt == c.getIdentifier()) {
						JOptionPane.showMessageDialog(null, "Vous ne pouvez pas envoyer de fichier à vous même !");
						return;
					}
					try {
						c.sendFile(idMembreInt, pathString);
						

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					JOptionPane.showMessageDialog(null, "Fichier transféré");
					fermer();
				}
			}
		});
		panel.add(envoyer);
			this.dispose();
		};
		public void fermer() {
			this.dispose();
	}

}
