 package com.tritiumlabs.arthur.nucleus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

 public class SignupActivity extends AppCompatActivity {
     //TODO PLEASE someone with enough autism make this shit line up like its supposed to -AB
     private static final String TAG = "SignupActivity";
     private static final int REQUEST_LOGIN = 0;
     AccountManager accountManager;
     EditText nameText;
     EditText userEmail;
     EditText passwordText;
     Button btnSignup;
     TextView loginLink;
     Set<String> derp;



     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_signup);
         nameText = (EditText) findViewById(R.id.input_name);
         userEmail = (EditText) findViewById(R.id.input_email);
         passwordText = (EditText) findViewById(R.id.input_password);
         btnSignup = (Button) findViewById(R.id.btn_signup);
         loginLink = (TextView) findViewById(R.id.link_login);


         //TODO: this is for easy testing because im lazy -AB
         nameText.setText("tester");
         userEmail.setText("Dildo@gmail.com");
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

     @Override
     public void onDestroy() {
         super.onDestroy();
         MyXMPP.disconnect();
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


         final String name = nameText.getText().toString();
         final String email = userEmail.getText().toString();
         final String password = passwordText.getText().toString();
         final Map<String, String> attributes = new HashMap<>();
         attributes.put("email", email);



         // TODO: Move method to authenticator class for organization -AB
         // TODO: make signup shorter the delay is arbitrary right now -AB

         AsyncTask<Void, Void, Boolean> signupThread = new AsyncTask<Void, Void, Boolean>() {
             @Override
             protected void onPreExecute()
             {

                 progressDialog.setIndeterminate(true);
                 progressDialog.setMessage("Creating Account...");
                 progressDialog.show();
             }


             @Override
             protected Boolean doInBackground(Void... params) {

                 MyXMPP.connect("Signup");
                 accountManager = AccountManager.getInstance(MyXMPP.connection);
                 accountManager.sensitiveOperationOverInsecureConnection(true);

                 try {
                     accountManager.createAccount(name, password, attributes);
                 } catch (XMPPException e1) {
                     Log.d(e1.getMessage(), String.valueOf(e1));
                     return false;
                 } catch (SmackException.NotConnectedException e) {
                     e.printStackTrace();
                     return false;
                 } catch (SmackException.NoResponseException e) {
                     e.printStackTrace();
                     return false;
                 }
                 return true;
             }

             @Override
             protected void onPostExecute(Boolean result)
             {
                 progressDialog.dismiss();
                 if (result)
                 {
                     onSignupSuccess(name);
                 }
                 else
                 {

                 }
             }
         };
         signupThread.execute();
     }


     public void onSignupSuccess(String name) {
         btnSignup.setEnabled(true);
         setResult(RESULT_OK, null);


         Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
         intent.putExtra("signupName", name);
         setResult(RESULT_OK, intent);
         startActivity(intent);
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
         String email = userEmail.getText().toString();
         String password = passwordText.getText().toString();

         if (name.isEmpty() || name.length() < 3) {
             nameText.setError("at least 3 characters");
             valid = false;
         } else {
             nameText.setError(null);
         }

         if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
             userEmail.setError("enter a valid email address");
             valid = false;
         } else {
             userEmail.setError(null);
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