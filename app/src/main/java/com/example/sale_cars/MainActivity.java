package com.example.sale_cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FirebaseAuth fAuth;
    private StorageReference mStorageRef;
    private List<ListCras> mCars;
    private View log,dec;
    private MyAdapter myAdapter;
    private View add, logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference mDatabaseRef;
        // Set up the RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        fAuth = FirebaseAuth.getInstance();
        add = findViewById(R.id.loginImageView);
        logout = findViewById(R.id.logoutImageView);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth != null) {
                    if (fAuth.getCurrentUser() != null) {
                        fAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Logout successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No user is currently logged in.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // FirebaseAuth instance is not initialized, show a message or handle it accordingly
                    Toast.makeText(getApplicationContext(), "FirebaseAuth not initialized.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() != null) {
                    try {
                        startActivity(new Intent(getApplicationContext(), add_cars.class));
                        Toast.makeText(getApplicationContext(), "Add Car", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: Unable to start activity.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        Toast.makeText(getApplicationContext(), "Login first !!!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: Unable to start activity.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Set up Firebase Storage

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Cars");

        // Set up the adapter and attach it to the RecyclerView
        mCars = new ArrayList<>();
        myAdapter = new MyAdapter(mCars);
        mRecyclerView.setAdapter(myAdapter);

        // Load the image URLs from Firebase Storage
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot carSnapshot : dataSnapshot.getChildren()) {
                    String carId = carSnapshot.getKey();
                    String imageUrl = carSnapshot.child("imageUrl").getValue(String.class);
                    String carTitle = carSnapshot.child("carTitle").getValue(String.class);
                    ListCras car = new ListCras(carId, imageUrl, carTitle);
                    mCars.add(car);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("Firebase", "Error getting car data", databaseError.toException());
            }
        });
    }

    // Adapter for the RecyclerView
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private FirebaseAuth fAuth;
        private List<ListCras> mCars;

        public MyAdapter(List<ListCras> cars) {
            mCars = cars;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            fAuth = FirebaseAuth.getInstance();
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ListCras car = mCars.get(position);
            String imageUrl = car.getImageUrl();
            String carTitle = car.getCarTitle(); // Corrected method name

            // Load the image from the URL using Glide
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.mImageView);

            // Set the car title to the TextView
            holder.textView.setText(carTitle);

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fAuth.getCurrentUser() != null) {
                        String carId = car.getCarId();
                        Toast.makeText(MainActivity.this, carId, Toast.LENGTH_SHORT).show();
                        Context context = v.getContext();
                        Intent intent = new Intent(context, Showcars.class);
                        intent.putExtra("carId", carId); // Pass the car ID
                        context.startActivity(intent);
                    } else {
                        // User not logged in, redirect to login activity
                        Context context = v.getContext();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCars.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView textView; // Add TextView for car title

            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.textView); // Initialize TextView
                mImageView.setAdjustViewBounds(true);
            }
        }
    }

}

