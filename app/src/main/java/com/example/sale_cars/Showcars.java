package com.example.sale_cars;

import android.os.Bundle;
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
    private TextView carTitleTextView;
    private TextView carDescriptionTextView;
    private ImageView carImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcars);

        // Initialize views
        carTitleTextView = findViewById(R.id.carTitleTextView);
        carDescriptionTextView = findViewById(R.id.carDescriptionTextView);
        carImageView = findViewById(R.id.imageView);

        // Set up Firebase Database Reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Images").child("1");

        // Add ValueEventListener to listen for changes in the database
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("carTitle").getValue(String.class);
                    String description = snapshot.child("carDescription").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    // Set text and image
                    carTitleTextView.setText(title);
                    carDescriptionTextView.setText(description);
                    Glide.with(Showcars.this).load(imageUrl).into(carImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Showcars.this, "Failed to load car details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
