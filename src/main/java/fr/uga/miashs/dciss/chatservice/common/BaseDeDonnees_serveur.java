package fr.uga.miashs.dciss.chatservice.common;
import java.sql.*;

public class BaseDeDonnees_serveur {

    private Connection connexion;

    public BaseDeDonnees_serveur() {
        String baseDir = System.getProperty("user.dir");
        String dbPath = baseDir + "/src/main/java/fr/uga/miashs/dciss/chatservice/database/database.sqlite";
        String url = "jdbc:sqlite:" + dbPath;
    
        try {
            this.connexion = DriverManager.getConnection(url);
            initialiserBase_serveur();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialiserBase_serveur() throws SQLException {
    
        String create_table_user = "CREATE TABLE IF NOT EXISTS user (id_u INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, status INTEGER NOT NULL)";
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate(create_table_user);
        }

        String create_table_groupe = "CREATE TABLE IF NOT EXISTS groupe (id_g INTEGER PRIMARY KEY AUTOINCREMENT, nom VARCHAR(50) NOT NULL, owner INTEGER NOT NULL, FOREIGN KEY (owner) REFERENCES user(id_u))";
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate(create_table_groupe);
        } 
    }

    public void ajouterUser(String nom, int status) {
        String insert = "INSERT INTO user" + " (nom, status) VALUES (?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(insert)) {
            pstmt.setString(1, nom);
            pstmt.setInt(2, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterGroupe( String nom, int owner) {
        String insert = "INSERT INTO groupe" + " (nom, owner) VALUES (?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(insert)) {
            pstmt.setString(1, nom);
            pstmt.setInt(2, owner);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void supprimer_user(int user_id) {
    
        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate("DELETE FROM user WHERE id_u = " + user_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stmt = connexion.createStatement()) {
            stmt.executeUpdate("DELETE FROM groupe WHERE owner = " + user_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    public void fermerConnexion(int user_id) {
        try {
            supprimer_user(user_id);
            
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }








    
}
