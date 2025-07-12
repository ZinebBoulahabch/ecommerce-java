package com.ecommerce.service;

import java.sql.SQLException;
import java.util.List;

import com.ecommerce.dao.CommandeDAO;
import com.ecommerce.model.Commande;

public class CommandeService {
    private final CommandeDAO commandeDAO;

    public CommandeService() {
        this.commandeDAO = new CommandeDAO();
    }

    public void ajouterCommande(Commande commande) throws SQLException {
        commandeDAO.ajouterCommande(commande);
    }

    public void modifierCommande(Commande commande) throws SQLException {
        commandeDAO.modifierCommande(commande);
    }

    public void supprimerCommande(int idCommande) throws SQLException {
        commandeDAO.supprimerCommande(idCommande);
    }

    public List<Commande> afficherCommandes() throws SQLException {
        return commandeDAO.afficherCommandes();
    }
}