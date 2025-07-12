package com.ecommerce.vue;

import com.ecommerce.model.Utilisateur;
import com.ecommerce.service.UtilisateurService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; 

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UtilisateurVue extends JPanel {
    private UtilisateurService utilisateurService;
    private JTable table;
    private DefaultTableModel tableModel;

    public UtilisateurVue() {
        utilisateurService = new UtilisateurService();
        Interface();
    }

    private void Interface() {
        setLayout(new BorderLayout(10, 10));


        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom Utilisateur", "Rôle"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(e -> ajouterUser());
        updateButton.addActionListener(e -> modifierUser());
        deleteButton.addActionListener(e -> supprimerUser());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadUtilisateurs();
    }

    private void loadUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.afficherUsers();
            tableModel.setRowCount(0);
            for (Utilisateur utilisateur : utilisateurs) {
                tableModel.addRow(new Object[]{
                        utilisateur.getIdUtilisateur(),
                        utilisateur.getNomUtilisateur(),
                        utilisateur.getRole()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterUser() {
        JTextField nomF = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Vendeur"});

        Object[] fields = {
                "Nom Utilisateur:", nomF,
                "Mot de Passe:", passwordField,
                "Rôle:", roleCombo
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Utilisateur", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nomUtilisateur = nomF.getText().trim();
                String motDePasse = new String(passwordField.getPassword()).trim();

                if (nomUtilisateur.isEmpty() || motDePasse.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nom d'utilisateur et mot de passe sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check for unique username
                List<Utilisateur> utilisateurs = utilisateurService.afficherUsers();
                if (utilisateurs.stream().anyMatch(u -> u.getNomUtilisateur().equalsIgnoreCase(nomUtilisateur))) {
                    JOptionPane.showMessageDialog(this, "Ce nom d'utilisateur existe déjà", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Utilisateur utilisateur = new Utilisateur(
                        nomUtilisateur,
                        motDePasse,
                        (String) roleCombo.getSelectedItem()
                );
                utilisateurService.ajouterUser(utilisateur);
                loadUtilisateurs();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'utilisateur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idUtilisateur = (int) tableModel.getValueAt(selectedRow, 0);
            JTextField nomF = new JTextField((String) tableModel.getValueAt(selectedRow, 1), 20);
            JPasswordField passwordField = new JPasswordField(20);
            JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Vendeur"});
            roleCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 2));

            Object[] fields = {
                    "Nom Utilisateur:", nomF,
                    "Mot de Passe:", passwordField,
                    "Rôle:", roleCombo
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Utilisateur", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String nomUtilisateur = nomF.getText().trim();
                    String motDePasse = new String(passwordField.getPassword()).trim();

                    if (nomUtilisateur.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Nom d'utilisateur est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }


                    List<Utilisateur> utilisateurs = utilisateurService.afficherUsers();
                    if (utilisateurs.stream()
                            .anyMatch(u -> u.getNomUtilisateur().equalsIgnoreCase(nomUtilisateur) && u.getIdUtilisateur() != idUtilisateur)) {
                        JOptionPane.showMessageDialog(this, "Ce nom d'utilisateur existe déjà", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Utilisateur utilisateur = new Utilisateur(
                            nomUtilisateur,
                            motDePasse.isEmpty() ? utilisateurs.stream()
                                    .filter(u -> u.getIdUtilisateur() == idUtilisateur)
                                    .findFirst().get().getMotDePasse() : motDePasse,
                            (String) roleCombo.getSelectedItem()
                    );
                    utilisateur.setIdUtilisateur(idUtilisateur);
                    utilisateurService.modifierUser(utilisateur);
                    loadUtilisateurs();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'utilisateur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idUtilisateur = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet utilisateur ?", "Confirmer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    utilisateurService.supprimerUser(idUtilisateur);
                    loadUtilisateurs();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'utilisateur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}