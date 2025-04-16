package fr.uga.miashs.dciss.chatservice.common;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;

public class BaseDeDonnees_client {

    private Connection connexion;
    private int user_id;


    public TreeSet<Message> messages_tous(int user_id) {
        TreeSet<Message> messages = new TreeSet<>();
        String nomTable = "message_client_" + this.user_id; 
    
        String query = "SELECT * FROM " + nomTable;
        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int forwarder = rs.getInt("id_forwarder");
                int recipient = rs.getInt("id_recipient");
                String content = rs.getString("content");
    
                Message m = new Message(id, forwarder, recipient, content);
                messages.add(m);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return messages;
    }


    public TreeSet<Message> messages_obtenus(int user_id) {
        TreeSet<Message> messages = new TreeSet<>();
        String nomTable = "message_client_" + this.user_id;
    
        String query = "SELECT * FROM " + nomTable + " WHERE id_recipient = " + user_id;
        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
    
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int forwarder = rs.getInt("id_forwarder");
                int recipient = rs.getInt("id_recipient");
                String content = rs.getString("content");
    
                Message m = new Message(id, forwarder, recipient, content);
                messages.add(m);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return messages;
    }

    public TreeSet<Message> messages_envoyes(int user_id) {
        TreeSet<Message> messages = new TreeSet<>();
        String nomTable = "message_client_" + this.user_id;
    
        String query = "SELECT * FROM " + nomTable + " WHERE id_forwarder = " + user_id;
        try (PreparedStatement pstmt = connexion.prepareStatement(query)) {
    
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int forwarder = rs.getInt("id_forwarder");
                int recipient = rs.getInt("id_recipient");
                String content = rs.getString("content");
    
                Message m = new Message(id, forwarder, recipient, content);
                messages.add(m);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return messages;
    }
    
    
    

    public ArrayList<Integer> conversations() {
        ArrayList<Integer> membres = new ArrayList<>();
        String nomTable = "conversation_client_" + this.user_id;
    
        String query = "SELECT member FROM " + nomTable;
        try (Statement stmt = connexion.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int memberId = rs.getInt("member");
                membres.add(memberId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return membres;
    }
    

    public BaseDeDonnees_client(int user_id) {
        this.user_id = user_id;
    
        String baseDir = System.getProperty("user.dir");
        String dbPath = baseDir + "/src/main/java/fr/uga/miashs/dciss/chatservice/database/database.sqlite";
        String url = "jdbc:sqlite:" + dbPath;
    
        try {
            this.connexion = DriverManager.getConnection(url);
            initialiserBase_client(user_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void initialiserBase_client(int user_id) throws SQLException {
        String nom_table_client_messages = "message_client_" + user_id;
    
        String create_table_client_messages = "CREATE TABLE IF NOT EXISTS " + nom_table_client_messages + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "content TEXT," +
                "id_forwarder INTEGER NOT NULL," +
                "id_recipient INTEGER NOT NULL," +
                "FOREIGN KEY (id_forwarder) REFERENCES user(id_u)," +
                "FOREIGN KEY (id_recipient) REFERENCES user(id_u))";
    
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate(create_table_client_messages);
        }

        String nom_table_client_conversations = "conversation_client_" + user_id;

        String create_table_client_conversations = "CREATE TABLE IF NOT EXISTS " + nom_table_client_conversations + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "member INTEGER NOT NULL CHECK(member != 0)," +
                "UNIQUE(member))";
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate(create_table_client_conversations);}
        }
    

    public void ajouterMessage( String contenu, int id_forwarder, int id_recipient) {
        String insert = "INSERT INTO message_client_" + user_id + " (content, id_forwarder, id_recipient) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(insert)) {
            pstmt.setString(1, contenu);
            pstmt.setInt(2, id_forwarder);
            pstmt.setInt(3, id_recipient);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterConversation(int user_id, int member_id) {
        String nomTable = "conversation_client_" + user_id;
        String checkQuery = "SELECT COUNT(*) FROM " + nomTable + " WHERE member = ?";
        String insertQuery = "INSERT INTO " + nomTable + " (member) VALUES (?)";
    
        try (PreparedStatement checkStmt = connexion.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, member_id);
            ResultSet rs = checkStmt.executeQuery();
    
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Ce membre existe déjà dans la table !");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    
        try (PreparedStatement insertStmt = connexion.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, member_id);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void viderTablesClient() {
        String nomTableMessages = "message_client_" + this.user_id;
        String nomTableConversations = "conversation_client_" + this.user_id;
    
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate("DELETE FROM " + nomTableMessages);
            stmt.executeUpdate("DELETE FROM " + nomTableConversations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    public void fermerConnexion() {
        try {
            viderTablesClient();
            
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}

