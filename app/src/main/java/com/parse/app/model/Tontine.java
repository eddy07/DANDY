package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Tontine")
public class Tontine extends ParseObject {

    public ParseFile getLogo() {
        return getParseFile("logo");
    }

    public void setLogo(ParseFile logo) {
        put("logo", logo);
    }

    public String getNom() {
        return getString("nom");
    }

    public void setNom(String nom) {
        put("nom", nom);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
       put("description", description);
    }

    public Integer getNbMembre() {
        return getInt("nbMembre");
    }

    public void setNbMembre(Integer nbMembre) {
        put("nbMembre", nbMembre);
    }

    public String getCreation_date() {
        return getString("date_creation");
    }

    public void setCreation_date(String creation_date) {
        put("date_creation", creation_date);
    }

    public ParseUser getPresident() {
        return getParseUser("president");
    }

    public void setPresident(ParseUser president) {
        put("president", president);
    }

    public void setMontantCotisation(Integer montant) {
        put("montant_cotisation", montant);
    }

    public Integer getMontantCotisation() {
        return getInt("montant_cotisation");
    }

    public void setJourCotisation(String jour) {
        put("jour_cotisation", jour);
    }

    public String getJourCotisation() {
        return getString("jour_cotisation");
    }

    public void setNbBeneficiaire(Integer nbBeneficiaire) {
        put("nb_beneficiaire", nbBeneficiaire);
    }

    public String getville() {
        return getString("ville");
    }

    public void setville(String ville) {
        put("ville", ville);
    }

    public Integer getNbBeneficiaire() {
        return getInt("nb_beneficiaire");
    }

    public String getSessionStatu(){return getString("session_statu");}

    public void setSessionStatu(String statu){put("session_statu",statu);}
}
