package com.ecommerce.service;

import com.ecommerce.dao.ClientDAO;
import com.ecommerce.model.Client;

import java.sql.SQLException;
import java.util.List;
// Ici on déclare les méthodes & objet DAO qu'ont utilisera dans les fichiers DAO
//Meme chose pour tout les autres fichiers services
public class ClientService {
    private ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public void ajoutClient(Client client) throws SQLException {
        clientDAO.ajoutClient(client);
    }

    public void modifierClient(Client client) throws SQLException {
        clientDAO.modifierClient(client);
    }

    public void supprimerClient(int idClient) throws SQLException {
        clientDAO.supprimerClient(idClient);
    }

    public List<Client> afficherClients() throws SQLException {
        return clientDAO.afficherClients();
    }
}