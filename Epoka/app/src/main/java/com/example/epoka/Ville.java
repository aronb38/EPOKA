package com.example.epoka;

public class Ville {
    private int id;
    private String nom;
    private String codePostal;

    public Ville(String nom, String codePostal) {
        this.id = id;
        this.nom = nom;
        this.codePostal = codePostal;
    }

    public String getNom() {
        return nom;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nom + " (" + codePostal + ")";
    }
}
