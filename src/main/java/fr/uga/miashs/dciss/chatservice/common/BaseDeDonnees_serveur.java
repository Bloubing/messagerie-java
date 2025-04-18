package fr.uga.miashs.dciss.chatservice.common;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;

public class BaseDeDonnees_serveur {

	private Connection connexion;

	public BaseDeDonnees_serveur() {
		String baseDir = System.getProperty("user.dir");
		String dbPath = baseDir + "/src/main/java/fr/uga/miashs/dciss/chatservice/database/database_serveur.sqlite";
		String url = "jdbc:sqlite:" + dbPath;

		try {
			this.connexion = DriverManager.getConnection(url);
			initialiserBase_serveur();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initialiserBase_serveur() throws SQLException {

		try (Statement stmt = connexion.createStatement()) {
			stmt.executeUpdate("DROP TABLE IF EXISTS user");
			stmt.executeUpdate("DROP TABLE IF EXISTS groupe");

		} catch (SQLException e) {
			e.printStackTrace();
		}

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

	public ArrayList<Integer> getConnected() {
		ArrayList<Integer> res = new ArrayList<Integer>();

		String query = "SELECT * FROM  user WHERE status = 1";
		try (PreparedStatement pstmt = connexion.prepareStatement(query)) {

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id_u");
				res.add(id);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	public void ajouterGroupe(String nom, int owner) {
		String insert = "INSERT INTO groupe" + " (nom, owner) VALUES (?, ?)";
		try (PreparedStatement pstmt = connexion.prepareStatement(insert)) {
			pstmt.setString(1, nom);
			pstmt.setInt(2, owner);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void supprimerGroupe(String groupName) {
		try (Statement stmt = connexion.createStatement()) {
			stmt.executeUpdate("DELETE FROM groupe WHERE nom =" + groupName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deconnecter_user(int user_id) {
		try (Statement stmt = connexion.createStatement()) {
			stmt.executeUpdate("UPDATE user SET status=0 WHERE id_u = " + user_id);
			System.out.println("DECONNEXION EFFECTUEE");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
