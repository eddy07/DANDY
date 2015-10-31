package com.parse.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.melnykov.fab.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.app.asynctask.SendPushAsyncTask;
import com.parse.app.model.Tontine;
import com.parse.app.model.Invitation;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class InfoTontineActivity extends ActionBarActivity {

    private TextView mNom;
    private TextView mVille;
    private TextView mMontant;
    private TextView mNbBeneficiaire;
    private TextView mJour;
    private TextView mDescription;
    private TextView btnInvite;
    private String presidentChannel;
    private String tontineId;
    private String nom;
    private String ville;
    private String montant;
    private Integer nbBeneficiaire;
    private String jour;
    private String description;
    private String date;
    private String nomPresident;
    private String emailPresident;
    private String presidentId;
    private String invitationChannel;
    private ParseUser user;
    public static int TYPE_NOT_CONNECTED = 0;
    private Invitation invitation;
    private Tontine tontine;
    private ParseUser responsable;
    private String dateNow;
    private Context context;
    private SendPushAsyncTask sendPushAsyncTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tontine);
        context = this;
        getSupportActionBar().setTitle("Info de la tontine");
        dateNow = DateFormat.getDateTimeInstance().format(new Date());
        btnInvite = (TextView)findViewById(R.id.btnInvite);
        user = ParseUser.getCurrentUser();
        mNom = (TextView)findViewById(R.id.nomtext);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        mVille = (TextView)findViewById(R.id.villetext);
        mMontant = (TextView)findViewById(R.id.montanttext);
        mNbBeneficiaire = (TextView)findViewById(R.id.nb_beneficiairetext);
        mJour = (TextView)findViewById(R.id.jour_tontinetext);
        mDescription = (TextView)findViewById(R.id.descriptiontext);

        nom = getIntent().getExtras().getString("NOM");
        ville = getIntent().getExtras().getString("VILLE");
        montant = getIntent().getExtras().getString("MONTANT");
        nbBeneficiaire = getIntent().getExtras().getInt("NB_BENEFICIAIRE");
        jour = getIntent().getExtras().getString("JOUR");
        description = getIntent().getExtras().getString("DESCRIPTION");
        date = getIntent().getExtras().getString("DATE");
        nomPresident = getIntent().getExtras().getString("PRESIDENT_NOM");
        presidentId = getIntent().getExtras().getString("PRESIDENT_ID");
        emailPresident = getIntent().getExtras().getString("PRESIDENT_EMAIL");
        presidentChannel = "idjangui " + tontineId + presidentId;
        //Toast.makeText(context,"tontineid = " + tontineId + " presi = " + presidentId + " presichanel = " + presidentChannel,Toast.LENGTH_LONG).show();
        user = ParseUser.getCurrentUser();
        mNom.setText(nom);
        mVille.setText(ville);
        mMontant.setText(" " + montant);
        mNbBeneficiaire.setText(" "+ nbBeneficiaire);
        mJour.setText(jour);
        mDescription.setText(description);
        if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if (e == null) {

                        ParseQuery<Invitation> invitationChannel = ParseQuery.getQuery(Invitation.class);
                        invitationChannel.whereEqualTo("statu", "en attente");
                        invitationChannel.whereEqualTo("demandeur", user);
                        invitationChannel.whereEqualTo("tontine", tontine);
                        invitationChannel.getFirstInBackground(new GetCallback<Invitation>() {
                            @Override
                            public void done(Invitation invitation, ParseException e) {
                                if (e == null) {
                                    btnInvite.setText("Demande envoyé !");
                                    btnInvite.setBackgroundResource(R.drawable.bg_invite_send);
                                    btnInvite.setClickable(false);
                                } else {
                                    Log.d("Invitation", "Not found with error : " + e.getMessage());
                                }
                            }
                        });

                    } else {
                        Log.d("Tontine", "Not found with error : " + e.getMessage());
                    }
                }
            });
        }
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, "tontineId = " + tontineId, Toast.LENGTH_SHORT).show();
                    invitation = new Invitation();
                    ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
                    tontineQuery.whereEqualTo("objectId", tontineId);
                    tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                        @Override
                        public void done(final Tontine tontine, ParseException e) {
                            if ((e == null) && (tontine != null)) {
                                responsable = tontine.getPresident();
                                invitation.setTontine(tontine);
                                invitation.setDemandeur(user);
                                invitation.setStatu("en attente");
                                invitation.setDateDemande(dateNow);
                                invitation.setResponsable(responsable);
                                invitation.pinInBackground();
                                invitation.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(context, "Demande envoyée", Toast.LENGTH_SHORT).show();
                                            Log.d("Invitation", "envoyé");
                                            btnInvite.setText("Demande envoyé");
                                            btnInvite.setBackgroundResource(R.drawable.bg_invite_send);
                                            btnInvite.setClickable(false);
                                            invitationChannel = invitation.getObjectId();
                                            ParsePush.subscribeInBackground(invitationChannel, new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        ParseInstallation.getCurrentInstallation().saveInBackground();
                                                        Log.d("com.parse.push", "successfully subscribed to the channel.");
                                                    } else {
                                                        Log.e("com.parse.push", "failed to subscribe for push" + e);
                                                    }
                                                }
                                            });
                                            String alert = user.getUsername() + "Souhaite rejoindre votre Tontine : " + tontine.getNom();
                                            String channel = presidentChannel;
                                            String title = "Demande d'inscription";
                                            String userId = user.getObjectId();
                                            sendPushNotification(userId,channel,alert,title);
                                        } else {
                                            Toast.makeText(context, "Erreur lors de l'envoie", Toast.LENGTH_SHORT).show();
                                            Log.d("Invitation", "Erreur lors de l'envoie");
                                        }
                                    }
                                });

                            }

                        }
                    });
                }
            }
        });

    }

    public void sendPushNotification(String userId, String channel, String alert, String title){
        sendPushAsyncTask = new SendPushAsyncTask(userId,channel,title,alert,this);
        sendPushAsyncTask.execute((Void) null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_tontine, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
