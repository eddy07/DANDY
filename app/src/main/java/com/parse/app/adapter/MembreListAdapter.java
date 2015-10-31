package com.parse.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Membre;

import java.util.ArrayList;
import java.util.List;

public class MembreListAdapter extends ArrayAdapter<Membre>{
    private final Context context;
    private List<Membre> values = new ArrayList<Membre>();

    public MembreListAdapter(Context context, List<Membre> values) {
        super(context, R.layout.membre_item, values);
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
                rowView = inflater.inflate(R.layout.membre_item, parent, false);
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            final TextView nom = (TextView) rowView.findViewById(R.id.nom);
            final TextView montant = (TextView) rowView.findViewById(R.id.montant);
            final Membre membre = values.get(position);
            membre.fetchIfNeededInBackground(new GetCallback<Membre>() {
                @Override
                public void done(Membre parseObject, ParseException e) {
                    ParseUser adherant = parseObject.getAdherant();
                    adherant.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseObject, ParseException e) {
                            if(parseObject.getUsername().equals(ParseUser.getCurrentUser())){
                                nom.setText("Moi");
                            }else{
                                nom.setText(parseObject.getUsername());
                            }
                        }
                    });
                    montant.setText("");
                }
            });

            return rowView;
	    }
    public List<Membre> getValuesList() {
        return values;
    }

    public void setItemList(List<Membre> valuesList) {
        this.values = valuesList;
    }

}
