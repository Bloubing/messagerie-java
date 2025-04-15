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

			cnx.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS user (id_u INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT NOT NULL, status INTEGER NOT NULL)");

            PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO user VALUES (?,?,?)");
			
			pstmt.setInt(1, 35);
			pstmt.setString(2, "Ofelia");
			pstmt.setInt(3, 1);
			
			boolean inserted = pstmt.executeUpdate()==1;


            // Afficher les utilisateurs
            String selectSQL = "SELECT * FROM user";
            try (Statement stmt = cnx.createStatement(); ResultSet res = stmt.executeQuery(selectSQL)) {
                System.out.println("Liste des utilisateurs :");
                while (res.next()) {
                    int id = res.getInt("id_u");
                    String nom = res.getString("nom");
                    int status = res.getInt("status");
                    System.out.println(id + " | " + nom + " | statut: " + status);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQLite:");
            e.printStackTrace();
        }
    }





}
