package com.fekrah.driver.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fekrah.driver.R;
import com.fekrah.driver.adapters.AcceptedOrderDriversAdapter;
import com.fekrah.driver.fragments.TalabatFragment;
import com.fekrah.driver.models.Driver;
import com.fekrah.driver.models.Offer;
import com.fekrah.driver.models.Order;
import com.fekrah.driver.models.OrderResponse;
import com.fekrah.driver.models.State;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafakob.drawme.DrawMeButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fekrah.driver.activities.MainActivity.isOrderSent;
import static com.fekrah.driver.activities.MainActivity.orderSent;
import static com.fekrah.driver.activities.MainActivity.setOrderSent;

public class CurrentOrderActivity extends BaseActivity {

    @BindView(R.id.from_location)
    TextView from;

    @BindView(R.id.to_location)
    TextView to;

    @BindView(R.id.distance_location)
    TextView distance;

    @BindView(R.id.cost_location)
    TextView cost;

    @BindView(R.id.details)
    TextView orderDetails;

    @BindView(R.id.notes)
    TextView notes;

    @BindView(R.id.cost_edt)
    EditText costEdt;

    @BindView(R.id.estimated_time_edt)
    EditText estimatedTimeEdt;

    @BindView(R.id.send_offer)
    DrawMeButton sendOffer;

    @BindView(R.id.empty_order)
    TextView emptyOrder;

    @BindView(R.id.current_order_view)
    NestedScrollView orderView;

    @BindView(R.id.send_offer_view)
    View sendOfferView;

    @BindView(R.id.offered_view)
    View offerdView ;

    @BindView(R.id.accepted_order_view)
    View acceptedOrderView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.offered_cost)
    TextView offerdCost;

    @BindView(R.id.go_chats)
    DrawMeButton goChats;

    @BindView(R.id.finish_order)
    DrawMeButton finishOrder;

    Order order;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i = MainActivity.i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        counter();
        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).child("order")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        from.setText(order.getArrival_location());
                        to.setText(order.getReceiver_location());
                        distance.setText(order.getDistance());
                        cost.setText(order.getCost());
                        orderDetails.setText(order.getDetails());
                        notes.setText(order.getNotes());
                        showOrder();
                        mCountDownTimer.start();
                    }

                }else {
                    mCountDownTimer.cancel();
                    mCountDownTimer.onFinish();
                    showText();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (costEdt.getText().toString().trim().equals("")){

                    displaySnackbar(getString(R.string.write_cost));
                    return;
                }

//                else if (estimatedTimeEdt.getText().toString().trim().equals("")){
//                    displaySnackbar(getString(R.string.write_time));
//                    return;
//                }
                //String result = results[0].substring(0, results[0].length() - 2);

                String key = FirebaseDatabase.getInstance().getReference().push().getKey();
                final OrderResponse offer = new OrderResponse(
                        MainActivity.driver.getName(),
                        key,
                        costEdt.getText().toString(),
                        MainActivity.results[0],
                        MainActivity.driver.getUser_key(),
                        MainActivity.driver.getImg(),
                        "",
                        MainActivity.driver.getRating_count(),
                        MainActivity.driver.getRating()
                );

                FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                        .child(order.getUser_key()).child("drivers").child(key).setValue(offer)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    MainActivity.setOrderSent(true);
                                    FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                            .child(FirebaseAuth.getInstance().getUid()).child("offer")
                                            .setValue("sent")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            setOrderSent(true);
                                            showOfferedView();
                                        }
                                    });

                                }

                            }
                        });
            }
        });

        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).child("offer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            if (dataSnapshot.getValue().toString().equals("sent"))
                            showOfferedView();
                            else if (dataSnapshot.getValue().toString().equals("accept")){
                                showAccepted();
//                                Toast.makeText(CurrentOrderActivity.this, ""+MainActivity.accepted, Toast.LENGTH_SHORT).show();
//                                if (!MainActivity.accepted){
//                                    MainActivity.accepted=true;
//                                    createNotifyAccept(MainActivity.user.getName(),MainActivity.user.getImg(),getString(R.string.accept_offer),"accept_order",124);
//                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        goChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifyIntent = new Intent(CurrentOrderActivity.this, ChatsRoomsActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(notifyIntent);
            }
        });

        finishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                        .child(FirebaseAuth.getInstance().getUid()).removeValue();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //NotifyAccept();
    }

    private void counter() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setProgress(i);
        mCountDownTimer = new CountDownTimer(40000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i++;
                mProgressBar.setProgress((int) i * 100 / (40000 / 1000));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i=0;
                mProgressBar.setProgress(i);
                mProgressBar.setVisibility(View.GONE);

            }
        };
    }

    private void showOfferedView() {
        offerdCost.setText(costEdt.getText().toString().trim());
        sendOfferView.setVisibility(View.GONE);
        offerdView.setVisibility(View.VISIBLE);
        acceptedOrderView.setVisibility(View.GONE);
    }

    private void showAccepted(){
        sendOfferView.setVisibility(View.GONE);
        offerdView.setVisibility(View.GONE);
        acceptedOrderView.setVisibility(View.VISIBLE);
    }
    private void showOrder() {
        orderView.setVisibility(View.VISIBLE);
        emptyOrder.setVisibility(View.GONE);
    }

    private void showText() {
        orderView.setVisibility(View.GONE);
        emptyOrder.setVisibility(View.VISIBLE);
    }

    private void NotifyAccept(){
        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).child("offer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            if (dataSnapshot.getValue().toString().equals("accept")){
                                if (!MainActivity.accepted){
                                    MainActivity.accepted=true;
                                    createNotifyAccept(MainActivity.user.getName(),MainActivity.user.getImg(),getString(R.string.accept_offer),"accept_order",124);
                                }
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    public void createNotifyAccept(final String name, String img, final String content, final String id, final int id2) {

        Intent notifyIntent = new Intent(this, ChatsRoomsActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Create the PendingIntent
        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(id, "Accept Order", importance);
            channel.setDescription("Notifications for accepted orders ");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        final Bitmap[] theBitmap = {null};
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_dummy_user);
        requestOptions.error(R.drawable.ic_dummy_user);
        Glide.with(this).asBitmap().
                load(img).into(new SimpleTarget<Bitmap>(90, 90) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                theBitmap[0] = resource;
                CharSequence charSequence = (CharSequence) content;
                NotificationCompat.Style d = new NotificationCompat.BigTextStyle()
                        .bigText(charSequence);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CurrentOrderActivity.this, id)
                        .setSmallIcon(R.drawable.shoppingcart)
                        .setContentTitle(name)
                        .setLargeIcon(resource)
                        .setContentText(content)
                        .setStyle(d)
                        .setColor(ContextCompat.getColor(CurrentOrderActivity.this, R.color.colorPrimary))
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 100, 1000, 100})
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.new_order))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CurrentOrderActivity.this);

                notificationManager.notify(id2, mBuilder.build());

//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                    ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.new_order));
//                    if (!ringtone.isPlaying())
//                        ringtone.play();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

        });

    }

}
