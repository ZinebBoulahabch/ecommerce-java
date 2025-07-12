package com.ecommerce.vue;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.ecommerce.model.Client;
import com.ecommerce.model.Commande;
import com.ecommerce.model.Produit;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.service.ClientService;
import com.ecommerce.service.CommandeService;
import com.ecommerce.service.ProduitService;

public class CommandeVue extends JPanel {
    private final CommandeService commandeService;
    private final ClientService clientService;
    private final ProduitService produitService;
    private JTable table;
    private DefaultTableModel tableModel;
    private final Utilisateur loggedInUser;

    public CommandeVue(Utilisateur loggedInUser) {
        this.loggedInUser = loggedInUser;
        commandeService = new CommandeService();
        clientService = new ClientService();
        produitService = new ProduitService();
        Interface();
    }
//créer interface commande
    private void Interface() {
        setLayout(new BorderLayout(10, 10));

// créer tables 
        tableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Client", "Total", "Statut"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

//boutons de gestions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

//acions boutons
        addButton.addActionListener(e -> ajouterCommande());
        updateButton.addActionListener(e -> {
            if ("Vendeur".equals(loggedInUser.getRole())) {
                modifier_statut_Commande();
            } else {
                modifierCommande();
            }
        });
        deleteButton.addActionListener(e -> supprimerCommande());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);

