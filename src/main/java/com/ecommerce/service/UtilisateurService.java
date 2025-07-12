package com.ecommerce.service;

import com.ecommerce.dao.UtilisateurDAO;
import com.ecommerce.model.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public class UtilisateurService {
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void ajouterUser(Utilisateur user) throws SQLException {
        utilisateurDAO.ajouterUser(user);
    }

    public void modifierUser(Utilisateur user) throws SQLException {
        utilisateurDAO.modifierUser(user);
    }

    public void supprimerUser(int id) throws SQLException {
        utilisateurDAO.supprimerUser(id);
    }

    public List<Utilisateur> afficherUsers() throws SQLException {
        return utilisateurDAO.afficherUsers();
    }

    public Utilisateur seconnecter(String username, String pswrd) throws SQLException {
        return utilisateurDAO.seconnecter(username, pswrd);
    }
}