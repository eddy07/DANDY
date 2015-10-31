package com.parse.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.app.model.Annonce;
import com.parse.app.utilities.NetworkUtil;


public class CreerAnnonceActivity extends ActionBarActivity {
    private EditText mMessage;
    private Context context;
    public static int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    ParseUser user;
    Annonce annonce;
    private boolean status = false;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_annonce);
        mMessage = (EditText)findViewById(R.id.message);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
    }

    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("en cours ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_creer_annonce, menu);
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
        }else if(id == R.id.send){
           creerannonce();
        }

        return super.onOptionsItemSelected(item);
    }

    public void creerannonce(){
        final String message = mMessage.getText().toString();
        if(!message.isEmpty()){
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else {
                initprogress();
                            annonce.setAuteur(user);
                            annonce.setMessage(message);
                            annonce.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        String tontineChannel = "idjangui" + tontineId;
                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereNotEqualTo("user",user);
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
                                        push.setChannel(tontineChannel);
                                        push.setMessage("Annonce de " + user.getUsername() + " : " + message);
                                        push.sendInBackground(new SendCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.d("annonce push", "send");
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Annonce envoyée", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Annonce non envoyée", Toast.LENGTH_SHORT).show();
                                                    Log.d("annonce push", "not send");
                                                }
                                            }
                                        });

                                    }else{
                            Log.d("Annonce", "not created with error " + e);
                        }
                    }
                });
            }
        }
    }
}
