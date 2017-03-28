package com.reality.escape.emonaly.Activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reality.escape.emonaly.R;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private Button login;
    private ProgressBar loginProgressBar;
    private TextView signupText;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        loginProgressBar=(ProgressBar)findViewById(R.id.login_progressbar);
        signupText=(TextView)findViewById(R.id.signup_textview);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                checkForNull();
                if(email.getText().toString().length()!=0&&password.getText().toString().length()!=0){
                    //==================FIrebase Login==================
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Log.w("TAG", "signInWithEmail:failed", task.getException());
                                        Toast.makeText(LoginActivity.this, "Sign-In Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        DatabaseReference reference = database.getReference();
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                                    for(DataSnapshot subChildSnapshot:childSnapshot.getChildren()){
                                                        if(subChildSnapshot.getValue().equals(email.getText().toString())){
                                                            name = childSnapshot.getKey();

                                                            Log.d("name",name);
                                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                            intent.putExtra("name",name);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());


                                    }

                                }
                            });
                    //==================================================
                }
            }
        });


        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (LoginActivity.this,SignUp.class);
                startActivity(intent);

            }
        });

        init();
        sessionCheck();
        setListeners();
    }

    private void sessionCheck() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                }
                // ...
            }
        };
    }

    private void checkForNull() {
        if(email.getText().toString().length()==0){
            email.setError("Email is mandatory");
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
        if(password.getText().toString().length()==0){
            password.setError("Password is mandatory");
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setListeners() {

    }



    private void init() {

        auth = FirebaseAuth.getInstance();

    }








    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);

    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }
}
