package com.ecommerce;

    import javax.swing.SwingUtilities;

import com.ecommerce.vue.PagePrincipale;
import com.ecommerce.vue.LoginFrame;

    public class MainApp {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }