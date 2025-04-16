package fr.uga.miashs.dciss.chatservice.common;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class BaseDeDonnees {

    private Connection connexion;


    public TreeSet<Message> messages(){
		//Retourne une treeSet de tous les messages où forwarder = this.userId  OU  recipient = this.userID;
		// Il faut donc pour chaque Ligne faire new Message(id, forwarder, recipient, message)
	}

    public ArrayList<Integer> conversations() {

        
    }





    public BaseDeDonnees() {

        // Chemin vers la base de données
        String baseDir = System.getProperty("user.dir"); // Directory courante
        String dbPath = baseDir + "/src/main/java/fr/uga/miashs/dciss/chatservice/database/database.sqlite";
        String url = "jdbc:sqlite:" + dbPath;

        try {
            this.connexion = DriverManager.getConnection(url);
            initialiserBase_message_client();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialiserBase_message_client() throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS message_client (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "content TEXT," +
                "id_forwarder INTEGER NOT NULL," +
                "id_recipient INTEGER NOT NULL," +
                "FOREIGN KEY (id_forwarder) REFERENCES user(id_u)," +
                "FOREIGN KEY (id_recipient) REFERENCES user(id_u))";
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate(createTable);
        }
    }

    public void ajouterMessage(String contenu, int idEmetteur, int idDestinataire) {
        String insert = "INSERT INTO message_client (content, id_forwarder, id_recipient) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(insert)) {
            pstmt.setString(1, contenu);
            pstmt.setInt(2, idEmetteur);
            pstmt.setInt(3, idDestinataire);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> listerMessagesRecus(int idUtilisateur) {
        List<String> messages = new ArrayList<>();
        String select = "SELECT * FROM message_client WHERE id_recipient = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(select)) {
            pstmt.setInt(1, idUtilisateur);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(formatMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<String> listerMessagesEnvoyes(int idUtilisateur) {
        List<String> messages = new ArrayList<>();
        String select = "SELECT * FROM message_client WHERE id_forwarder = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(select)) {
            pstmt.setInt(1, idUtilisateur);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(formatMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<String> listerTousLesMessages() {
        List<String> messages = new ArrayList<>();
        String select = "SELECT * FROM message_client";
        try (Statement stmt = connexion.createStatement()) {
            ResultSet rs = stmt.executeQuery(select);
            while (rs.next()) {
                messages.add(formatMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private String formatMessage(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String contenu = rs.getString("content");
        int emetteur = rs.getInt("id_forwarder");
        int destinataire = rs.getInt("id_recipient");
        return "Message " + id + " | De: " + emetteur + " | À: " + destinataire + " | Contenu: " + contenu;
    }

    public void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

