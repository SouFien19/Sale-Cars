package com.example.sale_cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText email,pass;
    private Button signup;
    private Button login;
    FirebaseAuth fAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.editTextTextEmailAddress);
        pass=findViewById(R.id.editTextTextPassword);
        signup=findViewById(R.id.signup_button);
        login=findViewById(R.id.button);
        fAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pas = pass.getText().toString();
                if (!mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    if (!pas.isEmpty()) {
                        fAuth.signInWithEmailAndPassword(mail, pas)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this, "Login . ", Toast.LENGTH_SHORT).show();
                                        Intent i= new Intent(Login.this, MainActivity.class);
                                        startActivity(i);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this, " Failed. ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });





    }
}