 package com.tritiumlabs.arthur.servertest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;


 public class SignupActivity extends AppCompatActivity
 {
    private static final String TAG = "SignupActivity";
    private static final int REQUEST_LOGIN= 0;
    public static Authenticator tempXMPPConnection;
     AccountManager accountManager;
    EditText nameText;
    EditText userName;
    EditText passwordText;
    Button btnSignup;
    TextView loginLink;




    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameText = (EditText)findViewById(R.id.input_name);
        userName = (EditText)findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        btnSignup = (Button)findViewById(R.id.btn_signup);
        loginLink = (TextView)findViewById(R.id.link_login);
        //TODO: there are now 3 places that have this hard coded, we need a global constant or something, maybe pop it in an sqlite table? - AB
        tempXMPPConnection = new Authenticator(this,"tritium","45.35.4.171",5222);
        //TODO: this is for easy testing because im lazy -AB
        nameText.setText("tester");
        userName.setText("Dildo@gmail.com");
        passwordText.setText("fuck123");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "finish" the registration screen and return to the Login activity -AB
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }


        btnSignup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = nameText.getText().toString();
        final String user = userName.getText().toString();
        final String password = passwordText.getText().toString();
        tempXMPPConnection.connect("Signup");
        accountManager = AccountManager.getInstance(tempXMPPConnection.connection);


        // TODO: Move method to authenticator class for organization -AB

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {


                        try {
                            accountManager.createAccount(name, password);
                        } catch (XMPPException e1) {
                            Log.d(e1.getMessage(), String.valueOf(e1));
                            onSignupFailed();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                            onSignupFailed();
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                            onSignupFailed();
                        }
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        btnSignup.setEnabled(true);
    }
    //TODO make logical password reqs -AB
    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = userName.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userName.setError("enter a valid email address");
            valid = false;
        } else {
            userName.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}