package com.parse.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.app.R;
import com.parse.app.model.Tontine;

import java.util.ArrayList;
import java.util.List;
public class TontinesAdapter extends ArrayAdapter<Tontine>{
    private final Context context;
    private List<Tontine> values = new ArrayList<Tontine>();

    public TontinesAdapter(Context context, List<Tontine> values) {
        super(context, R.layout.tontines_item, values);
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

            View rowView = convertView;
            if(rowView == null){
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.tontines_item, parent, false);
            }

            final TextView nom = (TextView) rowView.findViewById(R.id.nom_tontine);
            //TextView date_creation = (TextView) rowView.findViewById(R.id.creation_date);
            final TextView nbMembre = (TextView) rowView.findViewById(R.id.nb_membre);
            final TextView description = (TextView) rowView.findViewById(R.id.description_tontine);
            final Tontine tontine = values.get(position);
            tontine.fetchIfNeededInBackground(new GetCallback<Tontine>() {
                @Override
                public void done(Tontine parseObject, ParseException e) {
                    nom.setText(parseObject.getNom());
                    setFontNom(nom);
                    description.setText(parseObject.getDescription());
                    setFontDes(description);
                    //date_creation.setText(tontine.getCreation_date());
                    nbMembre.setText(parseObject.getNbMembre() + " membres");
                    setFontNbmembre(nbMembre);
                }
            });

            return rowView;
	    }
    public List<Tontine> getValuesList() {
        return values;
    }

    public void setItemList(List<Tontine> valuesList) {
        this.values = valuesList;
    }
    public void setFontNom(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        tv.setTypeface(tf);
    }
    public void setFontDes(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    public void setFontNbmembre(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-BoldItalic.ttf");
        tv.setTypeface(tf);
    }
}
