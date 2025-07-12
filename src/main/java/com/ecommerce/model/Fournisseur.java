package com.ecommerce.model;

import javax.persistence.*;

@Entity
@Table(name = "Fournisseur")
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFournisseur;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "contact")
    private String contact;

    @Column(name = "produitsFournis")
    private String produitsFournis;

    public Fournisseur() {}

    public Fournisseur(String nom, String contact, String produitsFournis) {
        this.nom = nom;
        this.contact = contact;
        this.produitsFournis = produitsFournis;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return nom;
    }
}