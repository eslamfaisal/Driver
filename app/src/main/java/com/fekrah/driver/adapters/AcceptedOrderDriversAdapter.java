package com.fekrah.driver.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.driver.R;
import com.fekrah.driver.models.Driver;
import com.rafakob.drawme.DrawMeButton;

import java.util.List;

public class AcceptedOrderDriversAdapter extends RecyclerView.Adapter<AcceptedOrderDriversAdapter.DriverViewHolder> {

    public static final String USER_ID = "user_id";
    Context context;
    List<Driver> drivers;

    public AcceptedOrderDriversAdapter(Context context, List<Driver> users) {
        this.context = context;
        this.drivers = users;

    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_accepted_order_driver_item_list, viewGroup, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int i) {

        Driver driver = drivers.get(i);

        holder.userName.setText(driver.getName());

        holder.rate.setRating(getStars(driver));
        holder.profileImage.setImageURI(driver.getImg());
        holder.refuseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.acceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class DriverViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView profileImage;
        TextView userName;
        DrawMeButton acceptOrder;
        DrawMeButton refuseOrder;
        RatingBar rate;
        View mainView;

        public DriverViewHolder(@NonNull View view) {
            super(view);

            mainView = view;
            profileImage = view.findViewById(R.id.profile_image);
            userName = view.findViewById(R.id.user_name);
            rate = view.findViewById(R.id.user_rate);
            acceptOrder = view.findViewById(R.id.accept);
            refuseOrder = view.findViewById(R.id.refuse);
        }
    }

    private int getStars(Driver driver){
        int stars = 0;
        int ratingUsersCount = driver.getRating_count();
        int rating = (int) driver.getRating();
        int maxRating = ratingUsersCount *5;
        //calculate
        int a = 5*rating;
        stars = a/maxRating;
        return stars;
    }

    public void add(Driver user) {
        drivers.add(user);
        notifyDataSetChanged();
    }
}
