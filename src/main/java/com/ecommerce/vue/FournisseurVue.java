package com.ecommerce.vue;

import com.ecommerce.model.Fournisseur;
import com.ecommerce.service.FournisseurService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FournisseurVue extends JPanel {
    private FournisseurService fournisseurService;
    private JTable table;
    private DefaultTableModel tableModel;

    public FournisseurVue() {
        fournisseurService = new FournisseurService();
        Interface();
    }

    private void Interface() {
        setLayout(new BorderLayout(10, 10));


        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Contact", "Produits Fournis"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(e -> ajouterFournisseur());
        updateButton.addActionListener(e -> modifierFournisseur());
        deleteButton.addActionListener(e -> supprimerFournisseur());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadFournisseurs();
    }

    private void loadFournisseurs() {
        try {
            List<Fournisseur> fournisseurs = fournisseurService.afficherFournisseurs();
            tableModel.setRowCount(0);
            for (Fournisseur fournisseur : fournisseurs) {
                tableModel.addRow(new Object[]{
                        fournisseur.getIdFournisseur(),
                        fournisseur.getNom(),
                        fournisseur.getContact(),
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des fournisseurs: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterFournisseur() {
        JTextField nomF = new JTextField(20);
        JTextField contactField = new JTextField(20);
        JTextField produitsField = new JTextField(20);

        Object[] fields = {
                "Nom:", nomF,
                "Contact:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Fournisseur", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = nomF.getText().trim();
                String contact = contactField.getText().trim();

                if (nom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le nom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Fournisseur fournisseur = new Fournisseur(
                        nom,
                        contact,
                        produitsField.getText().trim()
                );
                fournisseurService.ajouterFournisseur(fournisseur);
                loadFournisseurs();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du fournisseur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierFournisseur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idFournisseur = (int) tableModel.getValueAt(selectedRow, 0);
            JTextField nomF = new JTextField((String) tableModel.getValueAt(selectedRow, 1), 20);
            JTextField contactField = new JTextField((String) tableModel.getValueAt(selectedRow, 2), 20);
            JTextField produitsField = new JTextField((String) tableModel.getValueAt(selectedRow, 3), 20);

            Object[] fields = {
                    "Nom:", nomF,
                    "Contact:", contactField,
                    "Produits Fournis:", produitsField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Fournisseur", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String nom = nomF.getText().trim();
                    String contact = contactField.getText().trim();

                    if (nom.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Le nom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Fournisseur fournisseur = new Fournisseur(
                            nom,
                            contact,
                            produitsField.getText().trim()
                    );
                    fournisseur.setIdFournisseur(idFournisseur);
                    fournisseurService.modifierFournisseur(fournisseur);
                    loadFournisseurs();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification du fournisseur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerFournisseur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idFournisseur = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce fournisseur ?", "Confirmer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    fournisseurService.supprimerFournisseur(idFournisseur);
                    loadFournisseurs();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du fournisseur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}