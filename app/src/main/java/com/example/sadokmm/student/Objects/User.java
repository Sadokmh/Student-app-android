package com.example.sadokmm.student.Objects;

import android.graphics.Bitmap;

public class User {

    private String _id ;
    private String nom ;
    private String prenom ;
    private String email ;
    private String img;
    private String filiere;
    private int groupe ;
    private int niveau ;

    public User(String id, String nom, String prenom, String email, String img, String filiere, int groupe, int niveau) {
        this._id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.img = img;
        this.filiere = filiere;
        this.groupe = groupe;
        this.niveau = niveau;
    }


    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public int getGroupe() {
        return groupe;
    }

    public void setGroupe(int groupe) {
        this.groupe = groupe;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

}
