package com.parse.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Annonce;

import java.util.ArrayList;
import java.util.List;

public class AnnonceAdapter extends ArrayAdapter<Annonce>{
    private final Context context;
    private ParseUser user;
    private Annonce annonce;
    private List<Annonce> values = new ArrayList<Annonce>();

    public AnnonceAdapter(Context context, List<Annonce> values) {
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
            TextView auteur = (TextView) rowView.findViewById(R.id.auteur);
            TextView message = (TextView) rowView.findViewById(R.id.message);
            annonce = values.get(position);

            auteur.setText(annonce.getAuteur().getUsername());
            message.setText(annonce.getMessage());
            return rowView;
	    }
    public List<Annonce> getValuesList() {
        return values;
    }

    public void setItemList(List<Annonce> valuesList) {
        this.values = valuesList;
    }

}
