package com.ecommerce.vue;

    import com.ecommerce.model.Utilisateur;
    import com.ecommerce.service.UtilisateurService;

    import javax.swing.*;
    import java.awt.*;
    import java.sql.SQLException;
    import java.util.List;

    public class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private UtilisateurService utilisateurService;

        public LoginFrame() {
            utilisateurService = new UtilisateurService();
            Interface();
        }

        private void Interface() {
            setTitle("Authentification");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(350, 250);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel usernameLabel = new JLabel("Username :");
            usernameField = new JTextField(15);
            JLabel passwordLabel = new JLabel("Password :");
            passwordField = new JPasswordField(15);
            JButton loginButton = new JButton("Se connecter");

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(usernameLabel, gbc);
            gbc.gridy = 1;
            panel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(usernameField, gbc);
            gbc.gridy = 1;
            panel.add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(loginButton, gbc);

            add(panel);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez remplir les champs !!", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    List<Utilisateur> utilisateurs = utilisateurService.afficherUsers();
                    Utilisateur user = utilisateurs.stream()
                            .filter(u -> u.getNomUtilisateur().equals(username) && u.getMotDePasse().equals(password))
                            .findFirst()
                            .orElse(null);

                    if (user != null) {
                        dispose(); 
                        SwingUtilities.invokeLater(() -> {
                            new PagePrincipale(user).setVisible(true); 
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Username incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Sir, we have a problem..." + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }