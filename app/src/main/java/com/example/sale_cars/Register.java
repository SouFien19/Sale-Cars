package com.example.sale_cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private Button register;
    private EditText name,email,password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=findViewById(R.id.signup_button);
        name=findViewById(R.id.editTextName);
        email=findViewById(R.id.editTextNewEmailAddress);
        password=findViewById(R.id.editTextNewPassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Pass = password.getText().toString().trim();

                if (TextUtils.isEmpty(Name)) {
                    name.setError("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(Email)) {
                    email.setError("email is required");
                    return;
                }

                if (Pass.length() < 6) {
                    password.setError("Password must be at least 6 characters");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(Email, Pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String uid = currentUser.getUid();

                                    // Save user information in Firebase Realtime Database
                                    HashMap<String, String> userMap = new HashMap<>();
                                    userMap.put("Name", Name);
                                    userMap.put("Email", Email);

                                    mDatabase.child(uid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                                Intent i= new Intent(Register.this, Login.class);
                                                startActivity(i);
                                            } else {
                                                Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });



    }

}