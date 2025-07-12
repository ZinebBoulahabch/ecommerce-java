package com.ecommerce.dao;

import com.ecommerce.model.Produit;
import com.ecommerce.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    public void ajouterProduit(Produit produit) throws SQLException {
        String sql = "INSERT INTO Produit (nom, description, prix, quantiteStock, categorie) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantiteStock());
            stmt.setString(5, produit.getCategorie());
            stmt.executeUpdate();
        }
    }

    public void modifierProduit(Produit produit) throws SQLException {
        String sql = "UPDATE Produit SET nom = ?, description = ?, prix = ?, quantiteStock = ?, categorie = ? WHERE idProduit = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantiteStock());
            stmt.setString(5, produit.getCategorie());
            stmt.setInt(6, produit.getIdProduit());
            stmt.executeUpdate();
        }
    }

    public void supprimerProduit(int idProduit) throws SQLException {
        String sql = "DELETE FROM Produit WHERE idProduit = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduit);
            stmt.executeUpdate();
        }
    }

    public List<Produit> afficherProduits() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM Produit";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setIdProduit(rs.getInt("idProduit"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setQuantiteStock(rs.getInt("quantiteStock"));
                produit.setCategorie(rs.getString("categorie"));
                produits.add(produit);
            }
        }
        return produits;
    }

    public List<Produit> chercherProduit(String keyword, String categorie) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM Produit WHERE nom LIKE ? AND (categorie = ? OR ? IS NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, categorie);
            stmt.setString(3, categorie);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit();
                    produit.setIdProduit(rs.getInt("idProduit"));
                    produit.setNom(rs.getString("nom"));
                    produit.setDescription(rs.getString("description"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setQuantiteStock(rs.getInt("quantiteStock"));
                    produit.setCategorie(rs.getString("categorie"));
                    produits.add(produit);
                }
            }
        }
        return produits;
    }
}