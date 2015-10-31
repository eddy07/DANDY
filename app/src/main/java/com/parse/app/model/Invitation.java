package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 29/08/2015.
 */
@ParseClassName("Invitation")
public class Invitation extends ParseObject{
    public void setDemandeur(ParseUser user) {
        put("demandeur",user);
    }
    public ParseUser getDemandeur() {
        return getParseUser("demandeur");
    }
    public void setTontine(Tontine tontine) {
        put("tontine", tontine);
    }
    public Tontine getTontine(){return (Tontine)getParseObject("tontine");}
    public void setDateDemande(String date) {
        put("date_demande", date);
    }
    public String getDateDemande() {
        return getString("date_demande");
    }
    public void setStatu(String statu) {
        put("statu",statu);
    }
    public String getStatu() {
        return getString("statu");
    }
    public void setResponsable(ParseUser responsable) {
        put("responsable", responsable);
    }
    public ParseUser getResponsable(){return getParseUser("responsable");}
}

