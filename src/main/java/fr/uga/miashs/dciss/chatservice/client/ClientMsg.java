/*
 * Copyright (c) 2024.  Jerome David. Univ. Grenoble Alpes.
 * This file is part of DcissChatService.
 *
 * DcissChatService is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * DcissChatService is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.uga.miashs.dciss.chatservice.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import fr.uga.miashs.dciss.chatservice.common.Packet;
import fr.uga.miashs.dciss.chatservice.common.BaseDeDonnees_client;

/**
 * Manages the connection to a ServerMsg. Method startSession() is used to
 * establish the connection. Then messages can be send by a call to sendPacket.
 * The reception is done asynchronously (internally by the method receiveLoop())
 * and the reception of a message is notified to MessagesListeners. To register
 * a MessageListener, the method addMessageListener has to be called. Session
 * are closed thanks to the method closeSession().
 */
public class ClientMsg {

	private String serverAddress;
	private int serverPort;
	private BaseDeDonnees_client db;
	private Socket s;
	private DataOutputStream dos;
	private DataInputStream dis;

	private int identifier;

	private List<MessageListener> mListeners;
	private List<ConnectionListener> cListeners;

	/**
	 * Create a client with an existing id, that will connect to the server at the
	 * given address and port
	 * 
	 * @param id      The client id
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(int id, String address, int port) {
		if (id < 0)
			throw new IllegalArgumentException("id must not be less than 0");
		if (port <= 0)
			throw new IllegalArgumentException("Server port must be greater than 0");
		serverAddress = address;
		serverPort = port;
		identifier = id;
		mListeners = new ArrayList<>();
		cListeners = new ArrayList<>();
	}
	
	public BaseDeDonnees_client getDb() {
		return this.db;
	}

	/**
	 * Create a client without id, the server will provide an id during the the
	 * session start
	 * 
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(String address, int port) {
		this(0, address, port);
	}

	/**
	 * Register a MessageListener to the client. It will be notified each time a
	 * message is received.
	 * 
	 * @param l
	 */
	public void addMessageListener(MessageListener l) {
		if (l != null)
			mListeners.add(l);
	}
	public String formatageMessage(Packet p) {
		StringBuffer message = new StringBuffer("");
		if (p.destId < 0) {
			message.append("Message reçu de "+p.srcId+" dans le groupe ");
			System.out.println(message);
			ByteBuffer data = ByteBuffer.wrap(p.data);
			byte  longueurNom = data.get();
			for (int i = 0; i<longueurNom; i++) {
				message.append(data.getChar());
			}
			byte[] dataMessage = new byte[data.remaining()];
			int i  = 0;
			while(data.hasRemaining()) {
				dataMessage[i] = data.get();
				i +=1;
			}
			message.append(" : ");
			message.append(new String(dataMessage));

		}
		else {
			ByteBuffer data = ByteBuffer.wrap(p.data);
			int type = data.getInt();

			if (type != 10) {
				message.append("Message reçu de " + p.srcId + " : ");
				String text = new String(p.data);
			getDb().ajouterMessage(text, p.srcId, getIdentifier());
			getDb().ajouterConversation(getIdentifier(), p.srcId);

			message.append(text);
			
			} else { // c'est un fichier
				message.append("Fichier reçu de " + p.srcId + " : ");
				//On lit le nom du fichier
				byte longueurNomFichier = data.get();
				byte[] nameBytes = new byte[longueurNomFichier];
				data.get(nameBytes);
				String nomFichier = new String(nameBytes, StandardCharsets.UTF_8);
				String messageCheckExtension = nomFichier.toString();
				System.out.println(messageCheckExtension);
				System.out.println(messageCheckExtension.endsWith(".txt"));
			
				String textePourBdd = "vous a envoyé le fichier "+nomFichier;
				getDb().ajouterMessage(nomFichier, p.srcId, getIdentifier());
				getDb().ajouterConversation(getIdentifier(), p.srcId);

				if (messageCheckExtension.endsWith(".txt")) {
					message.append("\nContenu du fichier texte :\n");
					byte[] dataFichier = new byte[data.remaining()];
					int i  = 0;
					while (data.hasRemaining()) {
						dataFichier[i] = data.get();
						i += 1;
					}
					
					message.append(new String(dataFichier));
				} else {

					message.append(". Vérifiez votre répertoire.");
				}
				
			}
			
			

			
			
		}
		return message.toString();
	}

	protected void notifyMessageListeners(Packet p) {
		mListeners.forEach(x -> x.messageReceived(p));
	}

	/**
	 * Register a ConnectionListener to the client. It will be notified if the
	 * connection start or ends.
	 * 
	 * @param l
	 */
	public void addConnectionListener(ConnectionListener l) {
		if (l != null)
			cListeners.add(l);
		
	}

	protected void notifyConnectionListeners(boolean active) {
		cListeners.forEach(x -> x.connectionEvent(active));
	}

	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Method to be called to establish the connection.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void startSession() throws UnknownHostException {
		if (s == null || s.isClosed()) {
			try {
				s = new Socket(serverAddress, serverPort);
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
				dos.writeInt(identifier);
				dos.flush();
				if (identifier == 0) {
					identifier = dis.readInt();
				}
				// start the receive loop
				System.out.println("mon id est le  :"+this.getIdentifier());
				this.db = new BaseDeDonnees_client(identifier);
				new Thread(() -> receiveLoop()).start();
				notifyConnectionListeners(true);
			} catch (IOException e) {
				e.printStackTrace();
				// error, close session
				closeSession();
			}
		}
	}

