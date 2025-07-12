package com.ecommerce.vue;

import com.ecommerce.model.Client;
import com.ecommerce.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientVue extends JPanel {
    private ClientService clientService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ClientVue() {
        clientService = new ClientService();
        Interface();
    }
    //créer interface client
    private void Interface() {
        setLayout(new BorderLayout(10, 10));

    // créer tables 
        tableModel = new DefaultTableModel(new Object[]{"id", "Nom", "Prenom", "Email", "Téléphone", "Adresse"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

    //boutons de gestion
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
    //actions boutons
        addButton.addActionListener(e -> ajoutClient());
        updateButton.addActionListener(e -> modifierClient());
        deleteButton.addActionListener(e -> supprimerClient());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        preparer_listes();
    }
    //charger la liste des clients dans le tableau
    private void preparer_listes() {
        try {
            List<Client> clients = clientService.afficherClients();
            tableModel.setRowCount(0);
            for (Client client : clients) {
                tableModel.addRow(new Object[]{
                        client.getIdClient(),
                        client.getNom(),
                        client.getPrenom(),
                        client.getEmail(),
                        client.getTelephone(),
                        client.getAdresse()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Mochkil lors de la préparation des données :(  " + ex.getMessage(), "Erreur de données", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajoutClient() {
        JTextField nomF = new JTextField(20);
        JTextField prenomF = new JTextField(20);
        JTextField emailF = new JTextField(20);
        JTextField telephoneF = new JTextField(15);
        JTextField adrF = new JTextField(30);

        Object[] fields = {
                "Nom:", nomF,
                "Prénom:", prenomF,
                "Email:", emailF,
                "Téléphone:", telephoneF,
                "Adresse:", adrF
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Client", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = nomF.getText().trim();
                String prenom = prenomF.getText().trim();
                String email = emailF.getText().trim();
                String telephone = telephoneF.getText().trim();
                String adresse = adrF.getText().trim();

                if (nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez remplir les champs !!!! >:(", "Erreur de champs", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Client client = new Client();
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setEmail(email);
                client.setTelephone(telephone);
                client.setAdresse(adresse);

                clientService.ajoutClient(client);
                preparer_listes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "On a rencontré un problème : " + ex.getMessage(), "Erreur d'ajout", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idClient = (int) tableModel.getValueAt(selectedRow, 0);
            JTextField nomF = new JTextField((String) tableModel.getValueAt(selectedRow, 1), 20);
            JTextField prenomF = new JTextField((String) tableModel.getValueAt(selectedRow, 2), 20);
            JTextField emailF = new JTextField((String) tableModel.getValueAt(selectedRow, 3), 20);
            JTextField telephoneF = new JTextField((String) tableModel.getValueAt(selectedRow, 4), 15);
            JTextField adrF = new JTextField((String) tableModel.getValueAt(selectedRow, 5), 30);

            Object[] fields = {
                    "Nom:", nomF,
                    "Prénom:", prenomF,
                    "Email:", emailF,
                    "Téléphone:", telephoneF,
                    "Adresse:", adrF
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Client", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String nom = nomF.getText().trim();
                    String prenom = prenomF.getText().trim();
                    String email = emailF.getText().trim();
                    String telephone = telephoneF.getText().trim();
                    String adresse = adrF.getText().trim();

                    if (nom.isEmpty() || prenom.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Veuillez remplir les champs !!!! >:(", "Erreur de modification", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Client client = new Client();
                    client.setIdClient(idClient);
                    client.setNom(nom);
                    client.setPrenom(prenom);
                    client.setEmail(email);
                    client.setTelephone(telephone);
                    client.setAdresse(adresse);

                    clientService.modifierClient(client);
                    preparer_listes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Problim di moudificatioun :  " + ex.getMessage(), "Erreur de modification", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionne un client asahbi", "Erreur de modification", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idClient = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you wanna delete this mf ?", "Confirmer suppression", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    clientService.supprimerClient(idClient);
                    preparer_listes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Problème de suppression :  " + ex.getMessage(), "Erreur de suppression", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionne un client asahbi", "Erreur de suppression", JOptionPane.ERROR_MESSAGE);
        }
    }
}