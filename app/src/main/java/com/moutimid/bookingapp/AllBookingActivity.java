package com.moutimid.bookingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bookingadminapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllBookingActivity extends AppCompatActivity {


    private RecyclerView ProductsRecycler;
    private com.moutimid.bookingapp.AllBookingAdapter adapter;
    private List<BookingModel> adminProducts;
    private DatabaseReference mDataBaseRef;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_booking);
        ProductsRecycler = (RecyclerView) findViewById(R.id.ProductsRecycler);
        bar = findViewById(R.id.productProgressBar);
        Dialog lodingbar = new Dialog(AllBookingActivity.this);
        lodingbar.setContentView(R.layout.loading);
        Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
        lodingbar.setCancelable(false);
        lodingbar.show();
        mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("BookingApp").child("Details");
        adminProducts = new ArrayList<>();
        adapter = new com.moutimid.bookingapp.AllBookingAdapter(getApplicationContext(), adminProducts);
        ProductsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ProductsRecycler.setAdapter(adapter);
        mDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminProducts.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    adminProducts.add(new BookingModel(snapshot1.getKey(),
                            snapshot1.child("time").getValue(String.class),
                            snapshot1.child("name").getValue(String.class),
                            snapshot1.child("contact_no").getValue(String.class),
                            snapshot1.child("buzzer_no").getValue(String.class),
                            snapshot1.child("no_of_guest").getValue(String.class),
                            snapshot1.child("booked").getValue(boolean.class),
                            snapshot1.child("seated").getValue(boolean.class)));
                }
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.INVISIBLE);
                lodingbar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lodingbar.dismiss();
            }
        });

        adapter.setOnItemClickListener(new com.moutimid.bookingapp.AllBookingAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AllBookingActivity.this).setTitle("Confirmation").setMessage("Are You Sure You Want To Delete ?!").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference reference = mDataBaseRef.child(adminProducts.get(pos).getKey());
                        reference.removeValue();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);
                dialog.show();
            }
        });

    }
}
