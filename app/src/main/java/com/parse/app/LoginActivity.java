package com.parse.app;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.app.utilities.NetworkUtil;

import me.drakeet.materialdialog.MaterialDialog;


public class LoginActivity extends ActionBarActivity {

    private EditText mPassword;
    private EditText mEmail;
    private ImageButton mSendBtn;
    private Button loginBtn;
    private EditText mUsername;
    private String password;
    private String email;
    private String username;
    private MaterialDialog materialDialog;
    private ProgressDialog progressDialog;
    private Context context;
    private TextView textLostPsw;
    private LinearLayout linearLayout;
    private SnackBar snackbar;
    public static int TYPE_NOT_CONNECTED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        getSupportActionBar().setTitle("Login");
        mPassword = (EditText)findViewById(R.id.password);
        mUsername = (EditText)findViewById(R.id.username);
        mEmail = (EditText)findViewById(R.id.email);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        textLostPsw = (TextView)findViewById(R.id.psw);
        linearLayout = (LinearLayout)findViewById(R.id.layoutpsw);
        textLostPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout.getVisibility()== View.VISIBLE){
                    linearLayout.setVisibility(View.GONE);
                }else{
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        mSendBtn = (ImageButton)findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    email = mEmail.getText().toString();
                    if (!email.isEmpty()) {
                        passwordReset();
                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // An email was successfully sent with reset instructions.
                                    progressDialog.dismiss();
                                    materialDialog = new MaterialDialog(context);
                                    materialDialog.setMessage("Vous allez reçevoir un mail de confirmation. Connectez-vous à votre " +
                                            "compte de messagerie et réinitialiser votre mot de passe i-djangui :) ");

                                    materialDialog.setPositiveButton("Ok", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            materialDialog.dismiss();
                                        }
                                    });
                                    materialDialog.show();

                                } else {
                                    // Something went wrong. Look at the ParseException to see what's up.
                                    progressDialog.dismiss();
                                    //Toast.makeText(context, "Error : Something went wrong", Toast.LENGTH_LONG).show();
                                    snackBar(true);
                                }
                            }
                        });
                    }else{
                        mEmail.setError("Entrer votre email!");
                    }
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                                                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                                            } else {
                                                initprogress();
                                                password = mPassword.getText().toString();
                                                username = mUsername.getText().toString();
                                                ParseUser.logInInBackground(username, password, new LogInCallback() {
                                                    public void done(ParseUser user, ParseException e) {
                                                        if ((e == null) && (user != null)) {
                                                            // Hooray! The user is logged in.
                                                            progressDialog.dismiss();
                                                            Intent i = new Intent(context, MainActivity.class);
                                                            if (android.os.Build.VERSION.SDK_INT >= 16) {
                                                                Bundle bndlanimation =
                                                                        ActivityOptions.makeCustomAnimation(
                                                                                context,
                                                                                R.anim.anim_left_right,
                                                                                R.anim.anim_right_left).toBundle();
                                                                startActivity(i, bndlanimation);
                                                                finish();
                                                            } else {
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        } else {
                                                            progressDialog.dismiss();
                                                            // Signup failed. Look at the ParseException to see what happened.
                                                            Log.d("login", "error");
                                                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            mEmail.setError("Email invalide");
                                                            mPassword.setError("username invalide");
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
        setTitle("Login");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent homeIntent= new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
    public void snackBar(boolean statu){
        if(statu == true) {
            snackbar = new SnackBar(this, "Adresse Email invalide", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }else{
            snackbar = new SnackBar(this, "Oups... somethink went wrong, please try egain !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }

        snackbar.show();
    }
    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Loging in ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void passwordReset(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Sending request ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
