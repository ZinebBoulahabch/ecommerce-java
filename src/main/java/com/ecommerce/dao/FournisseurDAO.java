package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.Fournisseur;
import com.ecommerce.utils.DBConnection;

public class FournisseurDAO {
    public void ajouterFournisseur(Fournisseur fournisseur) throws SQLException {
        String sql = "INSERT INTO Fournisseur (nom, contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getContact());
            stmt.executeUpdate();
        }
    }

    public void modifierFournisseur(Fournisseur fournisseur) throws SQLException {
        String sql = "UPDATE Fournisseur SET nom = ?, contact = ? WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getContact());
            stmt.setInt(3, fournisseur.getIdFournisseur());
            stmt.executeUpdate();
        }
    }

    public void supprimerFournisseur(int idFournisseur) throws SQLException {
        String sql = "DELETE FROM Fournisseur WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFournisseur);
            stmt.executeUpdate();
        }
    }

    public List<Fournisseur> afficherFournisseurs() throws SQLException {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur();
                fournisseur.setIdFournisseur(rs.getInt("idFournisseur"));
                fournisseur.setNom(rs.getString("nom"));
                fournisseur.setContact(rs.getString("contact"));
                fournisseurs.add(fournisseur);
            }
        }
        return fournisseurs;
    }
}