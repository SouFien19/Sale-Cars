package com.example.sale_cars;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class add_cars extends AppCompatActivity {

    private EditText carTitleEditText, carDescriptionEditText, Phone;
    private ImageView carImageView;
    private Button pickImageButton, addCarButton;

    // Firebase
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cars);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cars");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Initialize views
        carTitleEditText = findViewById(R.id.carTitleEditText);
        carDescriptionEditText = findViewById(R.id.carDescriptionEditText);
        carImageView = findViewById(R.id.carImageView);
        pickImageButton = findViewById(R.id.pickImageButton);
        Phone = findViewById(R.id.editTextPhone);
        addCarButton = findViewById(R.id.addCarButton);

        // Button click listeners
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarToDatabase();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            carImageView.setImageURI(imageUri);
        }
    }

    private void addCarToDatabase() {
        final String carTitle = carTitleEditText.getText().toString().trim();
        final String carDescription = carDescriptionEditText.getText().toString().trim();
        final String carPhone = Phone.getText().toString().trim();

        if (!carTitle.isEmpty() && imageUri != null) {
            if (currentUser != null) {
                final String userId = currentUser.getUid();

                // Generate a unique key for the car
                final String carId = databaseReference.push().getKey();

                // Storage reference
                final StorageReference imageReference = storageReference.child("car_images").child(carId + ".jpg");

                // Upload image to Firebase Storage
                imageReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the download URL of the image
                                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();

                                        // Create Car object
                                        Car car = new Car(carId, userId, carTitle, carDescription, imageUrl, carPhone);
                                        // Add car to Firebase database
                                        databaseReference.child(carId).setValue(car);

                                        // Clear input fields
                                        carTitleEditText.setText("");
                                        carDescriptionEditText.setText("");
                                        carImageView.setImageResource(R.drawable.car_icon);

                                        // Inform user
                                        Toast.makeText(add_cars.this, "Car added successfully!", Toast.LENGTH_SHORT).show();
                                        Intent i= new Intent(add_cars.this, MainActivity.class);
                                        startActivity(i);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(add_cars.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            // Inform user that car title and image are required
            Toast.makeText(this, "Car title and image are required!", Toast.LENGTH_SHORT).show();
        }
    }
}