	//si il y a un fichier attaché  un message
	public void sendFile(int destId,String filePath) throws IOException {
		
		File file = new File(filePath);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		dos.writeInt(10); // Type d’action : transfert de fichier
		String fileName = file.getName();
		byte[] nameBytes = fileName.getBytes();
		dos.write(nameBytes.length); // longueur du nom
		dos.write(nameBytes); // nom du fichier

		byte[] fileBytes = Files.readAllBytes(file.toPath());
		dos.write(fileBytes); // données du fichier

		dos.flush();
		sendPacket(destId, bos.toByteArray());
	}


	/**
	 * Send a packet to the specified destination (etiher a userId or groupId)
	 * 
	 * @param destId the destinatiion id
	 * @param data   the data to be sent
	 */
	public void sendPacket(int destId, byte[] data) {
		try {
			synchronized (dos) {
				dos.writeInt(destId);
				dos.writeInt(data.length);
				dos.write(data);
				dos.flush();
			}
		} catch (IOException e) {
			// error, connection closed
			closeSession();
		}

	}

	/**
	 * Start the receive loop. Has to be called only once.
	 */
	private void receiveLoop() {
		try {
			while (s != null && !s.isClosed()) {

				int sender = dis.readInt();
				int dest = dis.readInt();
				int length = dis.readInt();
				byte[] data = new byte[length];
				dis.readFully(data);
				notifyMessageListeners(new Packet(sender, dest, data));

			}
		} catch (IOException e) {
			// error, connection closed
		}
		closeSession();
	}

	public void closeSession() {
		try {
			if (s != null)
				s.close();
		} catch (IOException e) {
		}
		s = null;
		notifyConnectionListeners(false);
	}

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		ClientMsg c = new ClientMsg("localhost", 1666);

		// add a dummy listener that print the content of message as a string
		c.addMessageListener(p -> System.out.println(p.srcId + " says to " + p.destId + ": " + new String(p.data)));
		// listener pour la version fichier
		c.addMessageListener(new MessageListenerImpl());

		// add a connection listener that exit application when connection closed
		c.addConnectionListener(active -> {
			if (!active)
				System.exit(0);
		});

		c.startSession();
		System.out.println("Vous êtes : " + c.getIdentifier());

		// Thread.sleep(5000);

		// l'utilisateur avec id 4 crée un grp avec 1 et 3 dedans (et lui meme)
		if (c.getIdentifier() == 4) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			// byte 1 : create group on server
			dos.writeByte(1);

			// nb members
			dos.writeInt(2);
			// list members
			dos.writeInt(1);
			dos.writeInt(3);
			dos.flush();

			c.sendPacket(0, bos.toByteArray());

		}

		Scanner sc = new Scanner(System.in);
		String lu = null;
		while (!"\\quit".equals(lu)) {
			try {
				System.out.println("Que voulez vous faire ? 0 : écrire, 1 créer un groupe, 2 quitter un groupe, 3 suprimer un groupe, 4 ajouter un membre, 5 retirer un membre, 6 renommer un groupe");
				int type = Integer.parseInt(sc.nextLine());				
				if ( type == 0) { //message classique
				System.out.println("à qui voulez-vous écrire");
				int dest = Integer.parseInt(sc.nextLine());
				System.out.println("Votre message ? ");
				lu = sc.nextLine();
				c.sendPacket(dest, lu.getBytes());
				}
				else if( type == 1) {
					ArrayList<Integer> membres= new ArrayList<Integer>();
					System.out.println("Ajouter id membre : (-1) pour stopper");
					int membre = Integer.parseInt(sc.nextLine());
					while (membre != -1) {
						membres.add(membre);
						System.out.println("Ajouter id membre : (-1) pour stopper");
						membre = Integer.parseInt(sc.nextLine());
					}
					ByteBuffer data = ByteBuffer.allocate(8 + (membres.size() * 4));
					data.putInt(type);
					data.putInt(membres.size());
					for (Integer idMembre : membres) {
						data.putInt(idMembre);
					}
					for (byte b : data.array()) {
						System.out.println(b);
					}
					c.sendPacket(0, data.array());

				}
				else if (type == 10) {
					System.out.println("À quel utilisateur voulez-vous envoyer un fichier ?");
					int dest = Integer.parseInt(sc.nextLine());
					System.out.println("Chemin vers le fichier : ");
					String path = sc.nextLine();
					
					
					
					c.sendFile(dest, path);
					
				}
			} catch (InputMismatchException | NumberFormatException e) {
				System.out.println("Mauvais format");
			}

		}

		/*
		 * int id =1+(c.getIdentifier()-1) % 2; System.out.println("send to "+id);
		 * c.sendPacket(id, "bonjour".getBytes());
		 * 
		 * 
		 * Thread.sleep(10000);
		 */

		c.closeSession();

	}

}
