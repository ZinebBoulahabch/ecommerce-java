package com.ecommerce.vue;

import com.ecommerce.model.Utilisateur;

import javax.swing.*;
import java.awt.*;

public class PagePrincipale extends JFrame {
    private Utilisateur utilisateur;
//cette interface sera princpalement affiché apres l'authentification
    public PagePrincipale(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        Interface();
    }

    private void Interface() {
        setTitle("Bienvenue dans votre système de gestion :)");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Commandes", new CommandeVue(utilisateur));
        tabbedPane.addTab("Clients", new ClientVue());

        if ("Admin".equals(utilisateur.getRole())) {
            tabbedPane.addTab("Utilisateurs", new UtilisateurVue());
            tabbedPane.addTab("Fournisseurs", new FournisseurVue());
            tabbedPane.addTab("Produits", new ProduitVue());
        }

        add(tabbedPane);
    }
}