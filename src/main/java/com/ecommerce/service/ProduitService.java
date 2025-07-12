package com.ecommerce.service;

import com.ecommerce.dao.ProduitDAO;
import com.ecommerce.model.Produit;

import java.sql.SQLException;
import java.util.List;

public class ProduitService {
    private ProduitDAO produitDAO;

    public ProduitService() {
        this.produitDAO = new ProduitDAO();
    }

    public void ajouterProduit(Produit p) throws SQLException {
        produitDAO.ajouterProduit(p);
    }

    public void modifierProduit(Produit p) throws SQLException {
        produitDAO.modifierProduit(p);
    }

    public void supprimerProduit(int id) throws SQLException {
        produitDAO.supprimerProduit(id);
    }

    public List<Produit> afficherProduits() throws SQLException {
        return produitDAO.afficherProduits();
    }

    public List<Produit> chercherProduit(String mot, String categorie) throws SQLException {
        return produitDAO.chercherProduit(mot, categorie);
    }
}