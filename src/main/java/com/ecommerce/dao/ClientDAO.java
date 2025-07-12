package com.ecommerce.dao;
import com.ecommerce.model.Client;
import com.ecommerce.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// DAO est un principe d'accès aux méthodes et données facilement
// Les objets DAO seront déclarés dans le dossier Service 
// Comme ça on ordonne le mapping des données
// Meme chose pour tout les autres fichiers DAO
public class ClientDAO {
    public void ajoutClient(Client client) throws SQLException {
        String sql = "INSERT INTO Client (nom, prenom, adresse, telephone, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getAdresse());
            stmt.setString(4, client.getTelephone());
            stmt.setString(5, client.getEmail());
            stmt.executeUpdate();
        }
    }

    public void modifierClient(Client client) throws SQLException {
        String sql = "UPDATE Client SET nom = ?, prenom = ?, adresse = ?, telephone = ?, email = ? WHERE idClient = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getAdresse());
            stmt.setString(4, client.getTelephone());
            stmt.setString(5, client.getEmail());
            stmt.setInt(6, client.getIdClient());
            stmt.executeUpdate();
        }
    }

    public void supprimerClient(int idClient) throws SQLException {
        String sql = "DELETE FROM Client WHERE idClient = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idClient);
            stmt.executeUpdate();
        }
    }

    public List<Client> afficherClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("idClient"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setAdresse(rs.getString("adresse"));
                client.setTelephone(rs.getString("telephone"));
                client.setEmail(rs.getString("email"));
                clients.add(client);
            }
        }
        return clients;
    }
}