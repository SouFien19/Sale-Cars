package com.example.sale_cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseRef;
    private List<Car> mCars;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Database Reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Cars");

        // Initialize Adapter
        mCars = new ArrayList<>();
        mAdapter = new MyAdapter(mCars);
        mRecyclerView.setAdapter(mAdapter);

        // Add ValueEventListener to listen for changes in the database
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCars.clear(); // Clear existing data
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Car car = childSnapshot.getValue(Car.class);
                    if (car != null) {
                        mCars.add(car); // Add car to the list
                    }
                }
                mAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(MainActivity.this, "Failed to load cars.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter for the RecyclerView
    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Car> mCars;

        public MyAdapter(List<Car> cars) {
            mCars = cars;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Car car = mCars.get(position);

            // Display car details in your UI
            holder.mCarTitleTextView.setText(car.getCarTitle());
            holder.mCarDescriptionTextView.setText(car.getCarDescription());
            // You can add more fields as needed

            // Load image using Glide
            Glide.with(holder.itemView.getContext())
                    .load(car.getImageUrl())
                    .placeholder(R.drawable.car_icon) // Placeholder image
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mCars.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView mCarTitleTextView;
            public TextView mCarDescriptionTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.imageView);
                mCarTitleTextView = itemView.findViewById(R.id.carTitleTextView);
                mCarDescriptionTextView = itemView.findViewById(R.id.carDescriptionTextView);
                mImageView.setAdjustViewBounds(true);
            }
        }
    }
}
