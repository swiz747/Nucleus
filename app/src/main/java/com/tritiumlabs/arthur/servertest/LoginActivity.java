package com.tritiumlabs.arthur.servertest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public static MyXMPP xmppConnection;
    EditText userName;
    EditText passwordText;
    Button btnLogin;
    TextView signupLink;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText)findViewById(R.id.input_email);
        passwordText = (EditText)findViewById(R.id.input_password);
        btnLogin = (Button)findViewById(R.id.btn_login);
        signupLink = (TextView)findViewById(R.id.link_signup);

        //TODO: add option to remain logged in/  or remember username and PW

        //TODO: this is for easy testing because im lazy -AB

        //userName.setText("phoneapp");
        //passwordText.setText("derpass747");
        userName.setText("tester");
        passwordText.setText("fuck123");

        xmppConnection = MyXMPP.getInstance(this);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);

            }
        });
        Log.d("OVERHERE", "test to see my message FFS");
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("validation fail");
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);


        final String user = userName.getText().toString();
        final String password = passwordText.getText().toString();
        xmppConnection.setLoginCreds(user, password);

        //TODO make login system more robust -AB

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params)
            {

                MyService.xmpp.connect("login");
                return MyService.xmpp.getLoggedIn();
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                progressDialog.dismiss();
                if(result)
                {
                    onLoginSuccess(user,password);
                }
                else
                {
                    onLoginFailed("Bad Credentials");
                }
            }
        };
        connectionThread.execute();
    }




    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String user, String password) {


        btnLogin.setEnabled(true);
        //TODO add username and password to DB -AB
        MyXMPP.dbHandler.setUserName(user);
        MyXMPP.dbHandler.setUserPassword(password);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String why) {
        Toast.makeText(getBaseContext(), "Login failed:" + why, Toast.LENGTH_LONG).show();

        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = userName.getText().toString();


        if (email.isEmpty() ) {
            userName.setError("enter a valid username");
            valid = false;
        } else {
            userName.setError(null);
        }


        return valid;
    }
}
