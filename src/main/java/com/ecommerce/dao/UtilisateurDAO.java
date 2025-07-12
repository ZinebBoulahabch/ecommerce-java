package com.ecommerce.dao;

import com.ecommerce.model.Utilisateur;
import com.ecommerce.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    public void ajouterUser(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO Utilisateur (nomUtilisateur, motDePasse, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getNomUtilisateur());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getRole());
            stmt.executeUpdate();
        }
    }

    public void modifierUser(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE Utilisateur SET nomUtilisateur = ?, motDePasse = ?, role = ? WHERE idUtilisateur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getNomUtilisateur());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getRole());
            stmt.setInt(4, utilisateur.getIdUtilisateur());
            stmt.executeUpdate();
        }
    }

    public void supprimerUser(int idUtilisateur) throws SQLException {
        String sql = "DELETE FROM Utilisateur WHERE idUtilisateur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            stmt.executeUpdate();
        }
    }

    public List<Utilisateur> afficherUsers() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNomUtilisateur(rs.getString("nomUtilisateur"));
                utilisateur.setMotDePasse(rs.getString("motDePasse"));
                utilisateur.setRole(rs.getString("role"));
                utilisateurs.add(utilisateur);
            }
        }
        return utilisateurs;
    }

    public Utilisateur seconnecter(String nomUtilisateur, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE nomUtilisateur = ? AND motDePasse = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomUtilisateur);
            stmt.setString(2, motDePasse);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                    utilisateur.setNomUtilisateur(rs.getString("nomUtilisateur"));
                    utilisateur.setMotDePasse(rs.getString("motDePasse"));
                    utilisateur.setRole(rs.getString("role"));
                    return utilisateur;
                }
            }
        }
        return null;
    }
}