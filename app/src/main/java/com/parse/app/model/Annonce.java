package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Annonce")
public class Annonce extends ParseObject{

    public String getMessage() {
        return getString("message");
    }
    public void setMessage(String message) {
        put("message", message);
    }
    public ParseUser getAuteur() {
        return getParseUser("auteur");
    }
    public void setAuteur(ParseUser auteur) {
        put("auteur", auteur);
    }
}