        if ("Admin".equals(loggedInUser.getRole())) {
            buttonPanel.add(deleteButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        preparer_commandes();
    }

    private void preparer_commandes() {
        try {
            List<Commande> commandes = commandeService.afficherCommandes();
            tableModel.setRowCount(0);
            for (Commande commande : commandes) {
                String clientName = (commande.getClient() != null) 
                    ? commande.getClient().getNom() + " " + (commande.getClient().getPrenom() != null ? commande.getClient().getPrenom() : "")
                    : "N/A";
                tableModel.addRow(new Object[]{
                        commande.getIdCommande(),
                        commande.getDateCommande(),
                        clientName,
                        commande.getTotal(),
                        commande.getStatut()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des commandes: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterCommande() {
        try {
            List<Client> clients = clientService.afficherClients();
            List<Produit> produits = produitService.afficherProduits();

            if (clients.isEmpty() || produits.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Données vides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
            DefaultListModel<Produit> produitModel = new DefaultListModel<>();
            for (Produit produit : produits) {
                produitModel.addElement(produit);
            }
            JList<Produit> produitList = new JList<>(produitModel);
            produitList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JTextField totalField = new JTextField(10);
            totalField.setEditable(false); // Rendre le champ total non éditable
            JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En attente", "Expediée", "Annulée"});

            // Panel for quantity inputs
            Map<Produit, JTextField> quantityFields = new HashMap<>();
            JPanel quantityPanel = new JPanel(new GridLayout(produits.size(), 2, 5, 5));
            for (Produit produit : produits) {
                quantityPanel.add(new JLabel(produit.getNom() + " (Prix: " + produit.getPrix() + " DH):"));
                JTextField qtyField = new JTextField("0", 5);
                quantityFields.put(produit, qtyField);
                quantityPanel.add(qtyField);
            }

            // Ajouter un listener pour calculer automatiquement le total
            ActionListener calculateTotalListener = e -> {
                double total = 0.0;
                for (Produit produit : produits) {
                    try {
                        int quantity = Integer.parseInt(quantityFields.get(produit).getText().trim());
                        if (quantity > 0) {
                            total += produit.getPrix() * quantity;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignorer les valeurs non numériques
                    }
                }
                totalField.setText(String.format(Locale.US, "%.2f", total));
            };

            // Ajouter le listener à tous les champs de quantité
            for (JTextField qtyField : quantityFields.values()) {
                qtyField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) { calculateTotalListener.actionPerformed(null); }
                    @Override
                    public void removeUpdate(DocumentEvent e) { calculateTotalListener.actionPerformed(null); }
                    @Override
                    public void changedUpdate(DocumentEvent e) { calculateTotalListener.actionPerformed(null); }
                });
            }

            Object[] fields = {
                    "Client:", clientCombo,
                    "Produits:", new JScrollPane(produitList),
                    "Quantités:", new JScrollPane(quantityPanel),
                    "Total:", totalField,
                    "Statut:", statutCombo
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Commande", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String totalText = totalField.getText().trim();
                // Nouvelle logique : sélectionner tous les produits dont la quantité > 0
                List<Produit> selectedProduits = new ArrayList<>();
                for (Produit produit : produits) {
                    String qtyText = quantityFields.get(produit).getText().trim();
                    int quantity = 0;
                    try {
                        quantity = Integer.parseInt(qtyText);
                    } catch (NumberFormatException ex) {
                        // ignorer
                    }
                    if (quantity > 0) {
                        selectedProduits.add(produit);
                    }
                }

                if (selectedProduits.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir une quantité positive pour au moins un produit", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double total;
                try {
                    total = Double.parseDouble(totalText);
                    if (total <= 0) {
                        JOptionPane.showMessageDialog(this, "Le total doit être supérieur à 0", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Total invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier les quantités et le stock pour les produits sélectionnés
                for (Produit produit : selectedProduits) {
                    String qtyText = quantityFields.get(produit).getText().trim();
                    int quantity;
                    try {
                        quantity = Integer.parseInt(qtyText);
                        if (quantity <= 0) {
                            JOptionPane.showMessageDialog(this, "La quantité pour " + produit.getNom() + " doit être positive", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (quantity > produit.getQuantiteStock()) {
                            JOptionPane.showMessageDialog(this, "Stock insuffisant pour " + produit.getNom() + " (Stock: " + produit.getQuantiteStock() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Quantité invalide pour " + produit.getNom(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                Commande commande = new Commande();
                commande.setDateCommande(new Date());
                commande.setClient((Client) clientCombo.getSelectedItem());
                commande.setTotal(total);
                commande.setStatut((String) statutCombo.getSelectedItem());
                commande.setListeProduits(new ArrayList<>(selectedProduits));
                commandeService.ajouterCommande(commande);
                preparer_commandes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void modifier_statut_Commande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
                Commande existingCommande = commandeService.afficherCommandes().stream()
                        .filter(c -> c.getIdCommande() == idCommande)
                        .findFirst()
                        .orElse(null);

                if (existingCommande == null) {
                    JOptionPane.showMessageDialog(this, "Commande introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En attente", "Expediée", "Annulée"});
                statutCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4));

                Object[] fields = {
                        "Statut:", statutCombo
                };

                int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Statut de la Commande", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    existingCommande.setStatut((String) statutCombo.getSelectedItem());
                    commandeService.modifierCommande(existingCommande);
                    preparer_commandes();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification du statut de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierCommande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
                List<Client> clients = clientService.afficherClients();
                List<Produit> produits = produitService.afficherProduits();

                if (clients.isEmpty() || produits.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Aucun client ou produit disponible. Vérifiez la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
                DefaultListModel<Produit> produitModel = new DefaultListModel<>();
                for (Produit produit : produits) {
                    produitModel.addElement(produit);
                }
                JList<Produit> produitList = new JList<>(produitModel);
                produitList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                JTextField totalField = new JTextField(10);
                totalField.setEditable(false); // Rendre le champ total non éditable
                JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En attente", "Expediée", "Annulée"});
                String statutValue = tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "En attente";
                statutCombo.setSelectedItem(statutValue);

                // Panel for quantity inputs
                Map<Produit, JTextField> quantityFields = new HashMap<>();
                JPanel quantityPanel = new JPanel(new GridLayout(produits.size(), 2, 5, 5));
                for (Produit produit : produits) {
                    quantityPanel.add(new JLabel(produit.getNom() + ":"));
                    JTextField qtyField = new JTextField("0", 5);
                    quantityFields.put(produit, qtyField);
                    quantityPanel.add(qtyField);
                }

                // Ajouter un listener pour calculer automatiquement le total
                ActionListener calculateTotalListener = e -> {
                    double total = 0.0;
                    for (Produit produit : produits) {
                        try {
                            int quantity = Integer.parseInt(quantityFields.get(produit).getText().trim());
                            if (quantity > 0) {
                                total += produit.getPrix() * quantity;
                            }
                        } catch (NumberFormatException ex) {
                            // Ignorer les valeurs non numériques
                        }
                    }
                    totalField.setText(String.format(Locale.US, "%.2f", total));
                };

                // Ajouter le listener à tous les champs de quantité
                for (JTextField qtyField : quantityFields.values()) {
                    qtyField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                        @Override
                        public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotalListener.actionPerformed(null); }
                        @Override
                        public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotalListener.actionPerformed(null); }
                        @Override
                        public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotalListener.actionPerformed(null); }
                    });
                }

                Object[] fields = {
                        "Client:", clientCombo,
                        "Produits:", new JScrollPane(produitList),
                        "Quantités:", new JScrollPane(quantityPanel),
                        "Total:", totalField,
                        "Statut:", statutCombo
                };

                int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Commande", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String totalText = totalField.getText().trim();
                    List<Produit> selectedProduits = produitList.getSelectedValuesList();

                    if (selectedProduits.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un produit", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double total;
                    try {
                        total = Double.parseDouble(totalText);
                        if (total <= 0) {
                            JOptionPane.showMessageDialog(this, "Le total doit être supérieur à 0", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Total invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    for (Produit produit : selectedProduits) {
                        String qtyText = quantityFields.get(produit).getText().trim();
                        int quantity;
                        try {
                            quantity = Integer.parseInt(qtyText);
                            if (quantity <= 0) {
                                JOptionPane.showMessageDialog(this, "La quantité pour " + produit.getNom() + " doit être positive", "Erreur", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (quantity > produit.getQuantiteStock()) {
                                JOptionPane.showMessageDialog(this, "Stock insuffisant pour " + produit.getNom() + " (Stock: " + produit.getQuantiteStock() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Quantité invalide pour " + produit.getNom(), "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    Commande commande = new Commande();
                    commande.setIdCommande(idCommande);
                    commande.setDateCommande(new Date());
                    commande.setClient((Client) clientCombo.getSelectedItem());
                    commande.setTotal(total);
                    commande.setStatut((String) statutCombo.getSelectedItem());
                    commande.setListeProduits(new ArrayList<>(selectedProduits));
                    commandeService.modifierCommande(commande);
                    preparer_commandes();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerCommande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette commande ?", "Confirmer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    commandeService.supprimerCommande(idCommande);
                    preparer_commandes();
                } catch (SQLException ex) {
                    if (ex.getMessage() != null && ex.getMessage().contains("expédiée")) {
                        JOptionPane.showMessageDialog(this, "Impossible de supprimer une commande expédiée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}