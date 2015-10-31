package com.parse.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;

import java.util.ArrayList;
import java.util.List;
public class MesTontinesAdapter extends ArrayAdapter<Tontine> {
    private final Context context;
    private boolean status;
    private List<Tontine> values = new ArrayList<Tontine>();

    public MesTontinesAdapter(Context context, List<Tontine> values) {
        super(context, R.layout.mestontines_item, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public void clear() {
        values.clear();
        super.clear();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ParseUser user = ParseUser.getCurrentUser();
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.mestontines_item, parent, false);
        }

        final TextView nom = (TextView) rowView.findViewById(R.id.nom_tontine);
        //TextView poste = (TextView) rowView.findViewById(R.id.poste);
        final TextView sessionStatu = (TextView) rowView.findViewById(R.id.session_statu);
        final TextView description = (TextView) rowView.findViewById(R.id.description_tontine);

        final Tontine tontine = values.get(position);
        tontine.fetchIfNeededInBackground(new GetCallback<Tontine>() {
            @Override
            public void done(Tontine parseObject, ParseException e) {
                nom.setText(parseObject.getNom());
                setFontNom(nom);
                description.setText(parseObject.getDescription());
                setFontDes(description);
                ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
                sessionParseQuery.whereEqualTo("tontine",parseObject);
                sessionParseQuery.whereEqualTo("statu","ouverte");
                sessionParseQuery.getFirstInBackground(new GetCallback<Session>() {
                    @Override
                    public void done(Session session, ParseException e) {
                        if(e==null){
                            sessionStatu.setText("session ouverte");
                            sessionStatu.setTextColor(Color.RED);
                            setFontstatu(sessionStatu);
                        }else{
                            sessionStatu.setText("");
                        }

                    }
                });

            }
        });

        /*if(isPresident(user,tontine.getObjectId())){
            poste.setText("Administrateur");
        }else{
            poste.setText("Membre");
        }*/

        return rowView;

    }
    public List<Tontine> getValuesList() {
        return values;
    }

    public void setItemList(List<Tontine> valuesList) {
        this.values = valuesList;
    }

    public boolean isPresident(ParseUser user,String tontineId){

        ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
        tontineQuery.whereEqualTo("objectId", tontineId);
        tontineQuery.whereEqualTo("president", user);
        tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
            @Override
            public void done(Tontine tontine, ParseException e) {
                if(tontine != null){
                    status = true;
                    Log.d("is president", "true");
                }else{
                    status = false;
                    Log.d("is president", "false");
                }
            }
        });
        return status;
    }

    public void setFontNom(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        tv.setTypeface(tf);
    }
    public void setFontDes(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    public void setFontstatu(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-BoldItalic.ttf");
        tv.setTypeface(tf);
    }

}
