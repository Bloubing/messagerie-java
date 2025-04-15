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
import java.awt.event.ActionEvent;

public class SendingFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField id;

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
	public SendingFrame(ClientMsg c) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 300, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel title = new JLabel("Envoyer un message :");
		contentPane.add(title, BorderLayout.NORTH);
		
		JTextArea message = new JTextArea();
		contentPane.add(message, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		id = new JTextField();
		panel_1.add(id, BorderLayout.CENTER);
		id.setColumns(10);
		
		JLabel idDest = new JLabel("Id dest :");
		panel_1.add(idDest, BorderLayout.WEST);
		
		JButton envoyer = new JButton("Envoyer");
		envoyer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( (!id.getText().equals("")) || !(message.getText().equals(""))) {
					// si l'id et le message sont remplis on envoi
					int dest = Integer.parseInt(id.getText());
					c.sendPacket(dest, message.getText().getBytes());
					JOptionPane.showMessageDialog(null, "Message envoyé");
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
