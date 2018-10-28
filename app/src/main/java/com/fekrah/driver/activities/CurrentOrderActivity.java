package com.fekrah.driver.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
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

    @BindView(R.id.offered_time)
    TextView offerdTime;

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
                String key = FirebaseDatabase.getInstance().getReference().push().getKey();
                final OrderResponse offer = new OrderResponse(
                        MainActivity.driver.getName(),
                        key,
                        costEdt.getText().toString(),
                        MainActivity.results[0]+getString(R.string.k_m),
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
                            else if (dataSnapshot.getValue().toString().equals("accept"))
                            showAccepted();
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
        offerdTime.setText(estimatedTimeEdt.getText().toString().trim());
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
}
