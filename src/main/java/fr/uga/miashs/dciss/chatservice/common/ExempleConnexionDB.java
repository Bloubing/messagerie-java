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

package fr.uga.miashs.dciss.chatservice.common;

import java.sql.*;



public class ExempleConnexionDB {

	public static void main(String[] args) {
        // Chemin vers la base de données
        String baseDir = System.getProperty("user.dir"); // Directory courante
        String dbPath = baseDir + "/src/main/java/fr/uga/miashs/dciss/chatservice/database/database.sqlite";
        String url = "jdbc:sqlite:" + dbPath;

        try (Connection cnx = DriverManager.getConnection(url)) {
            System.out.println("Connexion réussie à SQLite.");

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS user (id_u INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, status INTEGER NOT NULL)");

			//Exemple pour inserer plusieurs utilisateurs

			String[][] utilisateurs = {
				{"Ofelia", "1"},
				{"Pin", "1"},
				{"Miku Hatsune", "1"},
				{"Super Mario", "1"}
			};
			
			PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO user (nom, status) VALUES (?, ?)");
			
			for (String[] user : utilisateurs) {
				pstmt.setString(1, user[0]);                 // nom
				pstmt.setInt(2, Integer.parseInt(user[1]));  // status
				pstmt.executeUpdate();
			}
			
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les utilisateurs
            String selectSQL = "SELECT * FROM user";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des utilisateurs :");
                while (res.next()) {
                    int id_user = res.getInt("id_u");
                    String nom_user = res.getString("nom");
                    int status_user = res.getInt("status");
                    System.out.println(id_user + " | " + nom_user + " | statut: " + status_user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }


		//Exemple de la création et insertion dans la table groupe
		try (Connection cnx = DriverManager.getConnection(url)) {
            System.out.println("Connexion réussie à SQLite.");

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS groupe (id_g INTEGER PRIMARY KEY AUTOINCREMENT, nom VARCHAR(50) NOT NULL, owner INTEGER NOT NULL, nb_of_members INTEGER CHECK(nb_of_members >= 0), FOREIGN KEY (owner) REFERENCES user(id_u))");

            PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO groupe (nom, owner, nb_of_members) VALUES (?, ?, ?)");
			
			pstmt.setString(1, "Amateurs des perroquets");
			pstmt.setInt(2, 1);
			pstmt.setInt(3,1);
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les groupes
            String selectSQL = "SELECT * FROM groupe";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des groupes :");
                while (res.next()) {
                    int id_groupe = res.getInt("id_g");
                    String nom_groupe = res.getString("nom");
                    String owner = res.getString("owner");
					int nb_de_membres = res.getInt("nb_of_members");
                    System.out.println(id_groupe + " | " + nom_groupe + " | owner: " + owner + " | nombre des membres:" + nb_de_membres);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }


		//Exemple de la création et insertion dans la table message
		try (Connection cnx = DriverManager.getConnection(url)) {
            System.out.println("Connexion réussie à SQLite.");

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS message (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, id_forwarder INTEGER NOT NULL, id_recipient INTEGER NOT NULL, content TEXT, status INTEGER NOT NULL, FOREIGN KEY (id_forwarder) REFERENCES user(id_u), FOREIGN KEY (id_recipient) REFERENCES user(id_u))");

            PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO message (id_forwarder, id_recipient, content, status) VALUES (?, ?, ?, ?)");
			
			pstmt.setInt(1, 1);  //de qui
			pstmt.setInt(2, 2);  //à qui
			pstmt.setString(3,"Hallo mein Schatz)))");  //message
			pstmt.setInt(4,2);  //status
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les messages
            String selectSQL = "SELECT * FROM message";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des messages :");
                while (res.next()) {
                    int id_message = res.getInt("id");
                    String forwarder = res.getString("id_forwarder");
                    String recipient = res.getString("id_recipient");
					String content = res.getString("content");
					int status = res.getInt("status");
                    System.out.println(id_message + " | de " + forwarder + " | à " + recipient + " | " + content + " | status: " + status);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }


        //Exemple de la création et insertion dans la table user_group
		try (Connection cnx = DriverManager.getConnection(url)) {
            System.out.println("Connexion réussie à SQLite.");

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS user_group (id_relationship_ug INTEGER PRIMARY KEY AUTOINCREMENT, id_user INTEGER, id_group INTEGER,  FOREIGN KEY (id_user) REFERENCES user(id_u), FOREIGN KEY (id_group) REFERENCES groupe(id_g))");

            PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO user_group (id_user, id_group) VALUES (?, ?)");
			
			pstmt.setInt(1, 1);  
			pstmt.setInt(2, 1);   
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les relations user-group
            String selectSQL = "SELECT * FROM user_group";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des relations :");
                while (res.next()) {
                    int id_user = res.getInt("id_user");
					int id_group = res.getInt("id_group");
                    System.out.println(id_user + " | " + id_group);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }


        //Exemple de la création et insertion dans la table conversation_client
		try (Connection cnx = DriverManager.getConnection(url)) {
            System.out.println("Connexion réussie à SQLite.");

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS conversation_client (id INTEGER PRIMARY KEY AUTOINCREMENT, member INTEGER NOT NULL CHECK(member != 0))");

            PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO conversation_client (member) VALUES (?)");
			
			pstmt.setInt(1, 1);     
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les relations conversation_client
            String selectSQL = "SELECT * FROM conversation_client";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des conversations :");
                while (res.next()) {
                    int id = res.getInt("id");
					int membre = res.getInt("member");
                    System.out.println(id + " | " + membre);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }


        //Exemple de la création et insertion dans la table message_client
		try (Connection cnx = DriverManager.getConnection(url)) {
            System.out.println("Connexion réussie à SQLite.");

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS message_client (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, id_forwarder INTEGER NOT NULL, id_recipient INTEGER NOT NULL, FOREIGN KEY (id_forwarder) REFERENCES user(id_u), FOREIGN KEY (id_recipient) REFERENCES user(id_u) )");

            PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO message_client (content, id_forwarder, id_recipient) VALUES (?, ?, ?)");
			
            pstmt.setString(1, "Privet!"); 
            pstmt.setInt(2, 1); 
			pstmt.setInt(3, 1);     
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les relations message_client
            String selectSQL = "SELECT * FROM message_client";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des messages :");
                while (res.next()) {
                    int id = res.getInt("id");
                    String content = res.getString("content");
                    int forwarder = res.getInt("id_forwarder");
					int recipient = res.getInt("id_recipient");
                    System.out.println(id + " | " + content + " | " + forwarder + " | " + recipient);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }


    }


}
