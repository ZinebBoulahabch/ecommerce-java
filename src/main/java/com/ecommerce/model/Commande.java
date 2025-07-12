package com.ecommerce.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Commande")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCommande;

    @Column(name = "dateCommande", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCommande;

    @ManyToOne
    @JoinColumn(name = "idClient")
    private Client client;

    @Column(name = "total", nullable = false)
    private double total;

    @Column(name = "statut", nullable = false)
    private String statut;

    @ManyToMany
    @JoinTable(
        name = "Commande_Produit",
        joinColumns = @JoinColumn(name = "idCommande"),
        inverseJoinColumns = @JoinColumn(name = "idProduit")
    )
    private List<Produit> listeProduits = new ArrayList<>();

    public Commande() {}

    public Commande(Date dateCommande, Client client, double total, String statut) {
        this.dateCommande = dateCommande;
        this.client = client;
        this.total = total;
        this.statut = statut;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<Produit> getListeProduits() {
        return listeProduits;
    }

    public void setListeProduits(List<Produit> listeProduits) {
        this.listeProduits = listeProduits;
    }
}