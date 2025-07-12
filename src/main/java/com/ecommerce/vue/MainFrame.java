package com.ecommerce.vue;

import com.ecommerce.model.Utilisateur;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Utilisateur loggedInUser;

    public MainFrame(Utilisateur loggedInUser) {
        this.loggedInUser = loggedInUser;
        Interface();
    }

    private void Interface() {
        setTitle("Système de gestion de commerce de " + loggedInUser.getNomUtilisateur() + " (" + loggedInUser.getRole() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        

        tabbedPane.addTab("Commandes", new CommandeVue(loggedInUser));
        tabbedPane.addTab("Clients", new ClientVue());
        

        if ("Admin".equals(loggedInUser.getRole())) {
            tabbedPane.addTab("Produits", new ProduitVue());
            tabbedPane.addTab("Fournisseurs", new FournisseurVue());
            tabbedPane.addTab("Utilisateurs", new UtilisateurVue());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}