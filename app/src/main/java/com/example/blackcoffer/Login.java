package com.example.blackcoffer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextView Sign;
    TextInputLayout username, password;
    Button login_btn;
    ImageButton google_btn;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        createRequest();

        Sign = findViewById(R.id.SignUpTXT);
        username = findViewById(R.id.usernameLogin);
        password = findViewById(R.id.passwordLogin);
        login_btn = findViewById(R.id.login_btn);
        google_btn = findViewById(R.id.Google);

        Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

         google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {

                private Boolean ValidateUsername()
                {
                    String val = username.getEditText().getText().toString();

                    if (val.isEmpty()) {
                        username.setError("Field cannot be empty");
                        return false;
                    } else {
                        username.setError(null);
                        username.setErrorEnabled(false);
                        return true;
                    }
                }

                private Boolean ValidatePassword() {

                String val = password.getEditText().getText().toString();

                if (val.isEmpty()) {
                    password.setError("Field cannot be empty");
                    return false;
                } else {
                    password.setError(null);
                    password.setErrorEnabled(false);
                    return true;
                }
            }
            @Override
            public void onClick(View v) {

                Toast.makeText(Login.this, "Please wait few second's", Toast.LENGTH_SHORT).show();
                if (!ValidateUsername()|!ValidatePassword()) {
                    return;
                } else {
                    final String userEnteredName = username.getEditText().getText().toString().trim();
                    final String userEnteredPhone = password.getEditText().getText().toString().trim();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

                    Query checkUser = reference.orderByChild("username").equalTo(userEnteredName);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                            if (datasnapshot.exists()) {

                                username.setError(null);
                                username.setErrorEnabled(false);

                                String phoneFromDB = datasnapshot.child(userEnteredName).child("password").getValue(String.class);

                                if (phoneFromDB.equals(userEnteredPhone)) {

                                    username.setError(null);
                                    username.setErrorEnabled(false);

                                    Intent intent = new Intent(getApplicationContext(), EditTag.class);

                                    startActivity(intent);

                                    Toast.makeText(Login.this, "Thanks for login", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(Login.this, "may be password is incorrect", Toast.LENGTH_SHORT).show();
                                    password.setError("Please check your password");
                                    password.requestFocus();
                                }
                            } else {
                                Toast.makeText(Login.this, "may be wrong username name", Toast.LENGTH_SHORT).show();
                                username.setError("Please check your username");
                                username.requestFocus();
                            }
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    });
  }

    private void createRequest() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), EditTag.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Login.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
}

