package com.ecommerce.service;

import com.ecommerce.dao.FournisseurDAO;
import com.ecommerce.model.Fournisseur;

import java.sql.SQLException;
import java.util.List;

public class FournisseurService {
    private FournisseurDAO fournisseurDAO;

    public FournisseurService() {
        this.fournisseurDAO = new FournisseurDAO();
    }

    public void ajouterFournisseur(Fournisseur fournisseur) throws SQLException {
        fournisseurDAO.ajouterFournisseur(fournisseur);
    }

    public void modifierFournisseur(Fournisseur fournisseur) throws SQLException {
        fournisseurDAO.modifierFournisseur(fournisseur);
    }

    public void supprimerFournisseur(int idFournisseur) throws SQLException {
        fournisseurDAO.supprimerFournisseur(idFournisseur);
    }

    public List<Fournisseur> afficherFournisseurs() throws SQLException {
        return fournisseurDAO.afficherFournisseurs();
    }
}