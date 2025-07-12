package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.Client;
import com.ecommerce.model.Commande;
import com.ecommerce.model.Produit;
import com.ecommerce.utils.DBConnection;

public class CommandeDAO {
    public void ajouterCommande(Commande commande) throws SQLException {
        String sql = "INSERT INTO Commande (dateCommande, idClient, total, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(commande.getDateCommande().getTime()));
            stmt.setInt(2, commande.getClient().getIdClient());
            stmt.setDouble(3, commande.getTotal());
            stmt.setString(4, commande.getStatut());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    commande.setIdCommande(rs.getInt(1));
                }
            }

            // Insert products and update stock
            String produitSql = "INSERT INTO Commande_Produit (idCommande, idProduit, quantite) VALUES (?, ?, ?)";
            String updateStock = "UPDATE Produit SET quantiteStock = quantiteStock - ? WHERE idProduit = ?";
            try (PreparedStatement produitStmt = conn.prepareStatement(produitSql);
                 PreparedStatement stockStmt = conn.prepareStatement(updateStock)) {
                for (Produit produit : commande.getListeProduits()) {
                    int quantite = 1; // À remplacer par la vraie quantité si disponible
                    produitStmt.setInt(1, commande.getIdCommande());
                    produitStmt.setInt(2, produit.getIdProduit());
                    produitStmt.setInt(3, quantite);
                    produitStmt.executeUpdate();

                    stockStmt.setInt(1, quantite);
                    stockStmt.setInt(2, produit.getIdProduit());
                    stockStmt.executeUpdate();
                }
            }
        }
    }

    public void modifierCommande(Commande commande) throws SQLException {
        String sql = "UPDATE Commande SET dateCommande = ?, idClient = ?, total = ?, statut = ? WHERE idCommande = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(commande.getDateCommande().getTime()));
            stmt.setInt(2, commande.getClient().getIdClient());
            stmt.setDouble(3, commande.getTotal());
            stmt.setString(4, commande.getStatut());
            stmt.setInt(5, commande.getIdCommande());
            stmt.executeUpdate();
        }
    }

    public void supprimerCommande(int idCommande) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // 1. Récupérer le statut de la commande
            String getStatut = "SELECT statut FROM Commande WHERE idCommande = ?";
            String getProduits = "SELECT idProduit, quantite FROM Commande_Produit WHERE idCommande = ?";
            String updateStock = "UPDATE Produit SET quantiteStock = quantiteStock + ? WHERE idProduit = ?";
            String decrementStock = "UPDATE Produit SET quantiteStock = quantiteStock - ? WHERE idProduit = ?";
            String deleteProduits = "DELETE FROM Commande_Produit WHERE idCommande = ?";
            String deleteCommande = "DELETE FROM Commande WHERE idCommande = ?";

            String statut = null;
            try (PreparedStatement ps = conn.prepareStatement(getStatut)) {
                ps.setInt(1, idCommande);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statut = rs.getString("statut");
                    }
                }
            }
            if (statut == null) throw new SQLException("Commande introuvable");
            if ("Expediée".equals(statut)) {
                throw new SQLException("Impossible de supprimer une commande expédiée.");
            }
            // 2. Récupérer les produits et quantités de la commande
            List<int[]> produits = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(getProduits)) {
                ps.setInt(1, idCommande);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        produits.add(new int[]{rs.getInt("idProduit"), rs.getInt("quantite")});
                    }
                }
            }
            // 3. Ajuster le stock
            for (int[] prod : produits) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "Annulée".equals(statut) ? decrementStock : updateStock)) {
                    ps.setInt(1, prod[1]);
                    ps.setInt(2, prod[0]);
                    ps.executeUpdate();
                }
            }
            // 4. Supprimer les lignes associées puis la commande
            try (PreparedStatement ps1 = conn.prepareStatement(deleteProduits);
                 PreparedStatement ps2 = conn.prepareStatement(deleteCommande)) {
                ps1.setInt(1, idCommande);
                ps1.executeUpdate();
                ps2.setInt(1, idCommande);
                ps2.executeUpdate();
            }
        }
    }

    public List<Commande> afficherCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, cl.* FROM Commande c JOIN Client cl ON c.idClient = cl.idClient ORDER BY c.dateCommande DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("idClient"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));

                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setDateCommande(rs.getDate("dateCommande"));
                commande.setClient(client);
                commande.setTotal(rs.getDouble("total"));
                commande.setStatut(rs.getString("statut"));
                commandes.add(commande);
            }
        }
        return commandes;
    }
}