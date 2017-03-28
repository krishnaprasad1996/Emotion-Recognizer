package com.reality.escape.emonaly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reality.escape.emonaly.R;


public class SignUp extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText reenterPassword;
    private Button button;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private  ProgressBar progressBar;
    private boolean passwordMatchFlag=false;
    private FirebaseDatabase database;
    private String contactName;

    @Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_signup);
        name=(EditText)findViewById(R.id.signup_name);
        email=(EditText)findViewById(R.id.signup_email);
        password=(EditText)findViewById(R.id.signup_password);
        reenterPassword=(EditText)findViewById(R.id.signup_reenter_password);
        progressBar=(ProgressBar)findViewById(R.id.signup_progressbar);
        button=(Button)findViewById(R.id.upload_button);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                checkForNull();
                if(name.getText().toString().length()>0 && email.getText().toString().length()>0 && password.getText().toString().length()>0 && reenterPassword.getText().toString().length()>0 ){
                   if(passwordMatchFlag) {
                       signupFirebase();
                   }
                }
            }
        });
        
    }

    private void signupFirebase() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "SignUp failed. Try Again",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Vex-Life",task.getException().toString());
                        }
                        else{
                            contactName = name.getText().toString();
                            DatabaseReference myRef = database.getReference(name.getText().toString());
                            myRef.child("email").setValue(email.getText().toString());
                            Log.d("SignUp", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Intent intent = new Intent(SignUp.this,MainActivity.class);
                            intent.putExtra("name",name.getText().toString());
                            startActivity(intent);
                            finish();
                        }


                    }
                });
    }

    private void checkForNull() {
        if(!(password.getText().toString().equals(reenterPassword.getText().toString()))){
            password.setError("Password didn't match");
            reenterPassword.setError("Password didn't match");
            progressBar.setVisibility(View.INVISIBLE);
        }
        else{
            passwordMatchFlag=true;
        }

        if(name.getText().toString().length()==0){
            name.setError("Name is Mandatory");
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(email.getText().toString().length()==0){
            email.setError("Email is Mandatory");
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(password.getText().toString().length()==0){
            password.setError("Password is Mandatory");
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(reenterPassword.getText().toString().length()==0){
            reenterPassword.setError("Password is Mandatory");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
