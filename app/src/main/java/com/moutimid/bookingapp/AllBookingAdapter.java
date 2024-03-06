package com.moutimid.bookingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.bookingadminapp.R;

import java.util.Collections;
import java.util.List;

public class AllBookingAdapter extends RecyclerView.Adapter<AllBookingAdapter.ProductViewHolder> {

    private Context context;
    private List<BookingModel> bookingModels;
    private AllBookingAdapter.onItemClickListener itemListener;
    boolean is_booked, is_seated;

    public interface onItemClickListener {
        void onItemClick(int pos);
    }

    public AllBookingAdapter(Context context, List<BookingModel> bookingModels) {
        this.context = context;
        this.bookingModels = bookingModels;
    }

    public void addList(List<BookingModel> list) {
        bookingModels.clear();
        Collections.copy(bookingModels, list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.booking_list, parent, false);

        return new ProductViewHolder(v, itemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        String key = bookingModels.get(position).getKey();
        holder.name.setText("Name: " + bookingModels.get(position).getName());
        holder.time.setText("Time: " + bookingModels.get(position).getTime());
        is_booked = bookingModels.get(position).isBooked();
        is_seated = bookingModels.get(position).isSeated();
//        holder.contact_no.setText("Contact No: "+bookingModels.get(position).getContact_no());
        holder.buzzer_no.setText("Buzzer Number: " + bookingModels.get(position).getBuzzer_no() + "  ");
        holder.no_of_guest.setText("No. of guests: " + bookingModels.get(position).getNo_of_guest() + "  ");
// Assuming contactTextView is the TextView containing the mobile number
        holder.contact_no.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.contact_no.setText("Contact No: " + bookingModels.get(position).getContact_no());
                return true; // Returning true indicates that the long click event has been consumed
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingModels.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        if (bookingModels.get(position).isBooked() && bookingModels.get(position).isSeated()) {
            holder.booked.setChecked(true);
            holder.seated.setChecked(true);
            holder.PrContainer.setBackgroundColor(Color.parseColor("#8BC34A"));
        } else if (!bookingModels.get(position).isBooked() && bookingModels.get(position).isSeated()) {
            holder.booked.setChecked(false);
            holder.seated.setChecked(true);
            holder.PrContainer.setBackgroundColor(Color.parseColor("#ECE131"));
        } else if (bookingModels.get(position).isBooked() && !bookingModels.get(position).isSeated()) {
            holder.booked.setChecked(true);
            holder.seated.setChecked(false);
            holder.PrContainer.setBackgroundColor(Color.parseColor("#ECE131"));
        } else {
            holder.booked.setChecked(false);
            holder.seated.setChecked(false);
            holder.PrContainer.setBackgroundColor(Color.parseColor("#f51b00"));
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("BookingApp").child("Details");

        holder.booked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                is_booked = b;
                BookingModel booking = new BookingModel(bookingModels.get(position).getName(), bookingModels.get(position).getTime(), bookingModels.get(position).getName(), bookingModels.get(position).getContact_no(), bookingModels.get(position).getBuzzer_no(), bookingModels.get(position).getContact_no(), is_booked, is_seated);
                myRef.child(key).setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
//                            context.setResult(Activity.RESULT_OK, resultIntent);
//                            finish(); // Finish the activity after saving the changes
                        } else {
                            Toast.makeText(context, "Failed to update booking", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        holder.seated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                is_seated = b;
                BookingModel booking = new BookingModel(bookingModels.get(position).getName(), bookingModels.get(position).getTime(), bookingModels.get(position).getName(), bookingModels.get(position).getContact_no(), bookingModels.get(position).getBuzzer_no(), bookingModels.get(position).getContact_no(), is_booked, is_seated);
                myRef.child(key).setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
//
//                            context.setResult(Activity.RESULT_OK, resultIntent);
//                            finish(); // Finish the activity after saving the changes
                        } else {
                            Toast.makeText(context, "Failed to update booking", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        holder.contact_no.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    holder.contact_no.setText("Contact No: ********");
                }
                return false; // Allow other touch events to be handled
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingModels.size();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView time, name, contact_no, buzzer_no, no_of_guest;
        RelativeLayout PrContainer;
        CheckBox booked, seated;
        ImageView delete;

        public ProductViewHolder(@NonNull View itemView, final AllBookingAdapter.onItemClickListener itemlistener) {
            super(itemView);
            PrContainer = itemView.findViewById(R.id.PrContainer);
            delete = itemView.findViewById(R.id.delete);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            contact_no = itemView.findViewById(R.id.contact_number);
            buzzer_no = itemView.findViewById(R.id.buzzer_number);
            no_of_guest = itemView.findViewById(R.id.number_of_guests);
            booked = itemView.findViewById(R.id.booked);
            seated = itemView.findViewById(R.id.seated);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemlistener.onItemClick(position);
                        }
                    }
                }
            });


        }
    }
}
