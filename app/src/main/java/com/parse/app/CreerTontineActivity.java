package com.parse.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class CreerTontineActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private EditText mNom;
    private EditText mVille;
    private EditText mMontant;
    private EditText mNbBeneficiaire;
    private EditText mDescription;
    private Spinner mJour;
    String date;
    private ProgressDialog progressDialog;
    private boolean status = false;
    public static int TYPE_NOT_CONNECTED = 0;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_tontine);
        context = this;
        date = DateFormat.getDateTimeInstance().format(new Date());
        mNom = (EditText)findViewById(R.id.nom);
        mVille = (EditText)findViewById(R.id.ville_tontine);
        mMontant = (EditText)findViewById(R.id.montant);
        mJour =(Spinner)findViewById(R.id.jour_tontine);
        mNbBeneficiaire = (EditText)findViewById(R.id.nb_beneficiaire);
        mDescription = (EditText)findViewById(R.id.description);
        /*String[] values = { "Tous les jours", "Lundi","Mardi","Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(this,R.layout.spinner_item, values);
        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mJour.setAdapter(LTRadapter);*/
        setTitle("Créer une Tontine");
    }
    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("En cours de création ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_creer_tontine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_done) {
                createTontineService();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void createTontineService(){
        final Tontine tontine = new Tontine();
        final Membre membre = new Membre();
        String nom = mNom.getText().toString();
        String ville = mVille.getText().toString();
        Integer montant = Integer.parseInt(mMontant.getText().toString());
        Integer nbBeneficiaire = Integer.parseInt(mNbBeneficiaire.getText().toString());
        String jourCotisation = String.valueOf(mJour.getSelectedItem());
        String description = mDescription.getText().toString();
        final ParseUser president = ParseUser.getCurrentUser();

        if(!nom.isEmpty() && !ville.isEmpty() && !montant.toString().isEmpty() && !nbBeneficiaire.toString().isEmpty() && !jourCotisation.isEmpty()
                && !description.isEmpty()) {
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else {
                initprogress();
                tontine.setNom(nom);
                tontine.setville(ville);
                tontine.setMontantCotisation(montant);
                tontine.setNbBeneficiaire(nbBeneficiaire);
                tontine.setJourCotisation(jourCotisation);
                tontine.setDescription(description);
                tontine.setNbMembre(1);
                tontine.setSessionStatu("close");
                tontine.setPresident(president);
                tontine.setCreation_date(date);
                tontine.pinInBackground();
                tontine.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("tontine", "created.");
                            String tontineChannel = "idjangui" + tontine.getObjectId();
                            ParsePush.subscribeInBackground(tontineChannel, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseInstallation.getCurrentInstallation().saveInBackground();
                                        Log.d("com.parse.push", "successfully subscribed to the tontine.");
                                        //Toast.makeText(context, "successfully subscribed to the tontine.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e("com.parse.push", "failed to subscribe for push with error = " + e.getMessage());
                                        //Toast.makeText(context, "failed to subscribe for push with error = " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            String presidentChannel = "idjangui" + tontine.getObjectId() + president.getObjectId();
                            ParsePush.subscribeInBackground(presidentChannel, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseInstallation.getCurrentInstallation().saveInBackground();
                                        Log.d("com.parse.push", "successfully subscribed to the presidentChannel.");
                                        //Toast.makeText(context, "successfully subscribed to the presidentChannel.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e("com.parse.push", "failed to subscribe for push presidentChannel with error = " + e.getMessage());
                                        //Toast.makeText(context, "failed to subscribe for push presidentChannel with error = " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            ParseRelation<ParseObject> relation = president.getRelation("AdherantTontine");
                            relation.add(tontine);
                            president.saveInBackground();
                            membre.setTontine(tontine);
                            membre.setResponsable(president);
                            membre.setAdherant(president);
                            membre.setDateInscription(date);
                            membre.pinInBackground();
                            membre.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("membre", "successfully create membre.");
                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereNotEqualTo("user",president);
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
                                        push.setChannel("idjangui");
                                        push.setMessage("Nouvelle tontine crée : " + tontine.getNom());
                                        push.sendInBackground(new SendCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null){
                                                    Log.d("push","all user are aweard!");
                                                }else{
                                                    Log.d("push","error " + e.getMessage());
                                                }
                                            }
                                        });
                                        progressDialog.dismiss();
                                        Intent i = new Intent(context, MainActivity.class);
                                        startActivity(i);

                                    } else {
                                        Log.d("membre", "Fail to create membre." + e.getMessage());
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Log.d("tontine", "Fail to create tontine." + e.getMessage());
                            Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                });
            }
        }else{
            Toast.makeText(this,"Champs manquants", Toast.LENGTH_SHORT).show();
        }

    }
}
