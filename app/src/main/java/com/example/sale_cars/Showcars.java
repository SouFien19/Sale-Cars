package com.example.sale_cars;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Showcars extends AppCompatActivity {
    private static final String TAG = "Showcars";
    private TextView carTitleTextView;
    private TextView carDescriptionTextView;
    private ImageView carImageView;
    private Button Callbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcars);

        // Initialize views
        carTitleTextView = findViewById(R.id.carTitleTextView);
        carDescriptionTextView = findViewById(R.id.carDescriptionTextView);
        carImageView = findViewById(R.id.imageView);
        Callbtn = findViewById(R.id.Call);

        String carId = getIntent().getStringExtra("carId");
        Log.d(TAG, "carId: " + carId);

        // Set up Firebase Database Reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cars").child(carId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if data exists
                if (snapshot.exists()) {
                    // Get car details
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String title = snapshot.child("carTitle").getValue(String.class);
                    String description = snapshot.child("carDescription").getValue(String.class);

                    // Load the image using Glide
                    Glide.with(Showcars.this)
                            .load(imageUrl)
                            .into(carImageView);

                    carTitleTextView.setText(title);
                    carDescriptionTextView.setText(description);
                } else {
                    // Data doesn't exist
                    Log.d(TAG, "No data found for carId: " + carId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if data exists
                if (snapshot.exists()) {
                    Callbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the phone number from the database
                            String numero = snapshot.child("numero").getValue(String.class);
                            // Call the method to make a phone call
                            makePhoneCall(numero);
                        }
                    });
                } else {
                    // Data doesn't exist
                    Log.d(TAG, "No data found for carId: " + carId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}