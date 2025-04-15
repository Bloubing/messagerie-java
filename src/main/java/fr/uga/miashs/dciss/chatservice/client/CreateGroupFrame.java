package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;

public class CreateGroupFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private ArrayList<Integer> membres = new ArrayList<Integer>();
	private JTextField nom;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CreateGroupFrame frame = new CreateGroupFrame(new ClientMsg("localhost",1666));
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
	public CreateGroupFrame(ClientMsg c) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel title = new JLabel(" Creation Groupe :");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(title, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panelAjoutId = new JPanel();
		panel.add(panelAjoutId);
		panelAjoutId.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		panelAjoutId.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JButton ajouterId = new JButton("ajouter cette id");
		panelAjoutId.add(ajouterId, BorderLayout.WEST);
		
		JLabel idSelected = new JLabel(" idSelectionnes : ");
		idSelected.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(idSelected);
		ajouterId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( !textField.getText().equals("")) {
					int id = Integer.parseInt(textField.getText());
					membres.add(id);
					idSelected.setText(idSelected.getText()+ " "+ id+",");
					textField.setText("");
				}
			}
		});
		
		JPanel selectionNom = new JPanel();
		panel.add(selectionNom);
		selectionNom.setLayout(new GridLayout(1, 0, 0, 0));
		
		JLabel nomGroupeLabel = new JLabel("Choisissez un nom :");
		nomGroupeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selectionNom.add(nomGroupeLabel);
		
		nom = new JTextField();
		selectionNom.add(nom);
		nom.setColumns(10);
		
		JButton creer = new JButton("Creer Groupe");
		creer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(membres.size()>0 && nom.getText().length() > 0) {
					String nomGroupe = nom.getText();
					ByteBuffer data = ByteBuffer.allocate(8+(membres.size()*4)+(nomGroupe.length()*2));
					data.putInt(1);
					// on met le type de paquet ( 1 pour création de Groupe )
					data.putInt(membres.size());
					// on met la longueur du paquet;
					for ( Integer idMembre : membres) {
						data.putInt(idMembre);
					}
					for(int i = 0; i < nomGroupe.length(); i++) {
						data.putChar(nomGroupe.charAt(i));
					}
					c.sendPacket(0, data.array());
					// On envoit au destinaire 0 car création groupe, et avec le tableau de bytes;
					JOptionPane.showMessageDialog(null, "Groupe créé");
					fermer();
				}
			}
		});
		panel.add(creer);


		
	}
	public void fermer() {
		//fermeture automatique de la frame après creationGroupe
		this.dispose();
	}

}
